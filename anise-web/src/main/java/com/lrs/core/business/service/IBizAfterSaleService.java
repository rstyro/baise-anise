package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;

public interface IBizAfterSaleService extends IService<BizAfterSale> {

    Page<BizAfterSale> getPage(Page page, BaseDto dto);
    boolean add(BizAfterSale item);
    boolean edit(BizAfterSale item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
