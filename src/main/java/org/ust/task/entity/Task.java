package org.ust.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Task entity representing a task in the system.
 * Optimized with composite indexes and second-level caching for performance.
 */
@Entity
@Table(name = "tasks", indexes = {
    @Index(name = "idx_task_project_id", columnList = "project_id"),
    @Index(name = "idx_task_assignee_id", columnList = "assignee_id"),
    @Index(name = "idx_task_status", columnList = "status"),
    @Index(name = "idx_task_priority", columnList = "priority"),
    @Index(name = "idx_task_composite_project_status", columnList = "project_id, status"),
    @Index(name = "idx_task_composite_assignee_status", columnList = "assignee_id, status")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String title;
    
    @Column(length = 1000)
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;
    
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;
    
    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

