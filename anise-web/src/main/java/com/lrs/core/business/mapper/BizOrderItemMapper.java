package com.lrs.core.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrs.core.business.entity.BizOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 订单明细表 Mapper 接口
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Mapper
public interface BizOrderItemMapper extends BaseMapper<BizOrderItem> {

}
