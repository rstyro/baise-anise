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
     * 商品ID
     */
    private Long productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 规格名称
     */
    private String specName;

    /**
     * 主图
     */
    private String mainImage;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer quantity;

    /**
     * 小计金额
     */
    private BigDecimal totalAmount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}