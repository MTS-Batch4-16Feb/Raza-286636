package org.ust.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.dto.CommentCreateDTO;
import org.ust.task.dto.CommentDTO;
import org.ust.task.entity.Comment;
import org.ust.task.entity.Task;
import org.ust.task.entity.User;
import org.ust.task.exception.ResourceNotFoundException;
import org.ust.task.mapper.CommentMapper;
import org.ust.task.repository.CommentRepository;
import org.ust.task.repository.TaskRepository;
import org.ust.task.repository.UserRepository;

import java.util.List;

/**
 * Service layer for Comment entity operations.
 * Handles business logic, transaction management, and caching for comment-related operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService implements CommentServiceInterface {
    
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;
    
    /**
     * Create a new comment on a task.
     * Validates that both task and user exist before creating the comment.
     *
     * @param createDTO Comment creation data
     * @return Created CommentDTO
     * @throws ResourceNotFoundException if task or user not found
     */
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public CommentDTO createComment(CommentCreateDTO createDTO) {
        User user = userRepository.findById(createDTO.userId())
                .orElseThrow(() -> ResourceNotFoundException.user(createDTO.userId()));
        
        Task task = taskRepository.findById(createDTO.taskId())
                .orElseThrow(() -> ResourceNotFoundException.task(createDTO.taskId()));
        
        Comment comment = commentMapper.toEntity(createDTO);
        comment.setUser(user);
        comment.setTask(task);
        
        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully: {}", savedComment.getId());
        return commentMapper.toDTO(savedComment);
    }
    
    /**
     * Retrieve comment by ID with caching.
     *
     * @param id Comment ID
     * @return CommentDTO
     * @throws ResourceNotFoundException if comment not found
     */
    @Override
    @Cacheable(value = "comments", key = "#id")
    @Transactional(readOnly = true)
    public CommentDTO getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.comment(id));
        return commentMapper.toDTO(comment);
    }
    
    /**
     * Retrieve all comments with caching.
     *
     * @return List of all CommentDTOs
     */
    @Override
    @Cacheable(value = "comments", key = "'all'")
    @Transactional(readOnly = true)
    public List<CommentDTO> getAllComments() {
        return commentMapper.toDTOList(commentRepository.findAll());
    }
    
    /**
     * Retrieve all comments for a specific task, ordered by creation date descending.
     *
     * @param taskId Task ID
     * @return List of CommentDTOs for the task
     */
    @Override
    @Cacheable(value = "comments", key = "'task_' + #taskId")
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByTaskId(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return commentMapper.toDTOList(comments);
    }
    
    /**
     * Retrieve all comments created by a specific user, ordered by creation date descending.
     *
     * @param userId User ID
     * @return List of CommentDTOs created by the user
     */
    @Override
    @Cacheable(value = "comments", key = "'user_' + #userId")
    @Transactional(readOnly = true)
    public List<CommentDTO> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return commentMapper.toDTOList(comments);
    }
    
    /**
     * Update comment content.
     *
     * @param id Comment ID
     * @param newContent New comment content
     * @return Updated CommentDTO
     * @throws ResourceNotFoundException if comment not found
     */
    @Override
    @CachePut(value = "comments", key = "#id")
    @CacheEvict(value = "comments", allEntries = true)
    public CommentDTO updateComment(Long id, String newContent) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.comment(id));
        
        existingComment.setContent(newContent);
        Comment savedComment = commentRepository.save(existingComment);
        log.info("Comment updated successfully: {}", savedComment.getId());
        return commentMapper.toDTO(savedComment);
    }
    
    /**
     * Delete comment by ID.
     *
     * @param id Comment ID to delete
     * @throws ResourceNotFoundException if comment not found
     */
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw ResourceNotFoundException.comment(id);
        }
        commentRepository.deleteById(id);
        log.info("Comment deleted successfully: {}", id);
    }
}

