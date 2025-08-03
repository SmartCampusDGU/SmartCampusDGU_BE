package org.smartcampus.smartcampus_be.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins(
                "http://localhost:8080",
                "http://localhost:8081",
                "http://localhost:5173",
                "https://smart-campus-dgu.vercel.app" // 프론트엔드 배포 주소
            )
            .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
