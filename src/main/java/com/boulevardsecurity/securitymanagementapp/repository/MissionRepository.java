package com.boulevardsecurity.securitymanagementapp.repository;



import com.boulevardsecurity.securitymanagementapp.model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Long> {
}
