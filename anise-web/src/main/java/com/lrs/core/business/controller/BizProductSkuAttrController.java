package com.lrs.core.business.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lrs.common.vo.R;
import com.lrs.core.base.BaseController;
import com.lrs.core.business.entity.BizAttribute;
import com.lrs.core.business.entity.BizAttributeValue;
import com.lrs.core.business.entity.BizProductSkuAttr;
import com.lrs.core.business.service.IBizAttributeService;
import com.lrs.core.business.service.IBizAttributeValueService;
import com.lrs.core.business.service.IBizProductSkuAttrService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/business/bizProductSkuAttr")
public class BizProductSkuAttrController extends BaseController {

    @Resource
    private IBizProductSkuAttrService bizProductSkuAttrService;

    @Resource
    private IBizAttributeService bizAttributeService;

    @Resource
    private IBizAttributeValueService bizAttributeValueService;

    @GetMapping("/listBySku")
    public R listBySku(Long skuId) {
        LambdaQueryWrapper<BizProductSkuAttr> query = new LambdaQueryWrapper<>();
        query.eq(BizProductSkuAttr::getSkuId, skuId);
        List<BizProductSkuAttr> attrs = bizProductSkuAttrService.list(query);

        List<Map<String, Object>> result = new ArrayList<>();
        if (!attrs.isEmpty()) {
            Map<Long, BizAttribute> attrMap = bizAttributeService.listByIds(
                    attrs.stream().map(BizProductSkuAttr::getAttrId).collect(Collectors.toList())
            ).stream().collect(Collectors.toMap(BizAttribute::getId, a -> a));

            Map<Long, BizAttributeValue> valueMap = bizAttributeValueService.listByIds(
                    attrs.stream().map(BizProductSkuAttr::getAttrValueId).collect(Collectors.toList())
            ).stream().collect(Collectors.toMap(BizAttributeValue::getId, a -> a));

            for (BizProductSkuAttr attr : attrs) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", attr.getId());
                item.put("skuId", attr.getSkuId());
                item.put("attrId", attr.getAttrId());
                item.put("attrName", attrMap.get(attr.getAttrId()) != null ? attrMap.get(attr.getAttrId()).getAttrName() : "");
                item.put("attrValueId", attr.getAttrValueId());
                item.put("attrValue", valueMap.get(attr.getAttrValueId()) != null ? valueMap.get(attr.getAttrValueId()).getValue() : "");
                result.add(item);
            }
        }
        return R.ok(result);
    }

    @GetMapping("/listByProduct")
    public R listByProduct(Long productId) {
        LambdaQueryWrapper<BizProductSkuAttr> query = new LambdaQueryWrapper<>();
        query.eq(BizProductSkuAttr::getProductId, productId);
        return R.ok(bizProductSkuAttrService.list(query));
    }

    @PostMapping("/save")
    public R save(@RequestBody Map<String, Object> data) {
        Long skuId = ((Number) data.get("skuId")).longValue();
        List<Map<String, Object>> attrs = (List<Map<String, Object>>) data.get("attrs");

        BizProductSkuAttr first = bizProductSkuAttrService.getOne(new LambdaQueryWrapper<BizProductSkuAttr>().eq(BizProductSkuAttr::getSkuId, skuId));
        Long productId = first != null ? first.getProductId() : null;

        LambdaQueryWrapper<BizProductSkuAttr> deleteQuery = new LambdaQueryWrapper<>();
        deleteQuery.eq(BizProductSkuAttr::getSkuId, skuId);
        bizProductSkuAttrService.remove(deleteQuery);

        for (Map<String, Object> attr : attrs) {
            Long attrId = ((Number) attr.get("attrId")).longValue();
            Long attrValueId = ((Number) attr.get("attrValueId")).longValue();

            BizProductSkuAttr skuAttr = new BizProductSkuAttr()
                    .setSkuId(skuId)
                    .setProductId(productId)
                    .setAttrId(attrId)
                    .setAttrValueId(attrValueId);
            bizProductSkuAttrService.save(skuAttr);
        }

        return R.ok();
    }

    @GetMapping("/del")
    public R del(Long id) {
        bizProductSkuAttrService.removeById(id);
        return R.ok();
    }

}
