package org.ust.task.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import java.time.LocalDateTime;

/**
 * Comment entity representing a comment on a task.
 * Optimized with composite indexes and second-level caching.
 */
@Entity
@Table(name = "comments", indexes = {
    @Index(name = "idx_comment_task_id", columnList = "task_id"),
    @Index(name = "idx_comment_user_id", columnList = "user_id"),
    @Index(name = "idx_comment_composite_task_created", columnList = "task_id, created_at DESC")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
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

