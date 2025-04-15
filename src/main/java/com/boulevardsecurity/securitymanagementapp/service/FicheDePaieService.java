package com.boulevardsecurity.securitymanagementapp.service;


import com.boulevardsecurity.securitymanagementapp.model.FicheDePaie;
import java.util.List;

public interface FicheDePaieService {

    // CREATE
    FicheDePaie createFicheDePaie(FicheDePaie fiche);

    // READ - un seul
    FicheDePaie getFicheDePaieById(Long id);

    // READ - liste
    List<FicheDePaie> getAllFichesDePaie();

    // UPDATE
    FicheDePaie updateFicheDePaie(Long id, FicheDePaie ficheData);

    // DELETE
    void deleteFicheDePaie(Long id);
}
