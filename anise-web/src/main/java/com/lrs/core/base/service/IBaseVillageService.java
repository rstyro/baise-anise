package com.lrs.core.base.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.base.entity.BaseVillage;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  村级（村委会、居委会）-行政编码 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月24日
 */
public interface IBaseVillageService extends IService<BaseVillage> {

    Page<BaseVillage> getPage(Page page, BaseDto dto);
    boolean add(BaseVillage item);
    boolean edit(BaseVillage item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
