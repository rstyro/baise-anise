package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 删除购物车项参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class CartDeleteDto {

    /** 购物车项ID */
    private Long cartId;

}
