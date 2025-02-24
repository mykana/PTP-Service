package com.testplatform.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 测试用例版本实体
 * 实现git-like的版本控制
 */
@Data
@Entity
@Table(name = "case_versions")
public class CaseVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String versionId;  // 版本号

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id")
    private TestCase testCase;  // 关联的测试用例

    @Lob
    @Column(columnDefinition = "TEXT")
    private String caseContent;  // 用例内容

    @Column(length = 500)
    private String commitMessage;  // 提交信息

    @CreationTimestamp
    private LocalDateTime createTime;
} 