package com.testplatform.controller;

import com.testplatform.dto.TestCaseDTO;
import com.testplatform.dto.TestCaseDetailDTO;
import com.testplatform.dto.TestCaseQueryRequest;
import com.testplatform.entity.TestCase;
import com.testplatform.entity.enums.TestCasePriority;
import com.testplatform.service.TestCaseService;
import com.testplatform.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.testplatform.entity.enums.TestCaseStatus;
import com.testplatform.dto.TestCaseCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import com.testplatform.dto.TestCaseUpdateRequest;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 测试用例控制器
 * 该控制器处理与测试用例相关的所有HTTP请求，包括创建、更新、查询和删除测试用例。
 */
@RestController
@RequestMapping("/api/cases")  // 定义请求的基础路径
@RequiredArgsConstructor
@Slf4j  // 使用Slf4j记录日志
public class TestCaseController {

    private final TestCaseService testCaseService;  // 注入测试用例服务

    /**
     * 创建测试用例
     * @param request 创建请求体，包含测试用例的详细信息
     * @param userDetails 当前认证用户的信息
     * @return 创建成功的测试用例信息
     */
    @PostMapping
    public ResponseEntity<Result<TestCase>> createTestCase(
            @RequestBody TestCaseCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("接收到创建测试用例请求: {}", request.getTitle());
        log.debug("当前用户认证信息: {}", userDetails);
        
        try {
            // 检查用户认证信息
            if (userDetails == null) {
                log.error("用户未认证");
                return ResponseEntity.status(401)
                        .body(Result.error(401, "用户未认证"));
            }
            
            // 调用服务层创建测试用例
            TestCase testCase = testCaseService.createTestCase(request, userDetails);
            log.info("测试用例创建成功: {}", testCase.getId());
            return ResponseEntity.ok(Result.success(testCase));  // 返回创建成功的测试用例
        } catch (Exception e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : "创建测试用例失败";
            log.error("创建测试用例失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));  // 返回错误信息
        }
    }

    /**
     * 更新测试用例
     * @param id 测试用例的ID
     * @param request 更新请求体，包含更新后的测试用例信息
     * @return 更新后的测试用例详细信息
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<TestCaseDetailDTO>> updateTestCase(
            @PathVariable Integer id,
            @RequestBody TestCaseUpdateRequest request) {
        log.info("接收到更新测试用例请求: id={}", id);
        try {
            // 调用服务层更新测试用例
            TestCase updatedCase = testCaseService.updateTestCase(id, request);
            return ResponseEntity.ok(Result.success(TestCaseDetailDTO.fromEntity(updatedCase)));  // 返回更新后的测试用例
        } catch (Exception e) {
            log.error("更新测试用例失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));  // 返回错误信息
        }
    }

    /**
     * 分页查询测试用例
     * @param request 查询请求体，包含查询条件和分页信息
     * @return 查询到的测试用例列表及分页信息
     */
    @GetMapping
    public ResponseEntity<Result<Map<String, Object>>> getTestCases(TestCaseQueryRequest request) {
        log.info("接收到查询测试用例请求: {}", request);
        try {
            // 调用服务层查询测试用例
            Page<TestCase> page = testCaseService.findTestCases(request);
            
            // 转换为DTO
            List<TestCaseDTO> dtoList = page.getContent().stream()
                .map(TestCaseDTO::fromEntity)
                .collect(Collectors.toList());
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            response.put("total", page.getTotalElements());  // 总记录数
            response.put("page", request.getPage());        // 当前页码
            response.put("pageSize", request.getPageSize()); // 每页大小
            response.put("list", dtoList);                   // 测试用例列表
            
            log.info("查询到{}条测试用例", page.getTotalElements());
            return ResponseEntity.ok(Result.success(response));  // 返回查询结果
        } catch (Exception e) {
            log.error("查询测试用例失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));  // 返回错误信息
        }
    }

    /**
     * 删除测试用例
     * @param id 测试用例的ID
     * @return 删除成功的响应
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<Void>> deleteTestCase(@PathVariable Integer id) {
        log.info("接收到删除测试用例请求: id={}", id);
        try {
            // 调用服务层删除测试用例
            testCaseService.deleteTestCase(id);
            return ResponseEntity.ok(Result.success(null));  // 返回成功响应
        } catch (Exception e) {
            log.error("删除测试用例失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));  // 返回错误信息
        }
    }

    /**
     * 获取测试用例详情
     * @param request 请求体，包含测试用例ID
     * @return 测试用例的详细信息
     */
    @PostMapping("/detail")
    public ResponseEntity<Result<TestCaseDetailDTO>> getTestCaseDetail(@RequestBody Map<String, Integer> request) {
        log.info("接收到获取测试用例详情请求: {}", request.get("id"));
        try {
            // 调用服务层获取测试用例详情
            TestCase testCase = testCaseService.getTestCaseDetail(request.get("id"));
            return ResponseEntity.ok(Result.success(TestCaseDetailDTO.fromEntity(testCase)));  // 返回测试用例详情
        } catch (Exception e) {
            log.error("获取测试用例详情失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));  // 返回错误信息
        }
    }
} 