package com.lrs.core.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizMerchantSettlement;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.service.IBizMerchantSettlementService;
import com.lrs.core.business.service.IBizOrderSubService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商家后台 - 结算管理 Controller
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Slf4j
@RestController
@RequestMapping("/merchant/settlement")
@Validated
public class MerchantSettlementController extends BaseController {

    @Resource
    private IBizMerchantSettlementService bizMerchantSettlementService;

    @Resource
    private IBizOrderSubService bizOrderSubService;

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
     * 结算记录列表
     */
    @PostMapping("/list")
    @ResponseBody
    public R list() {
        Long merchantId = getMerchantId();

        LambdaQueryWrapper<BizMerchantSettlement> query = new LambdaQueryWrapper<>();
        query.eq(BizMerchantSettlement::getMerchantId, merchantId)
                .orderByDesc(BizMerchantSettlement::getId);

        Page<BizMerchantSettlement> page = new Page<>(
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );
        Page<BizMerchantSettlement> result = bizMerchantSettlementService.page(page, query);

        Map<String, Object> pageResult = new LinkedHashMap<>();
        pageResult.put("records", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("current", result.getCurrent());
        pageResult.put("pages", result.getPages());
        return R.ok(pageResult);
    }

    /**
     * 结算详情
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody SettlementIdDto dto) {
        Long merchantId = getMerchantId();
        Long settleId = dto.getSettleId();

        BizMerchantSettlement settlement = bizMerchantSettlementService.getById(settleId);
        if (settlement == null || !settlement.getMerchantId().equals(merchantId)) {
            return R.error("结算记录不存在");
        }

        // 查询该结算单关联的子订单
        LambdaQueryWrapper<BizOrderSub> subQuery = new LambdaQueryWrapper<>();
        subQuery.eq(BizOrderSub::getMerchantId, merchantId)
                .like(BizOrderSub::getSettlePeriod, settlement.getSettleNo());
        List<BizOrderSub> subs = bizOrderSubService.list(subQuery);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("settlement", settlement);
        result.put("orderSubs", subs);
        return R.ok(result);
    }

    /**
     * 商家申请结算
     */
    @OperateLog(title = "商家申请结算")
    @PostMapping("/apply")
    @ResponseBody
    public R apply() {
        Long merchantId = getMerchantId();

        BizMerchantSettlement settlement = bizMerchantSettlementService.generateSettlement(merchantId);
        if (settlement == null) {
            return R.error("暂无可结算的订单");
        }

        return R.ok(settlement);
    }

    /**
     * 商家确认收款
     */
    @OperateLog(title = "商家确认收款")
    @PostMapping("/confirm")
    @ResponseBody
    public R confirm(@RequestBody SettlementIdDto dto) {
        Long merchantId = getMerchantId();
        Long settleId = dto.getSettleId();

        BizMerchantSettlement settlement = bizMerchantSettlementService.getById(settleId);
        if (settlement == null || !settlement.getMerchantId().equals(merchantId)) {
            return R.error("结算记录不存在");
        }
        if (settlement.getStatus() != BizMerchantSettlement.STATUS_CONFIRMED) {
            return R.error("当前状态不可确认收款");
        }

        settlement.setStatus(BizMerchantSettlement.STATUS_ARRIVED);
        settlement.setArriveTime(java.time.LocalDateTime.now());
        bizMerchantSettlementService.updateById(settlement);

        return R.ok();
    }

    // ==================== DTO ====================

    public static class SettlementIdDto {
        private Long settleId;

        public Long getSettleId() { return settleId; }
        public void setSettleId(Long settleId) { this.settleId = settleId; }
    }
}
