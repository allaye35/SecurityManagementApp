package com.boulevardsecurity.securitymanagementapp.dto;

import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.Enums.StatutAgent;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgentDeSecuriteDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String adresse;
    private LocalDate dateNaissance;
    private StatutAgent statut;
    private Role role;

    /** Pour l'UI : savoir si l'email est confirmé */
    private boolean emailVerified;
    
    /** Pour l'UI : savoir si le compte est approuvé par un admin */
    private boolean adminApproved;

    /* Identifiants des relations */
    private Set<Long> zonesDeTravailIds;
    private Set<Long> missionsIds;
    private List<Long> disponibilitesIds;
    private List<Long> cartesProfessionnellesIds;
    private List<Long> diplomesSSIAPIds;
    private List<Long> contratsDeTravailIds;
    private List<Long> notificationsIds;
}
