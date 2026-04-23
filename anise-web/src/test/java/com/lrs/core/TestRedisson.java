package com.lrs.core;

import com.lrs.core.system.entity.SysUser;
import com.lrs.core.util.RedisUtil;
import com.lrs.core.util.RedissonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class TestRedisson {

    @Test
    public void test() {
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setNickName("rstyro");
        sysUser.setCreateTime(LocalDateTime.now());
        RedissonUtils.setCacheObject("name-redisson", sysUser);
        SysUser cacheObject = RedissonUtils.getCacheObject("name-redisson");
        System.out.println(cacheObject);
    }


    @Test
    public void test2() {
        SysUser sysUser = new SysUser();
        sysUser.setId(1L);
        sysUser.setNickName("rstyro");
        sysUser.setCreateTime(LocalDateTime.now());
        RedisUtil.set("name-redis",sysUser);
        SysUser cacheObject = RedisUtil.get("name-redis", SysUser.class);
        System.out.println(cacheObject);
    }
}
