package com.boulevardsecurity.securitymanagementapp.mapper;

import com.boulevardsecurity.securitymanagementapp.dto.*;
import com.boulevardsecurity.securitymanagementapp.model.*;
import com.boulevardsecurity.securitymanagementapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ContratMapper {

    private final DevisRepository devisRepo;
    private final MissionRepository missionRepo;
    private final ArticleContratRepository articleRepo;

    public ContratDto toDto(Contrat c) {
        return ContratDto.builder()
                .id(c.getId())
                .referenceContrat(c.getReferenceContrat())
                .dateSignature(c.getDateSignature())
                .dureeMois(c.getDureeMois())
                .taciteReconduction(c.getTaciteReconduction())
                .preavisMois(c.getPreavisMois())
                .devisId(c.getDevis() != null ? c.getDevis().getId() : null)
                .missionIds(c.getMissions().stream().map(Mission::getId).collect(Collectors.toList()))
                .articleIds(c.getArticles().stream().map(ArticleContrat::getId).collect(Collectors.toList()))
                .build();
    }    
    public Contrat toEntity(ContratCreateDto dto) {
        Contrat c = Contrat.builder()
                .referenceContrat(dto.getReferenceContrat())
                .dateSignature(dto.getDateSignature())
                .dureeMois(dto.getDureeMois())
                .taciteReconduction(dto.getTaciteReconduction())
                .preavisMois(dto.getPreavisMois())
                .build();
                
        if (dto.getDevisId() != null) {
            Devis d = devisRepo.findById(dto.getDevisId())
                    .orElseThrow(() -> new IllegalArgumentException("Devis introuvable id=" + dto.getDevisId()));
            if (d.getContrat() != null) {
                throw new IllegalArgumentException("ERREUR: Le devis id=" + dto.getDevisId() + 
                    " avec référence [" + d.getReferenceDevis() + "] est déjà lié au contrat id=" + 
                    d.getContrat().getId() + " - Veuillez choisir un autre devis.");
            }
            c.setDevis(d);
        }

        if (dto.getMissionIds() != null) {
            c.setMissions(dto.getMissionIds().stream()
                    .map(id -> missionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mission introuvable id=" + id)))
                    .peek(m -> m.setContrat(c))
                    .collect(Collectors.toList()));
        }
        if (dto.getArticleIds() != null) {
            c.setArticles(dto.getArticleIds().stream()
                    .map(id -> articleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Article introuvable id=" + id)))
                    .peek(a -> a.setContrat(c))
                    .collect(Collectors.toList()));
        }

        return c;
    }

    public void updateEntity(Contrat entity, ContratCreateDto dto) {
        if (dto.getReferenceContrat() != null) entity.setReferenceContrat(dto.getReferenceContrat());
        if (dto.getDateSignature() != null)   entity.setDateSignature(dto.getDateSignature());
        if (dto.getDureeMois() != null)       entity.setDureeMois(dto.getDureeMois());
        if (dto.getTaciteReconduction() != null) entity.setTaciteReconduction(dto.getTaciteReconduction());
        if (dto.getPreavisMois() != null)     entity.setPreavisMois(dto.getPreavisMois());
        if (dto.getDevisId() != null && (entity.getDevis() == null || !entity.getDevis().getId().equals(dto.getDevisId()))) {
            Devis d = devisRepo.findById(dto.getDevisId()).orElseThrow(() -> new IllegalArgumentException("Devis introuvable id=" + dto.getDevisId()));
            entity.setDevis(d);
        } else if (dto.getDevisId() == null) {
            entity.setDevis(null);
        }

        if (dto.getMissionIds() != null) {
            entity.getMissions().clear();
            entity.getMissions().addAll(dto.getMissionIds().stream()
                    .map(id -> missionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Mission introuvable id=" + id)))
                    .peek(m -> m.setContrat(entity))
                    .collect(Collectors.toList()));
        }
        if (dto.getArticleIds() != null) {
            entity.getArticles().clear();
            entity.getArticles().addAll(dto.getArticleIds().stream()
                    .map(id -> articleRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("Article introuvable id=" + id)))
                    .peek(a -> a.setContrat(entity))
                    .collect(Collectors.toList()));
        }
    }
}