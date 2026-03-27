package org.ust.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.ProjectCreateDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.ProjectUpdateDTO;
import org.ust.task.entity.Project;
import org.ust.task.entity.User;
import org.ust.task.exception.ResourceNotFoundException;
import org.ust.task.mapper.ProjectMapper;
import org.ust.task.mapper.TaskMapper;
import org.ust.task.repository.ProjectRepository;
import org.ust.task.repository.UserRepository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service layer for Project entity operations.
 * Handles business logic, transaction management, and caching for project-related operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProjectService implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectMapper projectMapper;
    private final TaskMapper taskMapper;

    @Override
    public CompletableFuture<ProjectDTO> createProject(ProjectCreateDTO createDTO, Long ownerId) {
        ProjectCreateDTO updatedCreateDTO = new ProjectCreateDTO(
                createDTO.name(),
                createDTO.description(),
                createDTO.startDate(),
                createDTO.endDate(),
                createDTO.status(),
                ownerId
        );
        return createProject(updatedCreateDTO);
    }

    /**
     * Create a new project and assign an owner asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @CacheEvict(value = "projects", allEntries = true)
    public CompletableFuture<ProjectDTO> createProject(ProjectCreateDTO createDTO) {
        User owner = userRepository.findById(createDTO.ownerId())
                .orElseThrow(() -> ResourceNotFoundException.user(createDTO.ownerId()));

        Project project = projectMapper.toEntity(createDTO);
        project.setOwner(owner);

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully: {}", savedProject.getId());
        return CompletableFuture.completedFuture(projectMapper.toDTO(savedProject));
    }


    /**
     * Retrieve project by ID with caching asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @Cacheable(value = "project", key = "#id")
    @Transactional(readOnly = true)
    public CompletableFuture<ProjectDTO> getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.project(id));
        return CompletableFuture.completedFuture(projectMapper.toDTO(project));
    }

    /**
     * Retrieve all projects with caching asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @Cacheable(value = "projects", key = "'all'")
    @Transactional(readOnly = true)
    public CompletableFuture<List<ProjectDTO>> getAllProjects() {
        List<Project> projects = projectRepository.findAll();
        return CompletableFuture.completedFuture(projectMapper.toDTOList(projects));
    }

    /**
     * Retrieve project with all tasks asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @Cacheable(value = "project", key = "'with_tasks_' + #id")
    @Transactional(readOnly = true)
    public CompletableFuture<ProjectDTO> getProjectWithTasks(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.project(id));
        ProjectDTO projectDTO = projectMapper.toDTO(project);

        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            projectDTO.setTasks(project.getTasks().stream()
                    .map(taskMapper::toDTO)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return CompletableFuture.completedFuture(projectDTO);
    }

    /**
     * Retrieve all projects owned by a specific user asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @Cacheable(value = "projects", key = "'owner_' + #ownerId")
    @Transactional(readOnly = true)
    public CompletableFuture<List<ProjectDTO>> getProjectsByOwnerId(Long ownerId) {
        List<Project> projects = projectRepository.findByOwnerId(ownerId);
        return CompletableFuture.completedFuture(projectMapper.toDTOList(projects));
    }

    /**
     * Update entire project with new data asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @CachePut(value = "project", key = "#id")
    @CacheEvict(value = "projects", allEntries = true)
    public CompletableFuture<ProjectDTO> updateProject(Long id, ProjectUpdateDTO updateDTO) {
        Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.project(id));

        projectMapper.updateEntityFromDTO(updateDTO, existingProject);

        if (updateDTO.getOwnerId() != null) {
            User newOwner = userRepository.findById(updateDTO.getOwnerId())
                    .orElseThrow(() -> ResourceNotFoundException.user(updateDTO.getOwnerId()));
            existingProject.setOwner(newOwner);
        }

        Project savedProject = projectRepository.save(existingProject);
        log.info("Project updated successfully: {}", savedProject.getId());
        return CompletableFuture.completedFuture(projectMapper.toDTO(savedProject));
    }

    /**
     * Partially update project with selected fields asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @CachePut(value = "project", key = "#id")
    @CacheEvict(value = "projects", allEntries = true)
    public CompletableFuture<ProjectDTO> partialUpdateProject(Long id, ProjectUpdateDTO updateDTO) {
        return updateProject(id, updateDTO);
    }

    /**
     * Delete project by ID asynchronously.
     */
    @Override
    @Async("taskExecutor")
    @CacheEvict(value = {"project", "projects"}, allEntries = true)
    public CompletableFuture<Void> deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw ResourceNotFoundException.project(id);
        }
        projectRepository.deleteById(id);
        log.info("Project deleted successfully: {}", id);
        return CompletableFuture.completedFuture(null);
    }
}