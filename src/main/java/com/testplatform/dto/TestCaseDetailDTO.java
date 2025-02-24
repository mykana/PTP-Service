package com.testplatform.dto;

import com.testplatform.entity.TestCase;
import com.testplatform.entity.enums.TestCasePriority;
import com.testplatform.entity.enums.TestCaseStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TestCaseDetailDTO {
    private Integer id;
    private String title;
    private TestCasePriority priority;
    private TestCaseStatus status;
    private String steps;
    private String precondition;
    private String remark;
    private String creator;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RequirementBriefDTO> requirements;

    @Data
    public static class RequirementBriefDTO {
        private Integer id;
        private String reqCode;
        private String reqName;
    }

    public static TestCaseDetailDTO fromEntity(TestCase testCase) {
        TestCaseDetailDTO dto = new TestCaseDetailDTO();
        dto.setId(testCase.getId());
        dto.setTitle(testCase.getTitle());
        dto.setPriority(testCase.getPriority());
        dto.setStatus(testCase.getStatus());
        dto.setSteps(testCase.getCaseDetail());
        dto.setPrecondition(testCase.getPrecondition());
        dto.setCreator(testCase.getCreator().getRealName());
        dto.setCreatedAt(testCase.getCreatedAt());
        dto.setUpdatedAt(testCase.getUpdatedAt());
        dto.setRemark(testCase.getCaseRemark());
        
        if (testCase.getRequirements() != null) {
            dto.setRequirements(testCase.getRequirements().stream()
                .map(req -> {
                    RequirementBriefDTO briefDTO = new RequirementBriefDTO();
                    briefDTO.setId(req.getId());
                    briefDTO.setReqCode(req.getReqCode());
                    briefDTO.setReqName(req.getReqName());
                    return briefDTO;
                })
                .collect(Collectors.toList()));
        }
        
        return dto;
    }
} 