package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.business.service.IBizAfterSaleService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/business/bizAfterSale")
@RequiredArgsConstructor
public class BizAfterSaleController extends BaseController {

    private final IBizAfterSaleService bizAfterSaleService;

    @SaCheckPermission(value = {"business:bizAfterSale:list","business:bizAfterSale:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_after_sale";
    }

    @SaCheckPermission(value = {"business:bizAfterSale:list","business:bizAfterSale:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
        Page<BizAfterSale> page = bizAfterSaleService.getPage(new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize()), dto);
        return R.ok(page);
    }

    @SaCheckPermission("business:bizAfterSale:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizAfterSale item) {
        return R.ok(bizAfterSaleService.add(item));
    }

    @SaCheckPermission("business:bizAfterSale:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizAfterSale item) {
        return R.ok(bizAfterSaleService.edit(item));
    }

    @SaCheckPermission("business:bizAfterSale:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizAfterSaleService.del(id));
    }

    @SaCheckPermission("business:bizAfterSale:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizAfterSaleService.batchDel(ids));
    }
}
