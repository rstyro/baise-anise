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
     * 运费
     */
    private BigDecimal freightAmount;

    /**
     * 优惠金额
     */
    private BigDecimal discountAmount;

    /**
     * 订单状态 0:已取消 1:待支付 2:待发货 3:已发货 4:已收货 5:已完成 6:退款中 7:已退款
     */
    private Byte status;

    /**
     * 支付方式 1:微信支付 2:余额支付 3:线下转账
     */
    private Byte payType;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

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

    /**
     * 涉及商家数量（多商家订单）
     */
    private Integer subCount;
}
