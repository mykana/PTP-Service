package com.testplatform.controller;

import com.testplatform.common.Result;
import com.testplatform.entity.Module;
import com.testplatform.service.ModuleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;

    /**
     * 获取所有启用的模块
     */
    @GetMapping
    public ResponseEntity<Result<List<Module>>> getAllModules() {
        log.info("接收到获取模块列表请求");
        try {
            List<Module> modules = moduleService.getAllActiveModules();
            log.info("成功获取{}个模块", modules.size());
            return ResponseEntity.ok(Result.success(modules));
        } catch (Exception e) {
            log.error("获取模块列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 创建新模块
     */
    @PostMapping
    public ResponseEntity<Result<Module>> createModule(@RequestBody Module module) {
        log.info("接收到创建模块请求: {}", module.getModuleName());
        try {
            Module createdModule = moduleService.createModule(module);
            log.info("模块创建成功: {}", createdModule.getId());
            return ResponseEntity.ok(Result.success(createdModule));
        } catch (Exception e) {
            log.error("创建模块失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 更新模块
     */
    @PutMapping("/{id}")
    public ResponseEntity<Result<Module>> updateModule(
            @PathVariable Integer id,
            @RequestBody Module module) {
        log.info("接收到更新模块请求: {}", id);
        try {
            Module updatedModule = moduleService.updateModule(id, module);
            log.info("模块更新成功: {}", id);
            return ResponseEntity.ok(Result.success(updatedModule));
        } catch (Exception e) {
            log.error("更新模块失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 获取模块详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Result<Module>> getModule(@PathVariable Integer id) {
        log.info("接收到获取模块详情请求: {}", id);
        try {
            Module module = moduleService.getModuleById(id);
            log.info("成功获取模块详情: {}", id);
            return ResponseEntity.ok(Result.success(module));
        } catch (Exception e) {
            log.error("获取模块详情失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 启用/停用模块
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Result<Void>> toggleModuleStatus(
            @PathVariable Integer id,
            @RequestParam Boolean status) {
        log.info("接收到切换模块状态请求: {} -> {}", id, status);
        try {
            moduleService.toggleModuleStatus(id, status);
            log.info("模块状态切换成功: {}", id);
            return ResponseEntity.ok(Result.success(null));
        } catch (Exception e) {
            log.error("切换模块状态失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }
} 