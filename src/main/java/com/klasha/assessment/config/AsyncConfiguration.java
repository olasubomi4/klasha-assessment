package com.klasha.assessment.config;

import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableAsync
 public class AsyncConfiguration{

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(3); // Set the number of core threads
        executor.setMaxPoolSize(3); // Set the maximum number of threads
        executor.setQueueCapacity(25); // Set the queue capacity
        executor.setThreadNamePrefix("new thread");
        executor.initialize();
        return executor;
    }

}