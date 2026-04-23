package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.business.service.IBizProductService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  商品表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/bizProduct")
@RequiredArgsConstructor
public class BizProductController extends BaseController {

    private final IBizProductService bizProductService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizProduct:list","business:bizProduct:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_product";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizProduct:list","business:bizProduct:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizProduct> menuPage = bizProductService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizProduct:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizProduct item) {
        return R.ok(bizProductService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizProduct:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizProduct item) {
        return R.ok(bizProductService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizProduct:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizProductService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizProduct:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizProductService.batchDel(ids));
    }

}