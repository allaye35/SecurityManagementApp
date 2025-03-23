package com.boulevardsecurity.securitymanagementapp.controller;
import com.boulevardsecurity.securitymanagementapp.model.DiplomeSSIAP;
import com.boulevardsecurity.securitymanagementapp.service.DiplomeSSIAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/diplomes")
public class DiplomeSSIAPController {

    private final DiplomeSSIAPService diplomeSSIAPService;

    @Autowired
    public DiplomeSSIAPController(DiplomeSSIAPService diplomeSSIAPService) {
        this.diplomeSSIAPService = diplomeSSIAPService;
    }

    //  Créer un nouveau diplôme
    @PostMapping
    public ResponseEntity<DiplomeSSIAP> creerDiplome(@RequestBody DiplomeSSIAP diplome) {
        DiplomeSSIAP nouveauDiplome = diplomeSSIAPService.creerDiplome(diplome);
        return ResponseEntity.ok(nouveauDiplome);
    }

    //  Obtenir tous les diplômes
    @GetMapping
    public ResponseEntity<List<DiplomeSSIAP>> obtenirTousLesDiplomes() {
        List<DiplomeSSIAP> diplomes = diplomeSSIAPService.obtenirTousLesDiplomes();
        return ResponseEntity.ok(diplomes);
    }

    //  Obtenir un diplôme par ID
    @GetMapping("/{id}")
    public ResponseEntity<DiplomeSSIAP> obtenirDiplomeParId(@PathVariable Long id) {
        Optional<DiplomeSSIAP> diplome = diplomeSSIAPService.obtenirDiplomeParId(id);
        return diplome.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //  Obtenir tous les diplômes d'un agent par son ID
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<DiplomeSSIAP>> obtenirDiplomesParAgent(@PathVariable Long agentId) {
        List<DiplomeSSIAP> diplomes = diplomeSSIAPService.obtenirDiplomesParAgent(agentId);
        return ResponseEntity.ok(diplomes);
    }

    //  Mettre à jour un diplôme par ID
    @PutMapping("/{id}")
    public ResponseEntity<DiplomeSSIAP> mettreAJourDiplome(@PathVariable Long id, @RequestBody DiplomeSSIAP nouveauDiplome) {
        try {
            DiplomeSSIAP diplomeMisAJour = diplomeSSIAPService.mettreAJourDiplome(id, nouveauDiplome);
            return ResponseEntity.ok(diplomeMisAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //  Supprimer un diplôme par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerDiplome(@PathVariable Long id) {
        try {
            diplomeSSIAPService.supprimerDiplome(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
