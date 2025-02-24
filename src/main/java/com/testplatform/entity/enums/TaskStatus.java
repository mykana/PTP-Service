package com.testplatform.entity.enums;

/**
 * 执行任务状态枚举
 */
public enum TaskStatus {
    QUEUED("已排队"),
    RUNNING("执行中"),
    COMPLETED("已完成"),
    FAILED("执行失败"),
    CANCELLED("已取消");

    private final String description;

    TaskStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 