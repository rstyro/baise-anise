package com.lrs.core.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lrs.core.business.entity.AppUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author rstyro
 * @since 2026-04-23
 */
@Mapper
public interface AppUserMapper extends BaseMapper<AppUser> {

}
