package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizOrderSub;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 商家订单子表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-13
 */
public interface IBizOrderSubService extends IService<BizOrderSub> {

    /**
     * 分页查询
     */
    Page<BizOrderSub> getPage(Page page, BaseDto dto);

    /**
     * 根据订单ID查询子订单
     */
    List<BizOrderSub> getByOrderId(Long orderId);

    /**
     * 根据商家ID查询子订单
     */
    List<BizOrderSub> getByMerchantId(Long merchantId);

    /**
     * 统计多个订单的子订单数量
     */
    Map<Long, Long> countByOrderIds(List<Long> orderIds);

    /**
     * 新增
     */
    boolean add(BizOrderSub item);

    /**
     * 编辑
     */
    boolean edit(BizOrderSub item);

    /**
     * 删除
     */
    boolean del(Long id);

    /**
     * 批量删除
     */
    boolean batchDel(List<Long> ids);
}
