package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商家取消子订单参数。
 */
@Data
@Accessors(chain = true)
public class MerchantOrderCancelDto {

    /**
     * 子订单ID。
     */
    private Long subId;

    /**
     * 取消原因。
     */
    private String cancelReason;
}
