package com.testplatform.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RequirementCodeGenerator {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final AtomicInteger sequence = new AtomicInteger(1);
    
    /**
     * 生成需求编号
     * 格式：REQ + 年月日 + 4位序号
     * 例如：REQ202402130001
     */
    public String generateReqCode() {
        String date = LocalDateTime.now().format(formatter);
        String seq = String.format("%04d", sequence.getAndIncrement());
        return "REQ" + date + seq;
    }
    
    /**
     * 重置序号（可选，比如每天零点重置）
     */
    public void resetSequence() {
        sequence.set(1);
    }
} 