package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 小程序 - 购物车列表项 VO（含商品和SKU信息）
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class CartItemVo {

    /** 购物车记录ID */
    private Long id;

    /** 商家ID */
    private Long merchantId;

    /** 商品ID */
    private Long productId;

    /** SKU ID */
    private Long skuId;

    /** 商品名称 */
    private String productName;

    /** 商品主图 */
    private String mainImage;

    /** 规格名称 */
    private String specName;

    /** SKU销售价 */
    private BigDecimal price;

    /** SKU划线原价 */
    private BigDecimal originalPrice;

    /** 购买数量 */
    private Integer quantity;

    /** SKU当前库存 */
    private Integer stock;

    /** 是否选中 0=否 1=是 */
    private Integer selected;

}

