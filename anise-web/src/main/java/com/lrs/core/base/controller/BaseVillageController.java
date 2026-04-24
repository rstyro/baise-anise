package com.lrs.core.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.base.entity.BaseVillage;
import com.lrs.core.base.service.IBaseVillageService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 村级（村委会、居委会）-行政编码 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月24日
 */
@Controller
@RequestMapping("/base/baseVillage")
@RequiredArgsConstructor
public class BaseVillageController extends BaseController {

    private final IBaseVillageService baseVillageService;


    /**
     * 页面跳转
     */
    @SaCheckPermission(value = {"base:baseVillage:list", "base:baseVillage:list:view"}, mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/base/base_village";
    }

    /**
     * 列表页
     */
    @SaCheckPermission(value = {"base:baseVillage:list", "base:baseVillage:list:view"}, mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
        Page<BaseVillage> menuPage = baseVillageService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
     * 添加
     */
    @SaCheckPermission("base:baseVillage:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BaseVillage item) {
        return R.ok(baseVillageService.add(item));
    }


    /**
     * 编辑
     */
    @SaCheckPermission("base:baseVillage:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BaseVillage item) {
        return R.ok(baseVillageService.edit(item));
    }

    /**
     * 删除
     */
    @SaCheckPermission("base:baseVillage:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(baseVillageService.del(id));
    }

    /**
     * 批量删除
     */
    @SaCheckPermission("base:baseVillage:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(baseVillageService.batchDel(ids));
    }

}