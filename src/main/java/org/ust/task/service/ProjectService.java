package org.ust.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
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
    
    /**
     * Create a new project and assign an owner.
     *
     * @param createDTO Project creation data
     * @return Created ProjectDTO
     * @throws ResourceNotFoundException if owner not found
     */
    @Override
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDTO createProject(ProjectCreateDTO createDTO) {
        User owner = userRepository.findById(createDTO.ownerId())
                .orElseThrow(() -> ResourceNotFoundException.user(createDTO.ownerId()));
        
        Project project = projectMapper.toEntity(createDTO);
        project.setOwner(owner);
        
        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully: {}", savedProject.getId());
        return projectMapper.toDTO(savedProject);
    }
    
    /**
     * Create project with explicit owner ID parameter for interface compatibility.
     *
     * @param createDTO Project creation data
     * @param ownerId Owner user ID
     * @return Created ProjectDTO
     */
    @Override
    public ProjectDTO createProject(ProjectCreateDTO createDTO, Long ownerId) {
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
     * Retrieve project by ID with caching.
     *
     * @param id Project ID
     * @return ProjectDTO
     * @throws ResourceNotFoundException if project not found
     */
    @Override
    @Cacheable(value = "project", key = "#id")
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.project(id));
        return projectMapper.toDTO(project);
    }
    
    /**
     * Retrieve all projects with caching.
     *
     * @return List of all ProjectDTOs
     */
    @Override
    @Cacheable(value = "projects", key = "'all'")
    @Transactional(readOnly = true)
    public List<ProjectDTO> getAllProjects() {
        return projectMapper.toDTOList(projectRepository.findAll());
    }
    
    /**
     * Retrieve project with all its tasks.
     *
     * @param id Project ID
     * @return ProjectDTO with tasks included
     * @throws ResourceNotFoundException if project not found
     */
    @Override
    @Cacheable(value = "project", key = "'with_tasks_' + #id")
    @Transactional(readOnly = true)
    public ProjectDTO getProjectWithTasks(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.project(id));
        ProjectDTO projectDTO = projectMapper.toDTO(project);
        // Manually map tasks since mapper ignores them by default
        if (project.getTasks() != null && !project.getTasks().isEmpty()) {
            projectDTO.setTasks(project.getTasks().stream()
                    .map(task -> taskMapper.toDTO(task))
                    .collect(java.util.stream.Collectors.toList()));
        }
        return projectDTO;
    }
    
    /**
     * Retrieve all projects owned by a specific user.
     *
     * @param ownerId Owner user ID
     * @return List of ProjectDTOs owned by the user
     */
    @Override
    @Cacheable(value = "projects", key = "'owner_' + #ownerId")
    @Transactional(readOnly = true)
    public List<ProjectDTO> getProjectsByOwnerId(Long ownerId) {
        return projectMapper.toDTOList(projectRepository.findByOwnerId(ownerId));
    }
    
    /**
     * Update entire project with new data.
     *
     * @param id Project ID
     * @param updateDTO Update data
     * @return Updated ProjectDTO
     * @throws ResourceNotFoundException if project or owner not found
     */
    @Override
    @CachePut(value = "project", key = "#id")
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDTO updateProject(Long id, ProjectUpdateDTO updateDTO) {
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
        return projectMapper.toDTO(savedProject);
    }
    
    /**
     * Partially update project with selected fields.
     *
     * @param id Project ID
     * @param updateDTO Update data (only non-null fields are updated)
     * @return Updated ProjectDTO
     * @throws ResourceNotFoundException if project not found
     */
    @Override
    @CachePut(value = "project", key = "#id")
    @CacheEvict(value = "projects", allEntries = true)
    public ProjectDTO partialUpdateProject(Long id, ProjectUpdateDTO updateDTO) {
        return updateProject(id, updateDTO);
    }
    
    /**
     * Delete project by ID.
     *
     * @param id Project ID to delete
     * @throws ResourceNotFoundException if project not found
     */
    @Override
    @CacheEvict(value = {"project", "projects"}, allEntries = true)
    public void deleteProject(Long id) {
        if (!projectRepository.existsById(id)) {
            throw ResourceNotFoundException.project(id);
        }
        projectRepository.deleteById(id);
        log.info("Project deleted successfully: {}", id);
    }
}

