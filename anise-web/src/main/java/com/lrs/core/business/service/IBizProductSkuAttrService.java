package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.dto.ProductSkuAttrSaveDto;
import com.lrs.core.business.entity.BizProductSkuAttr;

public interface IBizProductSkuAttrService extends IService<BizProductSkuAttr> {

    /**
     * 保存 SKU 属性配置，按 SKU 维度覆盖原有属性。
     *
     * @param dto SKU 属性保存参数
     */
    void saveSkuAttrs(ProductSkuAttrSaveDto dto);

}
