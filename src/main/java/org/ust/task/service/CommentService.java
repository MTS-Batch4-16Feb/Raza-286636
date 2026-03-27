package org.ust.task.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Async;
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
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService implements CommentServiceInterface {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    // Asynchronous create comment method
    @Async("taskExecutor")
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public CompletableFuture<CommentDTO> createComment(CommentCreateDTO createDTO) {
        User user = userRepository.findById(createDTO.userId())
                .orElseThrow(() -> ResourceNotFoundException.user(createDTO.userId()));

        Task task = taskRepository.findById(createDTO.taskId())
                .orElseThrow(() -> ResourceNotFoundException.task(createDTO.taskId()));

        Comment comment = commentMapper.toEntity(createDTO);
        comment.setUser(user);
        comment.setTask(task);

        Comment savedComment = commentRepository.save(comment);
        log.info("Comment created successfully: {}", savedComment.getId());
        return CompletableFuture.completedFuture(commentMapper.toDTO(savedComment));
    }

    // Asynchronous get comment by ID method
    @Async("taskExecutor")
    @Override
    @Cacheable(value = "comments", key = "#id")
    @Transactional(readOnly = true)
    public CompletableFuture<CommentDTO> getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.comment(id));
        return CompletableFuture.completedFuture(commentMapper.toDTO(comment));
    }

    // Asynchronous get all comments method
    @Async("taskExecutor")
    @Override
    @Cacheable(value = "comments", key = "'all'")
    @Transactional(readOnly = true)
    public CompletableFuture<List<CommentDTO>> getAllComments() {
        return CompletableFuture.completedFuture(commentMapper.toDTOList(commentRepository.findAll()));
    }

    // Asynchronous get comments by task ID method
    @Async("taskExecutor")
    @Override
    @Cacheable(value = "comments", key = "'task_' + #taskId")
    @Transactional(readOnly = true)
    public CompletableFuture<List<CommentDTO>> getCommentsByTaskId(Long taskId) {
        List<Comment> comments = commentRepository.findByTaskIdOrderByCreatedAtDesc(taskId);
        return CompletableFuture.completedFuture(commentMapper.toDTOList(comments));
    }

    // Asynchronous get comments by user ID method
      @Async("taskExecutor")
    @Override
    @Cacheable(value = "comments", key = "'user_' + #userId")
    @Transactional(readOnly = true)
    public CompletableFuture<List<CommentDTO>> getCommentsByUserId(Long userId) {
        List<Comment> comments = commentRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return CompletableFuture.completedFuture(commentMapper.toDTOList(comments));
    }

    // Asynchronous update comment content method
      @Async("taskExecutor")
    @Override
    @CachePut(value = "comments", key = "#id")
    @CacheEvict(value = "comments", allEntries = true)
    public CompletableFuture<CommentDTO> updateComment(Long id, String newContent) {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.comment(id));

        existingComment.setContent(newContent);
        Comment savedComment = commentRepository.save(existingComment);
        log.info("Comment updated successfully: {}", savedComment.getId());
        return CompletableFuture.completedFuture(commentMapper.toDTO(savedComment));
    }

    // Asynchronous delete comment method
      @Async("taskExecutor")
    @Override
    @CacheEvict(value = "comments", allEntries = true)
    public CompletableFuture<Void> deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw ResourceNotFoundException.comment(id);
        }
        commentRepository.deleteById(id);
        log.info("Comment deleted successfully: {}", id);
        return CompletableFuture.completedFuture(null);
    }
}