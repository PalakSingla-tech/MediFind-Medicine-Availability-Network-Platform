package com.medifind.pharmacy_service.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String auth = request.getHeader("Authorization");
                    if (auth != null && !auth.isEmpty()) {
                        template.header("Authorization", auth);
                    }
                    String userRole = request.getHeader("X-User-Role");
                    if (userRole != null && !userRole.isEmpty()) {
                        template.header("X-User-Role", userRole);
                    }
                    String userId = request.getHeader("X-User-Id");
                    if (userId != null && !userId.isEmpty()) {
                        template.header("X-User-Id", userId);
                    }
                }
            }
        };
    }
}
