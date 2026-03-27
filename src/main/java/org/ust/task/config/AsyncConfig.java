package org.ust.task.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Async Configuration for Java 21 Virtual Threads and Traditional Thread Pools.
 * Enables @Async annotation processing and configures virtual thread executors.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Virtual Thread Executor - uses Java 21 virtual threads for lightweight concurrency.
     * Virtual threads are ideal for I/O-bound tasks and high-throughput scenarios.
     * 
     * Since Spring Boot 3.2+, this uses the Project Loom virtual threads API.
     * Each virtual thread is extremely lightweight compared to platform threads.
     */
    @Bean(name = "virtualThreadExecutor")
    public Executor virtualThreadExecutor() {
        return new VirtualThreadExecutor();
    }

    /**
     * Traditional ThreadPoolTaskExecutor for CPU-bound or critical tasks.
     * Uses a fixed pool of platform threads.
     */
    @Bean(name = "platformThreadExecutor")
    public Executor platformThreadExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("platform-executor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        executor.initialize();
        return executor;
    }

    /**
     * Default async executor using virtual threads.
     */
    @Bean(name = "asyncExecutor")
    public Executor asyncExecutor() {
        return virtualThreadExecutor();
    }

    /**
     * Custom Virtual Thread Executor implementation.
     * Wraps Java 21's ExecutorService.newVirtualThreadPerTaskExecutor()
     */
    static class VirtualThreadExecutor implements Executor {
        private final java.util.concurrent.ExecutorService executorService;

        public VirtualThreadExecutor() {
            // Java 21: Create an executor that spawns a new virtual thread per task
            this.executorService = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor();
        }

        @Override
        public void execute(Runnable command) {
            executorService.submit(command);
        }
    }
}

