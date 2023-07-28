package com.brew.oauth20.server.config;

import com.brew.oauth20.server.middleware.CORSMiddleware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @SuppressWarnings("java:S3305")
    @Autowired
    private CORSMiddleware corsMiddleware;

    @Bean
    public FilterRegistrationBean<CORSMiddleware> corsFilterRegistration() {
        final FilterRegistrationBean<CORSMiddleware> registration = new FilterRegistrationBean<>();
        registration.setFilter(corsMiddleware);
        registration.addUrlPatterns("/*");
        return registration;
    }
}