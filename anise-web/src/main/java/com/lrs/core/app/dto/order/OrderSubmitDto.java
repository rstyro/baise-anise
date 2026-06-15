package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

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
    private List<Long> cartIds;

    /** SKU ID（立即购买时使用） */
    private Long skuId;

    /** 购买数量（立即购买时使用） */
    private Integer quantity;

}
