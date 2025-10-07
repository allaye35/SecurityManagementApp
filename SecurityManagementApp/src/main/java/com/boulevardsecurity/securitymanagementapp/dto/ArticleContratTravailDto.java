// src/main/java/com/boulevardsecurity/securitymanagementapp/dto/ArticleContratTravailDto.java
package com.boulevardsecurity.securitymanagementapp.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ArticleContratTravailDto {
    private Long id;
    private String libelle;
    private String contenu;
    private Long contratDeTravailId;
}
