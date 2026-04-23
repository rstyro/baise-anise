package com.lrs.core.business.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lrs.core.business.entity.AppUser;
import com.lrs.core.system.dto.BaseDto;

import java.util.List;


/**
 * <p>
 *  用户表 服务类
 * </p>
 *
 * @author rstyro
 * @since 2026年4月23日
 */
public interface IAppUserService extends IService<AppUser> {

    Page<AppUser> getPage(Page page, BaseDto dto);
    boolean add(AppUser item);
    boolean edit(AppUser item);
    boolean del(Long id);
    boolean batchDel(List<Long> ids);
}
