package com.lrs.core.app.controller;

import com.lrs.common.vo.R;
import com.lrs.core.app.dto.pay.PayOrderIdDto;
import com.lrs.core.app.vo.PayResultVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.service.IBizOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * 小程序 - 支付 Controller
 * <p>
 * 微信支付集成骨架。当前为模拟实现，接入真实微信支付时替换内部调用。
 * </p>
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Slf4j
@RestController
@RequestMapping("/app/pay")
@Validated
public class AppPayController extends BaseController {

    @Resource
    private IBizOrderService bizOrderService;

    /**
     * 统一下单（模拟）
     * 真实接入时：调用微信统一下单API → 获取 prepay_id → 签名 → 返回给小程序调起支付
     */
    @PostMapping("/unifiedOrder")
    @ResponseBody
    public R unifiedOrder(@RequestBody PayOrderIdDto dto) {
        Long orderId = dto.getOrderId();
        BizOrder order = bizOrderService.getById(orderId);
        if (order == null) return R.error("订单不存在");
        if (order.getStatus() != 1) return R.error("订单状态不正确");

        // TODO: 真实接入微信支付时替换以下逻辑
        // 1. 调用 WechatPayService.unifiedOrder(order)
        // 2. 返回 prepay_id + 签名参数

        PayResultVo result = new PayResultVo();
        result.setPrepayId("prepay_mock_" + orderId);
        result.setOrderNo(order.getOrderNo());
        result.setPayAmount(order.getPayAmount());
        result.setNonceStr("mock_nonce");
        result.setTimeStamp(String.valueOf(System.currentTimeMillis() / 1000));
        result.setSignType("MD5");
        result.setPaySign("mock_sign");
        return R.ok(result);
    }

    /**
     * 支付回调（模拟）
     * 真实接入时：此接口由微信异步调用，需验签 + 更新订单状态
     * 注意：此接口需在 LoginIntercept 中排除拦截
     */
    @PostMapping("/notify")
    @ResponseBody
    public String notify(@RequestBody String xmlBody) {
        // TODO: 真实接入时
        // 1. 验签 xmlBody
        // 2. 解析订单号
        // 3. 更新 order.payStatus=1, order.status=2, order.payTime
        log.info("支付回调通知(Mock): {}", xmlBody);
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    }

    /**
     * 模拟支付成功（开发测试用）
     */
    @PostMapping("/mockPaySuccess")
    @ResponseBody
    public R mockPaySuccess(@RequestBody PayOrderIdDto dto) {
        Long orderId = dto.getOrderId();
        BizOrder order = bizOrderService.getById(orderId);
        if (order == null) return R.error("订单不存在");

        order.setPayStatus((byte) 1);
        order.setStatus((byte) 2);
        order.setPayTime(LocalDateTime.now());
        order.setPayType((byte) 1);
        order.setUpdateTime(LocalDateTime.now());
        bizOrderService.updateById(order);

        return R.ok();
    }

}

