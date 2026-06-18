package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.annotation.RepeatSubmit;
import com.lrs.common.enums.LockType;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.order.ConfirmReceiveDto;
import com.lrs.core.app.dto.order.OrderIdDto;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.app.dto.order.OrderSubmitDto;
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
 * 小程序订单 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/app/order")
@Validated
public class AppOrderController extends BaseController {

    @Resource
    private IBizOrderService bizOrderService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) {
            throw new ServiceException("请先登录");
        }
        return user.getUserId();
    }

    /**
     * 提交订单，支持多商家和立即购买。
     */
    @OperateLog(title = "小程序-提交订单")
    @RepeatSubmit(lockType = LockType.APP_USER_PARAM, lockTime = 5000, message = "订单提交中，请勿重复提交")
    @PostMapping("/submit")
    @ResponseBody
    public R submit(@RequestBody OrderSubmitDto dto) {
        return R.ok(bizOrderService.submitAppOrder(dto, getUserId()));
    }

    /**
     * 订单列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) OrderQueryDto dto) {
        return R.ok(bizOrderService.listAppOrders(
                getUserId(),
                dto,
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()));
    }

    /**
     * 订单详情。
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody OrderIdDto dto) {
        return R.ok(bizOrderService.getAppOrderDetail(getUserId(), dto.getOrderId()));
    }

    /**
     * 订单物流详情。
     */
    @PostMapping("/logistics")
    @ResponseBody
    public R logistics(@RequestBody OrderIdDto dto) {
        return R.ok(bizOrderService.getAppOrderLogistics(getUserId(), dto.getOrderId()));
    }

    /**
     * 取消订单。
     */
    @OperateLog(title = "小程序-取消订单")
    @PostMapping("/cancel")
    @ResponseBody
    public R cancel(@RequestBody OrderIdDto dto) {
        bizOrderService.cancelAppOrder(getUserId(), dto.getOrderId());
        return R.ok();
    }

    /**
     * 订单统计。
     */
    @PostMapping("/count")
    @ResponseBody
    public R count() {
        return R.ok(bizOrderService.countByStatus(getUserId()));
    }

    /**
     * 确认收货，支持按子订单确认。
     */
    @OperateLog(title = "小程序-确认收货")
    @PostMapping("/confirmReceive")
    @ResponseBody
    public R confirmReceive(@RequestBody ConfirmReceiveDto dto) {
        bizOrderService.confirmAppReceive(getUserId(), dto.getOrderId(), dto.getSubId());
        return R.ok();
    }
}
