package com.lrs.core.app.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 小程序提交订单结果。
 */
@Data
@Accessors(chain = true)
public class OrderSubmitResultVo {

    /** 订单ID */
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 待支付金额 */
    private BigDecimal payAmount;

    /** 涉及商家数量 */
    private Integer merchantCount;
}
