package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Devis;
import java.util.List;

public interface DevisService {

    /**
     * Crée un nouveau devis.
     */
    Devis createDevis(Devis devis);

    /**
     * Récupère un devis par son ID.
     */
    Devis getDevisById(Long id);

    /**
     * Récupère la liste de tous les devis.
     */
    List<Devis> getAllDevis();

    /**
     * Met à jour un devis existant.
     */
    Devis updateDevis(Long id, Devis devisData);

    /**
     * Supprime un devis par son ID.
     */
    void deleteDevis(Long id);

    /**
     * (Optionnel) Exemple : accepter un devis et changer son statut.
     */
    Devis accepterDevis(Long id);
}
