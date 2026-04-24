package com.lrs.core.base.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.base.dto.RegionDto;
import com.lrs.core.base.entity.BaseRegion;
import com.lrs.core.base.mapper.BaseRegionMapper;
import com.lrs.core.base.service.IBaseRegionService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 国内省市区数据表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-24
 */
@Service
public class BaseRegionServiceImpl extends ServiceImpl<BaseRegionMapper, BaseRegion> implements IBaseRegionService {


    @Override
    public Page<BaseRegion> getPage(Page page, RegionDto dto) {
        LambdaQueryWrapper<BaseRegion> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            queryWrapper.and(q->q.like(BaseRegion::getName, dto.getKeyword())
                    .or().like(BaseRegion::getCode,dto.getKeyword()));
        }
        if (!ObjectUtils.isEmpty(dto.getLevel())) {
            queryWrapper.eq(BaseRegion::getLevel, dto.getLevel());
        }
        if (!ObjectUtils.isEmpty(dto.getCode())) {
            queryWrapper.eq(BaseRegion::getCode, dto.getCode());
        }
        if (!ObjectUtils.isEmpty(dto.getParentCode())) {
            queryWrapper.eq(BaseRegion::getParentCode, dto.getParentCode());
        }
        queryWrapper.orderByAsc(BaseRegion::getId);
        return page(page, queryWrapper);
    }

    @Override
    public boolean add(BaseRegion item) {
        return save(item);
    }

    @Override
    public boolean edit(BaseRegion item) {
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
