package com.carmaintenance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * WebClient Configuration
 * For communication with Python Analytics Service
 */
@Configuration
public class WebClientConfig {

    @Value("${analytics.service.url}")
    private String analyticsServiceUrl;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(analyticsServiceUrl)
                .build();
    }
}
