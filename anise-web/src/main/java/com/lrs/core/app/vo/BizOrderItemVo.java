package com.lrs.core.app.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单明细项 VO
 */
@Data
public class BizOrderItemVo {

    /**
     * 明细ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 子订单ID
     */
    private Long subId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品主图
     */
    private String productImage;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 规格名称
     */
    private String skuName;

    /**
     * 规格明细
     */
    private String skuSpecs;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 商品小计
     */
    private BigDecimal itemAmount;

    /**
     * 优惠分摊
     */
    private BigDecimal discountAmount;

    /**
     * 实付金额
     */
    private BigDecimal payAmount;

    /**
     * 退款状态 0:无退款 1:部分退款 2:全部退款
     */
    private Byte refundStatus;

    /**
     * 已退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
