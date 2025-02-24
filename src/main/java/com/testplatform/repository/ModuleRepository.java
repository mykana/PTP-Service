package com.testplatform.repository;

import com.testplatform.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer>, JpaSpecificationExecutor<Module> {
    List<Module> findByModuleStatus(Boolean status);
    boolean existsByModuleName(String moduleName);
} 