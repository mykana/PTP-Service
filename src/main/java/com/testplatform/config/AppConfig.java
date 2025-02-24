package com.testplatform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序配置类
 */
@Configuration
@EnableJpaAuditing
@EnableScheduling
public class AppConfig {
    // 这里可以添加其他配置Bean
} 