package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  收货地址表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizAddressService extends IService<BizAddress> {

    Page<BizAddress> getPage(Page page, BaseDto dto);
    boolean add(BizAddress item);
    boolean edit(BizAddress item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
