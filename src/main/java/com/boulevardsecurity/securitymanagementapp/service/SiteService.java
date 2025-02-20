package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.Site;
import com.boulevardsecurity.securitymanagementapp.repository.SiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SiteService {

    @Autowired
    private SiteRepository siteRepository;

    public List<Site> getAllSites() {
        return siteRepository.findAll();
    }

    public Optional<Site> getSiteById(Long id) {
        return siteRepository.findById(id);
    }

    public Site createSite(Site site) {
        return siteRepository.save(site);
    }

    public Site updateSite(Long id, Site updatedSite) {
        return siteRepository.findById(id).map(site -> {
            site.setNom(updatedSite.getNom());
            site.setAdresse(updatedSite.getAdresse());
            site.setDescription(updatedSite.getDescription());
            return siteRepository.save(site);
        }).orElse(null);
    }

    public void deleteSite(Long id) {
        siteRepository.deleteById(id);
    }
}
