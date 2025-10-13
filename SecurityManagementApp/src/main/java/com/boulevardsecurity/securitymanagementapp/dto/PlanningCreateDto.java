package com.boulevardsecurity.securitymanagementapp.dto;

import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PlanningCreateDto {
    
    private List<Long> missionIds;
}

