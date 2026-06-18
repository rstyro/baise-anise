package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.exception.ServiceException;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.utils.MerchantContextHolder;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.service.IBizProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商家后台商品管理 Controller，只负责参数接收和 Service 调用。
 */
@Slf4j
@RestController
@RequestMapping("/merchant/product")
@Validated
public class MerchantProductController extends BaseController {

    @Resource
    private IBizProductService bizProductService;

    private Long getMerchantId() {
        Long merchantId = MerchantContextHolder.getMerchantId();
        if (merchantId == null) {
            throw new ServiceException("请以商家身份登录");
        }
        return merchantId;
    }

    /**
     * 商家商品列表。
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody(required = false) ProductListDto dto) {
        return R.ok(bizProductService.listMerchantProducts(
                getMerchantId(),
                dto,
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()));
    }

    /**
     * 商家商品详情。
     */
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody ProductDetailDto dto) {
        return R.ok(bizProductService.getMerchantProductDetail(getMerchantId(), dto));
    }

    /**
     * 新增商品。
     */
    @OperateLog(title = "商家后台-新增商品")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizProduct product) {
        return R.ok(bizProductService.addMerchantProduct(getMerchantId(), product));
    }

    /**
     * 更新商品。
     */
    @OperateLog(title = "商家后台-更新商品")
    @PostMapping("/update")
    @ResponseBody
    public R update(@RequestBody BizProduct product) {
        bizProductService.updateMerchantProduct(getMerchantId(), product);
        return R.ok();
    }

    /**
     * 删除商品（下架）。
     */
    @OperateLog(title = "商家后台-删除商品")
    @PostMapping("/delete")
    @ResponseBody
    public R delete(@RequestBody ProductDetailDto dto) {
        bizProductService.deleteMerchantProduct(getMerchantId(), dto.getProductId());
        return R.ok();
    }
}
