package com.lrs.core.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单列表项 VO
 */
@Data
public class OrderListVo {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付状态
     */
    private Byte payStatus;

    /**
     * 发货状态
     */
    private Byte deliveryStatus;

    /**
     * 订单状态
     */
    private Byte status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 商品列表
     */
    private List<OrderGoodsVo> goodsList;
}