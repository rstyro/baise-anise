package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class CartItemVo {

    private Long id;

    private Long merchantId;

    private String merchantName;

    private Long productId;

    private Long skuId;

    private String productName;

    private String mainImage;

    private String specName;

    private String skuSpecs;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private Integer quantity;

    private BigDecimal stock;

    private Integer selected;

}
