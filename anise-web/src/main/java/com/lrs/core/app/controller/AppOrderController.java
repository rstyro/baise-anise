package com.lrs.core.app.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
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
import com.lrs.core.business.entity.*;
import com.lrs.core.business.service.*;
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
 * 小程序 - 订单 Controller
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Slf4j
@RestController
@RequestMapping("/app/order")
@Validated
public class AppOrderController extends BaseController {

    @Resource private IBizOrderService bizOrderService;
    @Resource private IBizOrderItemService bizOrderItemService;
    @Resource private IBizCartService bizCartService;
    @Resource private IBizProductService bizProductService;
    @Resource private IBizProductSkuService bizProductSkuService;
    @Resource private IBizAddressService bizAddressService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) throw new RuntimeException("请先登录");
        return user.getUserId();
    }

    /**
     * 提交订单
     */
    @OperateLog(title = "小程序-提交订单")
    @PostMapping("/submit")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public R submit(@RequestBody OrderSubmitDto dto) {
        Long userId = getUserId();

        // 获取选中的购物车项
        LambdaQueryWrapper<BizCart> cartQuery = new LambdaQueryWrapper<>();
        cartQuery.eq(BizCart::getUserId, userId)
                 .eq(BizCart::getSelected, 1);
        if (dto.getCartIds() != null && !dto.getCartIds().isEmpty()) {
            cartQuery.in(BizCart::getId, dto.getCartIds());
        }
        List<BizCart> cartItems = bizCartService.list(cartQuery);
        if (cartItems.isEmpty()) return R.error("购物车为空");

        // 批量查商品和SKU
        Set<Long> productIds = cartItems.stream().map(BizCart::getProductId).collect(Collectors.toSet());
        Set<Long> skuIds = cartItems.stream().map(BizCart::getSkuId).collect(Collectors.toSet());
        Map<Long, BizProduct> productMap = bizProductService.listByIds(productIds).stream()
                .collect(Collectors.toMap(BizProduct::getId, p -> p));
        Map<Long, BizProductSku> skuMap = bizProductSkuService.listByIds(skuIds).stream()
                .collect(Collectors.toMap(BizProductSku::getId, s -> s));

        // 获取收货地址
        BizAddress address = bizAddressService.getById(dto.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) return R.error("收货地址不存在");

        // 计算金额 + 校验库存
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (BizCart item : cartItems) {
            BizProductSku sku = skuMap.get(item.getSkuId());
            if (sku == null || sku.getStock() < item.getQuantity()) {
                BizProduct p = productMap.get(item.getProductId());
                return R.error((p != null ? p.getProductName() : "商品") + " 库存不足");
            }
            totalAmount = totalAmount.add(sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        // 创建订单
        BizOrder order = new BizOrder()
                .setOrderNo(OrderNumberGenerator.nextId())
                .setUserId(userId)
                .setMerchantId(dto.getMerchantId() != null ? dto.getMerchantId() : 1L)  // 使用传入的商家ID，默认1号商家
                .setOrderType((byte) 1)
                .setTotalAmount(totalAmount)
                .setPayAmount(totalAmount)
                .setFreightAmount(BigDecimal.ZERO)
                .setDiscountAmount(BigDecimal.ZERO)
                .setPayStatus((byte) 0)
                .setDeliveryStatus((byte) 0)
                .setAddressId(address.getId())
                .setAddressSnapshot(JSON.toJSONString(address))
                .setRemark(dto.getRemark())
                .setStatus((byte) 1)
                .setCreateTime(LocalDateTime.now())
                .setUpdateTime(LocalDateTime.now());
        bizOrderService.save(order);

        // 创建订单明细 + 扣库存
        for (BizCart item : cartItems) {
            BizProduct product = productMap.get(item.getProductId());
            BizProductSku sku = skuMap.get(item.getSkuId());

            BizOrderItem orderItem = new BizOrderItem()
                    .setOrderId(order.getId())
                    .setMerchantId(order.getMerchantId())  // 设置商家ID
                    .setProductId(item.getProductId())
                    .setSkuId(item.getSkuId())
                    .setProductName(product != null ? product.getProductName() : "")
                    .setSpecName(sku != null ? sku.getSpecName() : "")
                    .setMainImage(product != null ? product.getMainImage() : "")
                    .setPrice(sku != null ? sku.getPrice() : BigDecimal.ZERO)
                    .setQuantity(item.getQuantity())
                    .setTotalAmount(sku != null ? sku.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())) : BigDecimal.ZERO)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            bizOrderItemService.save(orderItem);

            // 扣减库存
            if (sku != null) {
                sku.setStock(sku.getStock() - item.getQuantity());
                bizProductSkuService.updateById(sku);
            }
        }

        // 清除购物车中已下单项
        List<Long> cartIdsToRemove = cartItems.stream().map(BizCart::getId).collect(Collectors.toList());
        bizCartService.removeByIds(cartIdsToRemove);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("orderId", order.getId());
        result.put("orderNo", order.getOrderNo());
        result.put("payAmount", totalAmount);
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
        if (status != null && status > 0) query.eq(BizOrder::getStatus, status);

        Page<BizOrder> page = new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize());
        Page<BizOrder> result = bizOrderService.page(page, query);

        // 查询订单明细
        if (!result.getRecords().isEmpty()) {
            List<Long> orderIds = result.getRecords().stream().map(BizOrder::getId).collect(Collectors.toList());
            LambdaQueryWrapper<BizOrderItem> itemQuery = new LambdaQueryWrapper<>();
            itemQuery.in(BizOrderItem::getOrderId, orderIds);
            Map<Long, List<BizOrderItem>> itemMap = bizOrderItemService.list(itemQuery).stream()
                    .collect(Collectors.groupingBy(BizOrderItem::getOrderId));

            List<OrderListVo> voList = result.getRecords().stream().map(order -> {
                OrderListVo vo = new OrderListVo();
                BeanUtil.copyProperties(order, vo);

                List<BizOrderItem> items = itemMap.getOrDefault(order.getId(), Collections.emptyList());
                List<OrderGoodsVo> goodsList = items.stream().map(item -> {
                    OrderGoodsVo goods = new OrderGoodsVo();
                    goods.setGoodsUrl(item.getMainImage());
                    goods.setTitle(item.getProductName());
                    goods.setType(item.getSpecName());
                    goods.setPrice(item.getPrice());
                    goods.setNumber(item.getQuantity());
                    return goods;
                }).collect(Collectors.toList());
                vo.setGoodsList(goodsList);
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
        if (order.getStatus() != 1) return R.error("仅待支付订单可取消");

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

        order.setStatus((byte) 0);
        order.setUpdateTime(LocalDateTime.now());
        bizOrderService.updateById(order);
        return R.ok();
    }

    /**
     * 确认收货
     */
    @OperateLog(title = "小程序-确认收货")
    @PostMapping("/confirmReceive")
    @ResponseBody
    public R confirmReceive(@RequestBody OrderIdDto dto) {
        Long userId = getUserId();
        Long orderId = dto.getOrderId();
        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getUserId().equals(userId)) return R.error("订单不存在");
        if (order.getStatus() != 3) return R.error("仅已发货订单可确认收货");

        order.setStatus((byte) 4);
        order.setDeliveryStatus((byte) 2);
        order.setReceiveTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        bizOrderService.updateById(order);
        return R.ok();
    }

}

