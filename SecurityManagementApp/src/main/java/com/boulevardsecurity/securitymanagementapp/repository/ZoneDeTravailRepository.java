package com.boulevardsecurity.securitymanagementapp.repository;
import com.boulevardsecurity.securitymanagementapp.model.ZoneDeTravail;
import com.boulevardsecurity.securitymanagementapp.Enums.TypeZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
// Repository JPA
public interface ZoneDeTravailRepository extends JpaRepository<ZoneDeTravail, Long> {

    List<ZoneDeTravail> findByNomContainingIgnoreCase(String nom);

    List<ZoneDeTravail> findByTypeZone(TypeZone typeZone);

    boolean existsByNomAndTypeZone(String nom, TypeZone typeZone);
    Optional<ZoneDeTravail> findById(Long id);
}
