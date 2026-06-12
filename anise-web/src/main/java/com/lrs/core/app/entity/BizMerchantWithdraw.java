package com.lrs.core.app.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商家提现申请实体
 * 
 * @author rstyro
 * @since 2026-06-12
 */
@Data
@TableName("biz_merchant_withdraw")
public class BizMerchantWithdraw {

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
     * 申请单号
     */
    private String applyNo;

    /**
     * 提现金额
     */
    private BigDecimal amount;

    /**
     * 手续费
     */
    private BigDecimal feeAmount;

    /**
     * 实际到账
     */
    private BigDecimal realAmount;

    /**
     * 银行名称
     */
    private String bankName;

    /**
     * 银行账号
     */
    private String bankAccount;

    /**
     * 开户姓名
     */
    private String bankUsername;

    /**
     * 状态 0:待审核 1:已打款 2:已拒绝
     */
    private Byte status;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 处理时间
     */
    private LocalDateTime handleTime;

    /**
     * 申请时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
