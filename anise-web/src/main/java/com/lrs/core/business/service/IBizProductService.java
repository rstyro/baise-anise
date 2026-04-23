package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  商品表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizProductService extends IService<BizProduct> {

    Page<BizProduct> getPage(Page page, BaseDto dto);
    boolean add(BizProduct item);
    boolean edit(BizProduct item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
