package com.lrs.core.app.dto.product;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 商品列表查询参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class ProductListDto {

    /** 分类ID（可选，null=全部） */
    private Long categoryId;

    /** 搜索关键词（可选） */
    private String keyword;

    /** 商家ID（可选，null=全部商家） */
    private Long merchantId;

}
