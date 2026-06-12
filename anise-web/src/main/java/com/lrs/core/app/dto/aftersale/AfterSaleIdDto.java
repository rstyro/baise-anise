package com.lrs.core.app.dto.aftersale;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 售后ID参数
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class AfterSaleIdDto {

    /** 售后单ID */
    private Long id;

}
