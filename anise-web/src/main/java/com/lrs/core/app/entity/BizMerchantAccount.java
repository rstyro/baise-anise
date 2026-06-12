package com.lrs.core.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家结算账户实体
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Data
@TableName("biz_merchant_account")
public class BizMerchantAccount {

    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商家ID
     */
    private Long merchantId;

    /**
     * 累计收入
     */
    private BigDecimal totalAmount;

    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;

    /**
     * 可提现金额
     */
    private BigDecimal availableAmount;

    /**
     * 累计提现
     */
    private BigDecimal totalWithdraw;

    /**
     * 累计退款
     */
    private BigDecimal totalRefund;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
