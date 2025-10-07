package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.Planning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
// Repository JPA
public interface PlanningRepository extends JpaRepository<Planning, Long> {

    List<Planning> findByDateCreationBetween(LocalDateTime dateDebut, LocalDateTime dateFin);

    List<Planning> findByMissions_Id(Long missionId);

    List<Planning> findByMissions_Agents_Id(Long agentId);

}
