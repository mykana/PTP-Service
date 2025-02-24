package com.testplatform.service;

import com.testplatform.dto.RequirementRequest;
import com.testplatform.entity.Module;
import com.testplatform.entity.Requirement;
import com.testplatform.entity.User;
import com.testplatform.entity.enums.RequirementStatus;
import com.testplatform.repository.ModuleRepository;
import com.testplatform.repository.RequirementRepository;
import com.testplatform.repository.UserRepository;
import com.testplatform.util.RequirementCodeGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.JoinType;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequirementService {

    private final RequirementRepository requirementRepository;
    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;
    private final RequirementCodeGenerator codeGenerator;

    /**
     * 创建需求
     */
    @Transactional
    public Requirement createRequirement(RequirementRequest request, UserDetails userDetails) {
        // 通过用户名查找用户
        User user = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        Requirement requirement = new Requirement();
        requirement.setReqName(request.getReqName());
        requirement.setDescription(request.getDescription());
        requirement.setReqStatus(request.getReqStatus());
        requirement.setCreator(user);  // 设置创建者对象
        
        // 自动生成需求编号
        String reqCode = codeGenerator.generateReqCode();
        log.info("创建需求, 生成需求编号: {}", reqCode);
        
        // 检查需求编号是否已存在（以防万一）
        while (requirementRepository.existsByReqCode(reqCode)) {
            reqCode = codeGenerator.generateReqCode();
            log.warn("需求编号重复，重新生成: {}", reqCode);
        }

        requirement.setReqCode(reqCode);  // 使用生成的编号
        
        // 设置模块
        if (request.getModuleId() != null) {
            Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("模块不存在"));
            requirement.setModule(module);
        }
        
        // 设置执行者ID列表
        if (request.getExecutorIds() != null && !request.getExecutorIds().isEmpty()) {
            requirement.setExecutorIds(request.getExecutorIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        }

        Requirement savedRequirement = requirementRepository.save(requirement);
        log.info("需求创建成功: {}", savedRequirement.getReqCode());
        return savedRequirement;
    }

    /**
     * 更新需求
     */
    @Transactional
    public void updateRequirement(Integer id, RequirementRequest request) {
        log.info("更新需求: {}", id);
        
        Requirement requirement = requirementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("需求不存在"));

        // 更新基本信息（保持需求编号不变）
        requirement.setReqName(request.getReqName());
        requirement.setDescription(request.getDescription());
        requirement.setReqStatus(request.getReqStatus());
        
        // 更新模块
        if (request.getModuleId() != null) {
            Module module = moduleRepository.findById(request.getModuleId())
                .orElseThrow(() -> new RuntimeException("模块不存在"));
            requirement.setModule(module);
        }
        
        // 更新执行者列表
        if (request.getExecutorIds() != null) {
            requirement.setExecutorIds(request.getExecutorIds().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",")));
        }

        requirementRepository.save(requirement);
        log.info("需求更新成功: {}", requirement.getReqCode());
    }

    /**
     * 获取需求详情
     */
    public Requirement getRequirementById(Integer id) {
        log.info("获取需求详情: {}", id);
        return requirementRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("需求不存在"));
    }

    /**
     * 分页查询需求
     */
    public Page<Requirement> findRequirements(String reqCode, String reqName, 
            Integer moduleId, String reqStatus, Pageable pageable) {
        log.info("查询需求列表: reqCode={}, reqName={}, moduleId={}, reqStatus={}", 
            reqCode, reqName, moduleId, reqStatus);
            
        Specification<Requirement> spec = (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();
            
            // 添加 fetch join 来预加载关联实体
            if (query.getResultType().equals(Requirement.class)) {
                root.fetch("creator", JoinType.LEFT);
                root.fetch("module", JoinType.LEFT);
            }
            
            if (StringUtils.hasText(reqCode)) {
                predicates.add(cb.like(root.get("reqCode"), "%" + reqCode + "%"));
            }
            if (StringUtils.hasText(reqName)) {
                predicates.add(cb.like(root.get("reqName"), "%" + reqName + "%"));
            }
            if (moduleId != null) {
                predicates.add(cb.equal(root.get("module").get("id"), moduleId));
            }
            if (StringUtils.hasText(reqStatus)) {
                predicates.add(cb.equal(root.get("reqStatus"), RequirementStatus.valueOf(reqStatus)));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return requirementRepository.findAll(spec, pageable);
    }

    /**
     * 搜索需求
     */
    public Page<Requirement> searchRequirements(String keyword, Pageable pageable) {
        log.info("搜索需求，关键字: {}", keyword);
        
        Specification<Requirement> spec = (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) {
                return null;
            }
            
            String likePattern = "%" + keyword.trim() + "%";
            return cb.or(
                cb.like(root.get("reqCode"), likePattern),
                cb.like(root.get("reqName"), likePattern)
            );
        };
        
        // 添加排序条件：按需求编号降序
        return requirementRepository.findAll(
            spec, 
            PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "reqCode")
            )
        );
    }
} 