package com.testplatform.dto;

import com.testplatform.entity.Requirement;
import com.testplatform.entity.enums.RequirementStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RequirementDTO {
    private Integer id;
    private String reqCode;
    private String reqName;
    private String description;
    private String creatorName;
    private String moduleName;
    private String executorIds;
    private RequirementStatus reqStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static RequirementDTO fromEntity(Requirement requirement) {
        RequirementDTO dto = new RequirementDTO();
        dto.setId(requirement.getId());
        dto.setReqCode(requirement.getReqCode());
        dto.setReqName(requirement.getReqName());
        dto.setDescription(requirement.getDescription());
        dto.setCreatorName(requirement.getCreator().getRealName());
        dto.setModuleName(requirement.getModule() != null ? requirement.getModule().getModuleName() : null);
        dto.setExecutorIds(requirement.getExecutorIds());
        dto.setReqStatus(requirement.getReqStatus());
        dto.setCreatedAt(requirement.getCreatedAt());
        dto.setUpdatedAt(requirement.getUpdatedAt());
        return dto;
    }
} 