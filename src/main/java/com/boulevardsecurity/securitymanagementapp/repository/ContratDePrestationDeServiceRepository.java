package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.ContratDePrestationDeService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratDePrestationDeServiceRepository extends JpaRepository<ContratDePrestationDeService, Long> {
}
