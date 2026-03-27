package org.ust.task.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.ust.task.entity.Project;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Project entity operations.
 * Optimized with EntityGraph and Fetch Join for preventing N+1.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    
    /**
     * Find projects by owner with eager loading of tasks.
     */
    @EntityGraph(attributePaths = {"owner", "tasks"})
    @Query("SELECT DISTINCT p FROM Project p WHERE p.owner.id = :ownerId")
    List<Project> findByOwnerIdWithTasks(@Param("ownerId") Long ownerId);
    
    /**
     * Find projects by owner without tasks.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT p FROM Project p WHERE p.owner.id = :ownerId")
    List<Project> findByOwnerId(@Param("ownerId") Long ownerId);
    
    /**
     * Find project by ID with all relationships.
     */
    @EntityGraph(attributePaths = {"owner", "tasks"})
    Optional<Project> findById(Long id);
    
    /**
     * Find all projects with owner information (fetch join).
     */
    @Query("SELECT DISTINCT p FROM Project p " +
           "LEFT JOIN FETCH p.tasks " +
           "WHERE p.status = :status")
    List<Project> findByStatusWithFetchJoin(@Param("status") String status);
    
    /**
     * Read-only transaction for finding projects by status.
     */
    @Transactional(readOnly = true)
    @Query("SELECT p FROM Project p WHERE p.status = :status")
    List<Project> findByStatus(@Param("status") String status);
    
    /**
     * Count projects by owner.
     */
    @Transactional(readOnly = true)
    @Query("SELECT COUNT(p) FROM Project p WHERE p.owner.id = :ownerId")
    long countByOwnerId(@Param("ownerId") Long ownerId);
}

