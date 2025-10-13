// src/main/java/com/boulevardsecurity/securitymanagementapp/dto/ClientCreateDto.java
package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.ModeContactPrefere;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeClient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientCreateDto {

    @NotBlank
    private String password;

    private TypeClient typeClient;

    private String nom;
    private String prenom;
    private String siege;
    private String representant;
    private String numeroSiret;

    @Email
    private String email;
    private String telephone;
    private String adresse;
    private String numeroRue;
    private String codePostal;
    private String ville;
    private String pays;
    private ModeContactPrefere modeContactPrefere;

    private List<Long> devisIds;
    private List<Long> notificationIds;
}
