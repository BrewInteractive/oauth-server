package com.brew.oauth20.server.config;

import com.brew.oauth20.server.filter.CORSFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<CORSFilter> corsFilterRegistration(CORSFilter corsFilter) {
        final FilterRegistrationBean<CORSFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(corsFilter);
        registration.addUrlPatterns("/oauth/authorize", "/oauth/token");
        return registration;
    }
}
