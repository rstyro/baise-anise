package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 订单列表查询参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class OrderQueryDto {

    /** 订单状态（可选，null=全部） */
    private Integer status;

}
