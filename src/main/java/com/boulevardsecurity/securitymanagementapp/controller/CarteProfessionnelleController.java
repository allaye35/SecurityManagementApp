package com.boulevardsecurity.securitymanagementapp.controller;
import com.boulevardsecurity.securitymanagementapp.model.CarteProfessionnelle;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import com.boulevardsecurity.securitymanagementapp.service.CarteProfessionnelleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cartes")
public class CarteProfessionnelleController {

    private final CarteProfessionnelleService carteProfessionnelleService;
    private final AgentDeSecuriteService agentDeSecuriteService;

    @Autowired
    public CarteProfessionnelleController(CarteProfessionnelleService carteProfessionnelleService,
                                          AgentDeSecuriteService agentDeSecuriteService) {
        this.carteProfessionnelleService = carteProfessionnelleService;
        this.agentDeSecuriteService = agentDeSecuriteService;
    }

    //  Créer une nouvelle carte professionnelle
    @PostMapping
    public ResponseEntity<CarteProfessionnelle> creerCarte(@RequestBody CarteProfessionnelle carte) {
        CarteProfessionnelle nouvelleCarte = carteProfessionnelleService.creerCarte(carte);
        return ResponseEntity.ok(nouvelleCarte);
    }

    //  Obtenir toutes les cartes professionnelles
    @GetMapping
    public ResponseEntity<List<CarteProfessionnelle>> obtenirToutesLesCartes() {
        List<CarteProfessionnelle> cartes = carteProfessionnelleService.obtenirToutesLesCartes();
        return ResponseEntity.ok(cartes);
    }

    //  Obtenir une carte par ID
    @GetMapping("/{id}")
    public ResponseEntity<CarteProfessionnelle> obtenirCarteParId(@PathVariable Long id) {
        Optional<CarteProfessionnelle> carte = carteProfessionnelleService.obtenirCarteParId(id);
        return carte.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    //  Obtenir toutes les cartes d'un agent par son ID
    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<CarteProfessionnelle>> obtenirCartesParAgent(@PathVariable Long agentId) {
        List<CarteProfessionnelle> cartes = carteProfessionnelleService.obtenirCartesParAgent(agentId);
        return ResponseEntity.ok(cartes);
    }

    //  Mettre à jour une carte par ID
    @PutMapping("/{id}")
    public ResponseEntity<CarteProfessionnelle> mettreAJourCarte(@PathVariable Long id, @RequestBody CarteProfessionnelle nouvelleCarte) {
        try {
            CarteProfessionnelle carteMiseAJour = carteProfessionnelleService.mettreAJourCarte(id, nouvelleCarte);
            return ResponseEntity.ok(carteMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    //  Supprimer une carte par ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerCarte(@PathVariable Long id) {
        try {
            carteProfessionnelleService.supprimerCarte(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    // 🔹 Ajouter une carte professionnelle à un agent
    @PostMapping("/{agentId}/cartesProfessionnelles")
    public ResponseEntity<CarteProfessionnelle> ajouterCarteProfessionnellePourAgent(
            @PathVariable Long agentId,
            @RequestBody CarteProfessionnelle carteProfessionnelle
    ) {
        try {
            CarteProfessionnelle nouvelleCarte = agentDeSecuriteService.ajouterCarteProfessionnelle(agentId, carteProfessionnelle);
            return ResponseEntity.ok(nouvelleCarte);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}

