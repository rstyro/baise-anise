package com.lrs.core.app.dto.order;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商家子订单发货参数。
 */
@Data
@Accessors(chain = true)
public class MerchantOrderDeliveryDto {

    /**
     * 子订单ID。
     */
    private Long subId;

    /**
     * 快递公司名称。
     */
    private String expressCompany;

    /**
     * 快递公司编码。
     */
    private String expressCode;

    /**
     * 快递单号。
     */
    private String expressNo;
}
