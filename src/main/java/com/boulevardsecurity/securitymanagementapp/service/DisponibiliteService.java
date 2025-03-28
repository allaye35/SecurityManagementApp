package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Disponibilite;
import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.repository.DisponibiliteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DisponibiliteService {

    private final DisponibiliteRepository disponibiliteRepository;
    private final AgentDeSecuriteRepository agentDeSecuriteRepository;

    @Autowired
    public DisponibiliteService(DisponibiliteRepository disponibiliteRepository, AgentDeSecuriteRepository agentDeSecuriteRepository) {
        this.disponibiliteRepository = disponibiliteRepository;
        this.agentDeSecuriteRepository = agentDeSecuriteRepository;
    }

    // 🔹 Ajouter une disponibilité pour un agent
    public Disponibilite ajouterDisponibilite(Long agentId, Disponibilite disponibilite) {
        // Vérifier si l'agent existe
        AgentDeSecurite agent = agentDeSecuriteRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé avec l'ID : " + agentId));

        // Associer l'agent à la disponibilité
        disponibilite.setAgentDeSecurite(agent);

        // Sauvegarder la disponibilité
        return disponibiliteRepository.save(disponibilite);
    }

    // 🔹 Créer une nouvelle disponibilité
    public Disponibilite creerDisponibilite(Disponibilite disponibilite) {
        return disponibiliteRepository.save(disponibilite);
    }

    // 🔹 Obtenir toutes les disponibilités
    public List<Disponibilite> obtenirToutesLesDisponibilites() {
        return disponibiliteRepository.findAll();
    }

    // 🔹 Obtenir une disponibilité par son ID
    public Optional<Disponibilite> obtenirDisponibiliteParId(int id) {
        return disponibiliteRepository.findById(id);
    }

    // 🔹 Obtenir toutes les disponibilités d'un agent
    public List<Disponibilite> obtenirDisponibilitesParAgent(Long agentId) {
        return disponibiliteRepository.findByAgentDeSecuriteId(agentId);
    }

    // 🔹 Mettre à jour une disponibilité existante
    public Disponibilite mettreAJourDisponibilite(int id, Disponibilite nouvelleDisponibilite) {
        Disponibilite disponibiliteExistante = disponibiliteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée !"));

        disponibiliteExistante.setDateDebut(nouvelleDisponibilite.getDateDebut());
        disponibiliteExistante.setDateFin(nouvelleDisponibilite.getDateFin());
        disponibiliteExistante.setAgentDeSecurite(nouvelleDisponibilite.getAgentDeSecurite());

        return disponibiliteRepository.save(disponibiliteExistante);
    }

    // 🔹 Supprimer une disponibilité par son ID
    public void supprimerDisponibilite(int id) {
        if (disponibiliteRepository.existsById(id)) {
            disponibiliteRepository.deleteById(id);
        } else {
            throw new RuntimeException("Disponibilité non trouvée !");
        }
    }
}
