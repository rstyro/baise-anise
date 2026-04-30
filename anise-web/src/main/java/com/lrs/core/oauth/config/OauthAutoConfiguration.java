package com.lrs.core.oauth.config;

import com.lrs.core.oauth.config.properties.OauthProperties;
import com.lrs.core.oauth.util.AuthRedisStateCache;
import me.zhyd.oauth.cache.AuthStateCache;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Social 配置属性
 */
@AutoConfiguration
@EnableConfigurationProperties(OauthProperties.class)
public class OauthAutoConfiguration {

    @Bean
    public AuthStateCache authStateCache() {
        return new AuthRedisStateCache();
    }

}
