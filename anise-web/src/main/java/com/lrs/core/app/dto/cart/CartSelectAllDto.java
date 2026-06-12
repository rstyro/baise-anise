package com.lrs.core.app.dto.cart;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 购物车全选/取消全选参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class CartSelectAllDto {

    /** 选中状态：1=全选，0=取消全选 */
    private Integer selected;

}
