package com.lrs.core.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.base.entity.BaseVillage;
import com.lrs.core.base.mapper.BaseVillageMapper;
import com.lrs.core.base.service.IBaseVillageService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 村级（村委会、居委会）-行政编码 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-24
 */
@Service
public class BaseVillageServiceImpl extends ServiceImpl<BaseVillageMapper, BaseVillage> implements IBaseVillageService {


    @Override
    public Page<BaseVillage> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BaseVillage> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.like(BaseVillage::getName, dto.getKeyword());
        }
        queryWrapper.orderByAsc(BaseVillage::getCode);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BaseVillage item) {
        return save(item);
    }

    @Override
    public boolean edit(BaseVillage item) {
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
