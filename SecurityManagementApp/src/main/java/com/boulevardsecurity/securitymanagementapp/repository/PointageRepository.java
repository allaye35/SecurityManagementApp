package com.boulevardsecurity.securitymanagementapp.repository;
import com.boulevardsecurity.securitymanagementapp.model.Pointage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Repository JPA
public interface PointageRepository extends JpaRepository<Pointage, Long> {

    List<Pointage> findByMissionId(Long missionId);
}

