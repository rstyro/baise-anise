package com.lrs.core.app.dto.address;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 小程序 - 地址ID参数（用于删除、设置默认等操作）
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@Accessors(chain = true)
public class AddressIdDto {

    /** 地址ID */
    private Long id;

}
