package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.AppUser;
import com.lrs.core.business.service.IAppUserService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  用户表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/appUser")
@RequiredArgsConstructor
public class AppUserController extends BaseController {

    private final IAppUserService appUserService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:appUser:list","business:appUser:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/app_user";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:appUser:list","business:appUser:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<AppUser> menuPage = appUserService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:appUser:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody AppUser item) {
        return R.ok(appUserService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:appUser:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody AppUser item) {
        return R.ok(appUserService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:appUser:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(appUserService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:appUser:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(appUserService.batchDel(ids));
    }

}