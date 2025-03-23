package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {

    // 🔹 Récupérer les missions qui commencent après une certaine date
    List<Mission> findByDateDebutAfter(LocalDate date);

    // 🔹 Récupérer les missions qui se terminent avant une certaine date
    List<Mission> findByDateFinBefore(LocalDate date);

    // 🔹 Récupérer toutes les missions d'un agent spécifique par son ID
    List<Mission> findByAgents_Id(Long agentId);

    // 🔹 Récupérer toutes les missions d'un Planning spécifique par son ID
    List<Mission> findByPlanning_Id(Long planningId);
}
