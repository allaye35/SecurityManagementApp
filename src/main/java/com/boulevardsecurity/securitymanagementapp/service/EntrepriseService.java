package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Entreprise;
import com.boulevardsecurity.securitymanagementapp.repository.EntrepriseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EntrepriseService {

    private final EntrepriseRepository entrepriseRepository;

    // 🔹 Récupérer toutes les entreprises
    public List<Entreprise> getAllEntreprises() {
        return entrepriseRepository.findAll();
    }

    // 🔹 Récupérer une entreprise par ID
    public Optional<Entreprise> getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id);
    }

    // 🔹 Ajouter ou mettre à jour une entreprise
    public Entreprise saveEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    // 🔹 Supprimer une entreprise par ID
    public void deleteEntreprise(Long id) {
        entrepriseRepository.deleteById(id);
    }
}
