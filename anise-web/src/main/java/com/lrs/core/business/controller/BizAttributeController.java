package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAttribute;
import com.lrs.core.business.service.IBizAttributeService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  属性定义表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年6月15日
 */
@Controller
@RequestMapping("/business/bizAttribute")
@RequiredArgsConstructor
public class BizAttributeController extends BaseController {

    private final IBizAttributeService bizAttributeService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizAttribute:list","business:bizAttribute:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_attribute";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizAttribute:list","business:bizAttribute:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizAttribute> menuPage = bizAttributeService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizAttribute:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizAttribute item) {
        return R.ok(bizAttributeService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizAttribute:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizAttribute item) {
        return R.ok(bizAttributeService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizAttribute:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizAttributeService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizAttribute:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizAttributeService.batchDel(ids));
    }

}