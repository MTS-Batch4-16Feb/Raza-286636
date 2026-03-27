package org.ust.task.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.entity.Task;
import org.ust.task.entity.TaskStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Task entity operations.
 * Optimized with EntityGraph, Fetch Join, and query caching.
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    /**
     * Find tasks by project with eager loading of comments to prevent N+1.
     * Uses EntityGraph for optimal fetch strategy.
     */
    @EntityGraph(attributePaths = {"project", "assignee", "comments"})
    @Query("SELECT DISTINCT t FROM Task t WHERE t.project.id = :projectId")
    List<Task> findByProjectIdWithComments(@Param("projectId") Long projectId);
    
    /**
     * Find tasks by assignee with eager loading.
     */
    @EntityGraph(attributePaths = {"project", "assignee"})
    @Query("SELECT DISTINCT t FROM Task t WHERE t.assignee.id = :assigneeId")
    List<Task> findByAssigneeIdWithProject(@Param("assigneeId") Long assigneeId);
    
    /**
     * Find all tasks with eager loading of relationships.
     */
    @EntityGraph(attributePaths = {"project", "assignee", "comments"})
    @Query("SELECT DISTINCT t FROM Task t")
    List<Task> findAllWithRelations();
    
    /**
     * Find tasks by project and status - using fetch join for N+1 prevention.
     */
    @Query("SELECT DISTINCT t FROM Task t " +
           "LEFT JOIN FETCH t.comments " +
           "WHERE t.project.id = :projectId AND t.status = :status")
    List<Task> findByProjectIdAndStatusWithFetchJoin(
            @Param("projectId") Long projectId,
            @Param("status") TaskStatus status);
    
    /**
     * Find task by ID with all relationships.
     */
    @EntityGraph(attributePaths = {"project", "assignee", "comments"})
    Optional<Task> findById(Long id);
    
    /**
     * Batch update task status - using @Modifying for DML operations.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.status = :newStatus WHERE t.project.id = :projectId")
    int updateTaskStatusByProjectId(@Param("projectId") Long projectId, @Param("newStatus") TaskStatus newStatus);
    
    /**
     * Batch update assignee - using @Modifying.
     */
    @Modifying
    @Transactional
    @Query("UPDATE Task t SET t.assignee = null WHERE t.assignee.id = :assigneeId")
    int unassignTasksByAssigneeId(@Param("assigneeId") Long assigneeId);
    
    /**
     * Find tasks by status (read-only query).
     */
    @Transactional(readOnly = true)
    @Query("SELECT t FROM Task t WHERE t.status = :status")
    List<Task> findByStatus(@Param("status") TaskStatus status);
    
    /**
     * Count tasks by project and status.
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(t) FROM Task t WHERE t.project.id = :projectId AND t.status = :status")
    long countByProjectIdAndStatus(@Param("projectId") Long projectId, @Param("status") TaskStatus status);
    
    // Standard JPA methods
    @Transactional(readOnly = true)
    List<Task> findByProjectId(Long projectId);
    
    @Transactional(readOnly = true)
    List<Task> findByAssigneeId(Long assigneeId);
}

