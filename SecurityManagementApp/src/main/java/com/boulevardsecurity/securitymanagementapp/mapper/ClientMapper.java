// src/main/java/com/boulevardsecurity/securitymanagementapp/mapper/ClientMapper.java
package com.boulevardsecurity.securitymanagementapp.mapper;

import com.boulevardsecurity.securitymanagementapp.dto.ClientCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.ClientDto;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.model.Devis;
import com.boulevardsecurity.securitymanagementapp.model.GestionnaireNotifications;
import com.boulevardsecurity.securitymanagementapp.repository.DevisRepository;
import com.boulevardsecurity.securitymanagementapp.repository.GestionnaireNotificationsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ClientMapper {

    private final DevisRepository devisRepo;
    private final GestionnaireNotificationsRepository notifRepo;

    /** ENTITÉ ➜ DTO */
    public ClientDto toDto(Client c) {
        return ClientDto.builder()
                .id(c.getId())
                .role(c.getRole())
                .typeClient(c.getTypeClient())
                .nom(c.getNom())
                .prenom(c.getPrenom())
                .siege(c.getSiege())
                .representant(c.getRepresentant())
                .numeroSiret(c.getNumeroSiret())
                .email(c.getEmail())
                .telephone(c.getTelephone())
                .adresse(c.getAdresse())
                .numeroRue(c.getNumeroRue())
                .codePostal(c.getCodePostal())
                .ville(c.getVille())
                .pays(c.getPays())
                .modeContactPrefere(c.getModeContactPrefere())
                .emailVerified(c.isEmailVerified())
                .adminApproved(c.isAdminApproved())
                .devisIds(c.getDevisList().stream().map(Devis::getId).collect(Collectors.toList()))
                .notificationIds(c.getNotifications().stream().map(GestionnaireNotifications::getId).collect(Collectors.toList()))
                .build();
    }

    /** DTO création ➜ ENTITÉ */
    public Client toEntity(ClientCreateDto dto) {
        Client c = Client.builder()
                .password(dto.getPassword())
                .typeClient(dto.getTypeClient())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .siege(dto.getSiege())
                .representant(dto.getRepresentant())
                .numeroSiret(dto.getNumeroSiret())
                .email(dto.getEmail())
                .telephone(dto.getTelephone())
                .adresse(dto.getAdresse())
                .numeroRue(dto.getNumeroRue())
                .codePostal(dto.getCodePostal())
                .ville(dto.getVille())
                .pays(dto.getPays())
                .modeContactPrefere(dto.getModeContactPrefere())
                .build();

        if (dto.getDevisIds() != null) {
            dto.getDevisIds().forEach(id -> {
                Devis d = devisRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Devis id=" + id + " introuvable"));
                d.setClient(c);
                c.getDevisList().add(d);
            });
        }
        if (dto.getNotificationIds() != null) {
            dto.getNotificationIds().forEach(id -> {
                GestionnaireNotifications n = notifRepo.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Notification id=" + id + " introuvable"));
                n.setClient(c);
                c.getNotifications().add(n);
            });
        }
        return c;
    }

    /** DTO update ➜ ENTITÉ existante (ne touche pas aux drapeaux de sécurité) */
    public void updateEntityFromDto(ClientDto dto, Client c) {
        // Mise à jour du rôle (ajouté pour permettre le changement de rôle par l'admin)
        if (dto.getRole() != null) {
            c.setRole(dto.getRole());
        }
        
        c.setNom(dto.getNom());
        c.setPrenom(dto.getPrenom());
        c.setSiege(dto.getSiege());
        c.setRepresentant(dto.getRepresentant());
        c.setNumeroSiret(dto.getNumeroSiret());
        c.setTelephone(dto.getTelephone());
        c.setAdresse(dto.getAdresse());
        c.setNumeroRue(dto.getNumeroRue());
        c.setCodePostal(dto.getCodePostal());
        c.setVille(dto.getVille());
        c.setPays(dto.getPays());
        c.setModeContactPrefere(dto.getModeContactPrefere());

        // NE PAS mettre à jour les collections pour éviter les problèmes de lazy loading
        // Les collections (devis et notifications) ne sont mises à jour que si explicitement demandé
    }

    /** Méthode spécifique pour la mise à jour simple (sans collections) */
    public void updateBasicFieldsFromDto(ClientDto dto, Client c) {
        // Mise à jour du rôle
        if (dto.getRole() != null) {
            c.setRole(dto.getRole());
        }
        
        // Mise à jour des champs simples uniquement
        if (dto.getNom() != null) c.setNom(dto.getNom());
        if (dto.getPrenom() != null) c.setPrenom(dto.getPrenom());
        if (dto.getSiege() != null) c.setSiege(dto.getSiege());
        if (dto.getRepresentant() != null) c.setRepresentant(dto.getRepresentant());
        if (dto.getNumeroSiret() != null) c.setNumeroSiret(dto.getNumeroSiret());
        if (dto.getTelephone() != null) c.setTelephone(dto.getTelephone());
        if (dto.getAdresse() != null) c.setAdresse(dto.getAdresse());
        if (dto.getNumeroRue() != null) c.setNumeroRue(dto.getNumeroRue());
        if (dto.getCodePostal() != null) c.setCodePostal(dto.getCodePostal());
        if (dto.getVille() != null) c.setVille(dto.getVille());
        if (dto.getPays() != null) c.setPays(dto.getPays());
        if (dto.getModeContactPrefere() != null) c.setModeContactPrefere(dto.getModeContactPrefere());
    }
}
