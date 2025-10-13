package com.boulevardsecurity.securitymanagementapp.mapper;

import com.boulevardsecurity.securitymanagementapp.dto.*;
import com.boulevardsecurity.securitymanagementapp.model.GestionnaireNotifications;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationDto toDto(GestionnaireNotifications entity) {
        return NotificationDto.builder()
                .id(entity.getId())
                .titre(entity.getTitre())
                .message(entity.getMessage())
                .destinataire(entity.getDestinataire())
                .dateEnvoi(entity.getDateEnvoi())
                .build();
    }

    public GestionnaireNotifications toEntity(NotificationCreateDto dto) {
        return GestionnaireNotifications.builder()
                .titre(dto.getTitre())
                .message(dto.getMessage())
                .destinataire(dto.getDestinataire())
                .build();
    }

    public void updateEntity(GestionnaireNotifications entity,
                             NotificationCreateDto dto) {

        if (dto.getTitre()        != null) entity.setTitre(dto.getTitre());
        if (dto.getMessage()      != null) entity.setMessage(dto.getMessage());
        if (dto.getDestinataire() != null) entity.setDestinataire(dto.getDestinataire());
        
    }
}

