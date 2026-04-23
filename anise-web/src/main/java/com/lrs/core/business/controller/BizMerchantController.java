package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizMerchant;
import com.lrs.core.business.service.IBizMerchantService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  商家/农户表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/bizMerchant")
@RequiredArgsConstructor
public class BizMerchantController extends BaseController {

    private final IBizMerchantService bizMerchantService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizMerchant:list","business:bizMerchant:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_merchant";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizMerchant:list","business:bizMerchant:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizMerchant> menuPage = bizMerchantService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizMerchant:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizMerchant item) {
        return R.ok(bizMerchantService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizMerchant:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizMerchant item) {
        return R.ok(bizMerchantService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizMerchant:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizMerchantService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizMerchant:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizMerchantService.batchDel(ids));
    }

}