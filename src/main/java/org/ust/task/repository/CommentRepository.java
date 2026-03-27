package org.ust.task.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.entity.Comment;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Comment entity operations.
 * Optimized with EntityGraph to prevent N+1 on relationships.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    /**
     * Find comments by task with eager loading of user.
     */
    @EntityGraph(attributePaths = {"user", "task"})
    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId ORDER BY c.createdAt DESC")
    List<Comment> findByTaskIdWithUser(@Param("taskId") Long taskId);
    
    /**
     * Standard find by task ID (read-only).
     */
    @Transactional(readOnly = true)
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    
    /**
     * Find comments by user with eager loading.
     */
    @EntityGraph(attributePaths = {"user", "task"})
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdWithTask(@Param("userId") Long userId);
    
    /**
     * Standard find by user ID (read-only).
     */
    @Transactional(readOnly = true)
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find comment by ID with all relationships.
     */
    @EntityGraph(attributePaths = {"user", "task"})
    Optional<Comment> findById(Long id);
    
    /**
     * Count comments by task.
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.task.id = :taskId")
    long countByTaskId(@Param("taskId") Long taskId);
}

