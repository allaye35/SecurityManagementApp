package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Facture;
import com.boulevardsecurity.securitymanagementapp.service.FactureService;
import com.boulevardsecurity.securitymanagementapp.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/factures")
public class FactureController {

    @Autowired
    private FactureService factureService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    // ------------------------------------------------------------------------------------
    // 1) Récupérer TOUTES les factures
    // ------------------------------------------------------------------------------------
    @GetMapping
    public List<Facture> getAllFactures() {
        return factureService.getAllFactures();
    }

    // ------------------------------------------------------------------------------------
    // 2) Récupérer UNE facture par ID
    // ------------------------------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        Optional<Facture> factureOpt = factureService.getFactureById(id);
        return factureOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ------------------------------------------------------------------------------------
    // 3) Créer une nouvelle facture
    // ------------------------------------------------------------------------------------
    @PostMapping
    public Facture createFacture(@RequestBody Facture facture) {
        // Optionnel : tu peux faire ici un recalcul des montants
        // facture.recalculerMontants();
        return factureService.createFacture(facture);
    }

    // ------------------------------------------------------------------------------------
    // 4) Mettre à jour une facture existante
    // ------------------------------------------------------------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<Facture> updateFacture(@PathVariable Long id, @RequestBody Facture updatedFacture) {
        Facture facture = factureService.updateFacture(id, updatedFacture);
        return (facture != null) ? ResponseEntity.ok(facture)
                : ResponseEntity.notFound().build();
    }

    // ------------------------------------------------------------------------------------
    // 5) Supprimer une facture par ID
    // ------------------------------------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------------------------
    // 6) Générer et télécharger le PDF d'une facture
    // ------------------------------------------------------------------------------------
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateFacturePdf(@PathVariable Long id) {
        // 1) Récupère la facture
        Optional<Facture> factureOpt = factureService.getFactureById(id);
        if (factureOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Facture facture = factureOpt.get();

        // 2) Génère le PDF (avec PdfGeneratorService)
        byte[] pdfBytes = pdfGeneratorService.generateFacturePdf(facture);

        // 3) Prépare la réponse HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Pour forcer le téléchargement (attachment)
        String filename = "facture_" + facture.getNumeroFacture() + ".pdf";
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
