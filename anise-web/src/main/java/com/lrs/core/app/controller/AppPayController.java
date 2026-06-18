package com.lrs.core.app.controller;

import com.lrs.common.vo.R;
import com.lrs.core.app.dto.pay.PayOrderIdDto;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.service.IBizOrderService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 小程序支付 Controller。
 */
@Slf4j
@RestController
@RequestMapping("/app/pay")
@Validated
public class AppPayController extends BaseController {

    @Resource
    private IBizOrderService bizOrderService;

    /**
     * 统一下单（模拟）。
     */
    @PostMapping("/unifiedOrder")
    @ResponseBody
    public R unifiedOrder(@RequestBody PayOrderIdDto dto) {
        return R.ok(bizOrderService.createMockPayOrder(dto.getOrderId()));
    }

    /**
     * 支付回调（模拟）。
     */
    @PostMapping("/notify")
    @ResponseBody
    public String notify(@RequestBody String xmlBody) {
        log.info("支付回调通知(Mock): {}", xmlBody);
        return "<xml><return_code><![CDATA[SUCCESS]]></return_code></xml>";
    }

    /**
     * 模拟支付成功。
     */
    @PostMapping("/mockPaySuccess")
    @ResponseBody
    public R mockPaySuccess(@RequestBody PayOrderIdDto dto) {
        bizOrderService.mockPaySuccess(dto.getOrderId());
        return R.ok();
    }
}
