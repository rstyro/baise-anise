package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 小程序 - 订单提交参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class OrderSubmitDto {

    /** 收货地址ID */
    private Long addressId;

    /** 订单备注 */
    private String remark;

    /** 购物车项ID列表（从购物车下单时使用） */
    private java.util.List<Long> cartIds;

    /** 商家ID */
    private Long merchantId;

}
