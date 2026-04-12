package com.example.my_api_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
public class AsyncConfig {

    @Bean("ioExecutor")
    public ExecutorService ioExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean("cpuExecutor")
    public ExecutorService cpuExecutor() {
        int coreCount = Runtime.getRuntime().availableProcessors();
        return Executors.newFixedThreadPool(coreCount);
    }
}
