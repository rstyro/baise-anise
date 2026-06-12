package com.lrs.core.app.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付结果 VO
 */
@Data
public class PayResultVo {

    /**
     * 预支付ID
     */
    private String prepayId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 支付金额
     */
    private BigDecimal payAmount;

    /**
     * 随机字符串
     */
    private String nonceStr;

    /**
     * 时间戳
     */
    private String timeStamp;

    /**
     * 签名类型
     */
    private String signType;

    /**
     * 支付签名
     */
    private String paySign;
}