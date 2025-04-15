package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ContratDeTravailService {

    // CREATE
    ContratDeTravail createContrat(ContratDeTravail contrat);

    // READ (un seul) - Optional pour gérer 404
    Optional<ContratDeTravail> getContratById(Long id);

    // READ - liste
    List<ContratDeTravail> getAllContrats();

    // UPDATE
    ContratDeTravail updateContrat(Long id, ContratDeTravail updatedContrat);

    // DELETE
    void deleteContrat(Long id);

    // PATCH - prolonger la date de fin
    boolean prolongerContrat(Long id, LocalDate nouvelleDateFin);

    // READ - Trouver tous les contrats d'un Agent
    // Dans l’interface ContratDeTravailService
    List<ContratDeTravail> getContratsByAgentId(Long agentId);
}

