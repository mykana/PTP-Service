package com.testplatform.controller;

import com.testplatform.common.Result;
import com.testplatform.dto.ExecutorDTO;
import com.testplatform.dto.LoginRequest;
import com.testplatform.dto.LoginResponse;
import com.testplatform.entity.User;
import com.testplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<Result<User>> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            return ResponseEntity.ok(Result.success(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<Result<User>> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(Result.success(createdUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<Result<User>> updateUser(@PathVariable String username, @RequestBody User user) {
        try {
            User existingUser = userService.findByUsername(username);
            // 更新用户信息，但保持用户名不变
            user.setId(existingUser.getId());
            user.setUsername(username);
            User updatedUser = userService.createUser(user);
            return ResponseEntity.ok(Result.success(updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Result<Void>> deleteUser(@PathVariable String username) {
        try {
            User user = userService.findByUsername(username);
            // TODO: 实现删除用户的逻辑
            return ResponseEntity.ok(Result.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    /**
     * 获取所有可作为执行者的用户列表
     */
    @GetMapping("/executors")
    public ResponseEntity<Result<List<ExecutorDTO>>> getAllExecutors() {
        log.info("接收到获取执行者列表请求");
        try {
            List<ExecutorDTO> executors = userService.getAllExecutors();
            log.info("成功获取执行者列表，数量：{}", executors.size());
            return ResponseEntity.ok(Result.success(executors));
        } catch (Exception e) {
            log.error("获取执行者列表失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }
} 