package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.FicheDePaie;
import com.boulevardsecurity.securitymanagementapp.repository.FicheDePaieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FicheDePaieService {

    @Autowired
    private FicheDePaieRepository ficheDePaieRepository;

    public List<FicheDePaie> getAllFiches() {
        return ficheDePaieRepository.findAll();
    }

    public Optional<FicheDePaie> getFicheById(Long id) {
        return ficheDePaieRepository.findById(id);
    }

    public FicheDePaie createFiche(FicheDePaie ficheDePaie) {
        return ficheDePaieRepository.save(ficheDePaie);
    }

    public FicheDePaie updateFiche(Long id, FicheDePaie updatedFiche) {
        return ficheDePaieRepository.findById(id).map(fiche -> {
            fiche.setNumeroFiche(updatedFiche.getNumeroFiche());
            fiche.setSalaireBrut(updatedFiche.getSalaireBrut());
            fiche.setSalaireNet(updatedFiche.getSalaireNet());
            fiche.setDateEmise(updatedFiche.getDateEmise());
            return ficheDePaieRepository.save(fiche);
        }).orElse(null);
    }

    public void deleteFiche(Long id) {
        ficheDePaieRepository.deleteById(id);
    }
}
