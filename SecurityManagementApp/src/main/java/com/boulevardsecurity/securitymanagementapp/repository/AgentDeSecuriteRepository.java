package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Client;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import com.boulevardsecurity.securitymanagementapp.model.ZoneDeTravail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Repository JPA
public interface AgentDeSecuriteRepository extends JpaRepository<AgentDeSecurite, Long> {

    Optional<Planning> findFirstByMissions_Agents_IdOrderByMissions_DateDebutDesc(Long agentId);
    Optional<AgentDeSecurite> findByEmail(String email);
    boolean existsByEmail(String email);
    
    List<AgentDeSecurite> findByZonesDeTravail(ZoneDeTravail zone);
    
    List<AgentDeSecurite> findByZonesDeTravail_Id(Long zoneId);
    List<AgentDeSecurite> findByEmailVerifiedTrueAndAdminApprovedFalse();

}
