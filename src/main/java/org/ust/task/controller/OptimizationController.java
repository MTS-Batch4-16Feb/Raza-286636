package org.ust.task.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ust.task.dto.TaskDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.CommentDTO;
import org.ust.task.service.async.AsyncProcessingService;
import org.ust.task.service.concurrent.ConcurrentOperationService;
import org.ust.task.service.batch.BatchProcessingService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * REST API controller for demonstrating Day 3 & 4 optimizations.
 * Exposes endpoints for:
 * - Async/Virtual Thread operations
 * - Concurrent operations with various lock types
 * - Batch processing capabilities
 */
@RestController
@RequestMapping("/api/v1/optimization")
@RequiredArgsConstructor
@Slf4j
public class OptimizationController {

    private final AsyncProcessingService asyncProcessingService;
    private final ConcurrentOperationService concurrentOperationService;
    private final BatchProcessingService batchProcessingService;

    // ============= Async/Virtual Thread Endpoints =============

    /**
     * Get all tasks asynchronously using virtual threads.
     * Demonstrates non-blocking I/O with CompletableFuture.
     */
    @GetMapping("/async/tasks")
    public CompletableFuture<ResponseEntity<List<TaskDTO>>> getAllTasksAsync() {
        log.info("Async request for all tasks on thread: {}", Thread.currentThread().getName());
        return asyncProcessingService.getAllTasksAsync()
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Get tasks by project asynchronously.
     */
    @GetMapping("/async/tasks/project/{projectId}")
    public CompletableFuture<ResponseEntity<List<TaskDTO>>> getTasksByProjectAsync(
            @PathVariable Long projectId) {
        log.info("Async request for tasks in project {}", projectId);
        return asyncProcessingService.getTasksByProjectAsync(projectId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Get tasks by assignee asynchronously.
     */
    @GetMapping("/async/tasks/assignee/{assigneeId}")
    public CompletableFuture<ResponseEntity<List<TaskDTO>>> getTasksByAssigneeAsync(
            @PathVariable Long assigneeId) {
        log.info("Async request for tasks assigned to user {}", assigneeId);
        return asyncProcessingService.getTasksByAssigneeAsync(assigneeId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Get all projects asynchronously.
     */
    @GetMapping("/async/projects")
    public CompletableFuture<ResponseEntity<List<ProjectDTO>>> getAllProjectsAsync() {
        log.info("Async request for all projects");
        return asyncProcessingService.getAllProjectsAsync()
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Get projects by owner asynchronously.
     */
    @GetMapping("/async/projects/owner/{ownerId}")
    public CompletableFuture<ResponseEntity<List<ProjectDTO>>> getProjectsByOwnerAsync(
            @PathVariable Long ownerId) {
        log.info("Async request for projects owned by user {}", ownerId);
        return asyncProcessingService.getProjectsByOwnerAsync(ownerId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Get comments for a task asynchronously.
     */
    @GetMapping("/async/comments/task/{taskId}")
    public CompletableFuture<ResponseEntity<List<CommentDTO>>> getCommentsByTaskAsync(
            @PathVariable Long taskId) {
        log.info("Async request for comments on task {}", taskId);
        return asyncProcessingService.getCommentsByTaskAsync(taskId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Combine multiple async operations.
     */
    @GetMapping("/async/combined")
    public CompletableFuture<ResponseEntity<String>> getCombinedDataAsync(
            @RequestParam Long projectId,
            @RequestParam Long assigneeId) {
        log.info("Async combined request for project {} and assignee {}", projectId, assigneeId);
        return asyncProcessingService.combineDataAsync(projectId, assigneeId)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    /**
     * Execute task on virtual thread.
     */
    @PostMapping("/async/virtual-thread")
    public CompletableFuture<ResponseEntity<String>> executeVirtualThreadTask(
            @RequestParam String taskName) {
        log.info("Executing virtual thread task: {}", taskName);
        return asyncProcessingService.executeVirtualThreadTask(taskName)
                .thenApply(ResponseEntity::ok)
                .exceptionally(e -> ResponseEntity.status(500).build());
    }

    // ============= Concurrent Operations Endpoints =============

    /**
     * Increment thread-safe counter.
     */
    @PostMapping("/concurrent/counter/increment")
    public ResponseEntity<Integer> incrementCounter() {
        log.info("Incrementing counter");
        int value = concurrentOperationService.incrementCounter();
        return ResponseEntity.ok(value);
    }

    /**
     * Read counter value with read lock (concurrent reads).
     */
    @GetMapping("/concurrent/counter/read")
    public ResponseEntity<Integer> readCounter() {
        log.info("Reading counter");
        int value = concurrentOperationService.readCounter();
        return ResponseEntity.ok(value);
    }

    /**
     * Fast read counter with optimistic locking.
     */
    @GetMapping("/concurrent/counter/fast-read")
    public ResponseEntity<Integer> fastReadCounter() {
        log.info("Fast reading counter with stamped lock");
        int value = concurrentOperationService.fastReadCounter();
        return ResponseEntity.ok(value);
    }

    /**
     * Reset counter.
     */
    @PostMapping("/concurrent/counter/reset")
    public ResponseEntity<String> resetCounter() {
        log.info("Resetting counter");
        concurrentOperationService.resetCounter();
        return ResponseEntity.ok("Counter reset");
    }

    /**
     * Put value in thread-safe cache.
     */
    @PostMapping("/concurrent/cache/put")
    public ResponseEntity<String> putInCache(
            @RequestParam String key,
            @RequestParam String value) {
        log.info("Putting in cache - Key: {}", key);
        concurrentOperationService.putInCache(key, value);
        return ResponseEntity.ok("Value cached");
    }

    /**
     * Get value from thread-safe cache.
     */
    @GetMapping("/concurrent/cache/get/{key}")
    public ResponseEntity<Object> getFromCache(@PathVariable String key) {
        log.info("Getting from cache - Key: {}", key);
        Object value = concurrentOperationService.getFromCache(key);
        return value != null ? ResponseEntity.ok(value) : ResponseEntity.notFound().build();
    }

    /**
     * Get cache statistics.
     */
    @GetMapping("/concurrent/cache/size")
    public ResponseEntity<Integer> getCacheSize() {
        int size = concurrentOperationService.getCacheSize();
        return ResponseEntity.ok(size);
    }

    /**
     * Get lock statistics.
     */
    @GetMapping("/concurrent/statistics")
    public ResponseEntity<String> getLockStatistics() {
        String stats = concurrentOperationService.getLockStatistics();
        return ResponseEntity.ok(stats);
    }

    /**
     * Try increment with timeout.
     */
    @PostMapping("/concurrent/counter/try-increment")
    public ResponseEntity<Boolean> tryIncrementWithTimeout(
            @RequestParam(defaultValue = "1000") Long timeoutMillis) {
        log.info("Trying increment with timeout: {}ms", timeoutMillis);
        boolean success = concurrentOperationService.tryIncrementWithTimeout(timeoutMillis);
        return ResponseEntity.ok(success);
    }

    // ============= Batch Processing Endpoints =============

    /**
     * Batch create tasks.
     */
    @PostMapping("/batch/tasks/create")
    public ResponseEntity<Integer> batchCreateTasks(
            @RequestParam Long projectId,
            @RequestParam(defaultValue = "20") int taskCount) {
        log.info("Batch creating {} tasks for project {}", taskCount, projectId);
        int created = batchProcessingService.batchCreateTasks(projectId, taskCount);
        return ResponseEntity.ok(created);
    }

    /**
     * Batch update task status.
     */
    @PutMapping("/batch/tasks/status")
    public ResponseEntity<Integer> batchUpdateTaskStatus(
            @RequestParam Long projectId,
            @RequestParam String status) {
        log.info("Batch updating task status for project {}", projectId);
        int updated = batchProcessingService.batchUpdateTaskStatus(projectId,
                org.ust.task.entity.TaskStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(updated);
    }

    /**
     * Batch unassign tasks.
     */
    @PutMapping("/batch/tasks/unassign")
    public ResponseEntity<Integer> batchUnassignTasks(
            @RequestParam Long assigneeId) {
        log.info("Batch unassigning tasks from user {}", assigneeId);
        int unassigned = batchProcessingService.batchUnassignTasks(assigneeId);
        return ResponseEntity.ok(unassigned);
    }

    /**
     * Batch delete tasks.
     */
    @DeleteMapping("/batch/tasks/delete")
    public ResponseEntity<Integer> batchDeleteTasks(
            @RequestParam Long projectId) {
        log.info("Batch deleting tasks for project {}", projectId);
        int deleted = batchProcessingService.batchDeleteTasks(projectId);
        return ResponseEntity.ok(deleted);
    }

    /**
     * Complex batch operation.
     */
    @PostMapping("/batch/complex-operation")
    public ResponseEntity<BatchProcessingService.BatchOperationStats> performComplexBatchOperation(
            @RequestParam Long projectId) {
        log.info("Performing complex batch operation for project {}", projectId);
        BatchProcessingService.BatchOperationStats stats = 
                batchProcessingService.performComplexBatchOperation(projectId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Optimization features active - Day 3 & 4 complete");
    }
}

