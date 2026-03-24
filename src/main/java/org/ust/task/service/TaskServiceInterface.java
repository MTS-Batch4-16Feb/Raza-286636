package org.ust.task.service;

import org.ust.task.dto.TaskCreateDTO;
import org.ust.task.dto.TaskDTO;
import org.ust.task.dto.TaskUpdateDTO;
import org.ust.task.entity.TaskStatus;

import java.util.List;

/**
 * Service interface for Task management operations
 */
public interface TaskServiceInterface {
    
    /**
     * Create a new task
     */
    TaskDTO createTask(TaskCreateDTO createDTO);
    
    /**
     * Retrieve task by ID
     */
    TaskDTO getTaskById(Long id);
    
    /**
     * Retrieve all tasks with optional filters
     */
    List<TaskDTO> getAllTasks(Long assigneeId, Long projectId, String status, String priority);
    
    /**
     * Retrieve all tasks for a project
     */
    List<TaskDTO> getTasksByProjectId(Long projectId);
    
    /**
     * Retrieve all tasks assigned to a user
     */
    List<TaskDTO> getTasksAssignedToUser(Long userId);
    
    /**
     * Update entire task (full update)
     */
    TaskDTO updateTaskFull(Long id, TaskCreateDTO updateDTO);
    
    /**
     * Update task with partial data
     */
    TaskDTO updateTask(Long id, TaskUpdateDTO updateDTO);
    
    /**
     * Update task status
     */
    TaskDTO updateTaskStatus(Long id, TaskStatus status);
    
    /**
     * Assign task to a user
     */
    TaskDTO assignTask(Long id, Long assigneeId);
    
    /**
     * Unassign task from current user
     */
    TaskDTO unassignTask(Long id);
    
    /**
     * Delete task
     */
    void deleteTask(Long id);
}

