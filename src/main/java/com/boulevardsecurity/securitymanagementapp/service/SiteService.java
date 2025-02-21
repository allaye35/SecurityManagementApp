package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Site;
import com.boulevardsecurity.securitymanagementapp.repository.SiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SiteService {

    private final SiteRepository siteRepository;

    // ✅ Récupérer tous les sites
    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    // ✅ Récupérer un site par ID
    public Optional<Site> getSiteById(Long id) {
        return siteRepository.findById(id);
    }

    // ✅ Ajouter un site
    public Site saveSite(Site site) {
        return siteRepository.save(site);
    }

    // ✅ Modifier un site
    public Optional<Site> updateSite(Long id, Site updatedSite) {
        return siteRepository.findById(id).map(existingSite -> {
            existingSite.setNom(updatedSite.getNom());
            existingSite.setAdresse(updatedSite.getAdresse());
            return siteRepository.save(existingSite);
        });
    }

    // ✅ Supprimer un site
    public boolean deleteSite(Long id) {
        if (siteRepository.existsById(id)) {
            siteRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
