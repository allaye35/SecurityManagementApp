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

    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    public Optional<Facture> getFactureById(Long id) {
        return factureRepository.findById(id);
    }

    public Facture createFacture(Facture facture) {
        return factureRepository.save(facture);
    }

    public Facture updateFacture(Long id, Facture updatedFacture) {
        return factureRepository.findById(id)
                .map(facture -> {
                    facture.setNumeroFacture(updatedFacture.getNumeroFacture());
                    facture.setMontant(updatedFacture.getMontant());
                    facture.setDateEmission(updatedFacture.getDateEmission());
                    facture.setStatutPaiement(updatedFacture.getStatutPaiement());
                    return factureRepository.save(facture);
                })
                .orElse(null);
    }

    public void deleteFacture(Long id) {
        factureRepository.deleteById(id);
    }
}
