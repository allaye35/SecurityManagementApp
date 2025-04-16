package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.ContratDeTravail;
import com.boulevardsecurity.securitymanagementapp.service.ContratDeTravailService;
import com.boulevardsecurity.securitymanagementapp.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/contrats-de-travail")
public class ContratDeTravailController {

    private final ContratDeTravailService contratService;
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    public ContratDeTravailController(ContratDeTravailService contratService, PdfGeneratorService pdfGeneratorService) {
        this.contratService = contratService;
        this.pdfGeneratorService = new PdfGeneratorService();
    }

    // GET ALL
    @GetMapping
    public List<ContratDeTravail> getAllContrats() {
        return contratService.getAllContrats();
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<ContratDeTravail> getContratById(@PathVariable Long id) {
        Optional<ContratDeTravail> contratOpt = contratService.getContratById(id);
        return contratOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // CREATE
    @PostMapping
    public ResponseEntity<ContratDeTravail> createContrat(@RequestBody ContratDeTravail contrat) {
        ContratDeTravail saved = contratService.createContrat(contrat);
        return ResponseEntity.ok(saved);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ContratDeTravail> updateContrat(
            @PathVariable Long id,
            @RequestBody ContratDeTravail updatedContrat) {
        ContratDeTravail updated = contratService.updateContrat(id, updatedContrat);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContrat(@PathVariable Long id) {
        contratService.deleteContrat(id);
        return ResponseEntity.noContent().build();
    }

    // PATCH - PROLONGER
    @PatchMapping("/{id}/prolonger")
    public ResponseEntity<String> prolongerContrat(@PathVariable Long id, @RequestParam LocalDate nouvelleDateFin) {
        boolean success = contratService.prolongerContrat(id, nouvelleDateFin);
        return success
                ? ResponseEntity.ok("Contrat prolongé avec succès")
                : ResponseEntity.badRequest().body("Impossible de prolonger le contrat (introuvable ou invalide).");
    }

    // GET - Contrats par agent
    @GetMapping("/agent/{agentId}")
    public List<ContratDeTravail> getContratsByAgent(@PathVariable Long agentId) {
        return contratService.getContratsByAgentId(agentId);
    }


    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadContratPdf(@PathVariable Long id) {
        Optional<ContratDeTravail> contratOpt = contratService.getContratById(id);
        if (contratOpt.isPresent()) {
            byte[] pdf = pdfGeneratorService.generateContratDeTravailPdf(contratOpt.get());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=contrat-" + id + ".pdf")
                    .header("Content-Type", "application/pdf")
                    .body(pdf);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
