package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.RapportIntervention;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Repository JPA
public interface RapportInterventionRepository extends JpaRepository<RapportIntervention, Long> {
    List<RapportIntervention> findByMissionId(Long missionId);}
