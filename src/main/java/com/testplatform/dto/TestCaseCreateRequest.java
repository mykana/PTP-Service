package com.testplatform.dto;

import com.testplatform.entity.enums.TestCasePriority;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseCreateRequest {
    private String title;
    private TestCasePriority priority;
    private List<Integer> requirementIds;
    private String steps;
    private String precondition;
    private String remark;
} 