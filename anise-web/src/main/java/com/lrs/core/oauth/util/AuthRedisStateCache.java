package com.lrs.core.oauth.util;

import com.lrs.core.util.RedisUtil;
import lombok.AllArgsConstructor;
import me.zhyd.oauth.cache.AuthStateCache;

import java.util.concurrent.TimeUnit;

/**
 * 授权状态state缓存
 */
@AllArgsConstructor
public class AuthRedisStateCache implements AuthStateCache {

    // 全局缓存key
    public final static String AUTH_STATE_KEY = "global:auth_state:";

    /**
     * 存入缓存
     *
     * @param key   缓存key
     * @param value 缓存内容
     */
    @Override
    public void cache(String key, String value) {
        // 授权超时时间 默认三分钟
        RedisUtil.set(AUTH_STATE_KEY + key, value, 3, TimeUnit.MINUTES);
    }

    /**
     * 存入缓存
     *
     * @param key     缓存key
     * @param value   缓存内容
     * @param timeout 指定缓存过期时间(毫秒)
     */
    @Override
    public void cache(String key, String value, long timeout) {
        RedisUtil.set(AUTH_STATE_KEY + key, value, timeout);
    }

    /**
     * 获取缓存内容
     *
     * @param key 缓存key
     * @return 缓存内容
     */
    @Override
    public String get(String key) {
        return RedisUtil.get(AUTH_STATE_KEY + key,String.class);
    }

    /**
     * 是否存在key，如果对应key的value值已过期，也返回false
     *
     * @param key 缓存key
     * @return true：存在key，并且value没过期；false：key不存在或者已过期
     */
    @Override
    public boolean containsKey(String key) {
        return RedisUtil.hasKey(AUTH_STATE_KEY + key);
    }
}
