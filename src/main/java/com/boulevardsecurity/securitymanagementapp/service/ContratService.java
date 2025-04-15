package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Contrat;
import com.boulevardsecurity.securitymanagementapp.model.Devis;
import java.util.List;

public interface ContratService {
    /**
     * Crée un nouveau contrat (Create).
     */
    Contrat createContrat(Contrat contrat);

    /**
     * Récupère un contrat par ID (Read).
     */
    Contrat getContratById(Long id);

    /**
     * Récupère tous les contrats (Read).
     */
    List<Contrat> getAllContrats();

    /**
     * Met à jour un contrat existant (Update).
     */
    Contrat updateContrat(Long id, Contrat contratData);

    /**
     * Supprime un contrat par ID (Delete).
     */
    void deleteContrat(Long id);

    /**
     * (Optionnel) Génère un contrat à partir d'un Devis.
     */
    Contrat generateFromDevis(Devis devis);
}
