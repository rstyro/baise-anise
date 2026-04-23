package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAddress;
import com.lrs.core.business.service.IBizAddressService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  收货地址表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
@Controller
@RequestMapping("/business/bizAddress")
@RequiredArgsConstructor
public class BizAddressController extends BaseController {

    private final IBizAddressService bizAddressService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizAddress:list","business:bizAddress:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_address";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizAddress:list","business:bizAddress:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizAddress> menuPage = bizAddressService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizAddress:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizAddress item) {
        return R.ok(bizAddressService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizAddress:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizAddress item) {
        return R.ok(bizAddressService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizAddress:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizAddressService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizAddress:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizAddressService.batchDel(ids));
    }

}