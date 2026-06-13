package com.lrs.core.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.order.MerchantSubIdDto;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.business.service.IBizOrderSubService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商家后台 - 订单管理 Controller（多商家版本）
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Slf4j
@RestController
@RequestMapping("/merchant/order")
@Validated
public class MerchantOrderController extends BaseController {

    @Resource
    private IBizOrderService bizOrderService;

    @Resource
    private IBizOrderSubService bizOrderSubService;

    @Resource
    private IBizOrderItemService bizOrderItemService;

    /**
     * 获取当前商家ID
     */
    private Long getMerchantId() {
        Long merchantId = MerchantContextHolder.getMerchantId();
        if (merchantId == null) {
            throw new RuntimeException("请以商家身份登录");
        }
        return merchantId;
    }

    /**
     * 商家订单列表（分页）- 基于子订单查询
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) OrderQueryDto dto) {
        Long merchantId = getMerchantId();

        // 查询该商家的所有子订单
        LambdaQueryWrapper<BizOrderSub> subQuery = new LambdaQueryWrapper<>();
        subQuery.eq(BizOrderSub::getMerchantId, merchantId)
                .orderByDesc(BizOrderSub::getId);

        if (dto != null && dto.getStatus() != null && dto.getStatus() > 0) {
            // 根据状态筛选子订单
            subQuery.eq(BizOrderSub::getDeliveryStatus, dto.getStatus());
        }

        Page<BizOrderSub> page = new Page<>(
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );
        Page<BizOrderSub> result = bizOrderSubService.page(page, subQuery);

        if (!result.getRecords().isEmpty()) {
            // 查询订单主表信息
            List<Long> orderIds = result.getRecords().stream()
                    .map(BizOrderSub::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());
            Map<Long, BizOrder> orderMap = bizOrderService.listByIds(orderIds).stream()
                    .collect(Collectors.toMap(BizOrder::getId, o -> o));

            List<Map<String, Object>> voList = result.getRecords().stream().map(sub -> {
                Map<String, Object> vo = new LinkedHashMap<>();
                vo.put("subId", sub.getId());
                vo.put("subNo", sub.getSubNo());
                vo.put("orderId", sub.getOrderId());
                vo.put("orderNo", sub.getOrderNo());
                vo.put("merchantName", sub.getMerchantName());
                vo.put("itemAmount", sub.getItemAmount());
                vo.put("freightAmount", sub.getFreightAmount());
                vo.put("payAmount", sub.getPayAmount());
                vo.put("deliveryStatus", sub.getDeliveryStatus());
                vo.put("deliveryTime", sub.getDeliveryTime());
                vo.put("createTime", sub.getCreateTime());

                // 关联主订单信息
                BizOrder order = orderMap.get(sub.getOrderId());
                if (order != null) {
                    vo.put("orderStatus", order.getStatus());
                    vo.put("addressSnapshot", order.getAddressSnapshot());
                    vo.put("remark", order.getRemark());
                }
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
     * 获取商家子订单详情
     */
    @PostMapping("/subDetail")
    @ResponseBody
    public R subDetail(@RequestBody MerchantSubIdDto dto) {
        Long merchantId = getMerchantId();
        Long subId = dto.getSubId();

        BizOrderSub sub = bizOrderSubService.getById(subId);
        if (sub == null || !sub.getMerchantId().equals(merchantId)) {
            return R.error("子订单不存在");
        }

        // 查询订单主表
        BizOrder order = bizOrderService.getById(sub.getOrderId());

        // 查询子订单下的商品明细
        LambdaQueryWrapper<BizOrderItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(BizOrderItem::getSubId, subId);
        List<BizOrderItem> items = bizOrderItemService.list(itemQuery);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("sub", sub);
        result.put("order", order);
        result.put("items", items);

        return R.ok(result);
    }

