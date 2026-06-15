package com.lrs.core.app.vo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * 订单商品项 VO
 */
@Data
public class OrderGoodsVo {

    /**
     * 订单项ID
     */
    private Long id;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 商家名称
     */
    private String merchantName;

    /**
     * 商品图片
     */
    private String goodsUrl;

    /**
     * 商品名称
     */
    private String title;

    /**
     * 规格类型（存储原始JSON或解析后的值）
     */
    private String type;

    /**
     * 单价
     */
    private BigDecimal price;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 解析 sku_specs JSON，格式化为 key value; 格式
     */
    public void parseSkuSpecs() {
        if (type == null || type.isEmpty() || "{}".equals(type)) {
            this.type = "";
            return;
        }
        try {
            JSONObject json = JSON.parseObject(type);
            this.type = json.entrySet().stream()
                    .filter(entry -> entry.getValue() != null && !entry.getValue().toString().isEmpty())
                    .map(entry -> String.valueOf(entry.getValue()))
                    .reduce((a, b) -> a + "，" + b)
                    .orElse("");
        } catch (Exception e) {
            this.type = "";
        }
    }
}