package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.FicheDePaie;
import com.boulevardsecurity.securitymanagementapp.service.FicheDePaieService;
import com.boulevardsecurity.securitymanagementapp.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fiches-de-paie")
@RequiredArgsConstructor
public class FicheDePaieController {

    private final FicheDePaieService ficheDePaieService;
    private final PdfGeneratorService pdfGeneratorService;

    // ------------------ CREATE ------------------
    @PostMapping
    public ResponseEntity<FicheDePaie> createFiche(@RequestBody FicheDePaie fiche) {
        FicheDePaie created = ficheDePaieService.createFicheDePaie(fiche);
        return ResponseEntity.status(201).body(created);
    }

    // ------------------ READ - une fiche ------------------
    @GetMapping("/{id}")
    public ResponseEntity<FicheDePaie> getFiche(@PathVariable Long id) {
        return ResponseEntity.ok(ficheDePaieService.getFicheDePaieById(id));
    }

    // ------------------ READ - toutes les fiches ------------------
    @GetMapping
    public List<FicheDePaie> getAllFiches() {
        return ficheDePaieService.getAllFichesDePaie();
    }

    // ------------------ UPDATE ------------------
    @PutMapping("/{id}")
    public ResponseEntity<FicheDePaie> updateFiche(
            @PathVariable Long id,
            @RequestBody FicheDePaie ficheData
    ) {
        FicheDePaie updated = ficheDePaieService.updateFicheDePaie(id, ficheData);
        return ResponseEntity.ok(updated);
    }

    // ------------------ DELETE ------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFiche(@PathVariable Long id) {
        ficheDePaieService.deleteFicheDePaie(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================================
    // NOUVEAU: GÉNÉRATION DU PDF DE LA FICHE DE PAIE
    // ===============================================================
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generateFichePdf(@PathVariable Long id) {
        // 1) Récupérer la fiche de paie
        FicheDePaie fiche = ficheDePaieService.getFicheDePaieById(id);

        // 2) Générer le PDF
        byte[] pdfBytes = pdfGeneratorService.generateFicheDePaiePdf(fiche);

        // 3) Construire la réponse HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        // Nom du fichier
        String filename = "fiche_de_paie_" + fiche.getReferenceBulletin() + ".pdf";
        headers.setContentDisposition(
                ContentDisposition.attachment().filename(filename).build()
        );

        // 4) Retourner le flux PDF en réponse
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
