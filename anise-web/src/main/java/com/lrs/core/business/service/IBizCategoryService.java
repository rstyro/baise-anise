package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizCategory;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  商品分类表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizCategoryService extends IService<BizCategory> {

    Page<BizCategory> getPage(Page page, BaseDto dto);
    boolean add(BizCategory item);
    boolean edit(BizCategory item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
