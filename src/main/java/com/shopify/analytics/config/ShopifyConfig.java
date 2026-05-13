package com.shopify.analytics.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "shopify")
@Data
public class ShopifyConfig {
    private String storeUrl;
    private String apiVersion;
    private String accessToken;
    private Sync sync = new Sync();

    @Data
    public static class Sync {
        private int batchSize = 250;
        private int retryAttempts = 3;
    }
}