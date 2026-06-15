package com.lrs.core.business.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lrs.common.utils.SecurityContextHolder;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAttributeValue;
import com.lrs.core.business.service.IBizAttributeValueService;
import com.lrs.core.system.dto.BaseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/business/bizAttributeValue")
@RequiredArgsConstructor
public class BizAttributeValueController extends BaseController {

    private final IBizAttributeValueService bizAttributeValueService;

    @SaCheckPermission(value = {"business:bizAttributeValue:list","business:bizAttributeValue:list:view"},mode = SaMode.OR)
    @GetMapping("/page")
    public String page() {
        return "page/business/biz_attribute_value";
    }

    @SaCheckPermission(value = {"business:bizAttributeValue:list","business:bizAttributeValue:list:view"},mode = SaMode.OR)
    @PostMapping("/list")
    @ResponseBody
    public R list(@RequestBody BaseDto dto) {
        Page<BizAttributeValue> page = new Page<>(SecurityContextHolder.getPageNo(), SecurityContextHolder.getPageSize());
        LambdaQueryWrapper<BizAttributeValue> query = new LambdaQueryWrapper<>();
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            query.like(BizAttributeValue::getValue, dto.getKeyword());
        }
        if (dto.getAttrId() != null) {
            query.eq(BizAttributeValue::getAttrId, dto.getAttrId());
        }
        query.orderByAsc(BizAttributeValue::getSortOrder);
        Page<BizAttributeValue> result = bizAttributeValueService.page(page, query);
        return R.ok(result);
    }

    @SaCheckPermission("business:bizAttributeValue:list:add")
    @PostMapping("/add")
    @ResponseBody
    public R add(@RequestBody BizAttributeValue item) {
        return R.ok(bizAttributeValueService.add(item));
    }

    @SaCheckPermission("business:bizAttributeValue:list:edit")
    @PostMapping("/edit")
    @ResponseBody
    public R edit(@RequestBody BizAttributeValue item) {
        return R.ok(bizAttributeValueService.edit(item));
    }

    @SaCheckPermission("business:bizAttributeValue:list:del")
    @GetMapping("/del")
    @ResponseBody
    public R del(Long id) {
        return R.ok(bizAttributeValueService.del(id));
    }

    @SaCheckPermission("business:bizAttributeValue:list:del")
    @PostMapping("/batchDel")
    @ResponseBody
    public R batchDel(@RequestBody List<Long> ids) {
        return R.ok(bizAttributeValueService.batchDel(ids));
    }

    @GetMapping("/listByAttr")
    @ResponseBody
    public R listByAttr(Long attrId) {
        LambdaQueryWrapper<BizAttributeValue> query = new LambdaQueryWrapper<>();
        query.eq(BizAttributeValue::getAttrId, attrId);
        query.orderByAsc(BizAttributeValue::getSortOrder);
        return R.ok(bizAttributeValueService.list(query));
    }

}
