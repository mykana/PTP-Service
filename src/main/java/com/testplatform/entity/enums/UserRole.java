package com.testplatform.entity.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    admin("admin"),
    test_manager("test_manager"),
    tester("tester");

    private final String role;

    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
} 