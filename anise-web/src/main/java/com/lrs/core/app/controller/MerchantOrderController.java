package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.order.MerchantOrderCancelDto;
import com.lrs.core.app.dto.order.MerchantOrderDeliveryDto;
import com.lrs.core.app.dto.order.MerchantSubIdDto;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.service.IBizOrderSubService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家后台订单管理 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/merchant/order")
@Validated
public class MerchantOrderController extends BaseController {

    @Resource
    private IBizOrderSubService bizOrderSubService;

    private Long getMerchantId() {
        Long merchantId = MerchantContextHolder.getMerchantId();
        if (merchantId == null) {
            throw new ServiceException("请以商家身份登录");
        }
        return merchantId;
    }

    /**
     * 商家订单列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) OrderQueryDto dto) {
        return R.ok(bizOrderSubService.listMerchantOrders(
                getMerchantId(),
                dto,
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()));
    }

    /**
     * 商家子订单详情。
     */
    @PostMapping("/subDetail")
    @ResponseBody
    public R subDetail(@RequestBody MerchantSubIdDto dto) {
        return R.ok(bizOrderSubService.getMerchantSubDetail(getMerchantId(), dto.getSubId()));
    }

    /**
     * 商家发货。
     */
    @OperateLog(title = "商家后台-发货")
    @PostMapping("/delivery")
    @ResponseBody
    public R delivery(@RequestBody MerchantOrderDeliveryDto dto) {
        bizOrderSubService.deliverMerchantSubOrder(getMerchantId(), dto);
        return R.ok();
    }

    /**
     * 商家取消子订单。
     */
    @OperateLog(title = "商家后台-取消子订单")
    @PostMapping("/cancelSub")
    @ResponseBody
    public R cancelSub(@RequestBody MerchantOrderCancelDto dto) {
        bizOrderSubService.cancelMerchantSubOrder(getMerchantId(), dto);
        return R.ok();
    }
}
