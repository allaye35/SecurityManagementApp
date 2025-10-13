package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.ModeContactPrefere;
import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeClient;
import lombok.*;

import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ClientDto {
    private Long id;
    private Role role;
    private TypeClient typeClient;

    private String nom;
    private String prenom;
    private String siege;
    private String representant;
    private String numeroSiret;

    private String email;
    private String telephone;
    private String adresse;
    private String numeroRue;
    private String codePostal;
    private String ville;
    private String pays;
    private ModeContactPrefere modeContactPrefere;

    private boolean emailVerified;
    private boolean adminApproved;

    private List<Long> devisIds;
    private List<Long> notificationIds;
}