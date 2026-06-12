package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 加入购物车参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class CartAddDto {

    /** SKU ID */
    private Long skuId;

    /** 数量，默认1 */
    private Integer quantity;

}
