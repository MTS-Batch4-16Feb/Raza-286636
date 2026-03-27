package org.ust.task.util;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

/**
 * Concurrency utilities providing thread-safe operations using various locking strategies.
 * Demonstrates best practices for synchronization and lock management.
 */
@Slf4j
public class ConcurrencyUtils {

    /**
     * Simple ReentrantLock wrapper for mutual exclusion.
     * Ensures only one thread can access a critical section at a time.
     */
    public static class ReentrantLockUtil {
        private final ReentrantLock lock = new ReentrantLock();

        /**
         * Execute code with mutual exclusion.
         */
        public <T> T executeWithLock(Supplier<T> supplier) {
            lock.lock();
            try {
                log.debug("Acquired ReentrantLock in thread: {}", Thread.currentThread().getName());
                return supplier.get();
            } finally {
                lock.unlock();
                log.debug("Released ReentrantLock in thread: {}", Thread.currentThread().getName());
            }
        }

        /**
         * Execute code with timeout (tryLock pattern).
         */
        public <T> T executeWithTimeout(Supplier<T> supplier, long timeoutMillis) throws InterruptedException {
            if (lock.tryLock(timeoutMillis, java.util.concurrent.TimeUnit.MILLISECONDS)) {
                try {
                    return supplier.get();
                } finally {
                    lock.unlock();
                }
            } else {
                throw new InterruptedException("Failed to acquire lock within timeout");
            }
        }

        /**
         * Get lock hold count (for debugging).
         */
        public int getLockHoldCount() {
            return lock.getHoldCount();
        }
    }

    /**
     * ReadWriteLock wrapper for scenarios with multiple readers and occasional writers.
     * Optimizes throughput when reads significantly outnumber writes.
     */
    public static class ReadWriteLockUtil {
        private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

        /**
         * Execute read operation (multiple threads can read concurrently).
         */
        public <T> T executeRead(Supplier<T> supplier) {
            rwLock.readLock().lock();
            try {
                log.debug("Acquired read lock in thread: {}", Thread.currentThread().getName());
                return supplier.get();
            } finally {
                rwLock.readLock().unlock();
                log.debug("Released read lock in thread: {}", Thread.currentThread().getName());
            }
        }

        /**
         * Execute write operation (exclusive access).
         */
        public <T> T executeWrite(Supplier<T> supplier) {
            rwLock.writeLock().lock();
            try {
                log.debug("Acquired write lock in thread: {}", Thread.currentThread().getName());
                return supplier.get();
            } finally {
                rwLock.writeLock().unlock();
                log.debug("Released write lock in thread: {}", Thread.currentThread().getName());
            }
        }
    }

    /**
     * StampedLock wrapper for optimistic locking scenarios.
     * Better performance than ReadWriteLock in high-contention scenarios.
     * Supports optimistic reads without holding locks.
     */
    public static class StampedLockUtil {
        private final StampedLock stampedLock = new StampedLock();

        /**
         * Execute read operation with optimistic locking.
         * Retries if concurrent modification detected.
         */
        public <T> T executeOptimisticRead(Supplier<T> supplier) {
            long stamp = stampedLock.tryOptimisticRead();
            try {
                T result = supplier.get();
                if (!stampedLock.validate(stamp)) {
                    log.debug("Optimistic read failed, retrying with pessimistic read");
                    // Retry with pessimistic read
                    stamp = stampedLock.readLock();
                    try {
                        return supplier.get();
                    } finally {
                        stampedLock.unlockRead(stamp);
                    }
                }
                return result;
            } catch (Exception e) {
                log.error("Error in optimistic read", e);
                throw e;
            }
        }

        /**
         * Execute read operation with pessimistic locking.
         */
        public <T> T executePessimisticRead(Supplier<T> supplier) {
            long stamp = stampedLock.readLock();
            try {
                log.debug("Acquired stamped read lock in thread: {}", Thread.currentThread().getName());
                return supplier.get();
            } finally {
                stampedLock.unlockRead(stamp);
                log.debug("Released stamped read lock in thread: {}", Thread.currentThread().getName());
            }
        }

        /**
         * Execute write operation with exclusive access.
         */
        public <T> T executeWrite(Supplier<T> supplier) {
            long stamp = stampedLock.writeLock();
            try {
                log.debug("Acquired stamped write lock in thread: {}", Thread.currentThread().getName());
                return supplier.get();
            } finally {
                stampedLock.unlockWrite(stamp);
                log.debug("Released stamped write lock in thread: {}", Thread.currentThread().getName());
            }
        }

        /**
         * Convert read lock to write lock if needed.
         */
        public <T> T executeConvertibleRead(Supplier<T> supplier, Supplier<T> writeSupplier, boolean needsWrite) {
            long stamp = stampedLock.readLock();
            try {
                T result = supplier.get();
                if (needsWrite) {
                    long writeStamp = stampedLock.tryConvertToWriteLock(stamp);
                    if (writeStamp != 0L) {
                        stamp = writeStamp;
                        // Execute write operation
                        return writeSupplier.get();
                    } else {
                        stampedLock.unlockRead(stamp);
                        stamp = stampedLock.writeLock();
                        return writeSupplier.get();
                    }
                }
                return result;
            } finally {
                stampedLock.unlock(stamp);
            }
        }
    }

    /**
     * Thread-safe counter using ReentrantLock.
     */
    public static class ThreadSafeCounter {
        private int count = 0;
        private final ReentrantLock lock = new ReentrantLock();

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public void decrement() {
            lock.lock();
            try {
                count--;
            } finally {
                lock.unlock();
            }
        }

        public int getValue() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }

        public void reset() {
            lock.lock();
            try {
                count = 0;
            } finally {
                lock.unlock();
            }
        }
    }

    /**
     * Thread-safe cache using StampedLock for high-performance access.
     */
    public static class ThreadSafeCache<K, V> {
        private final java.util.Map<K, V> cache = new java.util.HashMap<>();
        private final StampedLock lock = new StampedLock();

        public V get(K key) {
            long stamp = lock.tryOptimisticRead();
            V value = cache.get(key);
            if (lock.validate(stamp)) {
                return value;
            }
            stamp = lock.readLock();
            try {
                return cache.get(key);
            } finally {
                lock.unlockRead(stamp);
            }
        }

        public void put(K key, V value) {
            long stamp = lock.writeLock();
            try {
                cache.put(key, value);
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        public void remove(K key) {
            long stamp = lock.writeLock();
            try {
                cache.remove(key);
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        public int size() {
            long stamp = lock.tryOptimisticRead();
            int size = cache.size();
            if (lock.validate(stamp)) {
                return size;
            }
            stamp = lock.readLock();
            try {
                return cache.size();
            } finally {
                lock.unlockRead(stamp);
            }
        }
    }

    /**
     * Deadlock-free account transfer using ordered locking.
     * Always acquires locks in the same order to prevent deadlocks.
     */
    public static class OrderedLockingUtil {
        public static <T> void safeTransfer(Object account1, Object account2, Runnable operation) {
            Object first = account1.hashCode() < account2.hashCode() ? account1 : account2;
            Object second = first == account1 ? account2 : account1;

            synchronized (first) {
                synchronized (second) {
                    operation.run();
                }
            }
        }
    }
}

