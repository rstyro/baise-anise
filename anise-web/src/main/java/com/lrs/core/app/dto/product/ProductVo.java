package com.lrs.core.app.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private String categoryName;

    private List<SpuAttrVo> spuAttrs;

    @Data
    @Accessors(chain = true)
    public static class SpuAttrVo {
        private Long attrId;
        private String attrName;
        private Long attrValueId;
        private String attrValue;
    }

}
