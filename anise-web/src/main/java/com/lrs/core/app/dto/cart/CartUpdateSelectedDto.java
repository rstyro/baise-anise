package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 更新购物车选中状态参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class CartUpdateSelectedDto {

    /** 购物车项ID */
    private Long cartId;

    /** 选中状态：1=选中，0=未选中 */
    private Integer selected;

}
