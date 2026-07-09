package com.example.tiamedsadmin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns(
                                "http://localhost:3000",
                                "http://localhost:3001",
                                "https://admin-test.tiameds.ai/",
                                "https://pharma-test.tiameds.ai/",
                                "https://*.tiameds.ai",   // any subdomain, e.g. admin-test, pharma-test, api, etc.
                                "https://tiameds.ai")     // apex domain, if needed) // Next.js origins
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
}
