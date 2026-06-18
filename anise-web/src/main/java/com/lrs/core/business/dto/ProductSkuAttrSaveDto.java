package com.lrs.core.business.dto;

import lombok.Data;

import java.util.List;

/**
 * 商品 SKU 属性保存参数
 */
@Data
public class ProductSkuAttrSaveDto {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品 ID；首次保存 SKU 属性时必传
     */
    private Long productId;

    /**
     * 属性列表
     */
    private List<AttrItem> attrs;

    @Data
    public static class AttrItem {

        /**
         * 属性 ID
         */
        private Long attrId;

        /**
         * 属性值 ID
         */
        private Long attrValueId;
    }
}
