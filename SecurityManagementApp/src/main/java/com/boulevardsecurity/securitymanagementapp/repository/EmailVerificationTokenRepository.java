package com.boulevardsecurity.securitymanagementapp.repository;

import com.boulevardsecurity.securitymanagementapp.Enums.VerificationSubject;
import com.boulevardsecurity.securitymanagementapp.model.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByTokenHash(String tokenHash);

    Optional<EmailVerificationToken> findBySubjectIdAndSubjectTypeAndCodeHashAndConsumedAtIsNull(
            Long subjectId, VerificationSubject subjectType, String codeHash
    );
}