package com.lrs.core.business.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 售后申请表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_after_sale")
public class BizAfterSale implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 售后单号 */
    @TableField("after_sale_no")
    private String afterSaleNo;

    /** 订单ID */
    @TableField("order_id")
    private Long orderId;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 售后类型 1:仅退款 2:退货退款 3:坏果赔付 */
    @TableField("type")
    private Byte type;

    /** 售后原因 */
    @TableField("reason")
    private String reason;

    /** 凭证图片 */
    @TableField("evidence_images")
    private String evidenceImages;

    /** 退款金额 */
    @TableField("refund_amount")
    private BigDecimal refundAmount;

    /** 状态 0:待处理 1:同意 2:拒绝 3:完成 */
    @TableField("status")
    private Byte status;

    /** 处理备注 */
    @TableField("handle_remark")
    private String handleRemark;

    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @TableField("is_deleted")
    private Integer isDeleted;

}

