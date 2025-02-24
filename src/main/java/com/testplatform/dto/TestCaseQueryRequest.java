package com.testplatform.dto;

import com.testplatform.entity.enums.TestCasePriority;
import com.testplatform.entity.enums.TestCaseStatus;
import lombok.Data;

import java.util.List;

@Data
public class TestCaseQueryRequest {
    private Integer page = 1;
    private Integer pageSize = 10;
    private String title;
    private List<TestCasePriority> priority;
    private List<TestCaseStatus> status;
    private List<String> dateRange;
    private Integer userId;
    private String creatorName;
    private List<Integer> requirementIds;
} 