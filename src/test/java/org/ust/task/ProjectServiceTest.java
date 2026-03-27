package org.ust.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.ust.task.dto.ProjectCreateDTO;
import org.ust.task.dto.ProjectDTO;
import org.ust.task.dto.ProjectUpdateDTO;
import org.ust.task.entity.Project;
import org.ust.task.entity.Role;
import org.ust.task.entity.User;
import org.ust.task.exception.ResourceNotFoundException;
import org.ust.task.mapper.ProjectMapper;
import org.ust.task.mapper.TaskMapper;
import org.ust.task.repository.ProjectRepository;
import org.ust.task.repository.UserRepository;
import org.ust.task.service.ProjectService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class ProjectServiceTest {

    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectMapper projectMapper;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private Cache cache;

    private User mockUser;
    private Project mockProject;
    private ProjectDTO mockProjectDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockUser = new User(1L, "John", "john.doe@example.com", "password", Role.PROJECT_MANAGER,2,false,LocalDateTime.now(),LocalDateTime.now());
        mockProject = new Project(1L, "Project 1", "Description of Project 1", LocalDateTime.now(), LocalDateTime.now().plusDays(10), "TODO", mockUser, null, LocalDateTime.now(), LocalDateTime.now());
        mockProjectDTO = new ProjectDTO(1L, "Project 1", "Description of Project 1", LocalDateTime.now(), LocalDateTime.now().plusDays(10), "TODO", null, null);
    }

    @Test
    void testCreateProject() {
        ProjectCreateDTO createDTO = new ProjectCreateDTO("Project 1", "Description", LocalDateTime.now(), LocalDateTime.now().plusDays(10), "TODO", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(projectMapper.toEntity(createDTO)).thenReturn(mockProject);
        when(projectRepository.save(mockProject)).thenReturn(mockProject);
        when(projectMapper.toDTO(mockProject)).thenReturn(mockProjectDTO);

        CompletableFuture<ProjectDTO> result = projectService.createProject(createDTO);

        assertNotNull(result);
        verify(userRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(mockProject);
    }

    @Test
    void testCreateProjectThrowsResourceNotFoundException() {
        ProjectCreateDTO createDTO = new ProjectCreateDTO("Project 1", "Description", LocalDateTime.now(), LocalDateTime.now().plusDays(10), "TODO", 1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.createProject(createDTO));
    }

    @Test
    void testGetProjectById() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(projectMapper.toDTO(mockProject)).thenReturn(mockProjectDTO);

        CompletableFuture<ProjectDTO> result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals("Project 1", result.join().getName());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProjectByIdThrowsResourceNotFoundException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    void testUpdateProject() {
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO("Updated Project 1", "Updated description", LocalDateTime.now(), LocalDateTime.now().plusDays(5), "IN_PROGRESS", 1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(mockProject));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(projectMapper.toDTO(mockProject)).thenReturn(mockProjectDTO);
        when(projectRepository.save(mockProject)).thenReturn(mockProject);

        CompletableFuture<ProjectDTO> result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Project 1", result.join().getName());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateProjectThrowsResourceNotFoundException() {
        ProjectUpdateDTO updateDTO = new ProjectUpdateDTO("Updated Project 1", "Updated description", LocalDateTime.now(), LocalDateTime.now().plusDays(5), "IN_PROGRESS", 1L);
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.updateProject(1L, updateDTO));
    }

    @Test
    void testDeleteProject() {
        when(projectRepository.existsById(1L)).thenReturn(true);
        doNothing().when(projectRepository).deleteById(1L);

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteProjectThrowsResourceNotFoundException() {
        when(projectRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> projectService.deleteProject(1L));
    }

    @Test
    void testGetAllProjects() {
        List<Project> projects = Arrays.asList(mockProject);
        when(projectRepository.findAll()).thenReturn(projects);
        when(projectMapper.toDTOList(projects)).thenReturn(Arrays.asList(mockProjectDTO));

        CompletableFuture<List<ProjectDTO>> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.join().size());
        verify(projectRepository, times(1)).findAll();
    }
}