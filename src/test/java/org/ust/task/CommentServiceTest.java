package org.ust.task;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;
import org.ust.task.dto.UserDTO;
import org.ust.task.entity.Comment;
import org.ust.task.entity.Task;
import org.ust.task.entity.User;
import org.ust.task.mapper.CommentMapper;
import org.ust.task.repository.CommentRepository;
import org.ust.task.repository.TaskRepository;
import org.ust.task.repository.UserRepository;
import org.ust.task.service.CommentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentService commentService;

    @Test
    public void testCreateComment() throws Exception {
        CommentCreateDTO createDTO = new CommentCreateDTO("Test comment", 1L, 1L);
        Comment commentEntity = new Comment(1L, "Test comment", new Task(), new User(), LocalDateTime.now(), LocalDateTime.now());
        CommentDTO commentDTO = new CommentDTO(1L, "Test comment", 1L, new UserDTO(), LocalDateTime.now(), LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(taskRepository.findById(1L)).thenReturn(Optional.of(new Task()));
        when(commentMapper.toEntity(createDTO)).thenReturn(commentEntity);
        when(commentMapper.toDTO(commentEntity)).thenReturn(commentDTO);
        when(commentRepository.save(commentEntity)).thenReturn(commentEntity);

        CompletableFuture<CommentDTO> result = commentService.createComment(createDTO);

        assertNotNull(result.get());
        verify(commentRepository, times(1)).save(commentEntity);
    }
}