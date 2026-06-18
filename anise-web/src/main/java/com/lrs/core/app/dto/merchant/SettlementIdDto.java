package com.lrs.core.app.dto.merchant;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 商家结算记录ID参数。
 */
@Data
@Accessors(chain = true)
public class SettlementIdDto {

    /**
     * 结算记录ID。
     */
    private Long settleId;
}
