package com.klasha.assessment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;

@Configuration

//public class AsyncConfiguration extends AsyncConfigurerSupport {
 public class AsyncConfiguration{

//    @Bean
//    public Executor taskExecutor()  {
//
//
//    }
//    @Autowired
//    private AsyncExceptionHandler asyncExceptionHandler;
    @Bean
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(14);
        executor.setMaxPoolSize(14);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Thread vibes");
        executor.initialize();
        return executor;
    }

//    @Override
//    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
//        return asyncExceptionHandler;
//    }
}