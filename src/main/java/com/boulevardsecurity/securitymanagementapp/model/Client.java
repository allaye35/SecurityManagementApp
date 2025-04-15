package com.boulevardsecurity.securitymanagementapp.model;

import com.boulevardsecurity.securitymanagementapp.Enums.ModeContactPrefere;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
//@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------------------------------------
    // Champs pour un client Particulier / Entreprise
    // ---------------------------------------------
    // Pour savoir si c'est un client particulier ou une société
    @Enumerated(EnumType.STRING)
    private TypeClient typeClient;
    // (TypeClient.PARTICULIER ou TypeClient.ENTREPRISE)

    // Si c'est un particulier, "nom" et "prenom" suffisent
    private String nom;
    private String prenom;

    // Si c'est une entreprise, on peut mettre la raison sociale
    private String raisonSociale;

    // Numéro SIRET (France) ou numéro d'immatriculation (ailleurs)
    private String numeroSiret;

    // ---------------------------------------------
    // Coordonnées
    // ---------------------------------------------
    private String email;
    private String telephone;
    private String adresse;
    private String codePostal;
    private String ville;
    private String pays;

    // ---------------------------------------------
    // Informations complémentaires
    // ---------------------------------------------
    @Column(length = 500)
    private String remarques;

    // Par exemple si le client préfère être contacté par mail ou téléphone
    @Enumerated(EnumType.STRING)
    private ModeContactPrefere modeContactPrefere;

    // ---------------------------------------------
    // Relations avec Devis & Contrat
    // ---------------------------------------------
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Devis> devisList = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Contrat> contrats = new ArrayList<>();
}
