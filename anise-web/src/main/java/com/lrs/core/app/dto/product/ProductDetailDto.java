package com.lrs.core.app.dto.product;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 商品详情查询参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class ProductDetailDto {

    /** 商品ID */
    private Long productId;

}
