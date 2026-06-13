package com.lrs.core.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.OrderNumberGenerator;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.order.OrderIdDto;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.app.dto.order.OrderSubmitDto;
import com.lrs.core.app.vo.BizOrderItemVo;
import com.lrs.core.app.vo.OrderDetailVo;
import com.lrs.core.app.vo.OrderGoodsVo;
import com.lrs.core.app.vo.OrderListVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.entity.BizCart;
import com.lrs.core.business.entity.BizMerchant;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.service.IBizAddressService;
import com.lrs.core.business.service.IBizCartService;
import com.lrs.core.business.service.IBizMerchantService;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.business.service.IBizOrderSubService;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.business.service.IBizProductSkuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 小程序 - 订单 Controller（多商家版本）
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Slf4j
@RestController
@RequestMapping("/app/order")
@Validated
public class AppOrderController extends BaseController {

    @Resource
    private IBizOrderService bizOrderService;

    @Resource
    private IBizOrderSubService bizOrderSubService;

    @Resource
    private IBizOrderItemService bizOrderItemService;

    @Resource
    private IBizCartService bizCartService;

    @Resource
    private IBizProductService bizProductService;

    @Resource
    private IBizProductSkuService bizProductSkuService;

    @Resource
    private IBizAddressService bizAddressService;

