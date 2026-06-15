package com.lrs.core.app.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.annotation.OperateLog;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.cart.CartAddDto;
import com.lrs.core.app.dto.cart.CartDeleteDto;
import com.lrs.core.app.dto.cart.CartItemVo;
import com.lrs.core.app.dto.cart.CartSelectAllDto;
import com.lrs.core.app.dto.cart.CartUpdateQuantityDto;
import com.lrs.core.app.dto.cart.CartUpdateSelectedDto;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizCart;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.service.IBizCartService;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.business.service.IBizProductSkuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小程序 - 购物车 Controller
 *
 * @author rstyro
 * @since 2026-06-11
 */
@Slf4j
@RestController
@RequestMapping("/app/cart")
@Validated
public class AppCartController extends BaseController {

    @Resource
    private IBizCartService bizCartService;

    @Resource
    private IBizProductService bizProductService;

    @Resource
    private IBizProductSkuService bizProductSkuService;

    /**
     * 获取当前登录用户ID
     */
    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) throw new RuntimeException("请先登录");
        return user.getUserId();
    }

    /**
     * 加入购物车
     */
    @OperateLog(title = "小程序-加入购物车")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody CartAddDto dto) {
        Long userId = getUserId();
        Long skuId = dto.getSkuId();
        Integer quantity = dto.getQuantity() != null ? dto.getQuantity() : 1;

        BizProductSku sku = bizProductSkuService.getById(skuId);
        if (sku == null || sku.getStock() == null || sku.getStock().compareTo(BigDecimal.ZERO) <= 0) {
            return R.error("商品库存不足");
        }

        // 同一用户+同一SKU → 累加数量
        LambdaQueryWrapper<BizCart> query = new LambdaQueryWrapper<>();
        query.eq(BizCart::getUserId, userId).eq(BizCart::getSkuId, skuId);
        BizCart existing = bizCartService.getOne(query);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + quantity);
            existing.setUpdateTime(LocalDateTime.now());
            bizCartService.updateById(existing);
        } else {
            // 获取商品信息以获取商家ID
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
            bizCartService.save(cart);
        }

        return R.ok();
    }

    /**
     * 购物车列表
     */
    @PostMapping("/list")
    @ResponseBody
    public R list() {
        Long userId = getUserId();
        List<CartItemVo> voList = bizCartService.listWithDetails(userId);
        return R.ok(voList);
    }

    /**
     * 更新数量
     */
    @OperateLog(title = "小程序-更新购物车数量")
    @PostMapping("/updateQuantity")
    @ResponseBody
    public R updateQuantity(@RequestBody CartUpdateQuantityDto dto) {
        Long userId = getUserId();
        Long cartId = dto.getCartId();
        Integer quantity = dto.getQuantity();

        BizCart cart = bizCartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return R.error("购物车记录不存在");
        }

        BizProductSku sku = bizProductSkuService.getById(cart.getSkuId());
        if (sku != null && sku.getStock() != null && BigDecimal.valueOf(quantity).compareTo(sku.getStock()) > 0) {
            return R.error("库存不足");
        }

        cart.setQuantity(quantity);
        cart.setUpdateTime(LocalDateTime.now());
        bizCartService.updateById(cart);
        return R.ok();
    }

    /**
     * 更新选中状态
     */
    @PostMapping("/updateSelected")
    @ResponseBody
    public R updateSelected(@RequestBody CartUpdateSelectedDto dto) {
        Long userId = getUserId();
        Long cartId = dto.getCartId();
        Integer selected = dto.getSelected();

        BizCart cart = bizCartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return R.error("购物车记录不存在");
        }
        cart.setSelected(selected);
        cart.setUpdateTime(LocalDateTime.now());
        bizCartService.updateById(cart);
        return R.ok();
    }

    /**
     * 全选/取消全选
     */
    @PostMapping("/selectAll")
    @ResponseBody
    public R selectAll(@RequestBody CartSelectAllDto dto) {
        Long userId = getUserId();
        Integer selected = dto.getSelected();

        LambdaQueryWrapper<BizCart> query = new LambdaQueryWrapper<>();
        query.eq(BizCart::getUserId, userId);
        List<BizCart> list = bizCartService.list(query);
        list.forEach(c -> c.setSelected(selected));
        bizCartService.updateBatchById(list);
        return R.ok();
    }

    /**
     * 删除购物车项
     */
    @OperateLog(title = "小程序-删除购物车")
    @PostMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody CartDeleteDto dto) {
        Long userId = getUserId();
        Long cartId = dto.getCartId();
        if (cartId == null) return R.error("cartId不能为空");

        BizCart cart = bizCartService.getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            return R.error("购物车记录不存在");
        }
        bizCartService.removeById(cartId);
        return R.ok();
    }

    /**
     * 清除已选中的商品（下单后调用）
     */
    @PostMapping("/clearSelected")
    @ResponseBody
    public R clearSelected() {
        Long userId = getUserId();
        LambdaQueryWrapper<BizCart> query = new LambdaQueryWrapper<>();
        query.eq(BizCart::getUserId, userId).eq(BizCart::getSelected, 1);
        bizCartService.remove(query);
        return R.ok();
    }

}

