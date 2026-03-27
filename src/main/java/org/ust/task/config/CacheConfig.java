package org.ust.task.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.SimpleCacheResolver;

/**
 * Spring Cache Configuration for application-level caching.
 * Enables Spring's @Cacheable and related annotations.
 * Works in conjunction with Hibernate's second-level cache.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Define cache manager for Spring cache abstraction.
     * Uses ConcurrentMapCacheManager for in-memory caching.
     * In production, consider using EhCache or Redis.
     */
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
                "task",
                "tasks",
                "project",
                "projects",
                "comment",
                "comments",
                "user",
                "users",
                "tasksByProject",
                "tasksByAssignee",
                "projectsByOwner",
                "commentsByTask"
        );
    }

    /**
     * Configure cache resolver for advanced cache handling.
     */
    @Bean
    public CacheResolver cacheResolver(CacheManager cacheManager) {
        return new SimpleCacheResolver(cacheManager);
    }
}

