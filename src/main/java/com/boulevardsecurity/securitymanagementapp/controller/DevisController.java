package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.Devis;
import com.boulevardsecurity.securitymanagementapp.service.DevisService;
import com.boulevardsecurity.securitymanagementapp.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devis")
@RequiredArgsConstructor
public class DevisController {

    private final DevisService devisService;
    private final PdfGeneratorService pdfGeneratorService;

    // ------------------ CREATE ------------------
    @PostMapping
    public ResponseEntity<Devis> createDevis(@RequestBody Devis devis) {
        Devis newDevis = devisService.createDevis(devis);
        return ResponseEntity.status(201).body(newDevis);
    }

    // ------------------ READ (un devis) ------------------
    @GetMapping("/{id}")
    public ResponseEntity<Devis> getDevisById(@PathVariable Long id) {
        Devis devis = devisService.getDevisById(id);
        return ResponseEntity.ok(devis);
    }

    // ------------------ READ (tous les devis) ------------------
    @GetMapping
    public List<Devis> getAllDevis() {
        return devisService.getAllDevis();
    }

    // ------------------ UPDATE ------------------
    @PutMapping("/{id}")
    public ResponseEntity<Devis> updateDevis(@PathVariable Long id,
                                             @RequestBody Devis devisData) {
        Devis updated = devisService.updateDevis(id, devisData);
        return ResponseEntity.ok(updated);
    }

    // ------------------ DELETE ------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevis(@PathVariable Long id) {
        devisService.deleteDevis(id);
        return ResponseEntity.noContent().build();
    }

    // ------------------ EXEMPLE : ACCEPTER UN DEVIS ------------------
    @PostMapping("/{id}/accept")
    public ResponseEntity<Devis> accepterDevis(@PathVariable Long id) {
        Devis acceptedDevis = devisService.accepterDevis(id);
        return ResponseEntity.ok(acceptedDevis);
    }

    // =============================================================
    // NOUVEAU : GÉNÉRATION PDF
    // =============================================================
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateDevisPdf(@PathVariable Long id) {
        // 1) Récupérer le devis
        Devis devis = devisService.getDevisById(id);
        // 2) Générer le PDF
        byte[] pdfBytes = pdfGeneratorService.generateDevisPdf(devis);

        // 3) Construire la réponse HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Nom du fichier
        String filename = "devis_" + devis.getReferenceDevis() + ".pdf";
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        // 4) Retourner le flux
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
