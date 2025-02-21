package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Entreprise;
import com.boulevardsecurity.securitymanagementapp.repository.EntrepriseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    @Autowired
    public EntrepriseService(EntrepriseRepository entrepriseRepository) {
        this.entrepriseRepository = entrepriseRepository;
    }

    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    public Optional<Entreprise> getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id);
    }


    public Entreprise createEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    public Entreprise updateEntreprise(Long id, Entreprise updatedEntreprise) {
        Optional<Entreprise> existingEntreprise = entrepriseRepository.findById(id);
        if (existingEntreprise.isPresent()) {
            Entreprise entreprise = existingEntreprise.get();
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
