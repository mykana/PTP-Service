package com.testplatform.entity;

import com.testplatform.entity.enums.RequirementStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "requirement")
public class Requirement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "req_code", nullable = false, unique = true, length = 20)
    private String reqCode;

    @Column(name = "req_name", nullable = false, length = 200)
    private String reqName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Module module;

    @Column(name = "executor_ids", length = 255)
    private String executorIds;

    @Enumerated(EnumType.STRING)
    @Column(name = "req_status", nullable = false)
    private RequirementStatus reqStatus = RequirementStatus.需求评审中;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
} 