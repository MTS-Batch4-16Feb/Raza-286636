package org.ust.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration class to define a custom ThreadPoolExecutor.
 */
@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // Set the core pool size
        executor.setMaxPoolSize(10); // Set the max pool size
        executor.setQueueCapacity(100); // Set the queue capacity
        executor.setThreadNamePrefix("async-task-"); // Set the name prefix for threads
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy()); // Handle task rejection
        executor.initialize();
        return executor;
    }
}