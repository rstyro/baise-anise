package com.lrs.core.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.order.OrderIdDto;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.app.vo.BizOrderItemVo;
import com.lrs.core.app.vo.OrderDetailVo;
import com.lrs.core.app.vo.OrderGoodsVo;
import com.lrs.core.app.vo.OrderListVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家后台 - 订单管理 Controller
 * 
 * <p>商家管理员可以管理自己店铺的订单，数据通过 merchant_id 隔离。
 * </p>
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Slf4j
@RestController
@RequestMapping("/merchant/order")
@Validated
public class MerchantOrderController extends BaseController {

    @Resource
    private IBizOrderService bizOrderService;

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
     * 商家订单列表（分页）
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) OrderQueryDto dto) {
        Long merchantId = getMerchantId();
        
        LambdaQueryWrapper<BizOrder> query = new LambdaQueryWrapper<>();
        query.eq(BizOrder::getMerchantId, merchantId)
             .orderByDesc(BizOrder::getId);

        if (dto != null && dto.getStatus() != null && dto.getStatus() > 0) {
            query.eq(BizOrder::getStatus, dto.getStatus());
        }

        Page<BizOrder> page = new Page<>(
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );
        Page<BizOrder> result = bizOrderService.page(page, query);

        if (!result.getRecords().isEmpty()) {
            List<Long> orderIds = result.getRecords().stream()
                    .map(BizOrder::getId)
                    .collect(Collectors.toList());

            LambdaQueryWrapper<BizOrderItem> itemQuery = new LambdaQueryWrapper<>();
            itemQuery.in(BizOrderItem::getOrderId, orderIds);
            Map<Long, List<BizOrderItem>> itemMap = bizOrderItemService.list(itemQuery).stream()
                    .collect(Collectors.groupingBy(BizOrderItem::getOrderId));

            List<OrderListVo> voList = result.getRecords().stream().map(order -> {
                OrderListVo vo = new OrderListVo();
                BeanUtil.copyProperties(order, vo);

                List<BizOrderItem> items = itemMap.getOrDefault(order.getId(), List.of());
                vo.setGoodsList(items.stream().map(item -> {
                    OrderGoodsVo goods = new OrderGoodsVo();
                    goods.setGoodsUrl(item.getMainImage());
                    goods.setTitle(item.getProductName());
                    goods.setType(item.getSpecName());
                    goods.setPrice(item.getPrice());
                    goods.setNumber(item.getQuantity());
                    return goods;
                }).collect(Collectors.toList()));

                return vo;
            }).collect(Collectors.toList());

            Map<String, Object> pageResult = new java.util.LinkedHashMap<>();
            pageResult.put("records", voList);
            pageResult.put("total", result.getTotal());
            pageResult.put("current", result.getCurrent());
            pageResult.put("pages", result.getPages());
            return R.ok(pageResult);
        }

        return R.ok(result);
    }

    /**
     * 获取订单详情
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody OrderIdDto dto) {
        Long merchantId = getMerchantId();
        Long orderId = dto.getOrderId();

        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getMerchantId().equals(merchantId)) {
            return R.error("订单不存在");
        }

        LambdaQueryWrapper<BizOrderItem> itemQuery = new LambdaQueryWrapper<>();
        itemQuery.eq(BizOrderItem::getOrderId, orderId);
        List<BizOrderItem> items = bizOrderItemService.list(itemQuery);

        OrderDetailVo vo = new OrderDetailVo();
        BeanUtil.copyProperties(order, vo);

        List<BizOrderItemVo> itemVoList = items.stream()
                .map(item -> BeanUtil.toBean(item, BizOrderItemVo.class))
                .collect(Collectors.toList());
        vo.setItems(itemVoList);

        return R.ok(vo);
    }

    /**
     * 商家发货
     */
    @OperateLog(title = "商家后台-发货")
    @PostMapping("/delivery")
    @ResponseBody
    public R delivery(@RequestBody OrderIdDto dto) {
        Long merchantId = getMerchantId();
        Long orderId = dto.getOrderId();

        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getMerchantId().equals(merchantId)) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 2) {
            return R.error("当前订单状态不可发货");
        }

        order.setDeliveryStatus((byte) 1);
        order.setDeliveryTime(LocalDateTime.now());
        order.setStatus((byte) 3);
        bizOrderService.updateById(order);

        return R.ok();
    }

    /**
     * 取消订单（商家端）
     */
    @OperateLog(title = "商家后台-取消订单")
    @PostMapping("/cancel")
    @ResponseBody
    public R cancel(@RequestBody OrderIdDto dto) {
        Long merchantId = getMerchantId();
        Long orderId = dto.getOrderId();

        BizOrder order = bizOrderService.getById(orderId);
        if (order == null || !order.getMerchantId().equals(merchantId)) {
            return R.error("订单不存在");
        }
        if (order.getStatus() != 1) {
            return R.error("当前订单状态不可取消");
        }

        order.setStatus((byte) 0);
        bizOrderService.updateById(order);

        return R.ok();
    }
}