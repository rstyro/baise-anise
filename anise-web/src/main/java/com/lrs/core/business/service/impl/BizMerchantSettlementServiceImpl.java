package com.lrs.core.business.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.OrderNumberGenerator;
import com.lrs.core.business.entity.BizMerchantSettlement;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.mapper.BizMerchantSettlementMapper;
import com.lrs.core.business.service.IBizMerchantSettlementService;
import com.lrs.core.business.service.IBizOrderSubService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商家结算表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Service
public class BizMerchantSettlementServiceImpl extends ServiceImpl<BizMerchantSettlementMapper, BizMerchantSettlement>
        implements IBizMerchantSettlementService {

    private final IBizOrderSubService bizOrderSubService;

    public BizMerchantSettlementServiceImpl(IBizOrderSubService bizOrderSubService) {
        this.bizOrderSubService = bizOrderSubService;
    }

    @Override
    public Page<BizMerchantSettlement> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizMerchantSettlement> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizMerchantSettlement::getSettleNo, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizMerchantSettlement::getId);
        return page(page, queryWrapper);
    }

    @Override
    public List<BizMerchantSettlement> getByMerchantId(Long merchantId) {
        LambdaQueryWrapper<BizMerchantSettlement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizMerchantSettlement::getMerchantId, merchantId)
                .orderByDesc(BizMerchantSettlement::getId);
        return list(queryWrapper);
    }

    @Override
    public BizMerchantSettlement getBySettleNo(String settleNo) {
        LambdaQueryWrapper<BizMerchantSettlement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizMerchantSettlement::getSettleNo, settleNo);
        return getOne(queryWrapper);
    }

    @Override
    public Map<String, Object> listMerchantSettlements(Long merchantId, int pageNo, int pageSize) {
        LambdaQueryWrapper<BizMerchantSettlement> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizMerchantSettlement::getMerchantId, merchantId)
                .orderByDesc(BizMerchantSettlement::getId);
        Page<BizMerchantSettlement> result = page(new Page<>(pageNo, pageSize), queryWrapper);

        Map<String, Object> pageResult = new LinkedHashMap<>();
        pageResult.put("records", result.getRecords());
        pageResult.put("total", result.getTotal());
        pageResult.put("current", result.getCurrent());
        pageResult.put("pages", result.getPages());
        return pageResult;
    }

    @Override
    public Map<String, Object> getMerchantSettlementDetail(Long merchantId, Long settleId) {
        BizMerchantSettlement settlement = getMerchantSettlement(merchantId, settleId);
        LambdaQueryWrapper<BizOrderSub> subQuery = new LambdaQueryWrapper<>();
        subQuery.eq(BizOrderSub::getMerchantId, merchantId)
                .like(BizOrderSub::getSettlePeriod, settlement.getSettleNo());
        List<BizOrderSub> subs = bizOrderSubService.list(subQuery);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("settlement", settlement);
        result.put("orderSubs", subs);
        return result;
    }

    @Override
    public void confirmMerchantSettlement(Long merchantId, Long settleId) {
        BizMerchantSettlement settlement = getMerchantSettlement(merchantId, settleId);
        if (settlement.getStatus() != BizMerchantSettlement.STATUS_CONFIRMED) {
            throw new ServiceException("当前状态不可确认收款");
        }

        settlement.setStatus(BizMerchantSettlement.STATUS_ARRIVED);
        settlement.setArriveTime(LocalDateTime.now());
        updateById(settlement);
    }

    private BizMerchantSettlement getMerchantSettlement(Long merchantId, Long settleId) {
        if (settleId == null) {
            throw new ServiceException("结算记录ID不能为空");
        }
        BizMerchantSettlement settlement = getById(settleId);
        if (settlement == null || !merchantId.equals(settlement.getMerchantId())) {
            throw new ServiceException("结算记录不存在");
        }
        return settlement;
    }

    @Override
    public boolean add(BizMerchantSettlement item) {
        return save(item);
    }

    @Override
    public boolean edit(BizMerchantSettlement item) {
        return updateById(item);
    }

    @Override
    public boolean del(Long id) {
        return removeById(id);
    }

    @Override
    public boolean batchDel(List<Long> ids) {
        return removeBatchByIds(ids);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizMerchantSettlement generateSettlement(Long merchantId) {
        // 查询该商家所有未结算的子订单
        LambdaQueryWrapper<BizOrderSub> subQuery = new LambdaQueryWrapper<>();
        subQuery.eq(BizOrderSub::getMerchantId, merchantId)
                .eq(BizOrderSub::getSettleStatus, BizOrderSub.SETTLE_STATUS_UNSETTLED)
                .eq(BizOrderSub::getDeliveryStatus, BizOrderSub.DELIVERY_STATUS_RECEIVED); // 已收货的才能结算
        List<BizOrderSub> unsettledSubs = bizOrderSubService.list(subQuery);

        if (unsettledSubs.isEmpty()) {
            return null;
        }

        // 计算统计金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalCommission = BigDecimal.ZERO;
        BigDecimal totalRefund = BigDecimal.ZERO;
        BigDecimal settleAmount = BigDecimal.ZERO;

        for (BizOrderSub sub : unsettledSubs) {
            totalAmount = totalAmount.add(sub.getPayAmount());
            totalCommission = totalCommission.add(sub.getCommissionAmount());
            settleAmount = settleAmount.add(sub.getSettleAmount());
        }

        // 获取商家结算周期
        BizOrderSub firstSub = unsettledSubs.get(0);
        byte periodType = firstSub.getMerchantId() != null ? BizMerchantSettlement.PERIOD_TYPE_T1 : BizMerchantSettlement.PERIOD_TYPE_T1;

        // 生成结算单
        BizMerchantSettlement settlement = new BizMerchantSettlement();
        settlement.setSettleNo(OrderNumberGenerator.nextId());
        settlement.setMerchantId(merchantId);
        settlement.setPeriodType(periodType);
        settlement.setPeriodStart(LocalDate.now().minusDays(periodType)); // 往前推算周期
        settlement.setPeriodEnd(LocalDate.now());
        settlement.setTotalOrders(unsettledSubs.size());
        settlement.setTotalAmount(totalAmount);
        settlement.setTotalCommission(totalCommission);
        settlement.setTotalRefund(totalRefund);
        settlement.setSettleAmount(settleAmount);
        settlement.setStatus(BizMerchantSettlement.STATUS_PENDING);

        save(settlement);

        // 更新子订单结算状态
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (BizOrderSub sub : unsettledSubs) {
            sub.setSettleStatus(BizOrderSub.SETTLE_STATUS_PROCESSING);
            sub.setSettlePeriod(settlement.getPeriodStart().format(formatter) + " ~ " + settlement.getPeriodEnd().format(formatter));
            bizOrderSubService.updateById(sub);
        }

        return settlement;
    }
}
