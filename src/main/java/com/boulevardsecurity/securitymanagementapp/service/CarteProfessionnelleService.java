package com.boulevardsecurity.securitymanagementapp.service;
import com.boulevardsecurity.securitymanagementapp.model.CarteProfessionnelle;
import com.boulevardsecurity.securitymanagementapp.repository.CarteProfessionnelleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarteProfessionnelleService {

    private final CarteProfessionnelleRepository carteProfessionnelleRepository;

    @Autowired
    public CarteProfessionnelleService(CarteProfessionnelleRepository carteProfessionnelleRepository) {
        this.carteProfessionnelleRepository = carteProfessionnelleRepository;
    }

    //  Créer une nouvelle carte professionnelle
    public CarteProfessionnelle creerCarte(CarteProfessionnelle carte) {
        return carteProfessionnelleRepository.save(carte);
    }

    //  Obtenir toutes les cartes professionnelles
    public List<CarteProfessionnelle> obtenirToutesLesCartes() {
        return carteProfessionnelleRepository.findAll();
    }

    //  Obtenir une carte professionnelle par ID
    public Optional<CarteProfessionnelle> obtenirCarteParId(Long id) {
        return carteProfessionnelleRepository.findById(id);
    }

    // Obtenir toutes les cartes d'un agent par son ID
    public List<CarteProfessionnelle> obtenirCartesParAgent(Long agentId) {
        return carteProfessionnelleRepository.findByAgentDeSecuriteId(agentId);
    }

    //  Mettre à jour une carte professionnelle existante
    public CarteProfessionnelle mettreAJourCarte(Long id, CarteProfessionnelle nouvelleCarte) {
        CarteProfessionnelle carteExistante = carteProfessionnelleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Carte professionnelle non trouvée !"));

        carteExistante.setTypeCarte(nouvelleCarte.getTypeCarte());
        carteExistante.setNumeroCarte(nouvelleCarte.getNumeroCarte());
        carteExistante.setDateDebut(nouvelleCarte.getDateDebut());
        carteExistante.setDateFin(nouvelleCarte.getDateFin());
        carteExistante.setAgentDeSecurite(nouvelleCarte.getAgentDeSecurite());

        return carteProfessionnelleRepository.save(carteExistante);
    }

    //  Supprimer une carte professionnelle par ID
    public void supprimerCarte(Long id) {
        if (carteProfessionnelleRepository.existsById(id)) {
            carteProfessionnelleRepository.deleteById(id);
        } else {
            throw new RuntimeException("Carte professionnelle non trouvée !");
        }
    }
}

