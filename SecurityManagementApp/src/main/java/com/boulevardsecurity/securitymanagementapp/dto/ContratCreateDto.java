// src/main/java/com/boulevardsecurity/securitymanagementapp/dto/ContratCreateDto.java
package com.boulevardsecurity.securitymanagementapp.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContratCreateDto {
    
    private String referenceContrat;
    
    private LocalDate dateSignature;
    
    private Integer dureeMois;
    
    private Boolean taciteReconduction;
    
    private Integer preavisMois;    
    private Long devisId;
    
    private List<Long> missionIds;
    
    private List<Long> articleIds;
}
