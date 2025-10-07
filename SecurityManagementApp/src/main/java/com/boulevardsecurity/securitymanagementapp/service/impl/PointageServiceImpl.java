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
        Pointage pointage = mapper.toEntity(dto);

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
        Pointage enregistre = pointageRepository.save(pointage);
        return mapper.toDto(enregistre);
    }

    @Override
    public PointageDto modifierPointage(Long id, PointageCreateDto dto) {
        Pointage existant = pointageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pointage introuvable id=" + id));
        mapper.updateEntity(dto, existant);
        Pointage sauvegarde = pointageRepository.save(existant);
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
        // Récupérer la mission
        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + dto.getMissionId()));

        // Vérifier que l'agent est assigné à la mission
        boolean estAffecte = mission.getAgents().stream()
                .anyMatch(a -> a.getId().equals(dto.getAgentId()));
        if (!estAffecte) {
            throw new IllegalArgumentException("Agent non affecté à cette mission");
        }

        // Vérifier que l'agent n'est pas déjà en service pour cette mission
        boolean dejaEnService = pointageRepository.findByMissionId(dto.getMissionId()).stream()
                .anyMatch(p -> p.getAgentId() != null 
                    && p.getAgentId().equals(dto.getAgentId()) 
                    && p.getFinService() == null);
        if (dejaEnService) {
            throw new IllegalArgumentException("Agent déjà en service pour cette mission");
        }

        // Validation géolocalisation si la mission a une position GPS
        if (mission.getGeolocalisationGPS() != null && mission.getGeolocalisationGPS().getPosition() != null) {
            GeoPoint centre = mission.getGeolocalisationGPS().getPosition();
            Pointage pointage = mapper.toEntity(dto);
            if (!dansLaZone(centre, pointage.getPositionActuelle(), 100f)) {
                throw new IllegalArgumentException("Position GPS trop éloignée du site de la mission (max 100m)");
            }
        }

        // Créer le pointage de prise de service
        Pointage pointage = mapper.toEntity(dto);
        pointage.setDatePointage(new Date());
        pointage.setMission(mission);
        pointage.setAgentId(dto.getAgentId());
        pointage.setFinService(null); // Explicitement null pour indiquer "en service"
        
        Pointage enregistre = pointageRepository.save(pointage);
        return mapper.toDto(enregistre);
    }

    @Override
    public PointageDto enregistrerFinDeService(PointageCreateDto dto) {
        // Récupérer la mission
        Mission mission = missionRepository.findById(dto.getMissionId())
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + dto.getMissionId()));

        // Vérifier que l'agent est assigné à la mission
        boolean estAffecte = mission.getAgents().stream()
                .anyMatch(a -> a.getId().equals(dto.getAgentId()));
        if (!estAffecte) {
            throw new IllegalArgumentException("Agent non affecté à cette mission");
        }

        // Trouver le pointage de prise de service actif pour cet agent sur cette mission
        Optional<Pointage> priseDeService = pointageRepository.findByMissionId(dto.getMissionId()).stream()
                .filter(p -> p.getAgentId() != null 
                    && p.getAgentId().equals(dto.getAgentId()) 
                    && p.getFinService() == null)
                .findFirst();

        if (!priseDeService.isPresent()) {
            throw new IllegalArgumentException("Aucune prise de service active trouvée pour cet agent");
        }

        // Mettre à jour le pointage existant avec la fin de service
        Pointage pointage = priseDeService.get();
        pointage.setFinService(new Date());
        
        Pointage enregistre = pointageRepository.save(pointage);
        return mapper.toDto(enregistre);
    }

    @Override
    public List<AgentDeSecuriteDto> getAgentsEnService(Long idMission) {
        // Récupérer la mission pour avoir accès aux agents
        Mission mission = missionRepository.findById(idMission)
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable id=" + idMission));

        // Récupérer tous les pointages actifs (sans fin de service) pour cette mission
        List<Long> agentIdsEnService = pointageRepository.findByMissionId(idMission).stream()
                .filter(p -> p.getFinService() == null && p.getAgentId() != null)
                .map(Pointage::getAgentId)
                .distinct()
                .collect(Collectors.toList());

        // Récupérer les agents de la mission qui sont en service
        return mission.getAgents().stream()
                .filter(agent -> agentIdsEnService.contains(agent.getId()))
                .map(agentMapper::toDto)
                .collect(Collectors.toList());
    }

    // ── Méthodes utilitaires ─────────────────────────────────────────────────

    private boolean missionEnCours(Mission mission) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debut = LocalDateTime.of(mission.getDateDebut(), mission.getHeureDebut());
        LocalDateTime fin   = LocalDateTime.of(mission.getDateFin(),   mission.getHeureFin());
        return maintenant.isAfter(debut) && maintenant.isBefore(fin);
    }

    private boolean dansLaZone(GeoPoint centre, GeoPoint pt, float toleranceMetres) {
        final double R = 6_371_000; // rayon Terre en m
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
