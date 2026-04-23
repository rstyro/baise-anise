package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  商品规格库存表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizProductSkuService extends IService<BizProductSku> {

    Page<BizProductSku> getPage(Page page, BaseDto dto);
    boolean add(BizProductSku item);
    boolean edit(BizProductSku item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
