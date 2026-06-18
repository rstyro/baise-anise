package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductDetailVo;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.dto.product.ProductVo;
import com.lrs.core.app.vo.PageResultVo;
import com.lrs.core.business.entity.BizProduct;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  商品表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IBizProductService extends IService<BizProduct> {

    Page<BizProduct> getPage(Page page, BaseDto dto);
    boolean add(BizProduct item);
    boolean edit(BizProduct item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);

    PageResultVo<ProductVo> getAppProductList(ProductListDto dto, int pageNo, int pageSize);

    ProductDetailVo getAppProductDetail(ProductDetailDto dto);

    /**
     * 查询商家商品列表。
     */
    Object listMerchantProducts(Long merchantId, ProductListDto dto, int pageNo, int pageSize);

    /**
     * 查询商家商品详情。
     */
    ProductDetailVo getMerchantProductDetail(Long merchantId, ProductDetailDto dto);

    /**
     * 新增商家商品。
     */
    Long addMerchantProduct(Long merchantId, BizProduct product);

    /**
     * 更新商家商品。
     */
    void updateMerchantProduct(Long merchantId, BizProduct product);

    /**
     * 下架商家商品。
     */
    void deleteMerchantProduct(Long merchantId, Long productId);
}
