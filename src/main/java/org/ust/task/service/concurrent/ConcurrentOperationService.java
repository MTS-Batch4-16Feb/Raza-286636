package org.ust.task.service.concurrent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.ust.task.util.ConcurrencyUtils.*;

/**
 * Thread-safe concurrent service demonstrating various synchronization patterns.
 * Uses ReentrantLock, ReadWriteLock, and StampedLock for different scenarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConcurrentOperationService {

    private final ReentrantLockUtil reentrantLockUtil = new ReentrantLockUtil();
    private final ReadWriteLockUtil readWriteLockUtil = new ReadWriteLockUtil();
    private final StampedLockUtil stampedLockUtil = new StampedLockUtil();
    private final ThreadSafeCounter counter = new ThreadSafeCounter();
    private final ThreadSafeCache<String, Object> cache = new ThreadSafeCache<>();

    /**
     * Thread-safe counter increment using ReentrantLock.
     */
    public int incrementCounter() {
        return reentrantLockUtil.executeWithLock(() -> {
            counter.increment();
            return counter.getValue();
        });
    }

    /**
     * Thread-safe counter read using ReadWriteLock.
     * Multiple threads can read concurrently.
     */
    public int readCounter() {
        return readWriteLockUtil.executeRead(() -> {
            log.debug("Reading counter value: {}", counter.getValue());
            return counter.getValue();
        });
    }

    /**
     * Thread-safe counter reset using ReadWriteLock for write.
     */
    public void resetCounter() {
        readWriteLockUtil.executeWrite(() -> {
            counter.reset();
            log.info("Counter reset to 0");
            return null;
        });
    }

    /**
     * High-performance optimistic read using StampedLock.
     * Retries with pessimistic read if concurrent modification detected.
     */
    public int fastReadCounter() {
        return stampedLockUtil.executeOptimisticRead(() -> {
            log.debug("Fast reading counter value using stamped lock");
            return counter.getValue();
        });
    }

    /**
     * Cache put operation with write lock.
     */
    public void putInCache(String key, Object value) {
        cache.put(key, value);
        log.info("Put in cache - Key: {}, Cache size: {}", key, cache.size());
    }

    /**
     * Cache get operation with optimistic read.
     */
    public Object getFromCache(String key) {
        Object value = cache.get(key);
        log.debug("Get from cache - Key: {}, Found: {}", key, value != null);
        return value;
    }

    /**
     * Cache remove operation.
     */
    public void removeFromCache(String key) {
        cache.remove(key);
        log.info("Removed from cache - Key: {}, Cache size: {}", key, cache.size());
    }

    /**
     * Get cache size.
     */
    public int getCacheSize() {
        return cache.size();
    }

    /**
     * Demonstrate concurrent read operations with ReadWriteLock.
     * Multiple threads can read simultaneously.
     */
    public String demonstrateReadConcurrency(int iterations) {
        StringBuilder result = new StringBuilder();
        
        // Simulate multiple readers
        for (int i = 0; i < iterations; i++) {
            int value = readWriteLockUtil.executeRead(() -> {
                try {
                    Thread.sleep(10); // Simulate work
                    return counter.getValue();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return -1;
                }
            });
            result.append(value).append(" ");
        }
        
        log.info("Read concurrency demonstration completed");
        return result.toString();
    }

    /**
     * Demonstrate concurrent write operations with mutual exclusion.
     */
    public int demonstrateWriteConcurrency(int iterations) {
        for (int i = 0; i < iterations; i++) {
            readWriteLockUtil.executeWrite(() -> {
                try {
                    Thread.sleep(5); // Simulate work
                    counter.increment();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                return null;
            });
        }
        
        int finalValue = readWriteLockUtil.executeRead(counter::getValue);
        log.info("Write concurrency demonstration completed. Final counter: {}", finalValue);
        return finalValue;
    }

    /**
     * Demonstrate ReentrantLock timeout behavior.
     */
    public boolean tryIncrementWithTimeout(long timeoutMillis) {
        try {
            reentrantLockUtil.executeWithTimeout(() -> {
                counter.increment();
                return counter.getValue();
            }, timeoutMillis);
            return true;
        } catch (InterruptedException e) {
            log.warn("Failed to acquire lock within timeout: {}ms", timeoutMillis);
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Execute critical section with lock hold count monitoring.
     */
    public int executeCriticalSection(Runnable criticalCode) {
        return reentrantLockUtil.executeWithLock(() -> {
            int holdCount = reentrantLockUtil.getLockHoldCount();
            log.debug("Executing critical section. Lock hold count: {}", holdCount);
            criticalCode.run();
            return holdCount;
        });
    }

    /**
     * Deadlock-free transfer pattern (example).
     */
    public void performSafeTransfer(String from, String to, int amount) {
        Object account1 = from.intern();
        Object account2 = to.intern();
        
        OrderedLockingUtil.safeTransfer(account1, account2, () -> {
            log.info("Transfer {} -> {}: {} units", from, to, amount);
            // Perform transfer logic here
        });
    }

    /**
     * Get lock statistics for monitoring.
     */
    public String getLockStatistics() {
        return String.format(
                "Counter: %d, Cache Size: %d, Lock Hold Count: %d",
                counter.getValue(),
                cache.size(),
                reentrantLockUtil.getLockHoldCount()
        );
    }
}

