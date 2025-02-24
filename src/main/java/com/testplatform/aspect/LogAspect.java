package com.testplatform.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LogAspect {

    private final ObjectMapper objectMapper;

    @Pointcut("execution(* com.testplatform.controller..*.*(..))")
    public void controllerPoint() {}

    @Around("controllerPoint()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        
        // 记录请求信息
        log.info("===================请求开始===================");
        log.info("URL         : {}", request.getRequestURL().toString());
        log.info("HTTP Method : {}", request.getMethod());
        log.info("Class Method: {}.{}", point.getSignature().getDeclaringTypeName(), point.getSignature().getName());
        log.info("IP         : {}", request.getRemoteAddr());
        log.info("Request Args: {}", Arrays.toString(point.getArgs()));

        // 执行请求
        Object result = point.proceed();

        // 记录响应信息
        log.info("Response    : {}", objectMapper.writeValueAsString(result));
        log.info("Time Cost   : {} ms", System.currentTimeMillis() - beginTime);
        log.info("===================请求结束===================");

        return result;
    }
} 