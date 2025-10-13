package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.dto.FactureCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.FactureDto;
import com.boulevardsecurity.securitymanagementapp.dto.PeriodeFacturationDto;
import com.boulevardsecurity.securitymanagementapp.model.Facture;
import com.boulevardsecurity.securitymanagementapp.service.FactureService;
import com.boulevardsecurity.securitymanagementapp.service.impl.FactureServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService service;
    private final FactureServiceImpl serviceImpl;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<FactureDto> create(@RequestBody FactureCreateDto dto) {
        FactureDto created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<List<FactureDto>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AGENT_SECURITE') or (hasAuthority('CLIENT') and @authz.canClientReadFacture(authentication, #id))")
    public ResponseEntity<FactureDto> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/reference/{ref}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE', 'CLIENT')")
    public ResponseEntity<FactureDto> getByReference(@PathVariable String ref) {
        return service.findByReference(ref)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<FactureDto> update(
            @PathVariable Long id,
            @RequestBody FactureCreateDto dto
    ) {
        try {
            FactureDto updated = service.update(id, dto);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/periode")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> createForPeriod(@RequestBody PeriodeFacturationDto periodeDto) {
        try {
            Facture facture = serviceImpl.creerPourClientEtPeriode(
                periodeDto.getClientId(), 
                periodeDto.getDateDebut(), 
                periodeDto.getDateFin()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(facture.getId()).get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la création de la facture: " + e.getMessage());
        }
    }

    @PostMapping("/from-devis/{devisId}")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> createFromDevis(@PathVariable Long devisId) {
        try {
            Facture facture = serviceImpl.creerDepuisDevis(devisId);
            return ResponseEntity.status(HttpStatus.CREATED).body(service.findById(facture.getId()).get());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur lors de la création de la facture: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/pdf")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AGENT_SECURITE') or (hasAuthority('CLIENT') and @authz.canClientReadFacture(authentication, #id))")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        byte[] pdfContent = service.generatePdf(id);
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "facture-" + id + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        
        return new ResponseEntity<>(pdfContent, headers, HttpStatus.OK);
    }
}
