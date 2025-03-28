package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeZone;
import com.boulevardsecurity.securitymanagementapp.model.ZoneDeTravail;
import com.boulevardsecurity.securitymanagementapp.repository.ZoneDeTravailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ZoneDeTravailService {


    private ZoneDeTravailRepository zoneDeTravailRepository;

    @Autowired
    public ZoneDeTravailService(ZoneDeTravailRepository zoneDeTravailRepository) {
        this.zoneDeTravailRepository = zoneDeTravailRepository;
    }

    //  Création d'une nouvelle zone de travail
    public ZoneDeTravail creerZoneDeTravail(ZoneDeTravail zoneDeTravail) {
        if (!zoneDeTravailRepository.existsByNomAndTypeZone(zoneDeTravail.getNom(), zoneDeTravail.getTypeZone())) {
            return zoneDeTravailRepository.save(zoneDeTravail);
        }
        throw new IllegalArgumentException("La zone de travail existe déjà !");
    }

    //  Récupérer toutes les zones de travail
    public List<ZoneDeTravail> obtenirToutesLesZones() {
        return zoneDeTravailRepository.findAll();
    }

    //  Récupérer une zone de travail par son ID
    public Optional<ZoneDeTravail> obtenirZoneParId(Long id) {
        return zoneDeTravailRepository.findById(id);
    }

    //  Rechercher des zones par nom
    public List<ZoneDeTravail> rechercherZonesParNom(String nom) {
        return zoneDeTravailRepository.findByNomContainingIgnoreCase(nom);
    }

    //  Rechercher des zones par type (VILLE, DEPARTEMENT, REGION, CODE_POSTAL)
    public List<ZoneDeTravail> rechercherZonesParType(TypeZone typeZone) {
        return zoneDeTravailRepository.findByTypeZone(typeZone);
    }

    // ─────────────────────────────────────────────────────────────────────────────
//  Mettre à jour une zone de travail
// ─────────────────────────────────────────────────────────────────────────────
    public ZoneDeTravail mettreAJourZoneDeTravail(Long id, ZoneDeTravail nouvelleZone) {
        return zoneDeTravailRepository.findById(id).map(zone -> {
            zone.setNom(nouvelleZone.getNom());
            zone.setTypeZone(nouvelleZone.getTypeZone());

            // Mettre à jour les nouveaux champs
            zone.setCodePostal(nouvelleZone.getCodePostal());
            zone.setVille(nouvelleZone.getVille());
            zone.setDepartement(nouvelleZone.getDepartement());
            zone.setRegion(nouvelleZone.getRegion());
            zone.setPays(nouvelleZone.getPays());

            // Sauvegarde et retour de la zone mise à jour
            return zoneDeTravailRepository.save(zone);

        }).orElseThrow(() -> new RuntimeException("Zone de travail non trouvée pour l'ID : " + id));
    }


    //  Supprimer une zone de travail par son ID
    public void supprimerZoneDeTravail(Long id) {
        if (zoneDeTravailRepository.existsById(id)) {
            zoneDeTravailRepository.deleteById(id);
        } else {
            throw new RuntimeException("Zone de travail non trouvée !");
        }
    }
}

