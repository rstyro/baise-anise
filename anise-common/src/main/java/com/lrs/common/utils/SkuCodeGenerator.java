package com.lrs.common.utils;

import org.springframework.util.StringUtils;

/**
 * SKU编码生成器
 * 
 * 编码规则：BS-{商品ID}-{单位编码}{序号}
 * 示例：BS-0001-BX01（百色商家-商品1-箱装规格1）
 */
public class SkuCodeGenerator {
    
    private static final String SEPARATOR = "-";
    private static final String DEFAULT_MERCHANT_PREFIX = "BS";
    
    /**
     * 生成SKU编码（商家前缀固定为BS）
     * 
     * @param productId 商品ID
     * @param saleUnit 销售单位（如：箱、袋、斤）
     * @param specIndex 规格序号（从1开始）
     * @return SKU编码
     */
    public static String generate(Long productId, String saleUnit, int specIndex) {
        return generate(DEFAULT_MERCHANT_PREFIX,productId,saleUnit,specIndex);
    }
    
    /**
     * 生成SKU编码
     */
    public static String generate(String merchantPrefix, Long productId, String saleUnit, int specIndex) {
        // 商品ID：固定4-6位数字，不足补零
        String productCode = buildProductCode(productId);

        // 规格属性码：销售单位编码 + 序号
        String unitCode = getUnitCode(saleUnit);
        String specCode = unitCode + String.format("%02d", specIndex);

        return merchantPrefix + SEPARATOR + productCode + SEPARATOR + specCode;
    }
    
    /**
     * 生成SKU编码（兼容旧版本，商家前缀参数已忽略）
     */
    @Deprecated
    public static String generate(Long merchantId, Long productId, String saleUnit, int specIndex) {
        return generate(productId, saleUnit, specIndex);
    }
    
    /**
     * 构建商品编码
     */
    private static String buildProductCode(Long productId) {
        if (productId == null || productId <= 0) {
            return "0000";
        }
        if (productId < 1000) return String.format("%04d", productId);
        if (productId < 100000) return String.format("%05d", productId);
        return String.format("%06d", productId);
    }
    
    /**
     * 获取销售单位编码
     */
    private static String getUnitCode(String saleUnit) {
        if (!StringUtils.hasText(saleUnit)) return "OT"; // Other
        
        String unit = saleUnit.trim().toLowerCase();
        if (unit.contains("箱") || unit.contains("box")) return "BX";
        if (unit.contains("袋") || unit.contains("bag") || unit.contains("包") || unit.contains("pack")) return "DA";
        if (unit.contains("斤") || unit.contains("jin")) return "JD";
        if (unit.contains("个") || unit.contains("piece") || unit.contains("pcs")) return "GE";
        if (unit.contains("份") || unit.contains("portion") || unit.contains("share")) return "FN";
        if (unit.contains("公斤") || unit.equals("kg") || unit.contains("kilogram")) return "KG";
        if (unit.contains("瓶") || unit.contains("bottle")) return "PN";
        if (unit.contains("盒") || unit.contains("case")) return "BX";
        if (unit.contains("桶") || unit.contains("bucket")) return "TN";
        if (unit.contains("罐") || unit.contains("can") || unit.contains("tin")) return "GN";
        if (unit.contains("支") || unit.contains("stick") || unit.contains("piece")) return "GE";
        if (unit.contains("条") || unit.contains("strip")) return "TI";
        if (unit.contains("双") || unit.contains("pair")) return "PR";
        
        return "OT"; // Other
    }
    
    /**
     * 获取单位编码映射（用于显示）
     */
    public static String getUnitCodeDisplay(String unitCode) {
        if (!StringUtils.hasText(unitCode)) return "其他";
        
        return switch (unitCode.toUpperCase()) {
            case "BX" -> "箱";
            case "DA" -> "袋/包";
            case "JD" -> "斤";
            case "GE" -> "个/支";
            case "FN" -> "份";
            case "KG" -> "公斤";
            case "PN" -> "瓶";
            case "TN" -> "桶";
            case "GN" -> "罐";
            case "TI" -> "条";
            case "PR" -> "双";
            default -> "其他";
        };
    }
    
    /**
     * 判断SKU编码是否符合规则
     */
    public static boolean isValid(String skuCode) {
        if (!StringUtils.hasText(skuCode)) return false;
        // 格式：前缀-商品ID-单位编码序号
        String pattern = "^[A-Z0-9]{2,3}-\\d{4,6}-[A-Z]{2}\\d{2}$";
        return skuCode.matches(pattern);
    }
    
    /**
     * 生成唯一SKU编码（使用UUID，用于无法获取商家/商品信息的场景）
     */
    public static String generateUnique() {
        return "SKU-" + System.currentTimeMillis() + "-" + 
               String.format("%04d", (int) (Math.random() * 10000));
    }
    
    public static void main(String[] args) {
        System.out.println("=== SKU编码生成示例 ===");
        System.out.println(generateUnique());
        System.out.println(SkuCodeGenerator.generate("BS", 1L, "箱", 1));   // BS-0001-BX01
        System.out.println(SkuCodeGenerator.generate("BS",  1L, "袋", 2));   // BS-0001-DA02
        System.out.println(SkuCodeGenerator.generate("LN",  105L, "斤", 1)); // LN-00105-JD01
        System.out.println(SkuCodeGenerator.generate( 9999L, "公斤", 1));    // M003-09999-KG01
        System.out.println(SkuCodeGenerator.generate(123456L, "瓶", 3)); // M005-123456-PN03
    }
}
