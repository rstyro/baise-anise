package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizBanner;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  Banner轮播图表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年6月11日
 */
public interface IBizBannerService extends IService<BizBanner> {

    Page<BizBanner> getPage(Page page, BaseDto dto);
    /**
     * 查询小程序端启用的 Banner 列表。
     */
    List<BizBanner> listAppBanners();
    boolean add(BizBanner item);
    boolean edit(BizBanner item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
