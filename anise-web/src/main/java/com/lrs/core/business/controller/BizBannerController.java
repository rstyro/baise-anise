package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizBanner;
import com.lrs.core.business.service.IBizBannerService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * <p>
 *  Banner轮播图表 前端控制器
 * </p>
 *
 * @author rstyro
 * @since 2026年6月11日
 */
@Controller
@RequestMapping("/business/bizBanner")
@RequiredArgsConstructor
public class BizBannerController extends BaseController {

    private final IBizBannerService bizBannerService;


    /**
    * 页面跳转
    */
    @SaCheckPermission(value = {"business:bizBanner:list","business:bizBanner:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_banner";
    }

    /**
    * 列表页
    */
    @SaCheckPermission(value = {"business:bizBanner:list","business:bizBanner:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
    Page<BizBanner> menuPage = bizBannerService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(menuPage);
    }

    /**
    * 添加
    */
    @SaCheckPermission("business:bizBanner:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizBanner item) {
        return R.ok(bizBannerService.add(item));
    }


    /**
    * 编辑
    */
    @SaCheckPermission("business:bizBanner:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizBanner item) {
        return R.ok(bizBannerService.edit(item));
    }

    /**
    * 删除
    */
    @SaCheckPermission("business:bizBanner:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizBannerService.del(id));
    }

    /**
    * 批量删除
    */
    @SaCheckPermission("business:bizBanner:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizBannerService.batchDel(ids));
    }

}