package com.lrs.core.oauth.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * oauth 配置属性
 */
@Data
@ConfigurationProperties(prefix = "justauth")
@Configuration
public class OauthProperties {

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 授权类型
     */
    private Map<String, OauthLoginConfigProperties> type;

}
