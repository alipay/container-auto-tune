/*
 * Ant Group
 * Copyright (c) 2004-2022 All Rights Reserved.
 */
package com.alipay.autotuneservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;

/**
 * @author dutianze
 * @version AsyncConfig.java, v 0.1 2022年03月15日 18:51 dutianze
 */
@Configuration
@EnableAsync
@EnableScheduling
@Slf4j
public class AsyncConfig implements AsyncConfigurer {

    @Override
    @Bean(name = "webTaskExecutor")
    public AsyncTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("webTaskExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The webTaskExecutor is discarded"));
        executor.initialize();
        return executor;
    }

    @Bean(name = "podEventExecutor")
    public AsyncTaskExecutor podEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(200);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("podEventExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The podEventExecutor is discarded"));
        executor.setThreadNamePrefix("podEventExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The podEventExecutor is discarded"));
        executor.initialize();
        return executor;
    }

    @Bean(name = "grpcExecutor")
    public Executor grpcExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("grpcExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The grpcExecutor is discarded"));
        executor.initialize();
        return executor;
    }

    @Bean(name = "dynamoDBTaskExecutor")
    public Executor dynamoDBTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("dynamoDBTaskExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The dynamoDBTaskExecutor is discarded"));
        executor.initialize();
        return executor;
    }

    @Bean(name = "subExecutor")
    public AsyncTaskExecutor subExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("subExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The subExecutor is discarded"));
        executor.initialize();
        return executor;
    }

    @Bean(name = "eventExecutor")
    public AsyncTaskExecutor eventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(40);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("eventExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The eventExecutor is discarded"));
        executor.setThreadNamePrefix("eventExecutor-");
        executor.setRejectedExecutionHandler((r, executor1) -> log.info("The eventExecutor is discarded"));
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    @Bean
    public WebMvcConfigurer webMvcConfigurerConfigurer(AsyncTaskExecutor webTaskExecutor,
                                                       CallableProcessingInterceptor callableProcessingInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
                configurer.setDefaultTimeout(3600000).setTaskExecutor(webTaskExecutor);
                configurer.registerCallableInterceptors(callableProcessingInterceptor);
                WebMvcConfigurer.super.configureAsyncSupport(configurer);
            }
        };
    }

    @Bean
    public CallableProcessingInterceptor callableProcessingInterceptor() {
        return new TimeoutCallableProcessingInterceptor() {
            @Override
            public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
                return super.handleTimeout(request, task);
            }
        };
    }
}