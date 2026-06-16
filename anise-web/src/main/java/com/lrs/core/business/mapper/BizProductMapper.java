package com.lrs.core.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.lrs.core.app.dto.product.ProductDetailDto;
import com.lrs.core.app.dto.product.ProductDetailVo;
import com.lrs.core.app.dto.product.ProductListDto;
import com.lrs.core.app.dto.product.ProductVo;
import com.lrs.core.business.entity.BizProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Mapper
public interface BizProductMapper extends BaseMapper<BizProduct> {

    IPage<ProductVo> selectAppProductList(IPage<ProductVo> page, @Param("dto") ProductListDto dto);

    List<ProductVo.SpuAttrVo> selectProductSpuAttrs(@Param("productIds") List<Long> productIds);

    ProductDetailVo selectAppProductDetail(@Param("dto") ProductDetailDto dto);

    List<ProductDetailVo.SkuVo> selectProductSkuList(@Param("productId") Long productId);

    List<ProductDetailVo.SpuAttrVo> selectProductDetailSpuAttrs(@Param("productId") Long productId);

    List<ProductDetailVo.SkuAttrVo> selectProductSkuAttrs(@Param("skuIds") List<Long> skuIds);
}
