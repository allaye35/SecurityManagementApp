package com.boulevardsecurity.securitymanagementapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "entreprises") // Nom de la table dans la base de données
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Entreprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String adresse;
    private String telephone;
}

