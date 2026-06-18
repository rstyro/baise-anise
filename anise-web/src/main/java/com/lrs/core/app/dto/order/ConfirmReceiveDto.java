package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序确认收货参数。
 */
@Data
@Accessors(chain = true)
public class ConfirmReceiveDto {

    /**
     * 主订单ID。
     */
    private Long orderId;

    /**
     * 子订单ID，可选。传入时按子订单确认收货。
     */
    private Long subId;
}
