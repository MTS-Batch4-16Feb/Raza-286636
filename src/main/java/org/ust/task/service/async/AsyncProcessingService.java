package org.ust.task.service.async;

import org.ust.task.dto.TaskDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.CommentDTO;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Interface for asynchronous task processing.
 * Provides concurrent and non-blocking operations using virtual threads and CompletableFuture.
 */
public interface AsyncProcessingService {

    /**
     * Asynchronously retrieve all tasks.
     */
    CompletableFuture<List<TaskDTO>> getAllTasksAsync();

    /**
     * Asynchronously retrieve tasks by project ID.
     */
    CompletableFuture<List<TaskDTO>> getTasksByProjectAsync(Long projectId);

    /**
     * Asynchronously retrieve tasks by assignee ID.
     */
    CompletableFuture<List<TaskDTO>> getTasksByAssigneeAsync(Long assigneeId);

    /**
     * Asynchronously retrieve all projects.
     */
    CompletableFuture<List<ProjectDTO>> getAllProjectsAsync();

    /**
     * Asynchronously retrieve projects by owner ID.
     */
    CompletableFuture<List<ProjectDTO>> getProjectsByOwnerAsync(Long ownerId);

    /**
     * Asynchronously retrieve comments for a task.
     */
    CompletableFuture<List<CommentDTO>> getCommentsByTaskAsync(Long taskId);

    /**
     * Asynchronously process batch of tasks.
     */
    CompletableFuture<Integer> processBatchTasksAsync(List<Long> taskIds);

    /**
     * Combine multiple async operations.
     */
    CompletableFuture<String> combineDataAsync(Long projectId, Long assigneeId);

    /**
     * Run task with virtual threads.
     */
    CompletableFuture<String> executeVirtualThreadTask(String taskName);
}

