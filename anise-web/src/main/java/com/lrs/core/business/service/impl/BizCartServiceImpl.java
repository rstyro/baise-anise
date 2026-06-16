package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.app.dto.cart.CartAddDto;
import com.lrs.core.app.dto.cart.CartItemVo;
import com.lrs.core.business.entity.BizCart;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.mapper.BizCartMapper;
import com.lrs.core.business.service.IBizCartService;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.business.service.IBizProductSkuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 购物车服务实现类
 * 包含购物车增删改查及库存校验等业务逻辑
 */
@Service
public class BizCartServiceImpl extends ServiceImpl<BizCartMapper, BizCart> implements IBizCartService {

    @Resource
    private IBizProductSkuService bizProductSkuService;

    @Resource
    private IBizProductService bizProductService;

    @Override
    public List<CartItemVo> listWithDetails(Long userId) {
        if (userId == null) {
            return Collections.emptyList();
        }
        return getBaseMapper().selectCartListWithDetails(userId);
    }

    @Override
    public boolean addToCart(Long userId, CartAddDto dto) {
        Long skuId = dto.getSkuId();
        Integer quantity = dto.getQuantity() != null ? dto.getQuantity() : 1;

        BizProductSku sku = bizProductSkuService.getById(skuId);
        if (sku == null || sku.getStock() == null || sku.getStock().compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        LambdaQueryWrapper<BizCart> query = new LambdaQueryWrapper<>();
        query.eq(BizCart::getUserId, userId).eq(BizCart::getSkuId, skuId);
        BizCart existing = getOne(query);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUpdateTime(LocalDateTime.now());
            return updateById(existing);
        } else {
            BizProduct product = bizProductService.getById(sku.getProductId());
            Long merchantId = product != null ? product.getMerchantId() : 1L;

            BizCart cart = new BizCart()
                    .setUserId(userId)
                    .setMerchantId(merchantId)
                    .setProductId(sku.getProductId())
                    .setSkuId(skuId)
                    .setSkuSpecs(dto.getSkuSpecs())
                    .setQuantity(quantity)
                    .setSelected(1)
                    .setCreateTime(LocalDateTime.now())
                    .setUpdateTime(LocalDateTime.now());
            return save(cart);
        }
    }

    @Override
    public boolean updateCartQuantity(Long userId, Long cartId, Integer quantity) {
        BizCart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return false;
        }

        BizProductSku sku = bizProductSkuService.getById(cart.getSkuId());
        if (sku != null && sku.getStock() != null && BigDecimal.valueOf(quantity).compareTo(sku.getStock()) > 0) {
            return false;
        }

        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        return updateById(cart);
    }

    @Override
    public boolean updateCartSelected(Long userId, Long cartId, Integer selected) {
        BizCart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return false;
        }
        cart.setSelected(selected);
        cart.setUpdateTime(LocalDateTime.now());
        return updateById(cart);
    }

    @Override
    public void selectAll(Long userId, Integer selected) {
        LambdaQueryWrapper<BizCart> query = new LambdaQueryWrapper<>();
        query.eq(BizCart::getUserId, userId);
        List<BizCart> list = list(query);
        list.forEach(c -> c.setSelected(selected));
        updateBatchById(list);
    }

    @Override
    public boolean deleteCartItem(Long userId, Long cartId) {
        BizCart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return false;
        }
        return removeById(cartId);
    }

    @Override
    public void clearSelected(Long userId) {
        LambdaQueryWrapper<BizCart> query = new LambdaQueryWrapper<>();
        query.eq(BizCart::getUserId, userId).eq(BizCart::getSelected, 1);
        remove(query);
    }
}

