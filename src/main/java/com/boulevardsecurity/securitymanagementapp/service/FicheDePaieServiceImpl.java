package com.boulevardsecurity.securitymanagementapp.service;


import com.boulevardsecurity.securitymanagementapp.model.FicheDePaie;
import com.boulevardsecurity.securitymanagementapp.repository.FicheDePaieRepository;
import com.boulevardsecurity.securitymanagementapp.service.FicheDePaieService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FicheDePaieServiceImpl implements FicheDePaieService {

    private final FicheDePaieRepository ficheDePaieRepository;

    @Override
    public FicheDePaie createFicheDePaie(FicheDePaie fiche) {
        // Logique éventuelle : générer une référence unique, calculer salaireNet, etc.
        if (fiche.getReferenceBulletin() == null || fiche.getReferenceBulletin().isEmpty()) {
            fiche.setReferenceBulletin("FP-" + System.currentTimeMillis());
        }

        // Exemple simpliste de calcul net si non renseigné
        if (fiche.getSalaireNet() == null) {
            double brut = fiche.getSalaireBrut() != null ? fiche.getSalaireBrut() : 0.0;
            double cots = fiche.getCotisations() != null ? fiche.getCotisations() : 0.0;
            fiche.setSalaireNet(brut - cots);
        }

        // On enregistre
        return ficheDePaieRepository.save(fiche);
    }

    @Override
    public FicheDePaie getFicheDePaieById(Long id) {
        return ficheDePaieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fiche de paie introuvable : " + id));
    }

    @Override
    public List<FicheDePaie> getAllFichesDePaie() {
        return ficheDePaieRepository.findAll();
    }

    @Override
    public FicheDePaie updateFicheDePaie(Long id, FicheDePaie ficheData) {
        FicheDePaie existing = getFicheDePaieById(id);

        // Mise à jour des champs
        existing.setReferenceBulletin(ficheData.getReferenceBulletin());
        existing.setDateEmission(ficheData.getDateEmission());
        existing.setDebutPeriode(ficheData.getDebutPeriode());
        existing.setFinPeriode(ficheData.getFinPeriode());
        existing.setSalaireBrut(ficheData.getSalaireBrut());
        existing.setCotisations(ficheData.getCotisations());
        existing.setSalaireNet(ficheData.getSalaireNet());
        existing.setPrimeNuit(ficheData.getPrimeNuit());
        existing.setPrimeTransport(ficheData.getPrimeTransport());
        existing.setContratDeTravail(ficheData.getContratDeTravail());

        return ficheDePaieRepository.save(existing);
    }

    @Override
    public void deleteFicheDePaie(Long id) {
        FicheDePaie existing = getFicheDePaieById(id);
        ficheDePaieRepository.delete(existing);
    }
}

