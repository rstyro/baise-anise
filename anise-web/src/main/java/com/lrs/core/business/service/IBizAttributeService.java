package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizAttribute;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  属性定义表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年6月15日
 */
public interface IBizAttributeService extends IService<BizAttribute> {

    Page<BizAttribute> getPage(Page page, BaseDto dto);
    boolean add(BizAttribute item);
    boolean edit(BizAttribute item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
