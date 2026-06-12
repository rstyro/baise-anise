package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 订单ID参数（用于详情、取消、确认收货等操作）
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class OrderIdDto {

    /** 订单ID */
    private Long orderId;

}
