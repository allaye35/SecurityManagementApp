package com.boulevardsecurity.securitymanagementapp.security;

import com.boulevardsecurity.securitymanagementapp.model.*;
import com.boulevardsecurity.securitymanagementapp.repository.*;
import com.boulevardsecurity.securitymanagementapp.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service d'autorisation pour vérifier les permissions d'ownership
 * Utilisé avec @PreAuthorize dans les controllers
 */
@Service("authz")
@RequiredArgsConstructor
@Slf4j
public class AuthorizationService {

    private final MissionRepository missionRepository;
    private final RapportInterventionRepository rapportRepository;
    private final DevisRepository devisRepository;
    private final FactureRepository factureRepository;
    private final SiteRepository siteRepository;
    private final ContratRepository contratRepository;
    private final PlanningRepository planningRepository;
    private final PointageRepository pointageRepository;
    private final ClientRepository clientRepository;
    private final AgentDeSecuriteRepository agentRepository;

    // ==================== CLIENT OWNERSHIP CHECKS ====================

    /**
     * Vérifie si un client peut lire une mission
     * Règle: mission.contrat.devis.client.id == clientId
     */
    public boolean canClientReadMission(Authentication auth, Long missionId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return missionRepository.findById(missionId)
                .map(mission -> {
                    if (mission.getContrat() == null) return false;
                    Devis devis = mission.getContrat().getDevis();
                    return devis != null && devis.getClient() != null 
                            && devis.getClient().getId().equals(userId);
                })
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire un rapport
     * Règle: rapport.mission.contrat.devis.client.id == clientId
     */
    public boolean canClientReadRapport(Authentication auth, Long rapportId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return rapportRepository.findById(rapportId)
                .map(rapport -> {
                    Mission mission = rapport.getMission();
                    if (mission == null || mission.getContrat() == null) return false;
                    Devis devis = mission.getContrat().getDevis();
                    return devis != null && devis.getClient() != null 
                            && devis.getClient().getId().equals(userId);
                })
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire un devis
     * Règle: devis.client.id == clientId
     */
    public boolean canClientReadDevis(Authentication auth, Long devisId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return devisRepository.findById(devisId)
                .map(devis -> devis.getClient() != null && devis.getClient().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire une facture
     * Règle: facture.client.id == clientId
     */
    public boolean canClientReadFacture(Authentication auth, Long factureId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return factureRepository.findById(factureId)
                .map(facture -> facture.getClient() != null && facture.getClient().getId().equals(userId))
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire un site
     * Règle: Au moins une mission du site appartient au client
     */
    public boolean canClientReadSite(Authentication auth, Long siteId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return siteRepository.findById(siteId)
                .map(site -> site.getMissions().stream()
                        .anyMatch(mission -> {
                            if (mission.getContrat() == null) return false;
                            Devis devis = mission.getContrat().getDevis();
                            return devis != null && devis.getClient() != null 
                                    && devis.getClient().getId().equals(userId);
                        })
                )
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire un contrat
     * Règle: contrat.devis.client.id == clientId
     */
    public boolean canClientReadContrat(Authentication auth, Long contratId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return contratRepository.findById(contratId)
                .map(contrat -> {
                    Devis devis = contrat.getDevis();
                    return devis != null && devis.getClient() != null 
                            && devis.getClient().getId().equals(userId);
                })
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire un planning
     * Règle: Au moins une mission du planning appartient au client
     */
    public boolean canClientReadPlanning(Authentication auth, Long planningId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return planningRepository.findById(planningId)
                .map(planning -> planning.getMissions().stream()
                        .anyMatch(mission -> {
                            if (mission.getContrat() == null) return false;
                            Devis devis = mission.getContrat().getDevis();
                            return devis != null && devis.getClient() != null 
                                    && devis.getClient().getId().equals(userId);
                        })
                )
                .orElse(false);
    }

    /**
     * Vérifie si un client peut lire un pointage
     * Règle: pointage.mission.contrat.devis.client.id == clientId
     */
    public boolean canClientReadPointage(Authentication auth, Long pointageId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return pointageRepository.findById(pointageId)
                .map(pointage -> {
                    Mission mission = pointage.getMission();
                    if (mission == null || mission.getContrat() == null) return false;
                    Devis devis = mission.getContrat().getDevis();
                    return devis != null && devis.getClient() != null 
                            && devis.getClient().getId().equals(userId);
                })
                .orElse(false);
    }

    // ==================== AGENT OWNERSHIP CHECKS ====================

    /**
     * Vérifie si un agent peut modifier une mission
     * Règle: mission.agents contient l'agent
     */
    public boolean canAgentWriteMission(Authentication auth, Long missionId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return missionRepository.findById(missionId)
                .map(mission -> mission.getAgents().stream()
                        .anyMatch(agent -> agent.getId().equals(userId))
                )
                .orElse(false);
    }

    /**
     * Vérifie si un agent peut lire un rapport
     * Règle: rapport.mission.agents contient l'agent
     */
    public boolean canAgentReadRapport(Authentication auth, Long rapportId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return rapportRepository.findById(rapportId)
                .map(rapport -> {
                    Mission mission = rapport.getMission();
                    return mission != null && mission.getAgents().stream()
                            .anyMatch(agent -> agent.getId().equals(userId));
                })
                .orElse(false);
    }

    /**
     * Vérifie si un agent peut modifier un rapport
     * Règle: rapport.mission.agents contient l'agent
     */
    public boolean canAgentWriteRapport(Authentication auth, Long rapportId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return rapportRepository.findById(rapportId)
                .map(rapport -> {
                    Mission mission = rapport.getMission();
                    return mission != null && mission.getAgents().stream()
                            .anyMatch(agent -> agent.getId().equals(userId));
                })
                .orElse(false);
    }

    /**
     * Vérifie si un agent peut supprimer un rapport
     * Règle: rapport.mission.agents contient l'agent
     */
    public boolean canAgentDeleteRapport(Authentication auth, Long rapportId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return rapportRepository.findById(rapportId)
                .map(rapport -> {
                    Mission mission = rapport.getMission();
                    return mission != null && mission.getAgents().stream()
                            .anyMatch(agent -> agent.getId().equals(userId));
                })
                .orElse(false);
    }

    /**
     * Vérifie si un agent peut modifier un pointage
     * Règle: pointage.mission.agents contient l'agent OU pointage.agentId == agentId
     */
    public boolean canAgentWritePointage(Authentication auth, Long pointageId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return pointageRepository.findById(pointageId)
                .map(pointage -> {
                    // Vérifier si c'est le propre pointage de l'agent
                    if (pointage.getAgentId() != null && pointage.getAgentId().equals(userId)) {
                        return true;
                    }
                    // Vérifier si l'agent est assigné à la mission
                    Mission mission = pointage.getMission();
                    return mission != null && mission.getAgents().stream()
                            .anyMatch(agent -> agent.getId().equals(userId));
                })
                .orElse(false);
    }

    /**
     * Vérifie si un agent peut lire un planning
     * Règle: Au moins une mission du planning contient l'agent
     */
    public boolean canAgentReadPlanning(Authentication auth, Long planningId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return planningRepository.findById(planningId)
                .map(planning -> planning.getMissions().stream()
                        .anyMatch(mission -> mission.getAgents().stream()
                                .anyMatch(agent -> agent.getId().equals(userId))
                        )
                )
                .orElse(false);
    }

    /**
     * Vérifie si un agent peut créer un pointage pour une mission
     * Règle: L'agent doit être assigné à la mission
     */
    public boolean canAgentCreatePointageForMission(Authentication auth, Long missionId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return missionRepository.findById(missionId)
                .map(mission -> mission.getAgents().stream()
                        .anyMatch(agent -> agent.getId().equals(userId))
                )
                .orElse(false);
    }

    // ==================== SELF ACCESS CHECKS ====================

    /**
     * Vérifie si l'utilisateur accède à ses propres données
     */
    public boolean isSelf(Authentication auth, Long targetUserId) {
        Long userId = extractUserId(auth);
        return userId != null && userId.equals(targetUserId);
    }

    // ==================== HELPER METHODS ====================

    /**
     * Extrait l'ID utilisateur depuis l'authentification
     */
    private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        Object principal = auth.getPrincipal();
        
        // Si c'est déjà un AppUserDetails
        if (principal instanceof AppUserDetails) {
            return ((AppUserDetails) principal).getId();
        }
        
        // Si c'est une string (email), on cherche dans la DB
        if (principal instanceof String) {
            String email = (String) principal;
            
            // Chercher d'abord dans les clients
            Optional<Client> client = clientRepository.findByEmail(email);
            if (client.isPresent()) {
                return client.get().getId();
            }
            
            // Puis dans les agents
            Optional<AgentDeSecurite> agent = agentRepository.findByEmail(email);
            if (agent.isPresent()) {
                return agent.get().getId();
            }
        }
        
        log.warn("Could not extract user ID from authentication principal: {}", principal.getClass().getName());
        return null;
    }
}
