    // src/main/java/com/boulevardsecurity/securitymanagementapp/mapper/PointageMapper.java
    package com.boulevardsecurity.securitymanagementapp.mapper;

    import com.boulevardsecurity.securitymanagementapp.dto.PointageCreateDto;
    import com.boulevardsecurity.securitymanagementapp.dto.PointageDto;
    import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
    import com.boulevardsecurity.securitymanagementapp.model.GeoPoint;
    import com.boulevardsecurity.securitymanagementapp.model.Pointage;
    import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
    import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Component;

    @Component
    @RequiredArgsConstructor
    public class PointageMapper {

        private final MissionRepository missionRepo;
        private final AgentDeSecuriteRepository agentRepo;

    public PointageDto toDto(Pointage ent) {
        var pos = ent.getPositionActuelle();
        var mission = ent.getMission();
        
        // Récupérer l'agent si agentId existe
        AgentDeSecurite agent = null;
        if (ent.getAgentId() != null) {
            agent = agentRepo.findById(ent.getAgentId()).orElse(null);
        }
        
        // Log pour déboguer
        if (ent.getAgentId() != null) {
            System.out.println("DEBUG - AgentID dans Pointage: " + ent.getAgentId());
            System.out.println("DEBUG - Agent trouvé: " + (agent != null ? agent.getNom() + " " + agent.getPrenom() : "null"));
        }
        
        // Vérifier les coordonnées GPS
        double latitude = (pos != null && pos.getLatitude() != 0.0) ? pos.getLatitude() : 0.0;
        double longitude = (pos != null && pos.getLongitude() != 0.0) ? pos.getLongitude() : 0.0;
        
        return PointageDto.builder()
                .id(ent.getId())
                .datePointage(ent.getDatePointage())
                .estPresent(ent.isEstPresent())
                .estRetard(ent.isEstRetard())
                .latitude(latitude)
                .longitude(longitude)
                .missionId(mission != null ? mission.getId() : null)
                .missionTitre(mission != null ? mission.getTitre() : null)
                .agentId(ent.getAgentId())
                .agentNom(agent != null ? agent.getNom() : null)
                .agentPrenom(agent != null ? agent.getPrenom() : null)
                .build();
    }        public Pointage toEntity(PointageCreateDto dto) {
            GeoPoint point = GeoPoint.builder()
                    .latitude(dto.getLatitude())
                    .longitude(dto.getLongitude())
                    .build();

            var mission = missionRepo.findById(dto.getMissionId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Mission introuvable, id=" + dto.getMissionId()));

            return Pointage.builder()
                    .datePointage(dto.getDatePointage())
                    .estPresent(dto.isEstPresent())
                    .estRetard(dto.isEstRetard())
                    .positionActuelle(point)
                    .mission(mission)
                    .agentId(dto.getAgentId())
                    .build();
        }

        public void updateEntity(PointageCreateDto dto, Pointage ent) {
            // Mise à jour de la date et heure
            if (dto.getDatePointage() != null) {
                ent.setDatePointage(dto.getDatePointage());
            }
            
            // Mise à jour des statuts présence et retard
            ent.setEstPresent(dto.isEstPresent());
            ent.setEstRetard(dto.isEstRetard());

            // Mise à jour de la position GPS
            if (ent.getPositionActuelle() == null) {
                ent.setPositionActuelle(new GeoPoint());
            }
            ent.getPositionActuelle().setLatitude(dto.getLatitude());
            ent.getPositionActuelle().setLongitude(dto.getLongitude());

            // Mise à jour de la mission
            if (dto.getMissionId() != null
                    && (ent.getMission() == null || !dto.getMissionId().equals(ent.getMission().getId()))) {
                var mission = missionRepo.findById(dto.getMissionId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Mission introuvable, id=" + dto.getMissionId()));
                ent.setMission(mission);
            }
            
            // Mise à jour de l'agentId - IMPORTANT pour la modification
            if (dto.getAgentId() != null) {
                // Vérifier que l'agent existe
                agentRepo.findById(dto.getAgentId())
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Agent introuvable, id=" + dto.getAgentId()));
                ent.setAgentId(dto.getAgentId());
            }
        }
    }
