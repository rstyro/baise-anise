package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.business.mapper.BizOrderMapper;
import com.lrs.core.business.service.IBizOrderService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizOrderServiceImpl extends ServiceImpl<BizOrderMapper, BizOrder> implements IBizOrderService {


    @Override
    public Page<BizOrder> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizOrder> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizOrder::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizOrder::getId);
            return page(page, queryWrapper);
        }

        @Override
        public boolean add(BizOrder item) {
            return save(item);
        }

        @Override
        public boolean edit(BizOrder item) {
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
