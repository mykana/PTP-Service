package com.testplatform.controller;

import com.testplatform.common.Result;
import com.testplatform.dto.LoginRequest;
import com.testplatform.dto.LoginResponse;
import com.testplatform.dto.MessageResponse;
import com.testplatform.dto.RegisterRequest;
import com.testplatform.entity.User;
import com.testplatform.service.UserService;
import com.testplatform.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<Result<User>> register(@RequestBody RegisterRequest request) {
        try {
            log.info("开始注册用户: {}", request.getUsername());
            
            // 创建用户实体
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(request.getPassword());
            user.setRealName(request.getRealName());
            user.setRole(request.getRole());
            
            // 注册用户
            User registeredUser = userService.register(user);
            
            log.info("用户注册成功: {}", request.getUsername());
            return ResponseEntity.ok(Result.success(registeredUser));
        } catch (Exception e) {
            log.error("用户注册失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Result.error(400, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Result<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        log.debug("尝试登录用户: {}", loginRequest.getUsername());
        
        try {
            // 调用 service 层的登录方法
            LoginResponse loginResponse = userService.login(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            return ResponseEntity.ok(Result.success(loginResponse));
            
        } catch (AuthenticationException e) {
            log.error("认证失败: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(Result.error(401, "用户名或密码错误"));
        }
    }

    @GetMapping("/info")
    public ResponseEntity<Result<LoginResponse.UserInfo>> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.substring(7); // 去掉"Bearer "前缀
            Claims claims = jwtUtil.getClaimsFromToken(actualToken);
            User user = userService.findByUsername(claims.getSubject());
            
            LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .role(user.getRole().name())
                .build();
            
            return ResponseEntity.ok(Result.success(userInfo));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "未登录或token已过期"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Result<Void>> logout(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.substring(7);
            Claims claims = jwtUtil.getClaimsFromToken(actualToken);
            userService.logout(claims.getSubject());
            return ResponseEntity.ok(Result.success(null));
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(Result.error(401, "退出登录失败"));
        }
    }
} 