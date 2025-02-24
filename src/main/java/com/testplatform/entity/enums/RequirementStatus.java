package com.testplatform.entity.enums;

public enum RequirementStatus {
    测试中("测试中"),
    阻塞("阻塞"),
    开发中("开发中"),
    需求评审中("需求评审中"),
    已上线("已上线");

    private final String description;

    RequirementStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 