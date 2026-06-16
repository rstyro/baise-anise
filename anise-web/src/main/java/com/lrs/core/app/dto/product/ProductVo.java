package com.lrs.core.app.dto.product;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProductVo {

    private Long id;

    private String productName;

    private String productTitle;

    private String mainImage;

    private BigDecimal minPrice;

    private BigDecimal maxPrice;

    private BigDecimal originalPrice;

    private Integer sales;

    /**
     * 季节标签，如春季、秋季
     */
    private String seasonTag;
    private String categoryName;

    private List<SpuAttrVo> spuAttrs;

    @Data
    @Accessors(chain = true)
    public static class SpuAttrVo {
        private Long productId;
        private Long attrId;
        private String attrName;
        private Long attrValueId;
        private String attrValue;
    }

}
