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

    // 🔹 Ajouter une entreprise
    public Entreprise createEntreprise(Entreprise entreprise) {
        return entrepriseRepository.save(entreprise);
    }

    // 🔹 Modifier une entreprise
    public Entreprise updateEntreprise(Long id, Entreprise updatedEntreprise) {
        return entrepriseRepository.findById(id).map(existingEntreprise -> {
            existingEntreprise.setNom(updatedEntreprise.getNom());
            existingEntreprise.setAdresse(updatedEntreprise.getAdresse());
            existingEntreprise.setEmail(updatedEntreprise.getEmail());
            existingEntreprise.setTelephone(updatedEntreprise.getTelephone());
            return entrepriseRepository.save(existingEntreprise);
        }).orElseThrow(() -> new RuntimeException("Entreprise non trouvée avec ID: " + id));
    }

    // 🔹 Supprimer une entreprise
    public void deleteEntreprise(Long id) {
        entrepriseRepository.deleteById(id);
    }
}
