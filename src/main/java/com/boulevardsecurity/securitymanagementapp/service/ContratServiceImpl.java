package com.boulevardsecurity.securitymanagementapp.service;


import com.boulevardsecurity.securitymanagementapp.model.Contrat;
import com.boulevardsecurity.securitymanagementapp.model.Devis;
import com.boulevardsecurity.securitymanagementapp.repository.ContratRepository;
import com.boulevardsecurity.securitymanagementapp.service.ContratService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratServiceImpl implements ContratService {

    private final ContratRepository contratRepository;

    /**
     * Create
     */
    @Override
    public Contrat createContrat(Contrat contrat) {
        // Logique métier avant enregistrement (ex: validations)
        return contratRepository.save(contrat);
    }

    /**
     * Read - un seul contrat
     */
    @Override
    public Contrat getContratById(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contrat introuvable : " + id));
    }

    /**
     * Read - liste de contrats
     */
    @Override
    public List<Contrat> getAllContrats() {
        return contratRepository.findAll();
    }

    /**
     * Update
     */
    @Override
    public Contrat updateContrat(Long id, Contrat contratData) {
        Contrat existing = getContratById(id);

        // Mise à jour des champs
        existing.setDateDebut(contratData.getDateDebut());
        existing.setDateFin(contratData.getDateFin());
        existing.setMontant(contratData.getMontant());
        existing.setDevis(contratData.getDevis());
        existing.setClient(contratData.getClient());
        existing.setEntreprise(contratData.getEntreprise());
        existing.setContenuContrat(contratData.getContenuContrat());

        // Liste des missions si tu souhaites la gérer ici
        // existing.setMissions(contratData.getMissions());

        return contratRepository.save(existing);
    }

    /**
     * Delete
     */
    @Override
    public void deleteContrat(Long id) {
        Contrat existing = getContratById(id);
        contratRepository.delete(existing);
    }

    /**
     * (Optionnel) Génération d'un contrat depuis un Devis
     */
    @Override
    public Contrat generateFromDevis(Devis devis) {
        // Exemple de logique : création d'un Contrat basé sur un Devis
        Contrat contrat = Contrat.builder()
                .dateDebut(LocalDate.now())
                .dateFin(null)       // ou Date + X mois
                .montant(devis.getMontant())  // ou un autre calcul
                .devis(devis)
                .client(devis.getClient())
                .entreprise(devis.getEntreprise())
                .contenuContrat("Contrat généré depuis le devis " + devis.getReferenceDevis())
                .build();

        return contratRepository.save(contrat);
    }
}
