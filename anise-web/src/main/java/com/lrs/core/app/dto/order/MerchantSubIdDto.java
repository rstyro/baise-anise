package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商家后台 - 子订单ID参数
 *
 * @author rstyro
 * @since 2026-06-13
 */
@Data
@Accessors(chain = true)
public class MerchantSubIdDto {

    /** 子订单ID */
    private Long subId;

}
