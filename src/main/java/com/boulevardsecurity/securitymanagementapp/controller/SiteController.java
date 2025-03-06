package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Site;
import com.boulevardsecurity.securitymanagementapp.service.SiteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:3000") // Autorise les requêtes depuis React
@RestController
@RequestMapping("/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;

    // ✅ Récupérer tous les sites
    @GetMapping
    public List<Site> getAllSites() {
        return siteService.getAllSites();
    }

    // ✅ Récupérer un site par ID
    @GetMapping("/{id}")
    public ResponseEntity<Site> getSiteById(@PathVariable Long id) {
        Optional<Site> site = siteService.getSiteById(id);
        return site.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Ajouter un site
    @PostMapping
    public ResponseEntity<Site> createSite(@RequestBody Site site) {
        Site savedSite = siteService.saveSite(site);
        return ResponseEntity.ok(savedSite);
    }

    // ✅ Modifier un site
    @PutMapping("/{id}")
    public ResponseEntity<Site> updateSite(@PathVariable Long id, @RequestBody Site updatedSite) {
        return siteService.updateSite(id, updatedSite)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ✅ Supprimer un site
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(@PathVariable Long id) {
        if (siteService.deleteSite(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
