package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.dto.cart.CartItemVo;
import com.lrs.core.business.entity.BizCart;

import java.util.List;

/**
 * <p>
 * 购物车表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026-06-11
 */
public interface IBizCartService extends IService<BizCart> {

    List<CartItemVo> listWithDetails(Long userId);
}

