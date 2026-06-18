package com.lrs.core.app.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序订单物流信息 VO
 */
@Data
public class OrderLogisticsVo {

    /** 订单ID */
    private Long orderId;

    /** 订单编号 */
    private String orderNo;

    /** 订单状态 */
    private Byte status;

    /** 配送方式 1:快递 2:自提 3:送货上门 */
    private Byte deliveryType;

    /** 发货时间 */
    private LocalDateTime shipTime;

    /** 收货时间 */
    private LocalDateTime receiveTime;

    /** 多商家子订单物流 */
    private List<SubLogisticsVo> subList;

    /**
     * 子订单物流信息
     */
    @Data
    public static class SubLogisticsVo {

        /** 子订单编号 */
        private String subNo;

        /** 商家名称 */
        private String merchantName;

        /** 发货状态 0:待发货 1:已发货 2:已收货 */
        private Byte deliveryStatus;

        /** 发货时间 */
        private LocalDateTime deliveryTime;

        /** 收货时间 */
        private LocalDateTime receiveTime;

        /** 子订单物流包裹 */
        private List<PackageVo> packages;
    }

    /**
     * 物流包裹信息
     */
    @Data
    public static class PackageVo {

        /** 物流公司名称 */
        private String logisticsCompany;

        /** 物流公司编码 */
        private String expressCode;

        /** 物流单号 */
        private String trackingNo;

        /** 物流状态: 0-已揽收 1-运输中 2-派件中 3-已签收 4-异常 */
        private Byte status;

        /** 预计送达日期 */
        private LocalDate estimatedDeliveryDate;

        /** 实际签收时间 */
        private LocalDateTime deliveredTime;

        /** 最新轨迹描述 */
        private String lastTrackDetail;

        /** 创建时间 */
        private LocalDateTime createTime;
    }
}
