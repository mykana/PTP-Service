package com.testplatform.dto;

import com.testplatform.entity.enums.TestCasePriority;
import com.testplatform.entity.enums.TestCaseStatus;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseUpdateRequest {
    private String title;
    private TestCasePriority priority;
    private TestCaseStatus status;
    private String steps;
    private String precondition;
    private String remark;
    private List<Integer> requirementIds;
} 