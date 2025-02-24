package com.testplatform.repository;

import com.testplatform.entity.TestCase;
import com.testplatform.entity.enums.TestCaseStatus;
import com.testplatform.entity.enums.TestCasePriority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

/**
 * 测试用例数据访问层
 */
public interface TestCaseRepository extends JpaRepository<TestCase, Integer>, JpaSpecificationExecutor<TestCase> {
    
    /**
     * 按创建者ID查找测试用例
     */
    List<TestCase> findByCreatorId(Integer creatorId);
    
    /**
     * 按优先级和状态查找测试用例
     */
    List<TestCase> findByPriorityAndStatus(TestCasePriority priority, TestCaseStatus status);
    
    /**
     * 按标题模糊搜索
     */
    List<TestCase> findByTitleContaining(String keyword);
} 