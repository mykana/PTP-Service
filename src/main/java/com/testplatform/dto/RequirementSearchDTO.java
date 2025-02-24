package com.testplatform.dto;

import com.testplatform.entity.Requirement;
import lombok.Data;

@Data
public class RequirementSearchDTO {
    private Integer id;
    private String reqCode;
    private String reqName;

    public static RequirementSearchDTO fromEntity(Requirement requirement) {
        RequirementSearchDTO dto = new RequirementSearchDTO();
        dto.setId(requirement.getId());
        dto.setReqCode(requirement.getReqCode());
        dto.setReqName(requirement.getReqName());
        return dto;
    }
} 