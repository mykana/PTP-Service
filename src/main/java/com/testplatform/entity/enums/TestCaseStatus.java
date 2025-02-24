package com.testplatform.entity.enums;

/**
 * 测试用例状态枚举
 */
public enum TestCaseStatus {
    阻塞("阻塞"),
    测试中("测试中"),
    已完成("已完成"),
    编写_评审中("编写/评审中");

    private final String description;

    TestCaseStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 