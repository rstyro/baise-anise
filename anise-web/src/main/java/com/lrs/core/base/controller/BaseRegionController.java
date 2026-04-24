package com.lrs.core.base.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.base.dto.RegionDto;
import com.lrs.core.base.entity.BaseRegion;
import com.lrs.core.base.service.IBaseRegionService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 * 国内省市区数据表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年4月24日
 */
@Controller
@RequestMapping("/base/baseRegion")
@RequiredArgsConstructor
public class BaseRegionController extends BaseController {

    private final IBaseRegionService baseRegionService;


    /**
     * 页面跳转
     */
    @SaCheckPermission(value = {"base:baseRegion:list", "base:baseRegion:list:view"}, mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/base/base_region";
    }

    /**
     * 列表页
     */
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody RegionDto dto) {
        Page<BaseRegion> menuPage = baseRegionService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
     * 添加
     */
    @SaCheckPermission("base:baseRegion:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BaseRegion item) {
        return R.ok(baseRegionService.add(item));
    }


    /**
     * 编辑
     */
    @SaCheckPermission("base:baseRegion:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BaseRegion item) {
        return R.ok(baseRegionService.edit(item));
    }

    /**
     * 删除
     */
    @SaCheckPermission("base:baseRegion:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(baseRegionService.del(id));
    }

    /**
     * 批量删除
     */
    @SaCheckPermission("base:baseRegion:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(baseRegionService.batchDel(ids));
    }

}