package com.lrs.core.business.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.BizAfterSale;
import com.lrs.core.business.mapper.BizAfterSaleMapper;
import com.lrs.core.business.service.IBizAfterSaleService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizAfterSaleServiceImpl extends ServiceImpl<BizAfterSaleMapper, BizAfterSale> implements IBizAfterSaleService {

    @Override
    public Page<BizAfterSale> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<BizAfterSale> qw = new LambdaQueryWrapper<>();
        qw.orderByDesc(BizAfterSale::getId);
        return page(page, qw);
    }

    @Override
    public boolean add(BizAfterSale item) { return save(item); }

    @Override
    public boolean edit(BizAfterSale item) { return updateById(item); }

    @Override
    public boolean del(Long id) { return removeById(id); }

    @Override
    public boolean batchDel(List<Long> ids) { return removeBatchByIds(ids); }
}
