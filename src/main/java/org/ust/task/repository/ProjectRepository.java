package org.ust.task.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.ust.task.entity.Project;

import java.util.List;

/**
 * Repository interface for Project entity operations.
 * Includes methods for querying projects by owner and status.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    // Index on owner_id for faster lookups by owner
    List<Project> findByOwnerId(Long ownerId);

    // Index on status for querying projects by their status
    List<Project> findByStatus(String status);
}