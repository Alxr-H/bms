package com.hhz.bms.bms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class AsyncExecutorConfig {

    @Bean(name = "warnExecutor")
    public Executor warnExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);  // 核心线程数
        executor.setMaxPoolSize(32);   // 最大线程数
        executor.setQueueCapacity(1000); // 队列容量
        executor.setThreadNamePrefix("warn-handler-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}

