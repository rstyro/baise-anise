package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.BizOrderLogistics;
import com.lrs.core.system.dto.BaseDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 子订单物流信息 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-18
 */
public interface IBizOrderLogisticsService extends IService<BizOrderLogistics> {

    Page<BizOrderLogistics> getPage(Page page, BaseDto dto);
    boolean add(BizOrderLogistics item);
    boolean edit(BizOrderLogistics item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);

    /**
     * 批量查询子订单物流包裹。
     */
    List<BizOrderLogistics> getBySubOrderIds(List<Long> subOrderIds);

    /**
     * 将子订单下的物流包裹标记为已签收。
     */
    boolean markDeliveredBySubOrderIds(List<Long> subOrderIds, LocalDateTime deliveredTime);
}
