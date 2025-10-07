package com.boulevardsecurity.securitymanagementapp.model;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeZone;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "zones_de_travail")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "agents")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ZoneDeTravail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeZone typeZone;

    @Column(nullable = true)
    private String codePostal;

    @Column(nullable = true)
    private String ville;

    @Column(nullable = true)
    private String departement;

    @Column(nullable = true)
    private String region;

    @Column(nullable = true)
    private String pays;

    @ManyToMany(mappedBy = "zonesDeTravail", fetch = FetchType.EAGER)
    @Builder.Default
    private Set<AgentDeSecurite> agents = new HashSet<>();
}