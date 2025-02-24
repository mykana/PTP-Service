package com.testplatform.dto;

import com.testplatform.entity.enums.UserRole;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String realName;
    private UserRole role;
} 