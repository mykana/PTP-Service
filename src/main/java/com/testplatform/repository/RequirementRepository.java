package com.testplatform.repository;

import com.testplatform.entity.Requirement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface RequirementRepository extends JpaRepository<Requirement, Integer>, JpaSpecificationExecutor<Requirement> {
    boolean existsByReqCode(String reqCode);
} 