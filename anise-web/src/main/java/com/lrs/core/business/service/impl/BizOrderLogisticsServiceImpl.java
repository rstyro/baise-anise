package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizOrderLogistics;
import com.lrs.core.business.mapper.BizOrderLogisticsMapper;
import com.lrs.core.business.service.IBizOrderLogisticsService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 子订单物流信息 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-18
 */
@Service
public class BizOrderLogisticsServiceImpl extends ServiceImpl<BizOrderLogisticsMapper, BizOrderLogistics> implements IBizOrderLogisticsService {

    @Override
    public Page<BizOrderLogistics> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizOrderLogistics> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.like(BizOrderLogistics::getTrackingNo, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizOrderLogistics::getId);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BizOrderLogistics item) {
        return save(item);
    }

    @Override
    public boolean edit(BizOrderLogistics item) {
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
    public List<BizOrderLogistics> getBySubOrderIds(List<Long> subOrderIds) {
        if (subOrderIds == null || subOrderIds.isEmpty()) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<BizOrderLogistics> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BizOrderLogistics::getSubOrderId, subOrderIds)
                .orderByDesc(BizOrderLogistics::getId);
        return list(queryWrapper);
    }

    @Override
    public boolean markDeliveredBySubOrderIds(List<Long> subOrderIds, LocalDateTime deliveredTime) {
        if (subOrderIds == null || subOrderIds.isEmpty()) {
            return false;
        }
        LambdaUpdateWrapper<BizOrderLogistics> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(BizOrderLogistics::getSubOrderId, subOrderIds)
                .set(BizOrderLogistics::getStatus, BizOrderLogistics.STATUS_DELIVERED)
                .set(BizOrderLogistics::getDeliveredTime, deliveredTime)
                .set(BizOrderLogistics::getLastTrackDetail, "用户已确认收货")
                .set(BizOrderLogistics::getUpdateTime, deliveredTime);
        return update(updateWrapper);
    }
}
