package com.boulevardsecurity.securitymanagementapp;

import com.boulevardsecurity.securitymanagementapp.Enums.Role;
import com.boulevardsecurity.securitymanagementapp.Enums.StatutAgent;
import com.boulevardsecurity.securitymanagementapp.model.AgentDeSecurite;
import com.boulevardsecurity.securitymanagementapp.repository.AgentDeSecuriteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.LocalDate;

@SpringBootApplication
public class SecurityManagementAppApplication {

    private static final Logger log = LoggerFactory.getLogger(SecurityManagementAppApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SecurityManagementAppApplication.class, args);
        System.out.println("Hello, World! SecurityGuardManagerApplication is running!");
    }

    @Bean
    CommandLineRunner ensureDefaultAdmin(AgentDeSecuriteRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            final String adminEmail = "admin@boulevardsecurity.com";
            if (repository.existsByEmail(adminEmail)) {
                log.info("Default admin already present: {}", adminEmail);
                return;
            }

            AgentDeSecurite admin = AgentDeSecurite.builder()
                    .nom("Admin")
                    .prenom("System")
                    .email(adminEmail)
                    .password(passwordEncoder.encode("Admin@12345"))
                    .telephone("0698751802")
                    .adresse("10 Rue de Picardie, 35000 Rennes")
                    .dateNaissance(LocalDate.of(1990, 1, 1))
                    .statut(StatutAgent.EN_SERVICE)
                    .role(Role.ADMIN)
                    .emailVerified(true)
                    .adminApproved(true)
                    .adminApprovedAt(Instant.now())
                    .build();

            repository.save(admin);
            log.info("Default admin created with email: {}", adminEmail);
            log.info("Admin details - Name: {} {}, Phone: {}, Status: {}", 
                    admin.getPrenom(), admin.getNom(), admin.getTelephone(), admin.getStatut());
        };
    }
}
