package com.xreport.integration;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test Application Configuration
 * 用于集成测试的 Spring Boot 测试配置
 */
@SpringBootApplication
@EnableWebSecurity
@MapperScan("com.xreport.mapper")
@ComponentScan(
    basePackages = {"com.xreport"},
    excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE)
)
public class TestApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
    }

    /**
     * Test Security Configuration
     * 配置测试环境的安全设置
     */
    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c -> c.disable())
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/datasources/test/**").permitAll()
                .requestMatchers("/api/**").authenticated()
            );
        return http.build();
    }
}
