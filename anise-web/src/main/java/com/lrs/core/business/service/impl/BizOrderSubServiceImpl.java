package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.business.mapper.BizOrderSubMapper;
import com.lrs.core.business.service.IBizOrderSubService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 商家订单子表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Service
public class BizOrderSubServiceImpl extends ServiceImpl<BizOrderSubMapper, BizOrderSub> implements IBizOrderSubService {

    @Override
    public Page<BizOrderSub> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizOrderSub::getSubNo, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizOrderSub::getId);
        return page(page, queryWrapper);
    }

    @Override
    public List<BizOrderSub> getByOrderId(Long orderId) {
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizOrderSub::getOrderId, orderId);
        return list(queryWrapper);
    }

    @Override
    public List<BizOrderSub> getByMerchantId(Long merchantId) {
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizOrderSub::getMerchantId, merchantId);
        return list(queryWrapper);
    }

    @Override
    public Map<Long, Long> countByOrderIds(List<Long> orderIds) {
        if (orderIds == null || orderIds.isEmpty()) {
            return Collections.emptyMap();
        }
        LambdaQueryWrapper<BizOrderSub> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BizOrderSub::getOrderId, orderIds);
        return list(queryWrapper).stream()
                .collect(Collectors.groupingBy(BizOrderSub::getOrderId, LinkedHashMap::new, Collectors.counting()));
    }

    @Override
    public boolean add(BizOrderSub item) {
        return save(item);
    }

    @Override
    public boolean edit(BizOrderSub item) {
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
}