    @Resource
    private IBizMerchantService bizMerchantService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) throw new RuntimeException("请先登录");
        return user.getUserId();
    }

    /**
     * 提交订单（支持多商家，支持立即购买）
     */
    @OperateLog(title = "小程序-提交订单")
    @PostMapping("/submit")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R submit(@RequestBody OrderSubmitDto dto) {
        Long userId = getUserId();

        List<BizCart> cartItems = new ArrayList<>();

        // 判断是立即购买还是购物车结算
        if (dto.getSkuId() != null && dto.getQuantity() != null && dto.getQuantity() > 0) {
            // 立即购买：直接根据SKU创建临时购物车项
            BizProductSku sku = bizProductSkuService.getById(dto.getSkuId());
            if (sku == null) return R.error("商品规格不存在");

            BizProduct product = bizProductService.getById(sku.getProductId());
            if (product == null) return R.error("商品不存在");

            // 创建临时购物车项
            BizCart tempCart = new BizCart();
            tempCart.setId(-1L); // 临时ID，不会被保存到数据库
            tempCart.setUserId(userId);
            tempCart.setProductId(product.getId());
            tempCart.setSkuId(sku.getId());
            tempCart.setQuantity(dto.getQuantity());
            tempCart.setSelected(1);
            cartItems.add(tempCart);
        } else {
            // 购物车结算：获取选中的购物车项
            LambdaQueryWrapper<BizCart> cartQuery = new LambdaQueryWrapper<>();
            cartQuery.eq(BizCart::getUserId, userId)
                    .eq(BizCart::getSelected, 1);
            if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
                cartQuery.in(BizCart::getId, dto.getCartIds());
            }
            cartItems = bizCartService.list(cartQuery);
            if (cartItems.isEmpty()) return R.error("购物车为空");
        }

        // 2. 批量查商品、SKU和商家信息
        Set<Long> productIds = cartItems.stream().map(BizCart::getProductId).collect(Collectors.toSet());
        Set<Long> skuIds = cartItems.stream().map(BizCart::getSkuId).collect(Collectors.toSet());
        Map<Long, BizProduct> productMap = bizProductService.listByIds(productIds).stream()
                .collect(Collectors.toMap(BizProduct::getId, p -> p));
        Map<Long, BizProductSku> skuMap = bizProductSkuService.listByIds(skuIds).stream()
                .collect(Collectors.toMap(BizProductSku::getId, s -> s));
        Map<Long, BizMerchant> merchantMap = bizMerchantService.listByIds(
                productMap.values().stream().map(BizProduct::getMerchantId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(BizMerchant::getId, m -> m));

        // 3. 获取收货地址
        BizAddress address = bizAddressService.getById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) return R.error("收货地址不存在");

        // 4. 按商家分组购物车商品
        Map<Long, List<BizCart>> cartByMerchant = cartItems.stream()
                .collect(Collectors.groupingBy(item -> {
                    BizProduct p = productMap.get(item.getProductId());
                    return p != null ? p.getMerchantId() : 0L;
                }));

        // 5. 计算总金额并校验库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BizCart item : cartItems) {
            BizProductSku sku = skuMap.get(item.getSkuId());
            if (sku == null || sku.getStock() < item.getQuantity()) {
                BizProduct p = productMap.get(item.getProductId());
                return R.error((p != null ? p.getProductName() : "商品") + " 库存不足");
            }
            totalAmount = totalAmount.add(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 6. 创建订单主表
        String orderNo = OrderNumberGenerator.nextId();
        BizOrder order = new BizOrder()
                .setOrderNo(orderNo)
                .setUserId(userId)
                .setTotalAmount(totalAmount)
                .setFreightAmount(BigDecimal.ZERO)
                .setDiscountAmount(BigDecimal.ZERO)
                .setPayAmount(totalAmount)
                .setAddressId(address.getId())
                .setAddressSnapshot(JSON.toJSONString(address))
                .setRemark(dto.getRemark())
                .setStatus(BizOrder.STATUS_PENDING_PAY)
                .setDeliveryType(BizOrder.DELIVERY_TYPE_EXPRESS)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        bizOrderService.save(order);

        // 7. 按商家创建子订单和订单项
        for (Map.Entry<Long, List<BizCart>> entry : cartByMerchant.entrySet()) {
            Long merchantId = entry.getKey();
            List<BizCart> merchantCarts = entry.getValue();

            BizMerchant merchant = merchantMap.get(merchantId);
            String merchantName = merchant != null ? merchant.getMerchantName() : "未知商家";

            // 计算商家订单金额
            BigDecimal merchantTotal = BigDecimal.ZERO;
            for (BizCart item : merchantCarts) {
                BizProductSku sku = skuMap.get(item.getSkuId());
                if (sku != null) {
                    merchantTotal = merchantTotal.add(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }

            // 创建子订单
            String subNo = OrderNumberGenerator.nextId();
            BizOrderSub sub = new BizOrderSub()
                    .setSubNo(subNo)
                    .setOrderId(order.getId())
                    .setOrderNo(orderNo)
                    .setMerchantId(merchantId)
                    .setMerchantName(merchantName)
                    .setItemAmount(merchantTotal)
                    .setFreightAmount(BigDecimal.ZERO)
                    .setDiscountAmount(BigDecimal.ZERO)
                    .setMerchantDiscount(BigDecimal.ZERO)
                    .setPayAmount(merchantTotal)
                    .setCommissionRate(merchant != null ? merchant.getCommissionRate() : BigDecimal.ZERO)
                    .setCommissionAmount(BigDecimal.ZERO) // 平台抽成计算
                    .setSettleAmount(merchantTotal) // 商家实收
                    .setSettleStatus(BizOrderSub.SETTLE_STATUS_UNSETTLED)
                    .setDeliveryStatus(BizOrderSub.DELIVERY_STATUS_PENDING)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            bizOrderSubService.save(sub);

            // 创建订单明细 + 扣库存
            for (BizCart item : merchantCarts) {
                BizProduct product = productMap.get(item.getProductId());
                BizProductSku sku = skuMap.get(item.getSkuId());

                if (sku == null) continue;

                BizOrderItem orderItem = new BizOrderItem()
                        .setOrderId(order.getId())
                        .setOrderNo(orderNo)
                        .setSubId(sub.getId())
                        .setMerchantId(merchantId)
                        .setProductId(item.getProductId())
                        .setProductName(product != null ? product.getProductName() : "")
                        .setProductImage(product != null ? product.getMainImage() : "")
                        .setSkuId(item.getSkuId())
                        .setSkuName(sku.getSpecName())
                        .setSkuSpecs(sku.getSpecValues())
                        .setPrice(sku.getPrice())
                        .setQuantity(item.getQuantity())
                        .setItemAmount(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .setDiscountAmount(BigDecimal.ZERO)
                        .setPayAmount(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .setRefundStatus(BizOrderItem.REFUND_STATUS_NONE)
                        .setRefundAmount(BigDecimal.ZERO)
                        .setCreateTime(LocalDateTime.now())
                        .setUpdateTime(LocalDateTime.now());
                bizOrderItemService.save(orderItem);

                // 扣减库存
                sku.setStock(sku.getStock() - item.getQuantity());
                bizProductSkuService.updateById(sku);
            }
        }

        // 8. 清除购物车中已下单项（仅购物车结算时）
        if (dto.getSkuId() == null) {
            // 购物车结算：删除已下单的购物车项
            List<Long> cartIdsToRemove = cartItems.stream().map(BizCart::getId).collect(Collectors.toList());
            bizCartService.removeByIds(cartIdsToRemove);
        }
        // 立即购买时不需要删除购物车（因为使用的是临时购物车项）

        // 9. 返回结果
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", totalAmount);
        result.put("merchantCount", cartByMerchant.size()); // 涉及商家数量
        return R.ok(result);
    }

    /**
     * 订单列表
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody OrderQueryDto dto) {
        Long userId = getUserId();
        Integer status = dto.getStatus();

        LambdaQueryWrapper<BizOrder> query = new LambdaQueryWrapper<>();
        query.eq(BizOrder::getUserId, userId).orderByDesc(BizOrder::getId);
        
        if (status != null) {
            if (status == -1) {
                // 进行中：包含待发货、已发货
                query.in(BizOrder::getStatus, 2, 3);
            } else if (status == 4) {
                // 待评价（已完成）
                query.eq(BizOrder::getStatus, 4);
            } else if (status > 0) {
                query.eq(BizOrder::getStatus, status);
            }
            // status == 0 时查询全部订单，不添加额外条件
        }

        Page<BizOrder> page = new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize());
        Page<BizOrder> result = bizOrderService.page(page, query);

        if (!result.getRecords().isEmpty()) {
            List<Long> orderIds = result.getRecords().stream().map(BizOrder::getId).collect(Collectors.toList());

            // 批量查询订单商品（已包含商家信息）
            List<OrderGoodsVo> allGoods = bizOrderService.getGoodsList(orderIds);
            Map<Long, List<OrderGoodsVo>> goodsMap = allGoods.stream()
                    .collect(Collectors.groupingBy(OrderGoodsVo::getOrderId));

            // 查询子订单数量
            Map<Long, Long> subCountMap = bizOrderSubService.countByOrderIds(orderIds);

            List<OrderListVo> voList = result.getRecords().stream().map(order -> {
                OrderListVo vo = new OrderListVo();
                BeanUtil.copyProperties(order, vo);
                vo.setGoodsList(goodsMap.getOrDefault(order.getId(), Collections.emptyList()));
                vo.setSubCount(subCountMap.getOrDefault(order.getId(), 0L).intValue());
                return vo;
            }).collect(Collectors.toList());

            Map<String, Object> pageResult = new LinkedHashMap<>();
            pageResult.put("records", voList);
            pageResult.put("total", result.getTotal());
            pageResult.put("current", result.getCurrent());
            pageResult.put("pages", result.getPages());
            return R.ok(pageResult);
        }

        return R.ok(result);
    }

    /**
     * 订单详情
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody OrderIdDto dto) {
        Long userId = getUserId();
        Long orderId = dto.getOrderId();

        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) return R.error("订单不存在");

        // 查询子订单
        List<BizOrderSub> subs = bizOrderSubService.getByOrderId(orderId);

        // 查询订单明细
        LambdaQueryWrapper<BizOrderItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(BizOrderItem::getOrderId, orderId);
        List<BizOrderItem> items = bizOrderItemService.list(itemQuery);

        OrderDetailVo vo = new OrderDetailVo();
        BeanUtil.copyProperties(order, vo);

        List<BizOrderItemVo> itemVoList = items.stream()
                .map(item -> BeanUtil.toBean(item, BizOrderItemVo.class))
                .collect(Collectors.toList());
        vo.setItems(itemVoList);

        if (order.getAddressSnapshot() != null) {
            vo.setAddress(JSON.parseObject(order.getAddressSnapshot()));
        }

        // 附加子订单信息
        final Long finalUserId = userId;
        List<Map<String, Object>> subList = subs.stream().map(sub -> {
            Map<String, Object> subMap = new LinkedHashMap<>();
            subMap.put("subId", sub.getId());
            subMap.put("subNo", sub.getSubNo());
            subMap.put("merchantName", sub.getMerchantName());
            subMap.put("merchantId", sub.getMerchantId());
            subMap.put("deliveryStatus", sub.getDeliveryStatus());
            subMap.put("expressCompany", sub.getExpressCompany());
            subMap.put("expressNo", sub.getExpressNo());
            subMap.put("deliveryTime", sub.getDeliveryTime());
            return subMap;
        }).collect(Collectors.toList());
        vo.setSubList(subList);

        return R.ok(vo);
    }

    /**
     * 取消订单（仅待支付）
     */
    @OperateLog(title = "小程序-取消订单")
    @PostMapping("/cancel")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R cancel(@RequestBody OrderIdDto dto) {
        Long userId = getUserId();
        Long orderId = dto.getOrderId();

        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) return R.error("订单不存在");
        if (order.getStatus() != BizOrder.STATUS_PENDING_PAY) return R.error("仅待支付订单可取消");

        // 恢复库存
        LambdaQueryWrapper<BizOrderItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(BizOrderItem::getOrderId, orderId);
        List<BizOrderItem> items = bizOrderItemService.list(itemQuery);

        for (BizOrderItem item : items) {
            BizProductSku sku = bizProductSkuService.getById(item.getSkuId());
            if (sku != null) {
                sku.setStock(sku.getStock() + item.getQuantity());
                bizProductSkuService.updateById(sku);
            }
        }

        // 更新子订单状态
        List<BizOrderSub> subs = bizOrderSubService.getByOrderId(orderId);
        for (BizOrderSub sub : subs) {
            sub.setIsDeleted((byte) 1);
            sub.setUpdateTime(LocalDateTime.now());
            bizOrderSubService.updateById(sub);
        }

        // 更新主订单状态
        order.setStatus(BizOrder.STATUS_CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        bizOrderService.updateById(order);

        return R.ok();
    }

    /**
     * 订单统计（各状态数量）
     * 使用单SQL查询一次性获取所有状态统计，避免多次数据库查询
     */
    @PostMapping("/count")
    @ResponseBody
    public R count() {
        Long userId = getUserId();
        Map<String, Object> result = bizOrderService.countByStatus(userId);
        return R.ok(result);
    }

    /**
     * 确认收货（支持按子订单确认）
     */
    @OperateLog(title = "小程序-确认收货")
    @PostMapping("/confirmReceive")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R confirmReceive(@RequestBody ConfirmReceiveDto dto) {
        Long userId = getUserId();
        Long orderId = dto.getOrderId();
        Long subId = dto.getSubId(); // 可选，如果传了则按子订单确认

        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) return R.error("订单不存在");

        if (subId != null) {
            // 按子订单确认收货
            BizOrderSub sub = bizOrderSubService.getById(subId);
            if (sub == null) return R.error("子订单不存在");
            if (sub.getDeliveryStatus() != BizOrderSub.DELIVERY_STATUS_SHIPPED) {
                return R.error("仅已发货订单可确认收货");
            }

            sub.setDeliveryStatus(BizOrderSub.DELIVERY_STATUS_RECEIVED);
            sub.setReceiveTime(LocalDateTime.now());
            sub.setUpdateTime(LocalDateTime.now());
            bizOrderSubService.updateById(sub);

            // 检查是否所有子订单都已收货
            List<BizOrderSub> allSubs = bizOrderSubService.getByOrderId(orderId);
            boolean allReceived = allSubs.stream()
                    .allMatch(s -> s.getDeliveryStatus() == BizOrderSub.DELIVERY_STATUS_RECEIVED);

            if (allReceived) {
                order.setStatus(BizOrder.STATUS_RECEIVED);
                order.setReceiveTime(LocalDateTime.now());
                order.setUpdateTime(LocalDateTime.now());
                bizOrderService.updateById(order);
            }
        } else {
            // 按主订单确认收货（全部子订单）
            if (order.getStatus() != BizOrder.STATUS_DELIVERED) {
                return R.error("仅已发货订单可确认收货");
            }

            List<BizOrderSub> allSubs = bizOrderSubService.getByOrderId(orderId);
            for (BizOrderSub sub : allSubs) {
                if (sub.getDeliveryStatus() == BizOrderSub.DELIVERY_STATUS_SHIPPED) {
                    sub.setDeliveryStatus(BizOrderSub.DELIVERY_STATUS_RECEIVED);
                    sub.setReceiveTime(LocalDateTime.now());
                    sub.setUpdateTime(LocalDateTime.now());
                    bizOrderSubService.updateById(sub);
                }
            }

            order.setStatus(BizOrder.STATUS_RECEIVED);
            order.setReceiveTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            bizOrderService.updateById(order);
        }

        return R.ok();
    }

    // ==================== DTO ====================

    public static class ConfirmReceiveDto {
        private Long orderId;
        private Long subId; // 可选

        public Long getOrderId() { return orderId; }
        public void setOrderId(Long orderId) { this.orderId = orderId; }
        public Long getSubId() { return subId; }
        public void setSubId(Long subId) { this.subId = subId; }
    }
}
