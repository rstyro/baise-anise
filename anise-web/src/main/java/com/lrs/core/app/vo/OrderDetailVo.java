package com.lrs.core.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单详情 VO
 */
@Data
public class OrderDetailVo {

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
     * 支付流水号
     */
    private String payTransactionId;

    /**
     * 配送方式 1:快递 2:自提 3:送货上门
     */
    private Byte deliveryType;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 发货时间
     */
    private LocalDateTime shipTime;

    /**
     * 收货时间
     */
    private LocalDateTime receiveTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 取消原因
     */
    private String cancelReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 收货地址快照
     */
    private Map<String, Object> address;

    /**
     * 订单明细列表
     */
    private List<BizOrderItemVo> items;

    /**
     * 子订单列表（多商家订单）
     */
    private List<Map<String, Object>> subList;
}
