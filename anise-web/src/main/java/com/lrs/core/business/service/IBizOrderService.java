package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.vo.OrderGoodsVo;
import com.lrs.core.business.entity.BizOrder;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;
import java.util.Map;


/**
 * <p>
 *  订单表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizOrderService extends IService<BizOrder> {

    Page<BizOrder> getPage(Page page, BaseDto dto);
    boolean add(BizOrder item);
    boolean edit(BizOrder item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
    Map<String, Object> countByStatus(Long userId);
    List<OrderGoodsVo> getGoodsList(List<Long> orderIds);
}
