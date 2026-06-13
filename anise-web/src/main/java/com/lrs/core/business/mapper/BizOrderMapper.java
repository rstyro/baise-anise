package com.lrs.core.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrs.core.app.vo.OrderGoodsVo;
import com.lrs.core.business.entity.BizOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 订单表 Mapper 接口
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Mapper
public interface BizOrderMapper extends BaseMapper<BizOrder> {

    Map<String, Object> countByStatus(@Param("userId") Long userId);

    List<OrderGoodsVo> selectOrderGoodsList(@Param("orderIds") List<Long> orderIds);
}
