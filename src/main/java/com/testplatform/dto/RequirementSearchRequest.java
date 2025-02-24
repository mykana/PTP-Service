package com.testplatform.dto;

import lombok.Data;

@Data
public class RequirementSearchRequest {
    private String keyword;
    private int page = 1;
    private int pageSize = 10;
} 