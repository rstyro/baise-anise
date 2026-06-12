package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 更新购物车数量参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class CartUpdateQuantityDto {

    /** 购物车项ID */
    private Long cartId;

    /** 新数量 */
    private Integer quantity;

}
