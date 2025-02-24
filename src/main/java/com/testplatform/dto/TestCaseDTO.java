package com.testplatform.dto;

import com.testplatform.entity.TestCase;
import com.testplatform.entity.enums.TestCasePriority;
import com.testplatform.entity.enums.TestCaseStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TestCaseDTO {
    private Integer id;
    private String title;
    private TestCasePriority priority;
    private TestCaseStatus status;
    private String caseDetail;
    private String precondition;
    private String creatorName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<RequirementBriefDTO> requirements;

    @Data
    public static class RequirementBriefDTO {
        private Integer id;
        private String reqCode;
        private String reqName;
    }

    public static TestCaseDTO fromEntity(TestCase testCase) {
        TestCaseDTO dto = new TestCaseDTO();
        dto.setId(testCase.getId());
        dto.setTitle(testCase.getTitle());
        dto.setPriority(testCase.getPriority());
        dto.setStatus(testCase.getStatus());
        dto.setCaseDetail(testCase.getCaseDetail());
        dto.setPrecondition(testCase.getPrecondition());
        dto.setCreatorName(testCase.getCreator().getRealName());
        dto.setCreatedAt(testCase.getCreatedAt());
        dto.setUpdatedAt(testCase.getUpdatedAt());
        
        // 转换关联的需求信息
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