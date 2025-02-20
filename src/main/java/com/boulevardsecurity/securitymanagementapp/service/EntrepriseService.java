package com.boulevardsecurity.securitymanagementapp.service;


import com.boulevardsecurity.securitymanagementapp.model.Entreprise;
import com.boulevardsecurity.securitymanagementapp.repository.EntrepriseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    public EntrepriseService(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Entreprise getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id).orElse(null);
    }

    public Entreprise createEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    public Entreprise updateEntreprise(Long id, Entreprise updatedEntreprise) {
        Optional<Entreprise> entrepriseOpt = entrepriseRepository.findById(id);
        if (entrepriseOpt.isPresent()) {
            Entreprise entreprise = entrepriseOpt.get();
            entreprise.setNom(updatedEntreprise.getNom());
            entreprise.setAdresse(updatedEntreprise.getAdresse());
            entreprise.setTelephone(updatedEntreprise.getTelephone());
            return entrepriseRepository.save(entreprise);
        }
        return null;
    }

    public void deleteEntreprise(Long id) {
        entrepriseRepository.deleteById(id);
    }
}

