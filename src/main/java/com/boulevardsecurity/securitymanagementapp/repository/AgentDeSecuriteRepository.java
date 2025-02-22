package com.boulevardsecurity.securitymanagementapp.repository;


import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.model.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AgentDeSecuriteRepository extends JpaRepository<AgentDeSecurite, Long> {

    // 🔹 Récupérer le planning unique d'un agent via ses missions (JPA génère la requête SQL)
    Optional<Planning> findFirstByMissions_Agents_IdOrderByMissions_DateDebutDesc(Long agentId);
}
