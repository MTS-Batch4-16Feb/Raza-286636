package org.ust.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ust.task.entity.Comment;
import java.util.List;

/**
 * Repository interface for Comment entity operations.
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);
}

