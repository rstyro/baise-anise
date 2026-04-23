package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizProductSku;
import com.lrs.core.business.service.IBizProductSkuService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  商品规格库存表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/bizProductSku")
@RequiredArgsConstructor
public class BizProductSkuController extends BaseController {

    private final IBizProductSkuService bizProductSkuService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizProductSku:list","business:bizProductSku:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_product_sku";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizProductSku:list","business:bizProductSku:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizProductSku> menuPage = bizProductSkuService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizProductSku:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizProductSku item) {
        return R.ok(bizProductSkuService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizProductSku:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizProductSku item) {
        return R.ok(bizProductSkuService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizProductSku:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizProductSkuService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizProductSku:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizProductSkuService.batchDel(ids));
    }

}