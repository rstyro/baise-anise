package com.lrs.core.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.base.dto.RegionDto;
import com.lrs.core.base.entity.BaseRegion;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  国内省市区数据表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月24日
 */
public interface IBaseRegionService extends IService<BaseRegion> {

    Page<BaseRegion> getPage(Page page, RegionDto dto);
    boolean add(BaseRegion item);
    boolean edit(BaseRegion item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
