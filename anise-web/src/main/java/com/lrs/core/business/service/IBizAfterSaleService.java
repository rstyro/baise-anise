package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.dto.aftersale.AfterSaleQueryDto;
import com.lrs.core.app.vo.AfterSaleListVo;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;

public interface IBizAfterSaleService extends IService<BizAfterSale> {

    Page<BizAfterSale> getPage(Page page, BaseDto dto);
    /**
     * 小程序用户提交售后申请。
     */
    BizAfterSale applyAppAfterSale(Long userId, BizAfterSale afterSale);

    /**
     * 查询小程序用户售后列表，并带出列表展示商品信息。
     */
    List<AfterSaleListVo> listAppAfterSales(Long userId, AfterSaleQueryDto dto);

    /**
     * 查询小程序用户售后详情。
     */
    BizAfterSale getAppAfterSaleDetail(Long userId, Long id);

    boolean add(BizAfterSale item);
    boolean edit(BizAfterSale item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
