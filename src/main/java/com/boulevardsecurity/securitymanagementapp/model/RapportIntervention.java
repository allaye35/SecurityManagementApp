package com.boulevardsecurity.securitymanagementapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "mission")
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
    private String status; // Ex : "En cours", "Terminé", "Annulé"

    // Relation avec Mission (un rapport appartient à une mission)
    @ManyToOne
    @JoinColumn(name = "mission_id", nullable = true)
    private Mission mission;
}
