package com.boulevardsecurity.securitymanagementapp.service.impl;

import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.dto.*;
import com.boulevardsecurity.securitymanagementapp.mapper.*;
import com.boulevardsecurity.securitymanagementapp.model.*;
import com.boulevardsecurity.securitymanagementapp.repository.*;
import com.boulevardsecurity.securitymanagementapp.service.AgentDeSecuriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentDeSecuriteServiceImpl implements AgentDeSecuriteService {

    private final AgentDeSecuriteRepository agentRepo;
    private final ZoneDeTravailRepository zoneRepo;
    private final DisponibiliteRepository dispoRepo;
    private final CarteProfessionnelleRepository carteRepo;
    private final DiplomeSSIAPRepository diplomeRepo;

    private final AgentDeSecuriteMapper agentMapper;
    private final DisponibiliteMapper dispoMapper;
    private final CarteProfessionnelleMapper carteMapper;
    private final PlanningMapper planningMapper;
    private final DiplomeSsiapMapper diplomeSsiapMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<AgentDeSecuriteDto> getAllAgents() {
        return agentRepo.findAll().stream().map(agentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Optional<AgentDeSecuriteDto> getAgentById(Long id) {
        return agentRepo.findById(id).map(agentMapper::toDto);
    }

    @Override
    public Optional<AgentDeSecuriteDto> getAgentByEmail(String email) {
        return agentRepo.findByEmail(email).map(agentMapper::toDto);
    }

    @Override
    public AgentDeSecuriteDto createAgent(AgentDeSecuriteCreationDto dto) {
        AgentDeSecurite ent = agentMapper.toEntity(dto);
        if (ent.getRole() == null) ent.setRole(Role.AGENT_SECURITE);

        // Hash + état initial
        ent.setPassword(passwordEncoder.encode(ent.getPassword()));
        ent.setEmailVerified(false);
        ent.setPasswordChangedAt(Instant.now()); // première version de mdp

        return agentMapper.toDto(agentRepo.save(ent));
    }

    @Override
    public AgentDeSecuriteDto updateAgent(Long id, AgentDeSecuriteCreationDto dto) {
        AgentDeSecurite ent = agentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + id));

        // Le mapper copie éventuellement un password « brut »
        agentMapper.updateEntityFromCreationDto(dto, ent);

        // Si le mot de passe a été fourni → re-hash et maj passwordChangedAt
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            ent.setPassword(passwordEncoder.encode(dto.getPassword()));
            ent.setPasswordChangedAt(Instant.now());
        }

        return agentMapper.toDto(agentRepo.save(ent));
    }

    @Override
    public void deleteAgent(Long id) {
        if (!agentRepo.existsById(id)) throw new RuntimeException("Agent non trouvé : " + id);
        agentRepo.deleteById(id);
    }

    @Override
    public AgentDeSecuriteDto assignZoneDeTravail(Long agentId, Long zoneId) {
        AgentDeSecurite a = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        ZoneDeTravail z = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone non trouvée : " + zoneId));
        a.getZonesDeTravail().add(z);
        return agentMapper.toDto(agentRepo.save(a));
    }

    @Override
    public DisponibiliteDto ajouterDisponibilite(Long agentId, DisponibiliteCreationDto dispoDto) {
        AgentDeSecurite a = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        Disponibilite d = dispoMapper.toEntity(dispoDto);
        d.setAgentDeSecurite(a);
        return dispoMapper.toDto(dispoRepo.save(d));
    }

    @Override
    public CarteProfessionnelleDto ajouterCarteProfessionnelle(Long agentId, CarteProfessionnelleCreationDto carteDto) {
        AgentDeSecurite a = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        CarteProfessionnelle c = carteMapper.toEntity(carteDto);
        c.setAgentDeSecurite(a);
        return carteMapper.toDto(carteRepo.save(c));
    }

    @Override
    public AgentDeSecuriteDto changeRole(Long agentId, Role newRole) {
        AgentDeSecurite a = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        a.setRole(newRole);
        return agentMapper.toDto(agentRepo.save(a));
    }

    @Override
    public Optional<PlanningDto> getPlanningByAgentId(Long agentId) {
        return agentRepo.findFirstByMissions_Agents_IdOrderByMissions_DateDebutDesc(agentId)
                .map(planningMapper::toDto);
    }

    @Override
    public AgentDeSecuriteDto assignDisponibiliteExistante(Long agentId, Long disponibiliteId) {
        AgentDeSecurite agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        Disponibilite disponibilite = dispoRepo.findById(disponibiliteId)
                .orElseThrow(() -> new RuntimeException("Disponibilité non trouvée : " + disponibiliteId));
        disponibilite.setAgentDeSecurite(agent);
        dispoRepo.save(disponibilite);
        return agentMapper.toDto(agent);
    }

    @Override
    public DiplomeSsiapDto ajouterDiplomeSSIAP(Long agentId, DiplomeSsiapCreationDto diplomeDto) {
        AgentDeSecurite agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        DiplomeSSIAP diplome = diplomeSsiapMapper.toEntity(diplomeDto);
        diplome.setAgentDeSecurite(agent);
        return diplomeSsiapMapper.toDto(diplomeRepo.save(diplome));
    }

    @Override
    public AgentDeSecuriteDto assignCarteExistante(Long agentId, Long carteId) {
        AgentDeSecurite agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        CarteProfessionnelle carte = carteRepo.findById(carteId)
                .orElseThrow(() -> new RuntimeException("Carte non trouvée : " + carteId));
        carte.setAgentDeSecurite(agent);
        carteRepo.save(carte);
        return agentMapper.toDto(agent);
    }

    @Override
    public AgentDeSecuriteDto assignDiplomeExistante(Long agentId, Long diplomeId) {
        AgentDeSecurite agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        DiplomeSSIAP diplome = diplomeRepo.findById(diplomeId)
                .orElseThrow(() -> new RuntimeException("Diplôme non trouvé : " + diplomeId));
        diplome.setAgentDeSecurite(agent);
        diplomeRepo.save(diplome);
        return agentMapper.toDto(agent);
    }

    @Override
    public List<AgentDeSecuriteDto> getAgentsByZoneId(Long zoneId) {
        ZoneDeTravail zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new IllegalArgumentException("Zone non trouvée : " + zoneId));
        return agentRepo.findByZonesDeTravail(zone).stream().map(agentMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public AgentDeSecuriteDto removeFromZoneDeTravail(Long agentId, Long zoneId) {
        AgentDeSecurite agent = agentRepo.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent non trouvé : " + agentId));
        ZoneDeTravail zone = zoneRepo.findById(zoneId)
                .orElseThrow(() -> new RuntimeException("Zone non trouvée : " + zoneId));
        agent.getZonesDeTravail().remove(zone);
        return agentMapper.toDto(agentRepo.save(agent));
    }
}
