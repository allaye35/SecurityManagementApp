package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.model.*;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import com.boulevardsecurity.securitymanagementapp.repository.MissionRepository;
import com.boulevardsecurity.securitymanagementapp.repository.PointageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointageService {

    private final PointageRepository pointageRepository;
    private final MissionRepository missionRepository;
    private final AgentDeSecuriteRepository agentDeSecuriteRepository;

    /**
     * Créer un nouveau pointage
     */
    public Pointage creerPointage(Long missionId, Long agentId, Pointage pointage) {
        // 1) Vérifier si la mission existe
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable !"));

        // 2) Vérifier si l'agent est bien affecté à cette mission
        boolean estAssigne = mission.getAgents().stream().anyMatch(agent -> agent.getId().equals(agentId));
        if (!estAssigne) {
            throw new IllegalArgumentException("L'agent n'est pas affecté à cette mission !");
        }

        // 3) Vérifier si la mission est en cours
        if (!verifierMissionEnCours(mission)) {
            throw new IllegalArgumentException("La mission n'est pas en cours actuellement !");
        }

        // 4) Vérifier la position GPS
        GeolocalisationGPS geoMission = mission.getGeolocalisationGPS();
        if (geoMission == null || geoMission.getPosition() == null) {
            throw new IllegalArgumentException("La mission n'a pas de position GPS définie !");
        }

        // 5) Vérifier la position de l'agent
        float toleranceMetres = 100f;
        if (!verifierPositionPointage(geoMission.getPosition(), pointage.getPositionActuelle(), toleranceMetres)) {
            throw new IllegalArgumentException("❌ L'agent n'est pas dans la zone autorisée pour pointer !");
        }

        // 6) Enregistrer le pointage
        pointage.setDatePointage(new Date());
        pointage.setMission(mission);

        return pointageRepository.save(pointage);
    }

    /**
     * Vérifier si la mission est en cours
     */
    public boolean verifierMissionEnCours(Mission mission) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalDateTime debut = LocalDateTime.of(mission.getDateDebut(), mission.getHeureDebut());
        LocalDateTime fin = LocalDateTime.of(mission.getDateFin(), mission.getHeureFin());

        return (maintenant.isAfter(debut) && maintenant.isBefore(fin));
    }

    /**
     * Vérifier si la position de l'agent est dans la zone autorisée
     */
    private boolean verifierPositionPointage(GeoPoint missionPos, GeoPoint agentPos, float toleranceMetres) {
        double distance = calculerDistanceEnMetres(missionPos, agentPos);
        return distance <= toleranceMetres;
    }

    /**
     * Calculer la distance entre deux points géographiques
     */
    private double calculerDistanceEnMetres(GeoPoint p1, GeoPoint p2) {
        final int R = 6371000; // Rayon de la Terre en mètres

        double lat1 = Math.toRadians(p1.getLatitude());
        double lat2 = Math.toRadians(p2.getLatitude());
        double dLat = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double dLon = Math.toRadians(p2.getLongitude() - p1.getLongitude());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Récupérer tous les pointages
     */
    public List<Pointage> obtenirTousLesPointages() {
        return pointageRepository.findAll();
    }

    /**
     * Récupérer un pointage par ID
     */
    public Optional<Pointage> obtenirPointageParId(Long id) {
        return pointageRepository.findById(id);
    }

    /**
     * Récupérer tous les pointages d'une mission
     */
    public List<Pointage> obtenirPointagesParMission(Long missionId) {
        return pointageRepository.findByMissionId(missionId);
    }

    /**
     * Mettre à jour un pointage existant
     */
    public Pointage mettreAJourPointage(Long id, Pointage nouveauPointage) {
        Pointage pointageExistant = pointageRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pointage non trouvé !"));

        pointageExistant.setEstPresent(nouveauPointage.isEstPresent());
        pointageExistant.setEstRetard(nouveauPointage.isEstRetard());
        pointageExistant.setPositionActuelle(nouveauPointage.getPositionActuelle());
        pointageExistant.setMission(nouveauPointage.getMission());

        return pointageRepository.save(pointageExistant);
    }

    /**
     * Supprimer un pointage
     */
    public void supprimerPointage(Long id) {
        if (!pointageRepository.existsById(id)) {
            throw new NoSuchElementException("Pointage non trouvé !");
        }
        pointageRepository.deleteById(id);
    }
}
