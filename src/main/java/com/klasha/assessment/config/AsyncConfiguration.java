package com.klasha.assessment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
@Configuration
@EnableAsync
 public class AsyncConfiguration{
//
    @Value("${core.pool.size}")
    private int corePoolSize;
    @Value("${max.pool.size}")
    private int maxPoolSize;


    @Value("${queue.capacity}")
    private int queueCapacity;

    @Value("${thread.prefix}")
    private String threadPrefix;
    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix(threadPrefix);
        executor.initialize();
        return executor;
    }

}