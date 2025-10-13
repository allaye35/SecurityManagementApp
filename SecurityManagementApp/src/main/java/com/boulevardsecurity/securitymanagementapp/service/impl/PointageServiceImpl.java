// src/main/java/com/boulevardsecurity/securitymanagementapp/service/impl/PointageServiceImpl.java
package com.boulevardsecurity.securitymanagementapp.service.impl;

import com.boulevardsecurity.securitymanagementapp.dto.AgentDeSecuriteDto;
import com.boulevardsecurity.securitymanagementapp.dto.PointageCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.PointageDto;
import com.boulevardsecurity.securitymanagementapp.mapper.AgentDeSecuriteMapper;
import com.boulevardsecurity.securitymanagementapp.mapper.PointageMapper;
import com.boulevardsecurity.securitymanagementapp.model.GeoPoint;
import com.boulevardsecurity.securitymanagementapp.model.Mission;
import com.boulevardsecurity.securitymanagementapp.model.Pointage;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import com.boulevardsecurity.securitymanagementapp.repository.PointageRepository;
import com.boulevardsecurity.securitymanagementapp.service.PointageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointageServiceImpl implements PointageService {

    private final PointageRepository pointageRepository;
    private final MissionRepository missionRepository;
    private final PointageMapper mapper;
    private final AgentDeSecuriteMapper agentMapper;

    @Override
    public List<PointageDto> recupererTousLesPointages() {
        return pointageRepository.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<PointageDto> recupererPointageParId(Long id) {
        return pointageRepository.findById(id)
                .map(mapper::toDto);
    }

    @Override
    public List<PointageDto> recupererPointagesParMission(Long idMission) {
        return pointageRepository.findByMissionId(idMission).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PointageDto creerPointage(PointageCreateDto dto) {
        System.out.println("DEBUG - Création pointage avec DTO: " + dto);
        System.out.println("DEBUG - AgentID: " + dto.getAgentId());
        System.out.println("DEBUG - MissionID: " + dto.getMissionId());
        System.out.println("DEBUG - Latitude: " + dto.getLatitude() + ", Longitude: " + dto.getLongitude());
        
        Pointage pointage = mapper.toEntity(dto);
        
        System.out.println("DEBUG - Pointage après mapping:");
        System.out.println("DEBUG - AgentID dans entité: " + pointage.getAgentId());
        System.out.println("DEBUG - Position GPS: " + pointage.getPositionActuelle());

        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + dto.getMissionId()));

        boolean estAffecte = mission.getAgents().stream()
                .anyMatch(a -> a.getId().equals(dto.getAgentId()));
        if (!estAffecte) {
            throw new IllegalArgumentException("Agent non affecté à la mission id=" + dto.getAgentId());
        }

        if (!missionEnCours(mission)) {
            throw new IllegalArgumentException("Mission non en cours id=" + dto.getMissionId());
        }

        GeoPoint centre = mission.getGeolocalisationGPS().getPosition();
        if (centre == null) {
            throw new IllegalArgumentException("Mission sans position GPS id=" + dto.getMissionId());
        }
        if (!dansLaZone(centre, pointage.getPositionActuelle(), 100f)) {
            throw new IllegalArgumentException("Position hors zone autorisée");
        }

        pointage.setDatePointage(new Date());
        pointage.setMission(mission);
        
        System.out.println("DEBUG - Pointage avant sauvegarde:");
        System.out.println("DEBUG - AgentID: " + pointage.getAgentId());
        System.out.println("DEBUG - Position: " + pointage.getPositionActuelle());
        
        Pointage enregistre = pointageRepository.save(pointage);
        
        System.out.println("DEBUG - Pointage après sauvegarde:");
        System.out.println("DEBUG - ID: " + enregistre.getId());
        System.out.println("DEBUG - AgentID: " + enregistre.getAgentId());
        System.out.println("DEBUG - Position: " + enregistre.getPositionActuelle());
        
        return mapper.toDto(enregistre);
    }

    @Override
    public PointageDto modifierPointage(Long id, PointageCreateDto dto) {
        System.out.println("DEBUG MODIFICATION - DTO reçu: " + dto);
        System.out.println("DEBUG - Pointage ID: " + id);
        System.out.println("DEBUG - AgentID: " + dto.getAgentId());
        System.out.println("DEBUG - MissionID: " + dto.getMissionId());
        System.out.println("DEBUG - Latitude: " + dto.getLatitude() + ", Longitude: " + dto.getLongitude());
        System.out.println("DEBUG - Date: " + dto.getDatePointage());
        
        Pointage existant = pointageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pointage introuvable id=" + id));
        
        System.out.println("DEBUG - Pointage avant modification:");
        System.out.println("DEBUG - AgentID actuel: " + existant.getAgentId());
        System.out.println("DEBUG - MissionID actuel: " + (existant.getMission() != null ? existant.getMission().getId() : "null"));
        System.out.println("DEBUG - Position actuelle: " + existant.getPositionActuelle());
        
        // Utiliser le mapper pour mettre à jour l'entité
        mapper.updateEntity(dto, existant);
        
        System.out.println("DEBUG - Pointage après mapping:");
        System.out.println("DEBUG - AgentID: " + existant.getAgentId());
        System.out.println("DEBUG - MissionID: " + (existant.getMission() != null ? existant.getMission().getId() : "null"));
        System.out.println("DEBUG - Position: " + existant.getPositionActuelle());
        
        Pointage sauvegarde = pointageRepository.save(existant);
        
        System.out.println("DEBUG - Pointage sauvegardé:");
        System.out.println("DEBUG - ID: " + sauvegarde.getId());
        System.out.println("DEBUG - AgentID: " + sauvegarde.getAgentId());
        System.out.println("DEBUG - Position: " + sauvegarde.getPositionActuelle());
        
        return mapper.toDto(sauvegarde);
    }

    @Override
    public void supprimerPointage(Long id) {
        if (!pointageRepository.existsById(id)) {
            throw new NoSuchElementException("Pointage introuvable id=" + id);
        }
        pointageRepository.deleteById(id);
    }

    @Override
    public PointageDto enregistrerPriseDeService(PointageCreateDto dto) {
        System.out.println("DEBUG PRISE SERVICE - DTO reçu: " + dto);
        System.out.println("DEBUG - AgentID: " + dto.getAgentId());
        System.out.println("DEBUG - Latitude: " + dto.getLatitude() + ", Longitude: " + dto.getLongitude());
        
        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + dto.getMissionId()));

        boolean estAffecte = mission.getAgents().stream()
                .anyMatch(a -> a.getId().equals(dto.getAgentId()));
        if (!estAffecte) {
            throw new IllegalArgumentException("Agent non affecté à cette mission");
        }

        boolean dejaEnService = pointageRepository.findByMissionId(dto.getMissionId()).stream()
                .anyMatch(p -> p.getAgentId() != null 
                    && p.getAgentId().equals(dto.getAgentId()) 
                    && p.getFinService() == null);
        if (dejaEnService) {
            throw new IllegalArgumentException("Agent déjà en service pour cette mission");
        }

        if (mission.getGeolocalisationGPS() != null && mission.getGeolocalisationGPS().getPosition() != null) {
            GeoPoint centre = mission.getGeolocalisationGPS().getPosition();
            Pointage pointage = mapper.toEntity(dto);
            if (!dansLaZone(centre, pointage.getPositionActuelle(), 100f)) {
                throw new IllegalArgumentException("Position GPS trop éloignée du site de la mission (max 100m)");
            }
        }

        Pointage pointage = mapper.toEntity(dto);
        pointage.setDatePointage(new Date());
        pointage.setMission(mission);
        pointage.setAgentId(dto.getAgentId());
        pointage.setFinService(null);
        
        System.out.println("DEBUG - Pointage avant sauvegarde:");
        System.out.println("DEBUG - AgentID: " + pointage.getAgentId());
        System.out.println("DEBUG - Position: " + pointage.getPositionActuelle());
        
        Pointage enregistre = pointageRepository.save(pointage);
        
        System.out.println("DEBUG - Pointage sauvegardé:");
        System.out.println("DEBUG - ID: " + enregistre.getId());
        System.out.println("DEBUG - AgentID: " + enregistre.getAgentId());
        System.out.println("DEBUG - Position: " + enregistre.getPositionActuelle());
        
        return mapper.toDto(enregistre);
    }

    @Override
    public PointageDto enregistrerFinDeService(PointageCreateDto dto) {
        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + dto.getMissionId()));

        boolean estAffecte = mission.getAgents().stream()
                .anyMatch(a -> a.getId().equals(dto.getAgentId()));
        if (!estAffecte) {
            throw new IllegalArgumentException("Agent non affecté à cette mission");
        }

        Optional<Pointage> priseDeService = pointageRepository.findByMissionId(dto.getMissionId()).stream()
                .filter(p -> p.getAgentId() != null 
                    && p.getAgentId().equals(dto.getAgentId()) 
                    && p.getFinService() == null)
                .findFirst();

        if (!priseDeService.isPresent()) {
            throw new IllegalArgumentException("Aucune prise de service active trouvée pour cet agent");
        }

        Pointage pointage = priseDeService.get();
        pointage.setFinService(new Date());
        
        Pointage enregistre = pointageRepository.save(pointage);
        return mapper.toDto(enregistre);
    }

    @Override
    public List<AgentDeSecuriteDto> getAgentsEnService(Long idMission) {
        Mission mission = missionRepository.findById(idMission)
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + idMission));

        List<Long> agentIdsEnService = pointageRepository.findByMissionId(idMission).stream()
                .filter(p -> p.getFinService() == null && p.getAgentId() != null)
                .map(Pointage::getAgentId)
                .distinct()
                .collect(Collectors.toList());

        return mission.getAgents().stream()
                .filter(agent -> agentIdsEnService.contains(agent.getId()))
                .map(agentMapper::toDto)
                .collect(Collectors.toList());
    }

    private boolean missionEnCours(Mission mission) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debut = LocalDateTime.of(mission.getDateDebut(), mission.getHeureDebut());
        LocalDateTime fin   = LocalDateTime.of(mission.getDateFin(),   mission.getHeureFin());
        return maintenant.isAfter(debut) && maintenant.isBefore(fin);
    }

    private boolean dansLaZone(GeoPoint centre, GeoPoint pt, float toleranceMetres) {
        final double R = 6_371_000;
        double dLat = Math.toRadians(pt.getLatitude()  - centre.getLatitude());
        double dLon = Math.toRadians(pt.getLongitude() - centre.getLongitude());
        double a = Math.sin(dLat/2)*Math.sin(dLat/2)
                + Math.cos(Math.toRadians(centre.getLatitude()))
                * Math.cos(Math.toRadians(pt.getLatitude()))
                * Math.sin(dLon/2)*Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c <= toleranceMetres;
    }
}
