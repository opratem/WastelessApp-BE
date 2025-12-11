package com.wasteless.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class SpoonacularConfig {

    @Value("${spoonacular.api.base-url:https://api.spoonacular.com}")
    private String baseUrl;

    @Value("${spoonacular.api.key:}")
    private String apiKey;

    @Bean
    public WebClient spoonacularWebClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String getApiKey() {
        return apiKey;
    }
}
