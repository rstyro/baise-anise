package com.lrs.core.app.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 订单商品项 VO
 */
@Data
public class OrderGoodsVo {

    /**
     * 订单项ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 商品图片
     */
    private String goodsUrl;

    /**
     * 商品名称
     */
    private String title;

    /**
     * 规格类型
     */
    private String type;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer number;
}