package org.ust.task.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;
import org.ust.task.dto.TaskCreateDTO;
import org.ust.task.dto.TaskDTO;
import org.ust.task.dto.TaskUpdateDTO;
import org.ust.task.entity.TaskStatus;
import org.ust.task.response.ApiResponse;
import org.ust.task.service.CommentServiceInterface;
import org.ust.task.service.TaskServiceInterface;

import java.util.List;

/**
 * REST Controller for Task management endpoints.
 * Handles all HTTP requests related to task operations and task comments.
 */
@RestController
@RequestMapping("/api/v1/tasks")
@Tag(name = "Task Management", description = "Endpoints for managing tasks and task comments")
@RequiredArgsConstructor
public class TaskController {
    
    private final TaskServiceInterface taskService;
    private final CommentServiceInterface commentService;
    
    /**
     * Retrieve all tasks with optional filtering.
     *
     * @param assigneeId Filter by assignee ID (optional)
     * @param projectId Filter by project ID (optional)
     * @param status Filter by task status (optional)
     * @param priority Filter by task priority (optional)
     * @return List of filtered tasks
     */
    @GetMapping
    @Operation(summary = "Get all tasks", description = "Retrieves all tasks with optional filtering by assignee, project, status, and priority")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getAllTasks(
            @Parameter(description = "Assignee ID") @RequestParam(required = false) Long assigneeId,
            @Parameter(description = "Project ID") @RequestParam(required = false) Long projectId,
            @Parameter(description = "Task status") @RequestParam(required = false) String status,
            @Parameter(description = "Task priority") @RequestParam(required = false) String priority) {
        
        List<TaskDTO> tasks = taskService.getAllTasks(assigneeId, projectId, status, priority);
        ApiResponse<List<TaskDTO>> response = ApiResponse.success(tasks, 
            String.format("Retrieved %d tasks", tasks.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve task by ID.
     *
     * @param id Task ID
     * @return Task data
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Retrieves a task by its ID")
    public ResponseEntity<ApiResponse<TaskDTO>> getTaskById(@Parameter(description = "Task ID") @PathVariable Long id) {
        TaskDTO task = taskService.getTaskById(id);
        ApiResponse<TaskDTO> response = ApiResponse.success(task);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Create a new task.
     *
     * @param createDTO Task creation data
     * @return Created task response with 201 status
     */
    @PostMapping
    @Operation(summary = "Create task", description = "Creates a new task")
    public ResponseEntity<ApiResponse<TaskDTO>> createTask(@Valid @RequestBody TaskCreateDTO createDTO) {
        TaskDTO createdTask = taskService.createTask(createDTO);
        ApiResponse<TaskDTO> response = ApiResponse.success(createdTask, "Task created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Update entire task with new data.
     *
     * @param id Task ID to update
     * @param updateDTO Update data
     * @return Updated task data
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update task", description = "Updates all fields of a task")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskFull(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskCreateDTO updateDTO) {
        
        TaskDTO updatedTask = taskService.updateTaskFull(id, updateDTO);
        ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task updated successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Partially update task with selected fields.
     *
     * @param id Task ID to update
     * @param updateDTO Update data (only non-null fields are updated)
     * @return Updated task data
     */
    @PatchMapping("/{id}")
    @Operation(summary = "Partially update task", description = "Updates only the provided fields of a task")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody TaskUpdateDTO updateDTO) {
        
        TaskDTO updatedTask = taskService.updateTask(id, updateDTO);
        ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task partially updated");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Update task status.
     *
     * @param id Task ID
     * @param status New task status
     * @return Updated task data
     */
    @PatchMapping("/{id}/status")
    @Operation(summary = "Update task status", description = "Updates the status of a task")
    public ResponseEntity<ApiResponse<TaskDTO>> updateTaskStatus(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "New task status") @RequestParam TaskStatus status) {
        
        TaskDTO updatedTask = taskService.updateTaskStatus(id, status);
        ApiResponse<TaskDTO> response = ApiResponse.success(updatedTask, "Task status updated");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete task by ID.
     *
     * @param id Task ID to delete
     * @return Success message
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task", description = "Deletes a task by its ID")
    public ResponseEntity<ApiResponse<Void>> deleteTask(@Parameter(description = "Task ID") @PathVariable Long id) {
        taskService.deleteTask(id);
        ApiResponse<Void> response = ApiResponse.success("Task deleted successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Retrieve all comments for a task.
     *
     * @param id Task ID
     * @return List of comments for the task
     */
    @GetMapping("/{id}/comments")
    @Operation(summary = "Get task comments", description = "Retrieves all comments for a task")
    public ResponseEntity<ApiResponse<List<CommentDTO>>> getTaskComments(@Parameter(description = "Task ID") @PathVariable Long id) {
        List<CommentDTO> comments = commentService.getCommentsByTaskId(id).join();
        ApiResponse<List<CommentDTO>> response = ApiResponse.success(comments,
            String.format("Retrieved %d comments", comments.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Add a comment to a task.
     *
     * @param id Task ID
     * @param commentCreateDTO Comment data
     * @return Created comment response with 201 status
     */
    @PostMapping("/{id}/comments")
    @Operation(summary = "Add comment to task", description = "Adds a new comment to a task")
    public ResponseEntity<ApiResponse<CommentDTO>> addCommentToTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        
        // Ensure comment is tied to this specific task
        CommentDTO comment = commentService.createComment(
            new CommentCreateDTO(commentCreateDTO.content(), id, commentCreateDTO.userId())
        ).join();
        ApiResponse<CommentDTO> response = ApiResponse.success(comment, "Comment added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    /**
     * Retrieve all tasks assigned to a user.
     *
     * @param userId Assignee user ID
     * @return List of tasks assigned to the user
     */
    @GetMapping("/assigned-to/{userId}")
    @Operation(summary = "Get tasks assigned to user", description = "Retrieves all tasks assigned to a specific user")
    public ResponseEntity<ApiResponse<List<TaskDTO>>> getTasksAssignedToUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        
        List<TaskDTO> tasks = taskService.getTasksAssignedToUser(userId);
        ApiResponse<List<TaskDTO>> response = ApiResponse.success(tasks,
            String.format("Retrieved %d tasks assigned to user", tasks.size()));
        return ResponseEntity.ok(response);
    }
    
    /**
     * Assign task to a user.
     *
     * @param id Task ID
     * @param assigneeId Assignee user ID
     * @return Updated task data
     */
    @PatchMapping("/{id}/assign")
    @Operation(summary = "Assign task to user", description = "Assigns a task to a specific user")
    public ResponseEntity<ApiResponse<TaskDTO>> assignTask(
            @Parameter(description = "Task ID") @PathVariable Long id,
            @Parameter(description = "Assignee user ID") @RequestParam Long assigneeId) {
        
        TaskDTO task = taskService.assignTask(id, assigneeId);
        ApiResponse<TaskDTO> response = ApiResponse.success(task, "Task assigned successfully");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Unassign task from current assignee.
     *
     * @param id Task ID
     * @return Updated task data with no assignee
     */
    @PatchMapping("/{id}/unassign")
    @Operation(summary = "Unassign task", description = "Removes the assignee from a task")
    public ResponseEntity<ApiResponse<TaskDTO>> unassignTask(@Parameter(description = "Task ID") @PathVariable Long id) {
        TaskDTO task = taskService.unassignTask(id);
        ApiResponse<TaskDTO> response = ApiResponse.success(task, "Task unassigned successfully");
        return ResponseEntity.ok(response);
    }
}

