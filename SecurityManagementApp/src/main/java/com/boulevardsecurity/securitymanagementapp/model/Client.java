// src/main/java/com/boulevardsecurity/securitymanagementapp/model/Client.java
package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.ModeContactPrefere;
import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeClient;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* --------- Auth / Rôle --------- */
    @Column(nullable = false, unique = true)
    private String email;

    /** Stocké en base mais jamais exposé en JSON */
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.CLIENT;

    /* --------- Typologie --------- */
    @Enumerated(EnumType.STRING)
    private TypeClient typeClient; // PARTICULIER / ENTREPRISE

    /* --------- Si particulier --------- */
    private String nom;
    private String prenom;

    /* --------- Si entreprise --------- */
    private String siege;
    private String representant;
    private String numeroSiret;

    /* --------- Coordonnées --------- */
    private String telephone;
    private String adresse;
    private String numeroRue;
    private String codePostal;
    private String ville;
    private String pays;

    @Enumerated(EnumType.STRING)
    private ModeContactPrefere modeContactPrefere;

    /* --------- Sécurité / activation --------- */
    /** L’email a été confirmé (lien ou code) */
    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    /** Le compte a été validé par un administrateur */
    @Column(nullable = false)
    @Builder.Default
    private boolean adminApproved = false;

    /** Horodatage & référence d’admin lors de l’approbation (pour l’audit) */
    private Instant adminApprovedAt;
    private Long adminApprovedById;

    /** MAJ à chaque changement de mot de passe (invalide les anciens tokens si nécessaire) */
    private Instant passwordChangedAt;

    /* --------- Relations --------- */
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnore
    private List<Devis> devisList = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GestionnaireNotifications> notifications = new ArrayList<>();
}
