package com.testplatform.service;

import com.testplatform.dto.TestCaseCreateRequest;
import com.testplatform.dto.TestCaseQueryRequest;
import com.testplatform.dto.TestCaseUpdateRequest;
import com.testplatform.entity.*;
import com.testplatform.entity.enums.TestCaseStatus;
import com.testplatform.repository.TestCaseRepository;
import com.testplatform.repository.RequirementRepository;
import com.testplatform.repository.UserRepository;
import com.testplatform.repository.UserTestCaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import javax.persistence.criteria.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 测试用例服务层
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TestCaseService {

    private final TestCaseRepository testCaseRepository;
    private final RequirementRepository requirementRepository;
    private final UserRepository userRepository;
    private final UserTestCaseRepository userTestCaseRepository;
    
    /**
     * 创建测试用例
     */
    @Transactional
    public TestCase createTestCase(TestCaseCreateRequest request, UserDetails userDetails) {
        log.info("创建测试用例: {}", request.getTitle());
        
        // 获取创建者信息
        User creator = userRepository.findByUsername(userDetails.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 创建测试用例
        TestCase testCase = new TestCase();
        testCase.setTitle(request.getTitle());
        testCase.setPriority(request.getPriority());
        testCase.setCaseDetail(request.getSteps());
        testCase.setPrecondition(request.getPrecondition());
        testCase.setCaseRemark(request.getRemark());
        testCase.setCreator(creator);

        // 关联需求
        if (request.getRequirementIds() != null && !request.getRequirementIds().isEmpty()) {
            Set<Requirement> requirements = requirementRepository.findAllById(request.getRequirementIds())
                .stream()
                .collect(Collectors.toSet());
            
            if (requirements.size() != request.getRequirementIds().size()) {
                throw new RuntimeException("部分需求不存在");
            }
            
            testCase.setRequirements(requirements);
        }

        // 保存测试用例
        TestCase savedTestCase = testCaseRepository.save(testCase);
        log.info("测试用例创建成功: {}", savedTestCase.getId());
        
        return savedTestCase;
    }
    
    /**
     * 创建用例新版本
     */
    @Transactional
    public CaseVersion createVersion(Integer caseId, String content, String message) {
        TestCase testCase = testCaseRepository.findById(caseId)
            .orElseThrow(() -> new RuntimeException("测试用例不存在"));
            
        CaseVersion version = new CaseVersion();
        version.setVersionId(UUID.randomUUID().toString());
        version.setTestCase(testCase);
        version.setCaseContent(content);
        version.setCommitMessage(message);
        
        return version;
    }
    
    /**
     * 更新测试用例
     */
    @Transactional
    public TestCase updateTestCase(Integer id, TestCaseUpdateRequest request) {
        TestCase existingCase = testCaseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("测试用例不存在"));
            
        // 更新基本信息
        existingCase.setTitle(request.getTitle());
        existingCase.setPriority(request.getPriority());
        existingCase.setStatus(request.getStatus());
        existingCase.setCaseDetail(request.getSteps());
        existingCase.setPrecondition(request.getPrecondition());
        existingCase.setCaseRemark(request.getRemark());
        
        // 更新关联需求
        if (request.getRequirementIds() != null) {
            Set<Requirement> requirements = requirementRepository.findAllById(request.getRequirementIds())
                .stream()
                .collect(Collectors.toSet());
            
            if (requirements.size() != request.getRequirementIds().size()) {
                throw new RuntimeException("部分需求不存在");
            }
            
            existingCase.setRequirements(requirements);
        }
        
        return testCaseRepository.save(existingCase);
    }
    
    /**
     * 分页查询测试用例
     */
    public Page<TestCase> findTestCases(TestCaseQueryRequest request) {
        Specification<TestCase> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // 标题模糊搜索
            if (StringUtils.hasText(request.getTitle())) {
                predicates.add(cb.like(root.get("title"), 
                    "%" + request.getTitle() + "%"));
            }
            
            // 优先级过滤
            if (request.getPriority() != null && !request.getPriority().isEmpty()) {
                predicates.add(root.get("priority").in(request.getPriority()));
            }
            
            // 状态过滤
            if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                predicates.add(root.get("status").in(request.getStatus()));
            }
            
            // 创建时间范围过滤
            if (request.getDateRange() != null && request.getDateRange().size() == 2) {
                LocalDateTime startDate = LocalDateTime.parse(request.getDateRange().get(0) + "T00:00:00");
                LocalDateTime endDate = LocalDateTime.parse(request.getDateRange().get(1) + "T23:59:59");
                predicates.add(cb.between(root.get("createdAt"), startDate, endDate));
            }
            
            // 创建人名字模糊查询
            if (StringUtils.hasText(request.getCreatorName())) {
                Join<TestCase, User> creatorJoin = root.join("creator", JoinType.LEFT);
                predicates.add(cb.like(creatorJoin.get("realName"), 
                    "%" + request.getCreatorName() + "%"));
            }
            
            // 通过关联表查询用户的测试用例
            if (request.getUserId() != null) {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<UserTestCase> utcRoot = subquery.from(UserTestCase.class);
                subquery.select(utcRoot.get("testCase").get("id"))
                    .where(cb.equal(utcRoot.get("user").get("id"), request.getUserId()));
                predicates.add(root.get("id").in(subquery));
            }
            
            // 需求ID过滤
            if (request.getRequirementIds() != null && !request.getRequirementIds().isEmpty()) {
                Join<TestCase, Requirement> requirementJoin = root.join("requirements", JoinType.LEFT);
                predicates.add(requirementJoin.get("id").in(request.getRequirementIds()));
            }
            
            // 添加关联查询以避免N+1问题
            if (query.getResultType().equals(TestCase.class)) {
                root.fetch("creator", JoinType.LEFT);
                root.fetch("requirements", JoinType.LEFT);
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return testCaseRepository.findAll(spec, 
            PageRequest.of(request.getPage() - 1, request.getPageSize(), 
                Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    
    /**
     * 删除测试用例
     */
    @Transactional
    public void deleteTestCase(Integer id) {
        TestCase testCase = testCaseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("测试用例不存在"));
        
        // 删除用户-测试用例关联关系
        userTestCaseRepository.deleteByTestCaseId(id);
        
        // 删除测试用例
        testCaseRepository.delete(testCase);
        
        log.info("测试用例删除成功: {}", id);
    }
    
    /**
     * 获取测试用例详情
     */
    public TestCase getTestCaseDetail(Integer id) {
        return testCaseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("测试用例不存在"));
    }
} 