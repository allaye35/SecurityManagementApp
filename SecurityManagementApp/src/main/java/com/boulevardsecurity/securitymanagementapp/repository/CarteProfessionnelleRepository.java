package com.boulevardsecurity.securitymanagementapp.repository;
import com.boulevardsecurity.securitymanagementapp.model.CarteProfessionnelle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// Repository JPA
public interface CarteProfessionnelleRepository extends JpaRepository<CarteProfessionnelle, Long> {

    List<CarteProfessionnelle> findByAgentDeSecuriteId(Long agentId);
}

