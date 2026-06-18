package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.dto.order.OrderQueryDto;
import com.lrs.core.app.dto.order.OrderSubmitDto;
import com.lrs.core.app.vo.OrderDetailVo;
import com.lrs.core.app.vo.OrderGoodsVo;
import com.lrs.core.app.vo.OrderLogisticsVo;
import com.lrs.core.app.vo.OrderSubmitResultVo;
import com.lrs.core.app.vo.PayResultVo;
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
    Object listAppOrders(Long userId, OrderQueryDto dto, int pageNo, int pageSize);
    OrderDetailVo getAppOrderDetail(Long userId, Long orderId);
    OrderLogisticsVo getAppOrderLogistics(Long userId, Long orderId);
    void cancelAppOrder(Long userId, Long orderId);
    void confirmAppReceive(Long userId, Long orderId, Long subId);
    PayResultVo createMockPayOrder(Long orderId);
    void mockPaySuccess(Long orderId);

    /**
     * 提交小程序订单，支持立即购买和购物车结算。
     */
    OrderSubmitResultVo submitAppOrder(OrderSubmitDto dto, Long userId);
}
