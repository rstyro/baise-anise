package com.lrs.core.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.base.dto.VillageDto;
import com.lrs.core.base.entity.BaseVillage;
import com.lrs.core.base.mapper.BaseVillageMapper;
import com.lrs.core.base.service.IBaseVillageService;
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
    public Page<BaseVillage> getPage(Page page, VillageDto dto) {
        LambdaQueryWrapper<BaseVillage> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.and(q->q.like(BaseVillage::getName, dto.getKeyword())
                    .or().like(BaseVillage::getCode,dto.getKeyword()));
        }

        if (!ObjectUtils.isEmpty(dto.getProvinceCode())) {
            queryWrapper.eq(BaseVillage::getProvinceCode, dto.getProvinceCode());
        }
        if (!ObjectUtils.isEmpty(dto.getCityCode())) {
            queryWrapper.eq(BaseVillage::getCityCode, dto.getCityCode());
        }
        if (!ObjectUtils.isEmpty(dto.getAreaCode())) {
            queryWrapper.eq(BaseVillage::getAreaCode, dto.getAreaCode());
        }
        if (!ObjectUtils.isEmpty(dto.getStreetCode())) {
            queryWrapper.eq(BaseVillage::getStreetCode, dto.getStreetCode());
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
