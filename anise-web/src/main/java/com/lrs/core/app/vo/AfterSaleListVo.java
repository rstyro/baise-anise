package com.lrs.core.app.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 售后列表项 VO
 */
@Data
public class AfterSaleListVo {

    /**
     * 售后ID
     */
    private Long id;

    /**
     * 售后单号
     */
    private String afterSaleNo;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 售后类型
     */
    private Byte type;

    /**
     * 售后原因
     */
    private String reason;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 状态
     */
    private Byte status;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 规格名称
     */
    private String productName;

    /**
     * 商品主图快照
     */
    private String productImage;
}