package com.testplatform.service;

import com.testplatform.dto.LoginResponse;
import com.testplatform.dto.ExecutorDTO;
import com.testplatform.entity.User;
import com.testplatform.repository.UserRepository;
import com.testplatform.util.JwtUtil;
import com.testplatform.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户服务层
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RedisUtil redisUtil;
    private final AuthenticationManager authenticationManager;
    
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 用户登录
     * 返回完整的登录响应，包含用户信息和token
     */
    public LoginResponse login(String username, String password) {
        // 验证参数
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        // 认证
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        
        // 生成token
        String token = jwtUtil.generateToken(authentication);
        
        // 获取用户信息
        User user = findByUsername(username);
        
        // 构建响应
        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
            .id(user.getId())
            .username(user.getUsername())
            .realName(user.getRealName())
            .role(user.getRole().name())
            .build();
            
        return LoginResponse.builder()
            .token(token)
            .userInfo(userInfo)
            .build();
    }

    /**
     * 用户注册
     */
    @Transactional
    public User register(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 保存用户
        return userRepository.save(user);
    }

    /**
     * 根据用户名查找用户
     */
    public User findByUsername(String username) {
        // 先从Redis缓存中获取
        Object cachedUser = redisUtil.getUser(username);
        if (cachedUser != null) {
            return (User) cachedUser;
        }
        
        // 缓存中没有，从数据库查询
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 退出登录
     */
    public void logout(String username) {
        // 清除Redis中的token和用户信息
        redisUtil.deleteToken(username);
        redisUtil.deleteUser(username);
    }

    /**
     * 创建新用户
     */
    public User createUser(User user) {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 保存用户
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        // 检查用户是否存在
        User existingUser = findByUsername(user.getUsername());
        
        // 更新用户信息
        user.setId(existingUser.getId());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        return userRepository.save(user);
    }

    public void deleteUser(String username) {
        User user = findByUsername(username);
        userRepository.delete(user);
        // 清除缓存
        redisUtil.deleteUser(username);
        redisUtil.deleteToken(username);
    }

    /**
     * 获取所有可作为执行者的用户列表
     */
    public List<ExecutorDTO> getAllExecutors() {
        log.info("获取所有执行者列表");
        
        return userRepository.findAll().stream()
            .map(user -> {
                ExecutorDTO dto = new ExecutorDTO();
                dto.setId(user.getId());
                dto.setRealName(user.getRealName());
                dto.setRole(user.getRole().name());
                return dto;
            })
            .collect(Collectors.toList());
    }
} 