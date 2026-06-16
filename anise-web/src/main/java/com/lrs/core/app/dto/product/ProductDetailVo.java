package com.lrs.core.app.dto.product;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class ProductDetailVo {

    private Long id;

    private String productName;

    private String productTitle;

    private String mainImage;

    private String imageListStr;

    private List<String> imageList;

    private String description;

    private Long categoryId;

    private String categoryName;

    private Integer sales;

    private List<SkuVo> skuList;

    private Long merchantId;

    private String merchantName;

    private String merchantOriginPlace;

    private List<SpuAttrVo> spuAttrs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preSaleStart;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime preSaleEnd;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedShipDate;

    private String seasonTag;

    @Data
    @Accessors(chain = true)
    public static class SkuVo {
        private Long id;
        private String skuCode;
        private String saleUnit;
        private BigDecimal unitWeight;
        private Boolean isVariableWeight;
        private BigDecimal minQuantity;
        private BigDecimal maxQuantity;
        private BigDecimal quantityStep;
        private BigDecimal price;
        private BigDecimal originalPrice;
        private BigDecimal wholesalePrice;
        private BigDecimal stock;
        private Integer sales;
        private List<SkuAttrVo> skuAttrs;
    }

    @Data
    @Accessors(chain = true)
    public static class SpuAttrVo {
        private Long attrId;
        private String attrName;
        private Long attrValueId;
        private String attrValue;
    }

    @Data
    @Accessors(chain = true)
    public static class SkuAttrVo {
        private Long skuId;
        private Long attrId;
        private String attrName;
        private Long attrValueId;
        private String attrValue;
    }

}
