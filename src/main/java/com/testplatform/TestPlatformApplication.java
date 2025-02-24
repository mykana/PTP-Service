package com.testplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 测试平台主应用程序入口
 * 
 * @EnableAsync: 启用异步任务支持
 * @EnableTransactionManagement: 启用事务管理
 admin_user  123456789
 */
@SpringBootApplication
@EnableAsync
@EnableTransactionManagement
public class TestPlatformApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TestPlatformApplication.class, args);
    }
} 