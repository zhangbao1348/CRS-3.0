package com.crs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Web配置类
 * 用于配置HTTP响应的字符编码为UTF-8
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 确保所有字符串响应使用UTF-8编码
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof StringHttpMessageConverter) {
                ((StringHttpMessageConverter) converter).setDefaultCharset(StandardCharsets.UTF_8);
            }
        }
    }
}
