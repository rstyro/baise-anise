package com.lrs.core.app.utils;

/**
 * 商家上下文工具类
 * 
 * <p>用于在请求线程中存储当前商家ID，实现商家数据隔离。
 * 商家后台接口在请求进入时设置商家ID，Service层查询时自动带上该商家ID。
 * </p>
 * 
 * @author rstyro
 * @since 2026-06-12
 */
public class MerchantContextHolder {

    private static final ThreadLocal<Long> MERCHANT_ID = new ThreadLocal<>();
    
    /**
     * 设置当前商家ID
     */
    public static void setMerchantId(Long merchantId) {
        MERCHANT_ID.set(merchantId);
    }

    /**
     * 获取当前商家ID
     */
    public static Long getMerchantId() {
        return MERCHANT_ID.get();
    }

    /**
     * 清除当前商家ID
     */
    public static void clear() {
        MERCHANT_ID.remove();
    }

    /**
     * 判断是否处于商家上下文（即是否有商家ID）
     */
    public static boolean hasMerchant() {
        return MERCHANT_ID.get() != null;
    }
}