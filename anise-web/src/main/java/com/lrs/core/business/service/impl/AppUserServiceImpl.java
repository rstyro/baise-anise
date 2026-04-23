package com.lrs.core.business.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lrs.core.business.entity.AppUser;
import com.lrs.core.business.mapper.AppUserMapper;
import com.lrs.core.business.service.IAppUserService;
import com.lrs.core.system.dto.BaseDto;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;


/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Service
public class AppUserServiceImpl extends ServiceImpl<AppUserMapper, AppUser> implements IAppUserService {


    @Override
    public Page<AppUser> getPage(Page page, BaseDto dto) {
        LambdaQueryWrapper<AppUser> queryWrapper = new LambdaQueryWrapper<>();
        if (!ObjectUtils.isEmpty(dto.getKeyword())) {
            // queryWrapper.like(AppUser::getRemark, dto.getKeyword());
        }
        queryWrapper.orderByDesc(AppUser::getId);
            return page(page, queryWrapper);
        }

        @Override
        public boolean add(AppUser item) {
            return save(item);
        }

        @Override
        public boolean edit(AppUser item) {
            return updateById(item);
        }

        @Override
        public boolean del(Long id) {
            return removeById(id);
        }

        @Override
        public boolean batchDel(List<Long> ids) {
            return removeBatchByIds(ids);
         }

}
