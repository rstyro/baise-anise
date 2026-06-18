package com.lrs.core.business.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.common.exception.ServiceException;
import com.lrs.core.app.dto.aftersale.AfterSaleQueryDto;
import com.lrs.core.app.vo.AfterSaleListVo;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.entity.BizOrderItem;
import com.lrs.core.business.mapper.BizAfterSaleMapper;
import com.lrs.core.business.service.IBizAfterSaleService;
import com.lrs.core.business.service.IBizOrderItemService;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.system.dto.BaseDto;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BizAfterSaleServiceImpl extends ServiceImpl<BizAfterSaleMapper, BizAfterSale> implements IBizAfterSaleService {

    @Resource
    private IBizOrderService bizOrderService;

    @Resource
    private IBizOrderItemService bizOrderItemService;

    @Override
    public Page<BizAfterSale> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizAfterSale> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(BizAfterSale::getId);
        return page(page, qw);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizAfterSale applyAppAfterSale(Long userId, BizAfterSale afterSale) {
        if (afterSale == null || afterSale.getOrderId() == null) {
            throw new ServiceException("订单ID不能为空");
        }
        BizOrder order = bizOrderService.getById(afterSale.getOrderId());
        if (order == null || !userId.equals(order.getUserId())) {
            throw new ServiceException("订单不存在");
        }
        if (order.getStatus() < 2 || order.getStatus() == 5) {
            throw new ServiceException("当前订单状态不可申请售后");
        }

        LocalDateTime now = LocalDateTime.now();
        afterSale.setUserId(userId);
        afterSale.setAfterSaleNo("AS" + System.currentTimeMillis());
        afterSale.setStatus((byte) 0);
        afterSale.setCreateTime(now);
        afterSale.setUpdateTime(now);
        save(afterSale);

        order.setStatus((byte) 5);
        order.setUpdateTime(now);
        bizOrderService.updateById(order);
        return afterSale;
    }

    @Override
    public List<AfterSaleListVo> listAppAfterSales(Long userId, AfterSaleQueryDto dto) {
        LambdaQueryWrapper<BizAfterSale> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BizAfterSale::getUserId, userId)
                .orderByDesc(BizAfterSale::getId);
        if (dto != null && dto.getStatus() != null) {
            queryWrapper.eq(BizAfterSale::getStatus, dto.getStatus());
        }

        List<BizAfterSale> list = list(queryWrapper);
        if (list.isEmpty()) {
            return Collections.emptyList();
        }

        Set<Long> orderIds = list.stream().map(BizAfterSale::getOrderId).collect(Collectors.toSet());
        Map<Long, List<BizOrderItem>> itemMap = bizOrderItemService.list(
                new LambdaQueryWrapper<BizOrderItem>().in(BizOrderItem::getOrderId, orderIds)
        ).stream().collect(Collectors.groupingBy(BizOrderItem::getOrderId));

        return list.stream().map(afterSale -> {
            AfterSaleListVo vo = new AfterSaleListVo();
            BeanUtil.copyProperties(afterSale, vo);
            List<BizOrderItem> items = itemMap.get(afterSale.getOrderId());
            if (items != null && !items.isEmpty()) {
                BizOrderItem firstItem = items.get(0);
                vo.setGoodsName(firstItem.getProductName());
                vo.setProductName(firstItem.getProductName());
                vo.setProductImage(firstItem.getProductImage());
            }
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public BizAfterSale getAppAfterSaleDetail(Long userId, Long id) {
        if (id == null) {
            throw new ServiceException("售后单ID不能为空");
        }
        BizAfterSale afterSale = getById(id);
        if (afterSale == null || !userId.equals(afterSale.getUserId())) {
            throw new ServiceException("售后单不存在");
        }
        return afterSale;
    }

    @Override
    public boolean add(BizAfterSale item) { return save(item); }

    @Override
    public boolean edit(BizAfterSale item) { return updateById(item); }

    @Override
    public boolean del(Long id) { return removeById(id); }

    @Override
    public boolean batchDel(List<Long> ids) { return removeBatchByIds(ids); }
}
