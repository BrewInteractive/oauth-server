package com.brew.oauth20.server.config;

import com.brew.oauth20.server.middleware.CORSMiddleware;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<CORSMiddleware> corsFilterRegistration() {
        FilterRegistrationBean<CORSMiddleware> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CORSMiddleware());
        registration.addUrlPatterns("/*");
        return registration;
    }
}
