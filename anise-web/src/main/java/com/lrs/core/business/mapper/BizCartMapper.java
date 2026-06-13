package com.lrs.core.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrs.core.app.dto.cart.CartItemVo;
import com.lrs.core.business.entity.BizCart;

import java.util.List;

/**
 * <p>
 * 购物车表 Mapper
 * </p>
 *
 * @author rstyro
 * @since 2026-06-11
 */
public interface BizCartMapper extends BaseMapper<BizCart> {

    List<CartItemVo> selectCartListWithDetails(Long userId);
}

