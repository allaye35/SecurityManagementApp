package com.boulevardsecurity.securitymanagementapp.service;

import com.boulevardsecurity.securitymanagementapp.dto.MissionCreateDto;
import com.boulevardsecurity.securitymanagementapp.dto.MissionDto;

import java.time.LocalDate;
import java.util.List;

public interface IMissionService {

    List<MissionDto> listerToutes();
    MissionDto obtenirParId(Long id);

    List<MissionDto> missionsSansDevis();

    MissionDto creerMission(MissionCreateDto dto, String adresseSite);
    MissionDto creerMission(MissionCreateDto dto);
    MissionDto simulerCalcul(MissionCreateDto dto);

    MissionDto majMission(Long id, MissionCreateDto dto, String nouvelleAdresse);
    MissionDto majMission(Long id, MissionCreateDto dto);

    void supprimerMission(Long id);

    MissionDto affecterAgents(Long idMission, List<Long> idsAgents);
    MissionDto retirerAgent(Long idMission, Long idAgent);

    MissionDto associerRapport(Long idMission, Long idRapport);
    MissionDto associerPlanning(Long idMission, Long idPlanning);
    MissionDto associerSite(Long idMission, Long idSite);
    MissionDto associerGeoloc(Long idMission);
    MissionDto dissocierGeoloc(Long idMission);

    List<MissionDto> missionsCommencantApres(LocalDate date);
    List<MissionDto> missionsFinissantAvant(LocalDate date);
    List<MissionDto> missionsParAgent(Long idAgent);
    List<MissionDto> missionsParPlanning(Long idPlanning);
    List<MissionDto> missionsParContrat(Long contratId);

    MissionDto associerContratDeTravail(Long idMission, Long idContrat);
    MissionDto associerFacture(Long idMission, Long idFacture);
}
