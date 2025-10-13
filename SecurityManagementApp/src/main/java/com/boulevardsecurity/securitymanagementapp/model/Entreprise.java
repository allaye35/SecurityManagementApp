package com.boulevardsecurity.securitymanagementapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "entreprises")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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

    @Column(nullable = false, unique = true)
    private String nom;
    private String siretPrestataire;
    private String representantPrestataire;

    private String numeroRue;
    private String rue;
    private String codePostal;
    private String ville;
    private String pays;

    @Column(nullable = false, unique = true)
    private String telephone;

    private String email;

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<Devis> devisList = new ArrayList<>();

    @OneToMany(mappedBy = "entreprise", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ContratDeTravail> contratsDeTravail = new ArrayList<>();

            ;
}
