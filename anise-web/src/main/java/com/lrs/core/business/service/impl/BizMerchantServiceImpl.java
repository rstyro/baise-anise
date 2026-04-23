package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizMerchant;
import com.lrs.core.business.mapper.BizMerchantMapper;
import com.lrs.core.business.service.IBizMerchantService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 商家/农户表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class BizMerchantServiceImpl extends ServiceImpl<BizMerchantMapper, BizMerchant> implements IBizMerchantService {


    @Override
    public Page<BizMerchant> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizMerchant> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizMerchant::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizMerchant::getId);
            return page(page, queryWrapper);
        }

        @Override
        public boolean add(BizMerchant item) {
            return save(item);
        }

        @Override
        public boolean edit(BizMerchant item) {
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
