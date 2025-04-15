package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Facture;
import com.boulevardsecurity.securitymanagementapp.repository.FactureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    // -------------------------------------------------------------------------
    // 1) Récupérer TOUTES les factures
    // -------------------------------------------------------------------------
    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    // -------------------------------------------------------------------------
    // 2) Récupérer UNE facture par ID
    // -------------------------------------------------------------------------
    public Optional<Facture> getFactureById(Long id) {
        return factureRepository.findById(id);
    }

    // -------------------------------------------------------------------------
    // 3) Créer une nouvelle facture
    // -------------------------------------------------------------------------
    public Facture createFacture(Facture facture) {
        // Optionnel : recalcul des montants
        // facture.recalculerMontants();
        return factureRepository.save(facture);
    }

    // -------------------------------------------------------------------------
    // 4) Mettre à jour une facture existante
    // -------------------------------------------------------------------------
    public Facture updateFacture(Long id, Facture updatedFacture) {
        return factureRepository.findById(id)
                .map(facture -> {
                    // Mets à jour les champs pertinents
                    facture.setNumeroFacture(updatedFacture.getNumeroFacture());
                    facture.setDateEmission(updatedFacture.getDateEmission());
                    facture.setTotalHT(updatedFacture.getTotalHT());
                    facture.setTauxTVA(updatedFacture.getTauxTVA());
                    facture.setTotalTTC(updatedFacture.getTotalTTC());
                    facture.setStatut(updatedFacture.getStatut());
                    facture.setDatePaiement(updatedFacture.getDatePaiement());
                    facture.setDevis(updatedFacture.getDevis());

                    // Optionnel : recalcul des montants
                    // facture.recalculerMontants();

                    return factureRepository.save(facture);
                })
                .orElse(null); // ou tu peux lever une exception
    }

    // -------------------------------------------------------------------------
    // 5) Supprimer une facture
    // -------------------------------------------------------------------------
    public void deleteFacture(Long id) {
        factureRepository.deleteById(id);
    }
}
