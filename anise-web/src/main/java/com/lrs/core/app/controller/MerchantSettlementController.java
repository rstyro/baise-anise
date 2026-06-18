package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.merchant.SettlementIdDto;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizMerchantSettlement;
import com.lrs.core.business.service.IBizMerchantSettlementService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家后台结算管理 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/merchant/settlement")
@Validated
public class MerchantSettlementController extends BaseController {

    @Resource
    private IBizMerchantSettlementService bizMerchantSettlementService;

    private Long getMerchantId() {
        Long merchantId = MerchantContextHolder.getMerchantId();
        if (merchantId == null) {
            throw new ServiceException("请以商家身份登录");
        }
        return merchantId;
    }

    /**
     * 结算记录列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list() {
        return R.ok(bizMerchantSettlementService.listMerchantSettlements(
                getMerchantId(),
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()));
    }

    /**
     * 结算详情。
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody SettlementIdDto dto) {
        return R.ok(bizMerchantSettlementService.getMerchantSettlementDetail(getMerchantId(), dto.getSettleId()));
    }

    /**
     * 商家申请结算。
     */
    @OperateLog(title = "商家申请结算")
    @PostMapping("/apply")
    @ResponseBody
    public R apply() {
        BizMerchantSettlement settlement = bizMerchantSettlementService.generateSettlement(getMerchantId());
        if (settlement == null) {
            return R.error("暂无可结算的订单");
        }
        return R.ok(settlement);
    }

    /**
     * 商家确认收款。
     */
    @OperateLog(title = "商家确认收款")
    @PostMapping("/confirm")
    @ResponseBody
    public R confirm(@RequestBody SettlementIdDto dto) {
        bizMerchantSettlementService.confirmMerchantSettlement(getMerchantId(), dto.getSettleId());
        return R.ok();
    }
}
