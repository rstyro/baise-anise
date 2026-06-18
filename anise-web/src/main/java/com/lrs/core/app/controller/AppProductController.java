package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.dto.product.ProductVo;
import com.lrs.core.app.vo.PageResultVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.service.IBizCategoryService;
import com.lrs.core.business.service.IBizProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序商品控制器
 * 负责接收请求、调用Service、返回响应，不包含业务逻辑
 */
@Slf4j
@RestController
@RequestMapping("/app/product")
@Validated
public class AppProductController extends BaseController {

    @Resource
    private IBizProductService bizProductService;

    @Resource
    private IBizCategoryService bizCategoryService;

    /**
     * 获取商品分类列表
     */
    @PostMapping("/categoryList")
    @ResponseBody
    public R categoryList() {
        return R.ok(bizCategoryService.listAppCategories());
    }

    /**
     * 小程序-浏览商品列表
     */
    @OperateLog(title = "小程序-浏览商品列表")
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody ProductListDto dto) {
        PageResultVo<ProductVo> result = bizProductService.getAppProductList(
                dto,
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );
        return R.ok(result);
    }

    /**
     * 小程序-查看商品详情
     */
    @OperateLog(title = "小程序-查看商品详情")
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody ProductDetailDto dto) {
        if (dto.getProductId() == null) {
            return R.error("商品ID不能为空");
        }

        Object result = bizProductService.getAppProductDetail(dto);
        if (result == null) {
            return R.error("商品不存在或已下架");
        }

        return R.ok(result);
    }

}
