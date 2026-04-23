package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizMerchant;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  商家/农户表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizMerchantService extends IService<BizMerchant> {

    Page<BizMerchant> getPage(Page page, BaseDto dto);
    boolean add(BizMerchant item);
    boolean edit(BizMerchant item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
