package com.testplatform.service;

import com.testplatform.entity.Module;
import com.testplatform.repository.ModuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;

    /**
     * 获取所有启用的模块
     */
    public List<Module> getAllActiveModules() {
        log.info("获取所有启用的模块");
        return moduleRepository.findByModuleStatus(true);
    }

    /**
     * 创建新模块
     */
    @Transactional
    public Module createModule(Module module) {
        log.info("创建新模块: {}", module.getModuleName());
        
        // 检查模块名是否已存在
        if (moduleRepository.existsByModuleName(module.getModuleName())) {
            log.error("模块名已存在: {}", module.getModuleName());
            throw new RuntimeException("模块名已存在");
        }

        return moduleRepository.save(module);
    }

    /**
     * 更新模块
     */
    @Transactional
    public Module updateModule(Integer id, Module module) {
        log.info("更新模块: {}", id);
        
        Module existingModule = moduleRepository.findById(id)
            .orElseThrow(() -> {
                log.error("模块不存在: {}", id);
                return new RuntimeException("模块不存在");
            });

        // 检查新名称是否与其他模块重复
        if (!existingModule.getModuleName().equals(module.getModuleName()) 
            && moduleRepository.existsByModuleName(module.getModuleName())) {
            log.error("模块名已存在: {}", module.getModuleName());
            throw new RuntimeException("模块名已存在");
        }

        existingModule.setModuleName(module.getModuleName());
        existingModule.setDescription(module.getDescription());
        existingModule.setModuleStatus(module.getModuleStatus());

        return moduleRepository.save(existingModule);
    }

    /**
     * 获取模块详情
     */
    public Module getModuleById(Integer id) {
        log.info("获取模块详情: {}", id);
        return moduleRepository.findById(id)
            .orElseThrow(() -> {
                log.error("模块不存在: {}", id);
                return new RuntimeException("模块不存在");
            });
    }

    /**
     * 启用/停用模块
     */
    @Transactional
    public void toggleModuleStatus(Integer id, Boolean status) {
        log.info("切换模块状态: {} -> {}", id, status);
        
        Module module = moduleRepository.findById(id)
            .orElseThrow(() -> {
                log.error("模块不存在: {}", id);
                return new RuntimeException("模块不存在");
            });
            
        module.setModuleStatus(status);
        moduleRepository.save(module);
    }
} 