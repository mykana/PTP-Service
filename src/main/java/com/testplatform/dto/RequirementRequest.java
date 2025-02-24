package com.testplatform.dto;

import com.testplatform.entity.enums.RequirementStatus;
import lombok.Data;

import java.util.List;

@Data
public class RequirementRequest {
    private String reqName;
    private Integer moduleId;
    private List<Long> executorIds;
    private String description;
    private RequirementStatus reqStatus;
} 