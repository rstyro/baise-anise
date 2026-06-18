package com.lrs.core.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.annotation.RepeatSubmit;
import com.lrs.common.enums.LockType;
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
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.business.service.IBizOrderSubService;
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
    private IBizProductSkuService bizProductSkuService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) throw new RuntimeException("请先登录");
        return user.getUserId();
    }

    /**
     * 提交订单（支持多商家，支持立即购买）
     */
    @OperateLog(title = "小程序-提交订单")
    @RepeatSubmit(lockType = LockType.APP_USER_PARAM, lockTime = 5000, message = "订单提交中，请勿重复提交")
    @PostMapping("/submit")
    @ResponseBody
    public R submit(@RequestBody OrderSubmitDto dto) {
        return R.ok(bizOrderService.submitAppOrder(dto, getUserId()));
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
            // 解析 sku_specs JSON
            allGoods.forEach(OrderGoodsVo::parseSkuSpecs);
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
                sku.setStock(sku.getStock().add(BigDecimal.valueOf(item.getQuantity())));
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
