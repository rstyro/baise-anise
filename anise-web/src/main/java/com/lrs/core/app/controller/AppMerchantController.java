package com.lrs.core.app.controller;

import com.lrs.common.annotation.OperateLog;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.app.dto.merchant.MerchantDetailDto;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.vo.PageResultVo;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.service.IBizMerchantService;
import com.lrs.core.business.service.IBizProductService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 小程序商家控制器
 * 负责接收请求、调用Service、返回响应，不包含业务逻辑
 */
@Slf4j
@RestController
@RequestMapping("/app/merchant")
@Validated
public class AppMerchantController extends BaseController {

    @Resource
    private IBizMerchantService bizMerchantService;

    @Resource
    private IBizProductService bizProductService;

    @OperateLog(title = "小程序-查看商家详情")
    @PostMapping("/detail")
    @ResponseBody
    public R detail(@RequestBody MerchantDetailDto dto) {
        if (dto.getMerchantId() == null) {
            return R.error("商家ID不能为空");
        }

        Object result = bizMerchantService.getAppMerchantDetail(dto);
        if (result == null) {
            return R.error("商家不存在");
        }

        return R.ok(result);
    }

    @OperateLog(title = "小程序-查看商家商品列表")
    @PostMapping("/productList")
    @ResponseBody
    public R productList(@RequestBody MerchantDetailDto dto) {
        if (dto.getMerchantId() == null) {
            return R.error("商家ID不能为空");
        }

        ProductListDto productListDto = new ProductListDto();
        productListDto.setMerchantId(dto.getMerchantId());

        PageResultVo<?> result = bizProductService.getAppProductList(
                productListDto,
                SecurityContextHolder.getPageNo(),
                SecurityContextHolder.getPageSize()
        );

        return R.ok(result);
    }

}