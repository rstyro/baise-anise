package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.dto.cart.CartAddDto;
import com.lrs.core.app.dto.cart.CartItemVo;
import com.lrs.core.business.entity.BizCart;

import java.util.List;

/**
 * 购物车服务接口
 * 包含购物车增删改查及库存校验等业务逻辑
 */
public interface IBizCartService extends IService<BizCart> {

    /**
     * 获取购物车列表（含商品详情）
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<CartItemVo> listWithDetails(Long userId);

    /**
     * 加入购物车（自动处理同SKU累加）
     * @param userId 用户ID
     * @param dto 加入购物车参数
     * @return 是否成功
     */
    boolean addToCart(Long userId, CartAddDto dto);

    /**
     * 更新购物车数量（含库存校验）
     * @param userId 用户ID
     * @param cartId 购物车ID
     * @param quantity 数量
     * @return 是否成功
     */
    boolean updateCartQuantity(Long userId, Long cartId, Integer quantity);

    /**
     * 更新购物车选中状态
     * @param userId 用户ID
     * @param cartId 购物车ID
     * @param selected 选中状态（1选中，0未选中）
     * @return 是否成功
     */
    boolean updateCartSelected(Long userId, Long cartId, Integer selected);

    /**
     * 全选/取消全选
     * @param userId 用户ID
     * @param selected 选中状态（1选中，0未选中）
     */
    void selectAll(Long userId, Integer selected);

    /**
     * 删除购物车项（权限校验）
     * @param userId 用户ID
     * @param cartId 购物车ID
     * @return 是否成功
     */
    boolean deleteCartItem(Long userId, Long cartId);

    /**
     * 清除已选中的购物车项
     * @param userId 用户ID
     */
    void clearSelected(Long userId);
}

