package org.ust.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.TaskCreateDTO;
import org.ust.task.dto.TaskDTO;
import org.ust.task.dto.TaskUpdateDTO;
import org.ust.task.entity.Project;
import org.ust.task.entity.Task;
import org.ust.task.entity.TaskStatus;
import org.ust.task.entity.User;
import org.ust.task.exception.ResourceNotFoundException;
import org.ust.task.mapper.TaskMapper;
import org.ust.task.repository.ProjectRepository;
import org.ust.task.repository.TaskRepository;
import org.ust.task.repository.UserRepository;

import java.util.List;

/**
 * Service layer for Task entity operations.
 * Handles business logic, transaction management, and caching for task-related operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TaskService implements TaskServiceInterface {
    
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    
    /**
     * Create a new task and assign it to a project and optional assignee.
     *
     * @param createDTO Task creation data
     * @return Created TaskDTO
     * @throws ResourceNotFoundException if project or assignee not found
     */
    @Override
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO createTask(TaskCreateDTO createDTO) {
        Project project = projectRepository.findById(createDTO.projectId())
                .orElseThrow(() -> ResourceNotFoundException.project(createDTO.projectId()));
        
        Task task = taskMapper.toEntity(createDTO);
        task.setProject(project);
        
        if (createDTO.assigneeId() != null) {
            User assignee = userRepository.findById(createDTO.assigneeId())
                    .orElseThrow(() -> ResourceNotFoundException.user(createDTO.assigneeId()));
            task.setAssignee(assignee);
        }
        
        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully: {}", savedTask.getId());
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Retrieve task by ID with caching.
     *
     * @param id Task ID
     * @return TaskDTO
     * @throws ResourceNotFoundException if task not found
     */
    @Override
    @Cacheable(value = "task", key = "#id")
    @Transactional(readOnly = true)
    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.task(id));
        return taskMapper.toDTO(task);
    }
    
    /**
     * Retrieve all tasks with optional filtering by assignee, project, status, and priority.
     *
     * @param assigneeId Assignee user ID (optional)
     * @param projectId Project ID (optional)
     * @param status Task status (optional)
     * @param priority Task priority (optional)
     * @return List of filtered TaskDTOs
     */
    @Override
    @Cacheable(value = "tasks", key = "'filter_' + #assigneeId + '_' + #projectId + '_' + #status + '_' + #priority")
    @Transactional(readOnly = true)
    public List<TaskDTO> getAllTasks(Long assigneeId, Long projectId, String status, String priority) {
        List<Task> tasks = taskRepository.findAll();
        
        if (assigneeId != null) {
            tasks = tasks.stream().filter(t -> assigneeId.equals(t.getAssignee() != null ? t.getAssignee().getId() : null)).toList();
        }
        if (projectId != null) {
            tasks = tasks.stream().filter(t -> projectId.equals(t.getProject().getId())).toList();
        }
        if (status != null && !status.isBlank()) {
            tasks = tasks.stream().filter(t -> status.equals(t.getStatus().toString())).toList();
        }
        if (priority != null && !priority.isBlank()) {
            tasks = tasks.stream().filter(t -> priority.equals(t.getPriority().toString())).toList();
        }
        
        return taskMapper.toDTOList(tasks);
    }
    
    /**
     * Retrieve all tasks for a specific project.
     *
     * @param projectId Project ID
     * @return List of TaskDTOs in the project
     */
    @Override
    @Cacheable(value = "tasks", key = "'project_' + #projectId")
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksByProjectId(Long projectId) {
        return taskMapper.toDTOList(taskRepository.findByProjectId(projectId));
    }
    
    /**
     * Retrieve all tasks assigned to a specific user.
     *
     * @param userId Assignee user ID
     * @return List of TaskDTOs assigned to the user
     */
    @Override
    @Cacheable(value = "tasks", key = "'assigned_' + #userId")
    @Transactional(readOnly = true)
    public List<TaskDTO> getTasksAssignedToUser(Long userId) {
        return taskMapper.toDTOList(taskRepository.findByAssigneeId(userId));
    }
    
    /**
     * Update entire task with new data.
     *
     * @param id Task ID
     * @param updateDTO Update data
     * @return Updated TaskDTO
     * @throws ResourceNotFoundException if task, project, or assignee not found
     */
    @Override
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO updateTaskFull(Long id, TaskCreateDTO updateDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.task(id));
        
        Project project = projectRepository.findById(updateDTO.projectId())
                .orElseThrow(() -> ResourceNotFoundException.project(updateDTO.projectId()));
        
        taskMapper.updateEntityFromDTO(updateDTO, existingTask);
        existingTask.setProject(project);
        
        if (updateDTO.assigneeId() != null) {
            User assignee = userRepository.findById(updateDTO.assigneeId())
                    .orElseThrow(() -> ResourceNotFoundException.user(updateDTO.assigneeId()));
            existingTask.setAssignee(assignee);
        }
        
        Task savedTask = taskRepository.save(existingTask);
        log.info("Task updated fully: {}", savedTask.getId());
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Partially update task with selected fields.
     *
     * @param id Task ID
     * @param updateDTO Update data (only non-null fields are updated)
     * @return Updated TaskDTO
     * @throws ResourceNotFoundException if task not found
     */
    @Override
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO updateTask(Long id, TaskUpdateDTO updateDTO) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.task(id));
        
        taskMapper.updateEntityFromDTO(updateDTO, existingTask);
        
        if (updateDTO.getProjectId() != null) {
            Project newProject = projectRepository.findById(updateDTO.getProjectId())
                    .orElseThrow(() -> ResourceNotFoundException.project(updateDTO.getProjectId()));
            existingTask.setProject(newProject);
        }
        
        if (updateDTO.getAssigneeId() != null) {
            User assignee = userRepository.findById(updateDTO.getAssigneeId())
                    .orElseThrow(() -> ResourceNotFoundException.user(updateDTO.getAssigneeId()));
            existingTask.setAssignee(assignee);
        }
        
        Task savedTask = taskRepository.save(existingTask);
        log.info("Task updated partially: {}", savedTask.getId());
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Update task status.
     *
     * @param id Task ID
     * @param status New task status
     * @return Updated TaskDTO
     * @throws ResourceNotFoundException if task not found
     */
    @Override
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO updateTaskStatus(Long id, TaskStatus status) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.task(id));
        
        task.setStatus(status);
        Task savedTask = taskRepository.save(task);
        log.info("Task status updated: {} -> {}", id, status);
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Assign task to a user.
     *
     * @param id Task ID
     * @param assigneeId Assignee user ID
     * @return Updated TaskDTO
     * @throws ResourceNotFoundException if task or assignee not found
     */
    @Override
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO assignTask(Long id, Long assigneeId) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.task(id));
        
        User assignee = userRepository.findById(assigneeId)
                .orElseThrow(() -> ResourceNotFoundException.user(assigneeId));
        
        task.setAssignee(assignee);
        Task savedTask = taskRepository.save(task);
        log.info("Task assigned to user: Task {} -> User {}", id, assigneeId);
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Unassign task from current user.
     *
     * @param id Task ID
     * @return Updated TaskDTO with no assignee
     * @throws ResourceNotFoundException if task not found
     */
    @Override
    @CachePut(value = "task", key = "#id")
    @CacheEvict(value = "tasks", allEntries = true)
    public TaskDTO unassignTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.task(id));
        
        task.setAssignee(null);
        Task savedTask = taskRepository.save(task);
        log.info("Task unassigned: {}", id);
        return taskMapper.toDTO(savedTask);
    }
    
    /**
     * Delete task by ID.
     *
     * @param id Task ID to delete
     * @throws ResourceNotFoundException if task not found
     */
    @Override
    @CacheEvict(value = {"task", "tasks"}, allEntries = true)
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw ResourceNotFoundException.task(id);
        }
        taskRepository.deleteById(id);
        log.info("Task deleted successfully: {}", id);
    }
}

