package com.testplatform.controller;

import com.testplatform.common.Result;
import com.testplatform.dto.RequirementRequest;
import com.testplatform.dto.RequirementDTO;
import com.testplatform.dto.RequirementSearchRequest;
import com.testplatform.dto.RequirementSearchDTO;
import com.testplatform.entity.Requirement;
import com.testplatform.service.RequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/requirements")
@RequiredArgsConstructor
public class RequirementController {

    private final RequirementService requirementService;

    /**
     * 获取需求列表
     */
    @GetMapping
    public ResponseEntity<Result<Map<String, Object>>> getRequirements(
            @RequestParam(required = false) String reqCode,
            @RequestParam(required = false) String reqName,
            @RequestParam(required = false) Integer moduleId,
            @RequestParam(required = false) String reqStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        log.info("接收到获取需求列表请求");
        try {
            Page<Requirement> requirementPage = requirementService.findRequirements(
                reqCode, reqName, moduleId, reqStatus, PageRequest.of(page, pageSize));
            
            List<RequirementDTO> dtoList = requirementPage.getContent().stream()
                .map(RequirementDTO::fromEntity)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", requirementPage.getTotalElements());
            response.put("list", dtoList);
            
            log.info("成功获取需求列表，总数：{}", requirementPage.getTotalElements());
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("获取需求列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 创建需求
     */
    @PostMapping
    public ResponseEntity<Result<Requirement>> createRequirement(
            @RequestBody RequirementRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("接收到创建需求请求: {}", request.getReqName());
        try {
            Requirement requirement = requirementService.createRequirement(request, userDetails);
            log.info("需求创建成功: {}", requirement.getId());
            return ResponseEntity.ok(Result.success(requirement));
        } catch (Exception e) {
            log.error("创建需求失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 更新需求
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<Void>> updateRequirement(
            @PathVariable Integer id,
            @RequestBody RequirementRequest request) {
        
        log.info("接收到更新需求请求: {}", id);
        try {
            requirementService.updateRequirement(id, request);
            log.info("需求更新成功: {}", id);
            return ResponseEntity.ok(Result.success(null));
        } catch (Exception e) {
            log.error("更新需求失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 获取需求详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<Requirement>> getRequirement(@PathVariable Integer id) {
        log.info("接收到获取需求详情请求: {}", id);
        try {
            Requirement requirement = requirementService.getRequirementById(id);
            log.info("成功获取需求详情: {}", id);
            return ResponseEntity.ok(Result.success(requirement));
        } catch (Exception e) {
            log.error("获取需求详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 搜索需求
     */
    @PostMapping("/search")
    public ResponseEntity<Result<Map<String, Object>>> searchRequirements(
            @RequestBody RequirementSearchRequest request) {
        
        log.info("接收到需求搜索请求，关键字: {}", request.getKeyword());
        try {
            // 将页码转换为从0开始
            int pageNumber = Math.max(0, request.getPage() - 1);
            
            Page<Requirement> requirementPage = requirementService.searchRequirements(
                request.getKeyword(),
                PageRequest.of(pageNumber, request.getPageSize())
            );
            
            // 转换为DTO
            List<RequirementSearchDTO> dtoList = requirementPage.getContent().stream()
                .map(RequirementSearchDTO::fromEntity)
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("total", requirementPage.getTotalElements());
            response.put("list", dtoList);
            
            log.info("需求搜索成功，找到 {} 条记录", requirementPage.getTotalElements());
            return ResponseEntity.ok(Result.success(response));
        } catch (Exception e) {
            log.error("需求搜索失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }
} 