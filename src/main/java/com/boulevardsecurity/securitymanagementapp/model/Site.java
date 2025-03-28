package com.boulevardsecurity.securitymanagementapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "sites")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "missions")
public class Site {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = true)
    private String numero;       // Numéro de la rue (ex: 10)

    @Column(nullable = true)
    private String rue;           // Rue (ex: Rue de Rivoli)

    @Column(nullable = true)
    private String codePostal;     // Code Postal (ex: 75001)

    @Column(nullable = true)
    private String ville;          // Ville (ex: Paris)

    @Column(nullable = true)
    private String departement;     // Département (ex: Paris)

    @Column(nullable = true)
    private String region;          // Région (ex: Île-de-France)

    @Column(nullable = true)
    private String pays;            // Pays (ex: France)

    // Un site peut avoir plusieurs missions
    @OneToMany(mappedBy = "site", cascade = CascadeType.ALL)
    private List<Mission> missions;
}