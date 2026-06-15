package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizAttributeValue;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  属性值表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年6月15日
 */
public interface IBizAttributeValueService extends IService<BizAttributeValue> {

    Page<BizAttributeValue> getPage(Page page, BaseDto dto);
    boolean add(BizAttributeValue item);
    boolean edit(BizAttributeValue item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
