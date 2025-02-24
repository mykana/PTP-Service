package com.testplatform.repository;

import com.testplatform.entity.UserTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;

public interface UserTestCaseRepository extends JpaRepository<UserTestCase, Integer>, JpaSpecificationExecutor<UserTestCase> {
    List<UserTestCase> findByUserId(Integer userId);
    List<UserTestCase> findByTestCaseId(Integer testCaseId);
    boolean existsByUserIdAndTestCaseId(Integer userId, Integer testCaseId);
    void deleteByTestCaseId(Integer testCaseId);
} 