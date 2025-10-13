package com.boulevardsecurity.securitymanagementapp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Slf4j
@Component
@Order(2) 
public class DataInitializer implements CommandLineRunner {

    private final DataSource dataSource;

    public DataInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("=== Début de l'initialisation des données de test ===");
        
        try {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(new ClassPathResource("data.sql"));
            populator.setContinueOnError(false);
            populator.execute(dataSource);
            
            log.info("✅ Données de test insérées avec succès!");
            log.info("   - 8 agents de sécurité créés (IDs 10-17)");
            log.info("   - 5 clients créés");
            log.info("   - 7 missions créées");
            log.info("   - 8 contrats de travail créés");
            log.info("   - Login agents: sophie.martin@agent.com / Agent@123456");
            log.info("   - Login clients: contact@carrefour.fr / Client@123456");
            log.info("=== Fin de l'initialisation des données de test ===");
            
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'insertion des données de test", e);
            throw e;
        }
    }
}