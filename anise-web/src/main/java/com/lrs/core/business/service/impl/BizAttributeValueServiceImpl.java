package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizAttributeValue;
import com.lrs.core.business.mapper.BizAttributeValueMapper;
import com.lrs.core.business.service.IBizAttributeValueService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 属性值表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-15
 */
@Service
public class BizAttributeValueServiceImpl extends ServiceImpl<BizAttributeValueMapper, BizAttributeValue> implements IBizAttributeValueService {


    @Override
    public Page<BizAttributeValue> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizAttributeValue> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(BizAttributeValue::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(BizAttributeValue::getId);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BizAttributeValue item) {
        return save(item);
    }

    @Override
    public boolean edit(BizAttributeValue item) {
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
