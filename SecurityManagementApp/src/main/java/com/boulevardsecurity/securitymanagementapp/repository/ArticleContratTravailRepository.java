package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.model.ArticleContratTravail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleContratTravailRepository extends JpaRepository<ArticleContratTravail, Long> {

    List<ArticleContratTravail> findByContratDeTravail_IdOrderById(Long contratTravailId);
}
