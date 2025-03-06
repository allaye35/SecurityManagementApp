package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RapportIntervention {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateIntervention;
    private String description;
    private String agentNom;
    private String dateCreation;
    private String dateModification;
    private String agentEmail;
    private String agentTelephone;
    private String contenu;
    private String status; // Ex: "En cours", "Terminé", "Annulé"

    //  Relation avec Mission
    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = true) // Clé étrangère vers Mission
    private Mission mission;
}
