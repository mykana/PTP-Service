package com.testplatform.entity.enums;

public enum TestCasePriority {
    P0("最高"),
    P1("高"),
    P2("中");

    private final String description;

    TestCasePriority(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 