package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.model.RapportIntervention;
import com.boulevardsecurity.securitymanagementapp.service.PdfGeneratorService;
import com.boulevardsecurity.securitymanagementapp.service.RapportInterventionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rapports")
@CrossOrigin(origins = "http://localhost:3000") // Autorise les requêtes depuis React
public class RapportInterventionController {

    private final RapportInterventionService rapportService;
    private final PdfGeneratorService pdfGeneratorService;

    @Autowired
    public RapportInterventionController(RapportInterventionService rapportService, PdfGeneratorService pdfGeneratorService) {
        this.rapportService = rapportService;
        this.pdfGeneratorService = pdfGeneratorService;
    }
    // 🔹 Récupérer tous les rapports
    @GetMapping
    public List<RapportIntervention> getAllRapports() {
        return rapportService.getAllRapports();
    }

    // 🔹 Récupérer un rapport par ID
    @GetMapping("/{id}")
    public ResponseEntity<RapportIntervention> getRapportById(@PathVariable Long id) {
        return rapportService.getRapportById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Récupérer les rapports d'une mission
    @GetMapping("/mission/{missionId}")
    public List<RapportIntervention> getRapportsByMissionId(@PathVariable Long missionId) {
        return rapportService.getRapportsByMissionId(missionId);
    }

    // 🔹 Ajouter un rapport à une mission
    @PostMapping("/mission/{missionId}")
    public ResponseEntity<RapportIntervention> createRapport(@PathVariable Long missionId, @RequestBody RapportIntervention rapport) {
        return ResponseEntity.ok(rapportService.createRapport(missionId, rapport));
    }

    // 🔹 Modifier un rapport
    @PutMapping("/{id}")
    public ResponseEntity<RapportIntervention> updateRapport(@PathVariable Long id, @RequestBody RapportIntervention updatedRapport) {
        return ResponseEntity.ok(rapportService.updateRapport(id, updatedRapport));
    }

    // 🔹 Supprimer un rapport
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRapport(@PathVariable Long id) {
        rapportService.deleteRapport(id);
        return ResponseEntity.noContent().build();
    }


    // --- Nouvel endpoint : Imprimer (générer le PDF) du rapport d'intervention ---
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> printRapportPdf(@PathVariable Long id) {
        return rapportService.getRapportById(id)
                .map(rapport -> {
                    byte[] pdfBytes = pdfGeneratorService.generateRapportInterventionPdf(rapport);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_PDF);
                    String filename = "rapport_intervention_" + rapport.getId() + ".pdf";
                    headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());
                    return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}