package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Repository JPA
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByNom(String nom);

    boolean existsByEmail(String email);
    
    List<Client> findByEmailVerifiedIsTrueAndAdminApprovedIsFalse();
    List<Client> findByEmailVerifiedTrueAndAdminApprovedFalse();

}

