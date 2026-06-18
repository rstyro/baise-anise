package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.vo.R;
import com.lrs.common.vo.UserVo;
import com.lrs.core.app.dto.cart.CartAddDto;
import com.lrs.core.app.dto.cart.CartDeleteDto;
import com.lrs.core.app.dto.cart.CartItemVo;
import com.lrs.core.app.dto.cart.CartSelectAllDto;
import com.lrs.core.app.dto.cart.CartUpdateQuantityDto;
import com.lrs.core.app.dto.cart.CartUpdateSelectedDto;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.service.IBizCartService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序购物车 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/app/cart")
@Validated
public class AppCartController extends BaseController {

    @Resource
    private IBizCartService bizCartService;

    private Long getUserId() {
        UserVo user = getLoginSysUser();
        if (user == null) {
            throw new ServiceException("请先登录");
        }
        return user.getUserId();
    }

    /**
     * 加入购物车。
     */
    @OperateLog(title = "小程序-加入购物车")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody CartAddDto dto) {
        boolean success = bizCartService.addToCart(getUserId(), dto);
        return success ? R.ok() : R.error("商品库存不足");
    }

    /**
     * 购物车列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list() {
        List<CartItemVo> voList = bizCartService.listWithDetails(getUserId());
        return R.ok(voList);
    }

    /**
     * 更新数量。
     */
    @OperateLog(title = "小程序-更新购物车数量")
    @PostMapping("/updateQuantity")
    @ResponseBody
    public R updateQuantity(@RequestBody CartUpdateQuantityDto dto) {
        boolean success = bizCartService.updateCartQuantity(getUserId(), dto.getCartId(), dto.getQuantity());
        return success ? R.ok() : R.error("购物车记录不存在或库存不足");
    }

    /**
     * 更新选中状态。
     */
    @PostMapping("/updateSelected")
    @ResponseBody
    public R updateSelected(@RequestBody CartUpdateSelectedDto dto) {
        boolean success = bizCartService.updateCartSelected(getUserId(), dto.getCartId(), dto.getSelected());
        return success ? R.ok() : R.error("购物车记录不存在");
    }

    /**
     * 全选或取消全选。
     */
    @PostMapping("/selectAll")
    @ResponseBody
    public R selectAll(@RequestBody CartSelectAllDto dto) {
        bizCartService.selectAll(getUserId(), dto.getSelected());
        return R.ok();
    }

    /**
     * 删除购物车项。
     */
    @OperateLog(title = "小程序-删除购物车")
    @PostMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody CartDeleteDto dto) {
        if (dto.getCartId() == null) {
            return R.error("cartId不能为空");
        }
        boolean success = bizCartService.deleteCartItem(getUserId(), dto.getCartId());
        return success ? R.ok() : R.error("购物车记录不存在");
    }

    /**
     * 清除已选中的商品。
     */
    @PostMapping("/clearSelected")
    @ResponseBody
    public R clearSelected() {
        bizCartService.clearSelected(getUserId());
        return R.ok();
    }
}
