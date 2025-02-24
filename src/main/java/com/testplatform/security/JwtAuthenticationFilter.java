package com.testplatform.security;

import com.testplatform.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT 认证过滤器
 * 该过滤器用于处理每个请求，验证JWT token的有效性，并设置用户的认证信息到安全上下文中。
 */
@Component
@RequiredArgsConstructor
@Slf4j  // 使用Slf4j记录日志
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;  // JWT工具类，用于处理token的生成和验证
    private final UserDetailsService userDetailsService;  // 用户详情服务，用于加载用户信息

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        log.debug("Processing request: {} {}", request.getMethod(), request.getRequestURI());
        log.debug("Authorization header: {}", request.getHeader("Authorization"));
        
        try {
            // 从请求中获取JWT token
            String jwt = getJwtFromRequest(request);
            log.debug("Received JWT: {}", jwt);

            if (jwt != null) {
                // 从token中提取用户名
                String username = jwtUtil.getUsernameFromToken(jwt);
                log.debug("Username from JWT: {}", username);

                // 如果用户名不为空且当前没有认证信息，则加载用户信息
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    log.debug("Loaded UserDetails: {}", userDetails);
                    
                    // 验证token的有效性
                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        // 创建认证对象并设置到安全上下文中
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                userDetails, 
                                null, 
                                userDetails.getAuthorities()
                            );
                        
                        // 设置请求的详细信息
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        log.debug("Authentication set in SecurityContext");
                    } else {
                        log.warn("Token validation failed");
                    }
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("JWT token已过期: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"code\":401,\"message\":\"Token已过期，请重新登录\"}");
            return;  // 直接返回，不继续处理请求
        } catch (Exception e) {
            log.error("认证过程出错: {}", e.getMessage(), e);
        }

        // 继续执行过滤链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中获取JWT token
     * @param request HTTP请求
     * @return 提取的JWT token，如果没有则返回null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // 去掉"Bearer "前缀
        }
        return null;  // 如果没有token，返回null
    }
} 