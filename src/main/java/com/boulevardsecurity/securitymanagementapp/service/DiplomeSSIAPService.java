package com.boulevardsecurity.securitymanagementapp.service;
import com.boulevardsecurity.securitymanagementapp.model.DiplomeSSIAP;
import com.boulevardsecurity.securitymanagementapp.repository.DiplomeSSIAPRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DiplomeSSIAPService {

    private final DiplomeSSIAPRepository diplomeSSIAPRepository;

    @Autowired
    public DiplomeSSIAPService(DiplomeSSIAPRepository diplomeSSIAPRepository) {
        this.diplomeSSIAPRepository = diplomeSSIAPRepository;
    }

    // Créer un nouveau diplôme SSIAP
    public DiplomeSSIAP creerDiplome(DiplomeSSIAP diplome) {
        return diplomeSSIAPRepository.save(diplome);
    }

    // Obtenir tous les diplômes SSIAP
    public List<DiplomeSSIAP> obtenirTousLesDiplomes() {
        return diplomeSSIAPRepository.findAll();
    }

    //  Obtenir un diplôme par ID
    public Optional<DiplomeSSIAP> obtenirDiplomeParId(Long id) {
        return diplomeSSIAPRepository.findById(id);
    }

    //  Obtenir tous les diplômes d'un agent par son ID
    public List<DiplomeSSIAP> obtenirDiplomesParAgent(Long agentId) {
        return diplomeSSIAPRepository.findByAgentDeSecuriteId(agentId);
    }

    //  Mettre à jour un diplôme existant
    public DiplomeSSIAP mettreAJourDiplome(Long id, DiplomeSSIAP nouveauDiplome) {
        DiplomeSSIAP diplomeExistant = diplomeSSIAPRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diplôme non trouvé !"));

        diplomeExistant.setNiveau(nouveauDiplome.getNiveau());
        diplomeExistant.setDateObtention(nouveauDiplome.getDateObtention());
        diplomeExistant.setDateExpiration(nouveauDiplome.getDateExpiration());
        diplomeExistant.setAgentDeSecurite(nouveauDiplome.getAgentDeSecurite());

        return diplomeSSIAPRepository.save(diplomeExistant);
    }

    //  Supprimer un diplôme par son ID
    public void supprimerDiplome(Long id) {
        if (diplomeSSIAPRepository.existsById(id)) {
            diplomeSSIAPRepository.deleteById(id);
        } else {
            throw new RuntimeException("Diplôme non trouvé !");
        }
    }
}

