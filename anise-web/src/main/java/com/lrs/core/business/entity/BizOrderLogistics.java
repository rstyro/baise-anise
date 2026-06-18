package com.lrs.core.business.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 子订单物流信息表
 * </p>
 *
 * @author rstyro
 * @since 2026-06-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("biz_order_logistics")
public class BizOrderLogistics implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物流单ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 所属子订单ID
     */
    @TableField("sub_order_id")
    private Long subOrderId;

    /**
     * 物流公司名称
     */
    @TableField("logistics_company")
    private String logisticsCompany;

    /**
     * 物流公司编码
     */
    @TableField("express_code")
    private String expressCode;

    /**
     * 物流单号
     */
    @TableField("tracking_no")
    private String trackingNo;

    /**
     * 物流状态: 0-已揽收 1-运输中 2-派件中 3-已签收 4-异常
     */
    @TableField("status")
    private Byte status;

    /**
     * 预计送达日期
     */
    @TableField("estimated_delivery_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedDeliveryDate;

    /**
     * 实际签收时间
     */
    @TableField("delivered_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime deliveredTime;

    /**
     * 最新轨迹描述
     */
    @TableField("last_track_detail")
    private String lastTrackDetail;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    public static final byte STATUS_COLLECTED = 0;
    public static final byte STATUS_TRANSPORTING = 1;
    public static final byte STATUS_DELIVERING = 2;
    public static final byte STATUS_DELIVERED = 3;
    public static final byte STATUS_EXCEPTION = 4;
}
