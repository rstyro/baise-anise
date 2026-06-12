package com.lrs.core.app.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序 - 商品列表项 VO
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class ProductVo {

    /** 商品ID */
    private Long id;

    /** 商品名称 */
    private String productName;

    /** 商品卖点 */
    private String productTitle;

    /** 商品主图 */
    private String mainImage;

    /** 最低SKU价格 */
    private BigDecimal minPrice;

    /** 最高SKU价格 */
    private BigDecimal maxPrice;

    /** 划线原价（取SKU最低原价） */
    private BigDecimal originalPrice;

    /** 总销量 */
    private Integer sales;

    /** 产地 */
    private String originPlace;

    /** 是否无硫 */
    private Boolean isSulfurFree;

    /** 干度 */
    private String dryingLevel;

    /** 分类名称 */
    private String categoryName;

}

