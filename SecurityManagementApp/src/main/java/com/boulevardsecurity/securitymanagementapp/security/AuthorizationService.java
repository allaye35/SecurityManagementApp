package com.boulevardsecurity.securitymanagementapp.security;

import com.boulevardsecurity.securitymanagementapp.model.*;
import com.boulevardsecurity.securitymanagementapp.repository.*;
import com.boulevardsecurity.securitymanagementapp.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

public boolean canClientReadDevis(Authentication auth, Long devisId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return devisRepository.findById(devisId)
                .map(devis -> devis.getClient() != null && devis.getClient().getId().equals(userId))
                .orElse(false);
    }

public boolean canClientReadFacture(Authentication auth, Long factureId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return factureRepository.findById(factureId)
                .map(facture -> facture.getClient() != null && facture.getClient().getId().equals(userId))
                .orElse(false);
    }

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

public boolean canAgentWriteMission(Authentication auth, Long missionId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return missionRepository.findById(missionId)
                .map(mission -> mission.getAgents().stream()
                        .anyMatch(agent -> agent.getId().equals(userId))
                )
                .orElse(false);
    }

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

public boolean canAgentWritePointage(Authentication auth, Long pointageId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return pointageRepository.findById(pointageId)
                .map(pointage -> {
                    
                    if (pointage.getAgentId() != null && pointage.getAgentId().equals(userId)) {
                        return true;
                    }
                    
                    Mission mission = pointage.getMission();
                    return mission != null && mission.getAgents().stream()
                            .anyMatch(agent -> agent.getId().equals(userId));
                })
                .orElse(false);
    }

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

public boolean canAgentCreatePointageForMission(Authentication auth, Long missionId) {
        Long userId = extractUserId(auth);
        if (userId == null) return false;

        return missionRepository.findById(missionId)
                .map(mission -> mission.getAgents().stream()
                        .anyMatch(agent -> agent.getId().equals(userId))
                )
                .orElse(false);
    }

public boolean isSelf(Authentication auth, Long targetUserId) {
        Long userId = extractUserId(auth);
        return userId != null && userId.equals(targetUserId);
    }

private Long extractUserId(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return null;
        }

        Object principal = auth.getPrincipal();

if (principal instanceof AppUserDetails) {
            return ((AppUserDetails) principal).getId();
        }

if (principal instanceof String) {
            String email = (String) principal;

Optional<Client> client = clientRepository.findByEmail(email);
            if (client.isPresent()) {
                return client.get().getId();
            }

Optional<AgentDeSecurite> agent = agentRepository.findByEmail(email);
            if (agent.isPresent()) {
                return agent.get().getId();
            }
        }
        
        log.warn("Could not extract user ID from authentication principal: {}", principal.getClass().getName());
        return null;
    }
}