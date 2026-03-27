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
 * Project entity representing a project in the system.
 * Optimized with composite indexes and second-level caching.
 */
@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_project_owner_id", columnList = "owner_id"),
    @Index(name = "idx_project_status", columnList = "status"),
    @Index(name = "idx_project_composite_owner_status", columnList = "owner_id, status")
})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Cacheable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String name;
    
    @Column(length = 1000)
    private String description;
    
    @Column(name = "start_date")
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(nullable = false, length = 50)
    private String status;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @Builder.Default
    private List<Task> tasks = new ArrayList<>();
    
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

