package com.lrs.core.app.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序 - 商品详情 VO（含 SKU 列表 + 商家信息）
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class ProductDetailVo {

    // ===== 商品基本信息 =====

    /** 商品ID */
    private Long id;

    /** 商品名称 */
    private String productName;

    /** 商品卖点 */
    private String productTitle;

    /** 商品主图 */
    private String mainImage;

    /** 轮播图列表 */
    private List<String> imageList;

    /** 商品详情（富文本） */
    private String description;

    /** 产地 */
    private String originPlace;

    /** 是否无硫 */
    private Boolean isSulfurFree;

    /** 干度 */
    private String dryingLevel;

    /** 种植工艺 */
    private String plantingProcess;

    /** 分类ID */
    private Long categoryId;

    /** 分类名称 */
    private String categoryName;

    /** 总销量 */
    private Integer sales;

    // ===== SKU 列表 =====

    /** SKU规格列表 */
    private List<SkuVo> skuList;

    // ===== 商家信息 =====

    /** 商家ID */
    private Long merchantId;

    /** 商家名称 */
    private String merchantName;

    /** 产地地址 */
    private String merchantOriginPlace;

    // ===== SKU 子 VO =====

    @Data
    @Accessors(chain = true)
    public static class SkuVo {
        /** SKU ID */
        private Long id;

        /** 规格名称（如 "500g/袋"） */
        private String specName;

        /** 规格属性 JSON */
        private String specValues;

        /** 销售价 */
        private BigDecimal price;

        /** 划线原价 */
        private BigDecimal originalPrice;

        /** 当前库存 */
        private Integer stock;

        /** 销量 */
        private Integer sales;
    }

}

