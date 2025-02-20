package com.boulevardsecurity.securitymanagementapp.repository;


import com.boulevardsecurity.securitymanagementapp.model.Entreprise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
}

