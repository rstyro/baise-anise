package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizAttribute;
import com.lrs.core.business.mapper.BizAttributeMapper;
import com.lrs.core.business.service.IBizAttributeService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 属性定义表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-15
 */
@Service
public class BizAttributeServiceImpl extends ServiceImpl<BizAttributeMapper, BizAttribute> implements IBizAttributeService {


    @Override
    public Page<BizAttribute> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizAttribute> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizAttribute::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizAttribute::getId);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BizAttribute item) {
        return save(item);
    }

    @Override
    public boolean edit(BizAttribute item) {
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
