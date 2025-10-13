package com.boulevardsecurity.securitymanagementapp.controller;

import com.boulevardsecurity.securitymanagementapp.api.ApiErreur;
import com.boulevardsecurity.securitymanagementapp.dto.MissionCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.MissionDto;
import com.boulevardsecurity.securitymanagementapp.security.user.AppUserDetails;
import com.boulevardsecurity.securitymanagementapp.service.IMissionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/missions")
@CrossOrigin(origins = "http:
@RequiredArgsConstructor
public class MissionControleur {

    private final IMissionService serviceMission;

    private ResponseEntity<ApiErreur> erreur(HttpStatus statut, String msg, HttpServletRequest req) {
        return ResponseEntity
                .status(statut)
                .body(new ApiErreur(
                        Instant.now(),
                        statut.value(),
                        statut.getReasonPhrase(),
                        msg,
                        req.getRequestURI()));
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public List<MissionDto> listerToutes() {
        return serviceMission.listerToutes();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AGENT_SECURITE') or (hasAuthority('CLIENT') and @authz.canClientReadMission(authentication, #id))")
    public ResponseEntity<?> obtenirParId(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.obtenirParId(id));
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        }
    }

    @PostMapping("/simuler-calcul")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> simulerCalculMontants(
            @Valid @RequestBody MissionCreateDto missionDto,
            HttpServletRequest req) {
        
        try {
            MissionDto resultat = serviceMission.simulerCalcul(missionDto);
            return ResponseEntity.ok(resultat);
        } catch (IllegalArgumentException ex) {
            return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        }
    }

  @PostMapping
  @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
public ResponseEntity<?> creerMission(@Valid @RequestBody MissionCreateDto missionDto,
                                      @RequestParam(value = "adresseSite", required = false) String adresseSite,
                                      HttpServletRequest req) {
    try {
        MissionDto cree = serviceMission.creerMission(missionDto, adresseSite);
        return ResponseEntity.status(HttpStatus.CREATED).body(cree);
    } catch (NoSuchElementException ex) {
        return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    } catch (IllegalArgumentException ex) {
        return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
    }
}

    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.canAgentWriteMission(authentication, #id))")
    public ResponseEntity<?> majMission(
            @PathVariable Long id,
            @Valid @RequestBody MissionCreateDto missionDto,
            @RequestParam(value = "nouvelleAdresse", required = false) String nouvelleAdresse,
            HttpServletRequest req) {

        try {
            return ResponseEntity.ok(serviceMission.majMission(id, missionDto, nouvelleAdresse));
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        } catch (IllegalArgumentException ex) {
            return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> supprimerMission(@PathVariable Long id, HttpServletRequest req) {
        try {
            serviceMission.supprimerMission(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        }
    }

    @PutMapping("/{id}/agents")
    @PreAuthorize("hasAnyAuthority('ADMIN', 'AGENT_SECURITE')")
    public ResponseEntity<?> affecterAgents(@PathVariable Long id,
                                            @RequestBody List<Long> idsAgents,
                                            HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.affecterAgents(id, idsAgents));
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        } catch (IllegalArgumentException ex) {
            return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        }
    }

    @DeleteMapping("/{id}/agent/{idAgent}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> retirerAgent(@PathVariable Long id,
                                          @PathVariable Long idAgent,
                                          HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.retirerAgent(id, idAgent));
        } catch (RuntimeException ex) {
            return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        }
    }

    @PutMapping("/{id}/rapport/{idRapport}")
    public ResponseEntity<?> associerRapport(@PathVariable Long id,
                                             @PathVariable Long idRapport,
                                             HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.associerRapport(id, idRapport));
        } catch (RuntimeException ex) {
            return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        }
    }

    @PutMapping("/{id}/planning/{idPlanning}")
    public ResponseEntity<?> associerPlanning(@PathVariable Long id,
                                              @PathVariable Long idPlanning,
                                              HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.associerPlanning(id, idPlanning));
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        }
    }

    @PutMapping("/{id}/site/{idSite}")
    public ResponseEntity<?> associerSite(@PathVariable Long id,
                                          @PathVariable Long idSite,
                                          HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.associerSite(id, idSite));
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        }
    }

    @PutMapping("/{id}/geoloc")
    public ResponseEntity<?> associerGeoloc(@PathVariable Long id, HttpServletRequest req) {
        try {
            return ResponseEntity.ok(serviceMission.associerGeoloc(id));
        } catch (RuntimeException ex) {
            return erreur(HttpStatus.BAD_REQUEST, ex.getMessage(), req);
        }
    }

    @GetMapping("/apres")
    public List<MissionDto> missionsCommencantApres(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return serviceMission.missionsCommencantApres(date);
    }

    @GetMapping("/avant")
    public List<MissionDto> missionsFinissantAvant(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return serviceMission.missionsFinissantAvant(date);
    }

    @GetMapping("/agent/{idAgent}")
    @PreAuthorize("hasAuthority('ADMIN') or (hasAuthority('AGENT_SECURITE') and @authz.isSelf(authentication, #idAgent))")
    public List<MissionDto> missionsParAgent(@PathVariable Long idAgent) {
        return serviceMission.missionsParAgent(idAgent);
    }

    @GetMapping("/planning/{idPlanning}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AGENT_SECURITE') or (hasAuthority('CLIENT') and @authz.canClientReadPlanning(authentication, #idPlanning))")
    public List<MissionDto> missionsParPlanning(@PathVariable Long idPlanning) {
        return serviceMission.missionsParPlanning(idPlanning);
    }
    
    @GetMapping("/contrat/{contratId}")
    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('AGENT_SECURITE') or (hasAuthority('CLIENT') and @authz.canClientReadContrat(authentication, #contratId))")
    public List<MissionDto> missionsParContrat(@PathVariable Long contratId) {
        return serviceMission.missionsParContrat(contratId);
    }

    @PutMapping("/{idMission}/contrats-de-travail/{idContrat}")
    public ResponseEntity<?> associerContratDeTravail(
            @PathVariable Long idMission,
            @PathVariable Long idContrat,
            HttpServletRequest req) {
        try {
            MissionDto missionDto = serviceMission.associerContratDeTravail(idMission, idContrat);
            return ResponseEntity.ok(missionDto);
        } catch (NoSuchElementException e) {
            return erreur(HttpStatus.NOT_FOUND, e.getMessage(), req);
        } catch (IllegalArgumentException e) {
            return erreur(HttpStatus.BAD_REQUEST, e.getMessage(), req);
        }
    }

    @PutMapping("/{idMission}/factures/{idFacture}")
    public ResponseEntity<?> associerFacture(
            @PathVariable Long idMission,
            @PathVariable Long idFacture,
            HttpServletRequest req) {
        try {
            MissionDto updated = serviceMission.associerFacture(idMission, idFacture);
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException e) {
            return erreur(HttpStatus.NOT_FOUND, e.getMessage(), req);
        } catch (IllegalArgumentException e) {
            return erreur(HttpStatus.BAD_REQUEST, e.getMessage(), req);
        }
    }

    @DeleteMapping("/{id}/geoloc")
    public ResponseEntity<?> dissocierGeoloc(
            @PathVariable Long id,
            HttpServletRequest req
    ) {
        try {
            serviceMission.dissocierGeoloc(id);
            return ResponseEntity.noContent().build();
        } catch (NoSuchElementException ex) {
            return erreur(HttpStatus.NOT_FOUND, ex.getMessage(), req);
        }
    }

    @GetMapping("/sans-devis")
    public List<MissionDto> missionsSansDevis() {
        return serviceMission.missionsSansDevis();
    }

}