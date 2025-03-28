package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.Enums.TypeMission;
import com.boulevardsecurity.securitymanagementapp.model.*;
import com.boulevardsecurity.securitymanagementapp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Service gérant la création et la modification des Missions,
 * notamment la conversion d’adresse du site en coordonnées GPS,
 * l’enregistrement dans GeolocalisationGPS et l’envoi de notifications
 * (email + SMS) aux agents concernés.
 */
@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final AgentDeSecuriteRepository agentRepository;
    private final RapportInterventionRepository rapportRepository;
    private final PlanningRepository planningRepository;
    private final SiteRepository siteRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final GeolocalisationGPSRepository geolocalisationGPSRepository;

    // Pour le géocodage (ex. Google Maps, etc.)
    private final GeocodingService geocodingService;


    // ➜ Service d'envoi de mail + SMS
    private final NotificationService notificationService;


    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer toutes les missions
    // ─────────────────────────────────────────────────────────────────────────────
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer une mission par son ID
    // ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> getMissionById(Long id) {
        return missionRepository.findById(id);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Créer une nouvelle mission (avec conversion d’adresse -> position GPS)
    //    On suppose qu’on reçoit la Mission + l’adresse du site en paramètre
    //    => On envoie la notification aux agents déjà renseignés dans la mission.
    // ─────────────────────────────────────────────────────────────────────────────
    public Mission saveMission(Mission mission, String adresseDuSite) {

        // 1) Géocodage si l’adresse est fournie
        if (adresseDuSite != null && !adresseDuSite.isBlank()) {
            GeoPoint geoPoint = geocodingService.getCoordinatesFromAddress(adresseDuSite);

            // Créer un GeolocalisationGPS
            GeolocalisationGPS geo = new GeolocalisationGPS();
            geo.setPosition(geoPoint);
            geo.setGps_precision(5.0f);

            // Enregistrer la géolocalisation
            geolocalisationGPSRepository.save(geo);

            // Associer à la mission
            mission.setGeolocalisationGPS(geo);
        }

        // 2) Sauvegarde mission
        Mission savedMission = missionRepository.save(mission);

        // 3) Notifier les agents déjà liés à la mission
        // (dans mission.getAgents())
        for (AgentDeSecurite agent : savedMission.getAgents()) {
            envoyerNotificationAgent(
                    agent,
                    "[Création de Mission] " + savedMission.getTitre(),
                    "Bonjour " + agent.getNom()
                            + ",\nUne nouvelle mission vous est affectée : "
                            + savedMission.getTitre()
                            + "\nDate début : " + savedMission.getDateDebut()
                            + "\nDate fin : "   + savedMission.getDateFin()
            );
        }

        return savedMission;
    }

    // (Surcharge) pour garder compatibilité avec l’ancien code
    public Mission saveMission(Mission mission) {
        return saveMission(mission, null);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Supprimer une mission par ID => On notifie aussi
    // ─────────────────────────────────────────────────────────────────────────────
    public void deleteMission(Long id) {
        Optional<Mission> optional = missionRepository.findById(id);
        if (optional.isPresent()) {
            Mission existing = optional.get();

            // Avant de supprimer, on notifie tous les agents
            for (AgentDeSecurite agent : existing.getAgents()) {
                envoyerNotificationAgent(
                        agent,
                        "[Suppression de Mission] " + existing.getTitre(),
                        "Bonjour " + agent.getNom()
                                + ",\nLa mission " + existing.getTitre()
                                + " vient d'être supprimée."
                );
            }

            // Ensuite on supprime
            missionRepository.deleteById(id);
        }
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Mettre à jour une mission existante (avec possibilité de re-géocoder)
    //    => Notifie tous les agents de la mission
    // ─────────────────────────────────────────────────────────────────────────────
    public Mission updateMission(Long id, Mission updatedMission, String nouvelleAdresse) {
        Mission existing = missionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Mission introuvable pour l'ID " + id));

        // 1) Modifier les attributs de base
        existing.setTitre(updatedMission.getTitre());
        existing.setDescription(updatedMission.getDescription());
        existing.setDateDebut(updatedMission.getDateDebut());
        existing.setDateFin(updatedMission.getDateFin());
        existing.setHeureDebut(updatedMission.getHeureDebut());
        existing.setHeureFin(updatedMission.getHeureFin());
        existing.setStatutMission(updatedMission.getStatutMission());
        existing.setTypeMission(updatedMission.getTypeMission());

        // 2) Modifier les relations, si présentes
        //    On peut imaginer qu'on retient la liste "avant" pour enlever
        //    ou qu'on notifie tout le monde "après".
        if (updatedMission.getAgents() != null) {
            // On peut nettoyer puis rajouter
            existing.getAgents().clear();
            existing.getAgents().addAll(updatedMission.getAgents());
        }
        if (updatedMission.getPlanning() != null) {
            existing.setPlanning(updatedMission.getPlanning());
        }
        if (updatedMission.getSite() != null) {
            existing.setSite(updatedMission.getSite());
        }
        if (updatedMission.getEntreprise() != null) {
            existing.setEntreprise(updatedMission.getEntreprise());
        }
        if (updatedMission.getRapports() != null) {
            existing.getRapports().clear();
            existing.getRapports().addAll(updatedMission.getRapports());
        }
        if (updatedMission.getPointages() != null) {
            existing.getPointages().clear();
            existing.getPointages().addAll(updatedMission.getPointages());
        }

        // 3) Re-géocodage si besoin
        if (nouvelleAdresse != null && !nouvelleAdresse.isBlank()) {
            GeoPoint geoPoint = geocodingService.getCoordinatesFromAddress(nouvelleAdresse);
            GeolocalisationGPS geo = new GeolocalisationGPS();
            geo.setPosition(geoPoint);
            geo.setGps_precision(5.0f);

            geolocalisationGPSRepository.save(geo);
            existing.setGeolocalisationGPS(geo);
        }

        // 4) Sauvegarde
        Mission saved = missionRepository.save(existing);

        // 5) Notifier les agents
        for (AgentDeSecurite agent : saved.getAgents()) {
            envoyerNotificationAgent(
                    agent,
                    "[Mise à Jour de Mission] " + saved.getTitre(),
                    "Bonjour " + agent.getNom()
                            + ",\nLa mission " + saved.getTitre()
                            + " a été modifiée.\nDates : " + saved.getDateDebut()
                            + " au " + saved.getDateFin()
            );
        }

        return saved;
    }

    // Surcharge
    public Mission updateMission(Long id, Mission updatedMission) {
        return updateMission(id, updatedMission, null);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Planifier plusieurs agents sur une mission (avec vérifications)
    //    => Chaque fois qu’on affecte des agents, on peut les notifier s’ils sont nouveaux.
    // ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> assignAgents(Long missionId, List<Long> agentIds) {
        return missionRepository.findById(missionId).map(mission -> {
            List<AgentDeSecurite> agents = agentRepository.findAllById(agentIds);

            for (AgentDeSecurite agent : agents) {
                // Vérifications métier (ex. carte pro, dispo, etc.)
                if (!verifierZoneTravailAgent(agent, mission)) {
                    throw new IllegalArgumentException(" L'agent " + agent.getNom()
                            + " n'a pas la zone de travail correspondant au site de la mission.");
                }
                if (!verifierDisponibilite(agent, mission)) {
                    throw new IllegalArgumentException(" L'agent " + agent.getNom()
                            + " n'est pas disponible pendant la période de la mission.");
                }
                if (hasScheduleConflict(agent, mission)) {
                    throw new IllegalArgumentException(" L'agent " + agent.getNom()
                            + " est déjà affecté à une autre mission chevauchant celle-ci.");
                }
                if (!verifierCarteProfessionnelle(agent, mission.getTypeMission())) {
                    throw new IllegalArgumentException(" L'agent " + agent.getNom()
                            + " n'a pas la carte professionnelle valide pour ce type de mission.");
                }
                if (!verifierDiplomeSSIAP(agent, mission.getTypeMission())) {
                    throw new IllegalArgumentException(" L'agent " + agent.getNom()
                            + " n'a pas le diplôme SSIAP requis pour cette mission.");
                }

                // Ajout relation
                agent.getMissions().add(mission);
                mission.getAgents().add(agent);

                // Notifier l’agent de sa nouvelle affectation
                envoyerNotificationAgent(
                        agent,
                        "[Nouvelle Affectation] Mission " + mission.getTitre(),
                        "Bonjour " + agent.getNom()
                                + ",\nVous venez d'être affecté à la mission : " + mission.getTitre()
                );
            }

            // Sauvegarde
            missionRepository.save(mission);
            agentRepository.saveAll(agents);

            return mission;
        });
    }
    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Empêcher la rédaction d’un rapport avant le début de la mission
    // ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> assignRapport(Long missionId, Long rapportId) {
        return missionRepository.findById(missionId).flatMap(mission ->
                rapportRepository.findById(rapportId).map(rapport -> {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime missionStart = mission.getDateDebut().atStartOfDay();

                    if (now.isBefore(missionStart)) {
                        throw new IllegalArgumentException(
                                " Impossible de rédiger un rapport avant le début de la mission !");
                    }
                    mission.getRapports().add(rapport);
                    return missionRepository.save(mission);
                })
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer un planning à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> assignPlanning(Long missionId, Long planningId) {
        return missionRepository.findById(missionId).flatMap(mission ->
                planningRepository.findById(planningId).map(planning -> {
                    mission.setPlanning(planning);
                    return missionRepository.save(mission);
                })
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer un site à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> assignSite(Long missionId, Long siteId) {
        return missionRepository.findById(missionId).flatMap(mission ->
                siteRepository.findById(siteId).map(site -> {
                    mission.setSite(site);
                    return missionRepository.save(mission);
                })
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Associer une entreprise à une mission
    // ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> assignEntreprise(Long missionId, Long entrepriseId) {
        return missionRepository.findById(missionId).flatMap(mission ->
                entrepriseRepository.findById(entrepriseId).map(entreprise -> {
                    mission.setEntreprise(entreprise);
                    return missionRepository.save(mission);
                })
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
// 🔹 Associer une géolocalisation à une mission
// ─────────────────────────────────────────────────────────────────────────────
    public Optional<Mission> assignGeolocalisationToMission(Long missionId) {
        Optional<Mission> missionOptional = missionRepository.findById(missionId);

        if (missionOptional.isEmpty()) {
            System.out.println("Mission non trouvée pour l'ID : " + missionId);
            return Optional.empty();
        }

        Mission mission = missionOptional.get();

        // Récupérer le site associé à la mission
        Site site = mission.getSite();
        if (site == null) {
            System.out.println("Aucun site associé à cette mission.");
            return Optional.empty();
        }

        // Construire l'adresse complète
        StringBuilder adresseBuilder = new StringBuilder();
        if (site.getNumero() != null) adresseBuilder.append(site.getNumero()).append(" ");
        if (site.getRue() != null) adresseBuilder.append(site.getRue()).append(", ");
        if (site.getCodePostal() != null) adresseBuilder.append(site.getCodePostal()).append(" ");
        if (site.getVille() != null) adresseBuilder.append(site.getVille()).append(", ");
        if (site.getDepartement() != null) adresseBuilder.append(site.getDepartement()).append(", ");
        if (site.getRegion() != null) adresseBuilder.append(site.getRegion()).append(", ");
        if (site.getPays() != null) adresseBuilder.append(site.getPays());

        String adresseComplete = adresseBuilder.toString().trim();
        if (adresseComplete.isEmpty()) {
            System.out.println("Aucune adresse valide trouvée pour ce site.");
            return Optional.empty();
        }

        // Obtenir les coordonnées GPS via le GeocodingService
        GeoPoint geoPoint;
        try {
            geoPoint = geocodingService.getCoordinatesFromAddress(adresseComplete);
        } catch (Exception e) {
            System.out.println("Erreur lors de la récupération des coordonnées GPS : " + e.getMessage());
            return Optional.empty();
        }

        // Supprimer l'ancienne géolocalisation si elle existe
        if (mission.getGeolocalisationGPS() != null) {
            geolocalisationGPSRepository.delete(mission.getGeolocalisationGPS());
        }

        // Créer une nouvelle entrée de géolocalisation
        GeolocalisationGPS geolocalisationGPS = new GeolocalisationGPS();
        geolocalisationGPS.setPosition(geoPoint);
        geolocalisationGPS.setGps_precision(5.0f);
        geolocalisationGPSRepository.save(geolocalisationGPS);

        // Associer la géolocalisation à la mission
        mission.setGeolocalisationGPS(geolocalisationGPS);
        missionRepository.save(mission);

        return Optional.of(mission);
    }




    // ─────────────────────────────────────────────────────────────────────────────
//  Vérifier la zone de travail (adresse du site) via GeoApiService
// ─────────────────────────────────────────────────────────────────────────────
    private boolean verifierZoneTravailAgent(AgentDeSecurite agent, Mission mission) {
        if (mission.getSite() == null) return false;
        Site site = mission.getSite();

        // Obtenir les informations de l'adresse du site
        String villeSite = site.getVille();
        String departementSite = site.getDepartement();
        String codePostalSite = site.getCodePostal();
        String regionSite = site.getRegion();
        String paysSite = site.getPays();

        // Vérifier les zones de travail de l'agent
        return agent.getZonesDeTravail().stream().anyMatch(zone -> {

            // Vérification de chaque critère
            boolean villeCorrespond = zone.getVille() != null && zone.getVille().equalsIgnoreCase(villeSite);
            boolean departementCorrespond = zone.getDepartement() != null && zone.getDepartement().equalsIgnoreCase(departementSite);
            boolean codePostalCorrespond = zone.getCodePostal() != null && zone.getCodePostal().equalsIgnoreCase(codePostalSite);
            boolean regionCorrespond = zone.getRegion() != null && zone.getRegion().equalsIgnoreCase(regionSite);
            boolean paysCorrespond = zone.getPays() != null && zone.getPays().equalsIgnoreCase(paysSite);

            // La correspondance est validée si au moins un critère est vérifié
            return villeCorrespond || departementCorrespond || codePostalCorrespond || regionCorrespond || paysCorrespond;
        });
    }


    // ─────────────────────────────────────────────────────────────────────────────
    //  Vérifier la disponibilité (dates + heures)
    // ─────────────────────────────────────────────────────────────────────────────
    private boolean verifierDisponibilite(AgentDeSecurite agent, Mission mission) {
        LocalDateTime missionStart = LocalDateTime.of(
                mission.getDateDebut(),
                mission.getHeureDebut() != null ? mission.getHeureDebut() : LocalTime.MIN
        );
        LocalDateTime missionEnd = LocalDateTime.of(
                mission.getDateFin(),
                mission.getHeureFin() != null ? mission.getHeureFin() : LocalTime.MAX
        );

        return agent.getDisponibilites().stream().anyMatch(dispo -> {
            LocalDateTime dispoStart = dispo.getDateDebut()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            LocalDateTime dispoEnd = dispo.getDateFin()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            return !missionStart.isBefore(dispoStart) && !missionEnd.isAfter(dispoEnd);
        });
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Vérifier s'il y a un conflit d’horaire entre la nouvelle mission et
    //    les missions existantes de l’agent
    // ─────────────────────────────────────────────────────────────────────────────
    private boolean hasScheduleConflict(AgentDeSecurite agent, Mission newMission) {
        LocalDateTime newStart = LocalDateTime.of(
                newMission.getDateDebut(),
                newMission.getHeureDebut() != null ? newMission.getHeureDebut() : LocalTime.MIN
        );
        LocalDateTime newEnd = LocalDateTime.of(
                newMission.getDateFin(),
                newMission.getHeureFin() != null ? newMission.getHeureFin() : LocalTime.MAX
        );

        for (Mission existingMission : agent.getMissions()) {
            LocalDateTime existingStart = LocalDateTime.of(
                    existingMission.getDateDebut(),
                    existingMission.getHeureDebut() != null ? existingMission.getHeureDebut() : LocalTime.MIN
            );
            LocalDateTime existingEnd = LocalDateTime.of(
                    existingMission.getDateFin(),
                    existingMission.getHeureFin() != null ? existingMission.getHeureFin() : LocalTime.MAX
            );

            // Conflit si les plages se chevauchent
            if (!(newEnd.isBefore(existingStart) || newStart.isAfter(existingEnd))) {
                return true;
            }
        }
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Vérifier la carte professionnelle (type mission => carte correspondante)
    // ─────────────────────────────────────────────────────────────────────────────
    private boolean verifierCarteProfessionnelle(AgentDeSecurite agent, TypeMission typeMission) {
        return agent.getCartesProfessionnelles().stream().anyMatch(carte ->
                carte.getTypeCarte().name().equals(typeMission.name()) &&
                        carte.getDateFin().after(new Date())
        );
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Vérifier le diplôme SSIAP si la mission est de type SSIAP
    // ─────────────────────────────────────────────────────────────────────────────
    private boolean verifierDiplomeSSIAP(AgentDeSecurite agent, TypeMission typeMission) {
        if (typeMission.name().startsWith("SSIAP")) {
            return agent.getDiplomesSSIAP().stream().anyMatch(diplome ->
                    diplome.getNiveau().name().equals(typeMission.name()) &&
                            diplome.getDateExpiration().after(new Date())
            );
        }
        return true;
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer les missions qui commencent après une certaine date
    // ─────────────────────────────────────────────────────────────────────────────
    public List<Mission> getMissionsStartingAfter(LocalDate date) {
        return missionRepository.findByDateDebutAfter(date);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer les missions qui se terminent avant une certaine date
    // ─────────────────────────────────────────────────────────────────────────────
    public List<Mission> getMissionsEndingBefore(LocalDate date) {
        return missionRepository.findByDateFinBefore(date);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer toutes les missions d'un agent
    // ─────────────────────────────────────────────────────────────────────────────
    public List<Mission> getMissionsByAgentId(Long agentId) {
        return missionRepository.findByAgents_Id(agentId);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    // 🔹 Récupérer toutes les missions d'un planning
    // ─────────────────────────────────────────────────────────────────────────────
    public List<Mission> getMissionsByPlanningId(Long planningId) {
        return missionRepository.findByPlanning_Id(planningId);
    }

    // ─────────────────────────────────────────────────────────────────────────────
    //  Méthode utilitaire pour notifier un agent par email et SMS
    // ─────────────────────────────────────────────────────────────────────────────
    private void envoyerNotificationAgent(AgentDeSecurite agent, String subject, String content) {
        // Email (si l’agent a un email valide)
        if (agent.getEmail() != null && !agent.getEmail().isEmpty()) {
            notificationService.sendEmail(agent.getEmail(), subject, content);
        }
        // SMS (si l’agent a un numéro de téléphone)
        if (agent.getTelephone() != null && !agent.getTelephone().isEmpty()) {
            notificationService.sendSMS(agent.getTelephone(), content);
        }
    }

    public void removeAgentFromMission(Long missionId, Long agentId) {
        Mission mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new NoSuchElementException("Mission non trouvée pour l'ID : " + missionId));

        AgentDeSecurite agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new NoSuchElementException("Agent non trouvé pour l'ID : " + agentId));

        if (mission.getAgents().contains(agent)) {
            mission.getAgents().remove(agent);
            missionRepository.save(mission);
        } else {
            throw new NoSuchElementException("L'agent n'est pas affecté à cette mission.");
        }
    }
}
