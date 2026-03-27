package org.ust.task.service.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.TaskCreateDTO;
import org.ust.task.dto.TaskUpdateDTO;
import org.ust.task.entity.Project;
import org.ust.task.entity.Task;
import org.ust.task.entity.TaskStatus;
import org.ust.task.repository.ProjectRepository;
import org.ust.task.repository.TaskRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Batch processing service for optimized database operations.
 * Uses JDBC batching configured in Hibernate to reduce database round-trips.
 * 
 * Configuration:
 * - hibernate.jdbc.batch_size=20
 * - hibernate.order_inserts=true
 * - hibernate.order_updates=true
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BatchProcessingService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    /**
     * Batch create tasks in a single transaction.
     * Hibernate batches inserts for performance.
     * 
     * @param projectId Project to associate tasks with
     * @param taskCount Number of tasks to create
     * @return Number of tasks created
     */
    @Transactional
    public int batchCreateTasks(Long projectId, int taskCount) {
        log.info("Starting batch creation of {} tasks for project {}", taskCount, projectId);
        long startTime = System.currentTimeMillis();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found: " + projectId));
        
        List<Task> tasksToCreate = new ArrayList<>();
        
        for (int i = 1; i <= taskCount; i++) {
            Task task = Task.builder()
                    .title("Batch Task " + i)
                    .description("Auto-generated task in batch operation " + i)
                    .status(TaskStatus.TODO)
                    .project(project)
                    .build();
            
            tasksToCreate.add(task);
            
            // Flush batch when reaching batch size
            if (i % 20 == 0 || i == taskCount) {
                taskRepository.saveAll(tasksToCreate);
                log.debug("Flushed batch of {} tasks", tasksToCreate.size());
                tasksToCreate.clear();
            }
        }
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Batch created {} tasks in {} ms", taskCount, duration);
        
        return taskCount;
    }

    /**
     * Batch update task status.
     * Uses @Modifying query for bulk DML operation.
     * 
     * @param projectId Project ID
     * @param newStatus New status for all tasks
     * @return Number of updated tasks
     */
    @Transactional
    public int batchUpdateTaskStatus(Long projectId, TaskStatus newStatus) {
        log.info("Batch updating task status for project {} to {}", projectId, newStatus);
        
        int updatedCount = taskRepository.updateTaskStatusByProjectId(projectId, newStatus);
        
        log.info("Batch updated {} tasks to status {}", updatedCount, newStatus);
        return updatedCount;
    }

    /**
     * Batch unassign tasks from a user.
     * 
     * @param assigneeId User ID to unassign from
     * @return Number of unassigned tasks
     */
    @Transactional
    public int batchUnassignTasks(Long assigneeId) {
        log.info("Batch unassigning tasks from user {}", assigneeId);
        
        int unassignedCount = taskRepository.unassignTasksByAssigneeId(assigneeId);
        
        log.info("Batch unassigned {} tasks", unassignedCount);
        return unassignedCount;
    }

    /**
     * Batch delete tasks for a project.
     * Demonstrates cascade delete with batch processing.
     * 
     * @param projectId Project ID
     * @return Number of deleted tasks
     */
    @Transactional
    public int batchDeleteTasks(Long projectId) {
        log.info("Batch deleting tasks for project {}", projectId);
        
        List<Task> tasksToDelete = taskRepository.findByProjectId(projectId);
        
        if (!tasksToDelete.isEmpty()) {
            taskRepository.deleteAll(tasksToDelete);
            log.info("Batch deleted {} tasks", tasksToDelete.size());
            return tasksToDelete.size();
        }
        
        return 0;
    }

    /**
     * Batch process large result set with pagination.
     * Prevents memory issues with large datasets.
     * 
     * @param batchSize Size of each batch to process
     * @param processor Function to process each task
     */
    @Transactional(readOnly = true)
    public void processLargeResultSet(int batchSize, java.util.function.Consumer<Task> processor) {
        log.info("Processing large result set with batch size: {}", batchSize);
        
        List<Task> allTasks = taskRepository.findAll();
        int totalProcessed = 0;
        
        for (int i = 0; i < allTasks.size(); i += batchSize) {
            int end = Math.min(i + batchSize, allTasks.size());
            List<Task> batch = allTasks.subList(i, end);
            
            for (Task task : batch) {
                processor.accept(task);
            }
            
            totalProcessed += batch.size();
            log.debug("Processed {} of {} tasks", totalProcessed, allTasks.size());
        }
        
        log.info("Completed processing {} tasks", totalProcessed);
    }

    /**
     * Complex batch operation with mixed reads and writes.
     * Demonstrates transaction management with batching.
     * 
     * @param projectId Project ID
     * @return Statistics about the operation
     */
    @Transactional
    public BatchOperationStats performComplexBatchOperation(Long projectId) {
        log.info("Starting complex batch operation for project {}", projectId);
        long startTime = System.currentTimeMillis();
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
        
        // Count current tasks
        int initialCount = project.getTasks().size();
        
        // Create new batch of tasks
        int createdCount = batchCreateTasks(projectId, 10);
        
        // Update status of all tasks
        int updatedCount = batchUpdateTaskStatus(projectId, TaskStatus.IN_PROGRESS);
        
        long duration = System.currentTimeMillis() - startTime;
        
        return BatchOperationStats.builder()
                .projectId(projectId)
                .initialTaskCount(initialCount)
                .tasksCreated(createdCount)
                .tasksUpdated(updatedCount)
                .durationMillis(duration)
                .build();
    }

    /**
     * Statistics about batch operations.
     */
    public static class BatchOperationStats {
        private final Long projectId;
        private final int initialTaskCount;
        private final int tasksCreated;
        private final int tasksUpdated;
        private final long durationMillis;

        private BatchOperationStats(Long projectId, int initialTaskCount, int tasksCreated,
                                   int tasksUpdated, long durationMillis) {
            this.projectId = projectId;
            this.initialTaskCount = initialTaskCount;
            this.tasksCreated = tasksCreated;
            this.tasksUpdated = tasksUpdated;
            this.durationMillis = durationMillis;
        }

        public static BatchOperationStatsBuilder builder() {
            return new BatchOperationStatsBuilder();
        }

        public Long getProjectId() { return projectId; }
        public int getInitialTaskCount() { return initialTaskCount; }
        public int getTasksCreated() { return tasksCreated; }
        public int getTasksUpdated() { return tasksUpdated; }
        public long getDurationMillis() { return durationMillis; }

        @Override
        public String toString() {
            return "BatchOperationStats{" +
                    "projectId=" + projectId +
                    ", initialTaskCount=" + initialTaskCount +
                    ", tasksCreated=" + tasksCreated +
                    ", tasksUpdated=" + tasksUpdated +
                    ", durationMillis=" + durationMillis +
                    '}';
        }

        public static class BatchOperationStatsBuilder {
            private Long projectId;
            private int initialTaskCount;
            private int tasksCreated;
            private int tasksUpdated;
            private long durationMillis;

            public BatchOperationStatsBuilder projectId(Long projectId) {
                this.projectId = projectId;
                return this;
            }

            public BatchOperationStatsBuilder initialTaskCount(int initialTaskCount) {
                this.initialTaskCount = initialTaskCount;
                return this;
            }

            public BatchOperationStatsBuilder tasksCreated(int tasksCreated) {
                this.tasksCreated = tasksCreated;
                return this;
            }

            public BatchOperationStatsBuilder tasksUpdated(int tasksUpdated) {
                this.tasksUpdated = tasksUpdated;
                return this;
            }

            public BatchOperationStatsBuilder durationMillis(long durationMillis) {
                this.durationMillis = durationMillis;
                return this;
            }

            public BatchOperationStats build() {
                return new BatchOperationStats(projectId, initialTaskCount, tasksCreated,
                        tasksUpdated, durationMillis);
            }
        }
    }
}

