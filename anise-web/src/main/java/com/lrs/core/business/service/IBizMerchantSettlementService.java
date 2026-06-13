package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizMerchantSettlement;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;

/**
 * <p>
 * 商家结算表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
public interface IBizMerchantSettlementService extends IService<BizMerchantSettlement> {

    /**
     * 分页查询
     */
    Page<BizMerchantSettlement> getPage(Page page, BaseDto dto);

    /**
     * 根据商家ID查询结算记录
     */
    List<BizMerchantSettlement> getByMerchantId(Long merchantId);

    /**
     * 根据结算单号查询
     */
    BizMerchantSettlement getBySettleNo(String settleNo);

    /**
     * 新增
     */
    boolean add(BizMerchantSettlement item);

    /**
     * 编辑
     */
    boolean edit(BizMerchantSettlement item);

    /**
     * 删除
     */
    boolean del(Long id);

    /**
     * 批量删除
     */
    boolean batchDel(List<Long> ids);

    /**
     * 生成结算单
     */
    BizMerchantSettlement generateSettlement(Long merchantId);
}
