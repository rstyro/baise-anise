package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizCategory;
import com.lrs.core.business.service.IBizCategoryService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  商品分类表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/bizCategory")
@RequiredArgsConstructor
public class BizCategoryController extends BaseController {

    private final IBizCategoryService bizCategoryService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizCategory:list","business:bizCategory:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_category";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizCategory:list","business:bizCategory:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizCategory> menuPage = bizCategoryService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizCategory:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizCategory item) {
        return R.ok(bizCategoryService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizCategory:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizCategory item) {
        return R.ok(bizCategoryService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizCategory:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizCategoryService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizCategory:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizCategoryService.batchDel(ids));
    }

}