    /**
     * 商家发货（基于子订单发货）
     */
    @OperateLog(title = "商家后台-发货")
    @PostMapping("/delivery")
    @ResponseBody
    public R delivery(@RequestBody OrderSubDeliveryDto dto) {
        Long merchantId = getMerchantId();
        Long subId = dto.getSubId();

        BizOrderSub sub = bizOrderSubService.getById(subId);
        if (sub == null || !sub.getMerchantId().equals(merchantId)) {
            return R.error("子订单不存在");
        }
        if (sub.getDeliveryStatus() != BizOrderSub.DELIVERY_STATUS_PENDING) {
            return R.error("当前子订单状态不可发货");
        }

        // 更新子订单发货状态
        sub.setDeliveryStatus(BizOrderSub.DELIVERY_STATUS_SHIPPED);
        sub.setDeliveryTime(LocalDateTime.now());
        sub.setExpressCompany(dto.getExpressCompany());
        sub.setExpressNo(dto.getExpressNo());
        bizOrderSubService.updateById(sub);

        // 检查是否所有子订单都已发货，更新主订单状态
        Long orderId = sub.getOrderId();
        List<BizOrderSub> allSubs = bizOrderSubService.getByOrderId(orderId);
        boolean allDelivered = allSubs.stream()
                .allMatch(s -> s.getDeliveryStatus() == BizOrderSub.DELIVERY_STATUS_SHIPPED
                        || s.getDeliveryStatus() == BizOrderSub.DELIVERY_STATUS_RECEIVED);

        if (allDelivered) {
            BizOrder order = bizOrderService.getById(orderId);
            if (order != null) {
                // 取最后一个快递信息作为主订单快递信息
                BizOrderSub lastSub = allSubs.get(allSubs.size() - 1);
                order.setStatus(BizOrder.STATUS_DELIVERED);
                order.setShipTime(LocalDateTime.now());
                order.setExpressCompany(lastSub.getExpressCompany());
                order.setExpressNo(lastSub.getExpressNo());
                bizOrderService.updateById(order);
            }
        }

        return R.ok();
    }

    /**
     * 取消子订单（商家端）
     */
    @OperateLog(title = "商家后台-取消子订单")
    @PostMapping("/cancelSub")
    @ResponseBody
    public R cancelSub(@RequestBody OrderSubCancelDto dto) {
        Long merchantId = getMerchantId();
        Long subId = dto.getSubId();
        String reason = dto.getCancelReason();

        BizOrderSub sub = bizOrderSubService.getById(subId);
        if (sub == null || !sub.getMerchantId().equals(merchantId)) {
            return R.error("子订单不存在");
        }
        if (sub.getDeliveryStatus() != BizOrderSub.DELIVERY_STATUS_PENDING) {
            return R.error("当前子订单状态不可取消");
        }

        // 标记子订单为已删除（逻辑删除）
        sub.setIsDeleted((byte) 1);
        sub.setMerchantRemark(reason);
        bizOrderSubService.updateById(sub);

        // 检查是否所有子订单都已取消，更新主订单状态
        Long orderId = sub.getOrderId();
        List<BizOrderSub> allSubs = bizOrderSubService.getByOrderId(orderId);
        boolean allCancelled = allSubs.stream()
                .allMatch(s -> s.getIsDeleted() == 1);

        if (allCancelled) {
            BizOrder order = bizOrderService.getById(orderId);
            if (order != null) {
                order.setStatus(BizOrder.STATUS_CANCELLED);
                order.setCancelReason("所有商家已取消");
                order.setCancelTime(LocalDateTime.now());
                bizOrderService.updateById(order);
            }
        }

        return R.ok();
    }

    // ==================== DTO内部类 ====================

    /**
     * 子订单发货DTO
     */
    public static class OrderSubDeliveryDto {
        private Long subId;
        private String expressCompany;
        private String expressNo;

        public Long getSubId() { return subId; }
        public void setSubId(Long subId) { this.subId = subId; }
        public String getExpressCompany() { return expressCompany; }
        public void setExpressCompany(String expressCompany) { this.expressCompany = expressCompany; }
        public String getExpressNo() { return expressNo; }
        public void setExpressNo(String expressNo) { this.expressNo = expressNo; }
    }

    /**
     * 子订单取消DTO
     */
    public static class OrderSubCancelDto {
        private Long subId;
        private String cancelReason;

        public Long getSubId() { return subId; }
        public void setSubId(Long subId) { this.subId = subId; }
        public String getCancelReason() { return cancelReason; }
        public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
    }
}
