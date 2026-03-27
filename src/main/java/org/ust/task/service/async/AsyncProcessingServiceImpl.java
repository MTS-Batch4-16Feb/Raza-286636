package org.ust.task.service.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.CommentDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.TaskDTO;
import org.ust.task.mapper.CommentMapper;
import org.ust.task.mapper.ProjectMapper;
import org.ust.task.mapper.TaskMapper;
import org.ust.task.repository.CommentRepository;
import org.ust.task.repository.ProjectRepository;
import org.ust.task.repository.TaskRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Asynchronous processing service using CompletableFuture and virtual threads.
 * Implements non-blocking, concurrent operations for improved throughput.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AsyncProcessingServiceImpl implements AsyncProcessingService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final CommentRepository commentRepository;
    private final TaskMapper taskMapper;
    private final ProjectMapper projectMapper;
    private final CommentMapper commentMapper;

    /**
     * Asynchronously retrieve all tasks using virtual threads.
     * This method runs on a virtual thread executor.
     */
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<TaskDTO>> getAllTasksAsync() {
        log.info("Fetching all tasks asynchronously on virtual thread: {}", Thread.currentThread().getName());
        return CompletableFuture.supplyAsync(() ->
                taskRepository.findAllWithRelations()
                        .stream()
                        .map(taskMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Asynchronously retrieve tasks by project ID.
     */
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<TaskDTO>> getTasksByProjectAsync(Long projectId) {
        log.info("Fetching tasks for project {} asynchronously", projectId);
        return CompletableFuture.supplyAsync(() ->
                taskRepository.findByProjectIdWithComments(projectId)
                        .stream()
                        .map(taskMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Asynchronously retrieve tasks by assignee ID.
     */
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<TaskDTO>> getTasksByAssigneeAsync(Long assigneeId) {
        log.info("Fetching tasks for assignee {} asynchronously", assigneeId);
        return CompletableFuture.supplyAsync(() ->
                taskRepository.findByAssigneeIdWithProject(assigneeId)
                        .stream()
                        .map(taskMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Asynchronously retrieve all projects.
     */
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<ProjectDTO>> getAllProjectsAsync() {
        log.info("Fetching all projects asynchronously");
        return CompletableFuture.supplyAsync(() ->
                projectRepository.findAll()
                        .stream()
                        .map(projectMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Asynchronously retrieve projects by owner ID.
     */
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<ProjectDTO>> getProjectsByOwnerAsync(Long ownerId) {
        log.info("Fetching projects for owner {} asynchronously", ownerId);
        return CompletableFuture.supplyAsync(() ->
                projectRepository.findByOwnerIdWithTasks(ownerId)
                        .stream()
                        .map(projectMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Asynchronously retrieve comments for a task.
     */
    @Override
    @Async("virtualThreadExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<List<CommentDTO>> getCommentsByTaskAsync(Long taskId) {
        log.info("Fetching comments for task {} asynchronously", taskId);
        return CompletableFuture.supplyAsync(() ->
                commentRepository.findByTaskIdWithUser(taskId)
                        .stream()
                        .map(commentMapper::toDTO)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Asynchronously process batch of tasks.
     * Demonstrates concurrent processing of multiple tasks.
     */
    @Override
    @Async("virtualThreadExecutor")
    public CompletableFuture<Integer> processBatchTasksAsync(List<Long> taskIds) {
        log.info("Processing batch of {} tasks asynchronously", taskIds.size());
        
        return CompletableFuture.supplyAsync(() -> {
            int processedCount = 0;
            for (Long taskId : taskIds) {
                try {
                    // Simulate processing time
                    Thread.sleep(100);
                    processedCount++;
                    log.debug("Processed task: {}", taskId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("Batch processing interrupted", e);
                    break;
                }
            }
            log.info("Batch processing completed: {} tasks processed", processedCount);
            return processedCount;
        });
    }

    /**
     * Combine multiple async operations using thenCombine.
     * Demonstrates composing CompletableFutures.
     */
    @Override
    public CompletableFuture<String> combineDataAsync(Long projectId, Long assigneeId) {
        log.info("Combining data for project {} and assignee {}", projectId, assigneeId);
        
        CompletableFuture<List<TaskDTO>> projectTasksFuture = getTasksByProjectAsync(projectId);
        CompletableFuture<List<TaskDTO>> assigneeTasksFuture = getTasksByAssigneeAsync(assigneeId);

        return projectTasksFuture.thenCombine(assigneeTasksFuture, (projectTasks, assigneeTasks) -> {
            int totalTasks = projectTasks.size() + assigneeTasks.size();
            return String.format("Project has %d tasks, Assignee has %d tasks, Total: %d",
                    projectTasks.size(), assigneeTasks.size(), totalTasks);
        });
    }

    /**
     * Execute a task using virtual threads with simulated work.
     */
    @Override
    @Async("virtualThreadExecutor")
    public CompletableFuture<String> executeVirtualThreadTask(String taskName) {
        log.info("Executing virtual thread task: {} on thread: {}", taskName, Thread.currentThread().getName());
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate work
                Thread.sleep(500);
                String result = String.format("Task '%s' completed on virtual thread: %s",
                        taskName, Thread.currentThread().getName());
                log.info(result);
                return result;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Task interrupted: " + taskName;
            }
        });
    }
}

