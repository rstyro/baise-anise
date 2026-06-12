package com.lrs.core.app.dto.pay;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 支付订单ID参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class PayOrderIdDto {

    /** 订单ID */
    private Long orderId;

}
