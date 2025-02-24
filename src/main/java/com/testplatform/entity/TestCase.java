package com.testplatform.entity;

import com.testplatform.entity.enums.TestCasePriority;
import com.testplatform.entity.enums.TestCaseStatus;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.testplatform.converter.TestCaseStatusConverter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 测试用例实体类
 */
@Data
@Entity
@Table(name = "test_case")
public class TestCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TestCasePriority priority = TestCasePriority.P1;

    @Column(name = "case_detail", nullable = false, columnDefinition = "TEXT")
    private String caseDetail;

    @Column(columnDefinition = "TEXT")
    private String precondition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User creator;

    @Convert(converter = TestCaseStatusConverter.class)
    @Column(nullable = false)
    private TestCaseStatus status = TestCaseStatus.编写_评审中;

    @ManyToMany
    @JoinTable(
        name = "test_case_requirement",
        joinColumns = @JoinColumn(name = "test_case_id"),
        inverseJoinColumns = @JoinColumn(name = "requirement_id")
    )
    private Set<Requirement> requirements = new HashSet<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "case_remark", columnDefinition = "TEXT")
    private String caseRemark;
} 