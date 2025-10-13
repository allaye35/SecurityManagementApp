-- =====================================================
-- SCRIPT SQL - Nettoyage et insertion des données de test
-- Système de Gestion de Sécurité - Boulevard Security
-- =====================================================
-- Date: 2025-10-12
-- Ce script nettoie les anciennes données de test et insère les nouvelles
-- =====================================================

-- Désactiver les contraintes de clés étrangères temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- NETTOYAGE DES DONNÉES EXISTANTES
-- =====================================================
DELETE FROM facture_missions;
DELETE FROM factures;
DELETE FROM article_contrat_travail;
DELETE FROM article_contrat;
DELETE FROM contrats_de_travail;
DELETE FROM contrats;
DELETE FROM mission_agents;
DELETE FROM missions;
DELETE FROM devis;
DELETE FROM disponibilites;
DELETE FROM diplomes_ssiap;
DELETE FROM cartes_professionnelles;
DELETE FROM agents_zones;
DELETE FROM agents_de_securite WHERE email != 'admin@boulevardsecurity.com';
DELETE FROM clients;
DELETE FROM zones_de_travail;
DELETE FROM sites;
DELETE FROM entreprises;
DELETE FROM tarifs_mission;

-- =====================================================
-- 1. ENTREPRISES
-- =====================================================
INSERT INTO entreprises (id, nom, siret_prestataire, representant_prestataire, numero_rue, rue, code_postal, ville, pays, telephone, email) VALUES
(1, 'Boulevard Security France', '85234567890123', 'Jean-Pierre Durand', '15', 'Avenue des Champs-Élysées', '75008', 'Paris', 'France', '0145678901', 'contact@boulevardsecurity.fr'),
(2, 'SecuriGuard Services', '78965432109876', 'Marie Leclerc', '42', 'Rue de la République', '69002', 'Lyon', 'France', '0478901234', 'info@securiguard.fr'),
(3, 'ProtecPro Solutions', '91234567812345', 'Ahmed Benali', '8', 'Boulevard de la Liberté', '13001', 'Marseille', 'France', '0491234567', 'contact@protecpro.fr');

-- =====================================================
-- 2. ZONES DE TRAVAIL
-- =====================================================
INSERT INTO zones_de_travail (id, nom, type_zone, code_postal, ville, departement, region, pays) VALUES
(1, 'Paris Centre', 'VILLE', '75001', 'Paris', '75', 'Île-de-France', 'France'),
(2, 'Lyon Métropole', 'VILLE', '69000', 'Lyon', '69', 'Auvergne-Rhône-Alpes', 'France'),
(3, 'Marseille Nord', 'VILLE', '13001', 'Marseille', '13', 'Provence-Alpes-Côte d''Azur', 'France'),
(4, 'Rennes Centre', 'VILLE', '35000', 'Rennes', '35', 'Bretagne', 'France'),
(5, 'Toulouse Centre', 'VILLE', '31000', 'Toulouse', '31', 'Occitanie', 'France'),
(6, 'Département 75', 'DEPARTEMENT', NULL, NULL, '75', 'Île-de-France', 'France'),
(7, 'Département 69', 'DEPARTEMENT', NULL, NULL, '69', 'Auvergne-Rhône-Alpes', 'France'),
(8, 'Région Île-de-France', 'REGION', NULL, NULL, NULL, 'Île-de-France', 'France');

-- =====================================================
-- 3. AGENTS DE SÉCURITÉ (IDs commencent à 10 pour éviter conflit avec l'admin)
-- =====================================================
-- Mot de passe pour tous: Agent@123456
INSERT INTO agents_de_securite (id, nom, prenom, email, password, telephone, adresse, date_naissance, statut, role, email_verified, admin_approved, admin_approved_at, admin_approved_by_id) VALUES
(10, 'Martin', 'Sophie', 'sophie.martin@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0612345678', '12 Rue de la Paix, 75002 Paris', '1990-05-15', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-15 10:00:00', 1),
(11, 'Dubois', 'Thomas', 'thomas.dubois@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0623456789', '45 Avenue Foch, 69006 Lyon', '1988-03-22', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-16 11:00:00', 1),
(12, 'Bernard', 'Julie', 'julie.bernard@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0634567890', '78 Cours Julien, 13006 Marseille', '1992-07-08', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-17 09:00:00', 1),
(13, 'Petit', 'Alexandre', 'alexandre.petit@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0645678901', '23 Rue Saint-Malo, 35000 Rennes', '1991-11-30', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-18 14:00:00', 1),
(14, 'Robert', 'Émilie', 'emilie.robert@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0656789012', '56 Allée Jean Jaurès, 31000 Toulouse', '1989-09-12', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-19 15:00:00', 1),
(15, 'Richard', 'Lucas', 'lucas.richard@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0667890123', '34 Boulevard Haussmann, 75009 Paris', '1993-02-28', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-20 10:30:00', 1),
(16, 'Moreau', 'Camille', 'camille.moreau@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0678901234', '67 Rue Masséna, 06000 Nice', '1994-06-17', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-21 11:00:00', 1),
(17, 'Simon', 'Pierre', 'pierre.simon@agent.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', '0689012345', '89 Cours Lafayette, 69003 Lyon', '1987-12-05', 'EN_SERVICE', 'AGENT_SECURITE', true, true, '2025-01-22 09:30:00', 1);

-- =====================================================
-- 4. ASSOCIATION AGENTS - ZONES DE TRAVAIL
-- =====================================================
INSERT INTO agents_zones (agent_id, zone_id) VALUES
-- Sophie Martin - Paris
(10, 1), (10, 6), (10, 8),
-- Thomas Dubois - Lyon
(11, 2), (11, 7),
-- Julie Bernard - Marseille
(12, 3),
-- Alexandre Petit - Rennes
(13, 4),
-- Émilie Robert - Toulouse
(14, 5),
-- Lucas Richard - Paris
(15, 1), (15, 6), (15, 8),
-- Camille Moreau - Multi-zones
(16, 1), (16, 3),
-- Pierre Simon - Lyon
(17, 2), (17, 7);

-- =====================================================
-- 5. CARTES PROFESSIONNELLES
-- =====================================================
INSERT INTO cartes_professionnelles (id, type_carte, numero_carte, date_debut, date_fin, agent_id) VALUES
(1, 'CQP_APS', 'CP-2023-001234', '2023-01-15', '2028-01-15', 10),
(2, 'SURVEILLANCE', 'CP-2023-001235', '2023-02-10', '2028-02-10', 11),
(3, 'SECURITE_EVENEMENTIELLE', 'CP-2023-001236', '2023-03-05', '2028-03-05', 12),
(4, 'RONDIER', 'CP-2023-001237', '2023-01-20', '2028-01-20', 13),
(5, 'CQP_APS', 'CP-2023-001238', '2023-02-15', '2028-02-15', 14),
(6, 'SURVEILLANCE', 'CP-2023-001239', '2023-03-10', '2028-03-10', 15),
(7, 'TELESURVEILLANCE', 'CP-2023-001240', '2023-04-12', '2028-04-12', 16),
(8, 'SECURITE_EVENEMENTIELLE', 'CP-2023-001241', '2023-01-25', '2028-01-25', 17);

-- =====================================================
-- 6. DIPLÔMES SSIAP
-- =====================================================
INSERT INTO diplomes_ssiap (id, niveau, date_obtention, date_expiration, agent_id) VALUES
(1, 'SSIAP_1', '2022-06-15', '2025-06-15', 10),
(2, 'SSIAP_2', '2021-09-20', '2024-09-20', 11),
(3, 'SSIAP_1', '2023-03-10', '2026-03-10', 12),
(4, 'SSIAP_1', '2022-11-05', '2025-11-05', 13),
(5, 'SSIAP_3', '2020-04-18', '2023-04-18', 14),
(6, 'SSIAP_2', '2022-07-22', '2025-07-22', 15),
(7, 'SSIAP_1', '2023-01-15', '2026-01-15', 16),
(8, 'SSIAP_2', '2021-10-30', '2024-10-30', 17);

-- =====================================================
-- 7. DISPONIBILITÉS (périodes longues)
-- =====================================================
INSERT INTO disponibilites (id, date_debut, date_fin, agent_id) VALUES
-- Sophie Martin - Disponible tout le mois de novembre 2025
(1, '2025-11-01 08:00:00', '2025-11-30 18:00:00', 10),
-- Thomas Dubois - Disponible novembre-décembre 2025
(2, '2025-11-01 06:00:00', '2025-12-31 20:00:00', 11),
-- Julie Bernard - Disponible octobre-novembre 2025
(3, '2025-10-15 09:00:00', '2025-11-30 19:00:00', 12),
-- Alexandre Petit - Disponible fin octobre - novembre 2025
(4, '2025-10-20 07:00:00', '2025-11-25 18:00:00', 13),
-- Émilie Robert - Disponible tout novembre 2025
(5, '2025-11-01 08:00:00', '2025-11-30 22:00:00', 14),
-- Lucas Richard - Disponible novembre-décembre 2025
(6, '2025-11-05 06:00:00', '2025-12-20 20:00:00', 15),
-- Camille Moreau - Disponible octobre-décembre 2025
(7, '2025-10-15 10:00:00', '2025-12-31 18:00:00', 16),
-- Pierre Simon - Disponible tout le quatrième trimestre 2025
(8, '2025-10-01 08:00:00', '2025-12-31 18:00:00', 17);

-- =====================================================
-- 8. CLIENTS
-- =====================================================
-- Mot de passe pour tous: Client@123456
INSERT INTO clients (id, email, password, role, type_client, nom, prenom, siege, representant, numero_siret, telephone, adresse, numero_rue, code_postal, ville, pays, mode_contact_prefere, email_verified, admin_approved, admin_approved_at, admin_approved_by_id) VALUES
(1, 'contact@carrefour.fr', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', 'CLIENT', 'ENTREPRISE', 'Carrefour Market', NULL, 'Paris La Défense', 'Pierre Duchamp', '44356789012345', '0140123456', 'Siège Carrefour', '1', '92000', 'Nanterre', 'France', 'EMAIL', true, true, '2025-01-10 10:00:00', 1),
(2, 'securite@auchan.fr', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', 'CLIENT', 'ENTREPRISE', 'Auchan France', NULL, 'Croix - Nord', 'Sophie Lefebvre', '38765432187654', '0320987654', 'Siège Auchan', '200', '59170', 'Croix', 'France', 'TELEPHONE', true, true, '2025-01-11 11:00:00', 1),
(3, 'jean.martin@gmail.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', 'CLIENT', 'PARTICULIER', 'Martin', 'Jean', NULL, NULL, NULL, '0612987654', 'Villa Martin', '45', '06400', 'Cannes', 'France', 'TELEPHONE', true, true, '2025-01-12 09:00:00', 1),
(4, 'contact@leclerc.fr', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', 'CLIENT', 'ENTREPRISE', 'E.Leclerc', NULL, 'Ivry-sur-Seine', 'Marc Leclerc', '45678912345678', '0149876543', 'Siège E.Leclerc', '26', '94200', 'Ivry-sur-Seine', 'France', 'EMAIL', true, true, '2025-01-13 14:00:00', 1),
(5, 'marie.dubois@hotmail.com', '$2a$10$vI3k7V4j0PtPqLQm8QK8y.MHPGZcZ7nH5.qJ4Q3oF8XZGvYmCHN6q', 'CLIENT', 'PARTICULIER', 'Dubois', 'Marie', NULL, NULL, NULL, '0623456789', 'Appartement Dubois', '12', '75016', 'Paris', 'France', 'EMAIL', true, true, '2025-01-14 15:00:00', 1);

-- =====================================================
-- 9. SITES
-- =====================================================
INSERT INTO sites (id, nom, numero, rue, code_postal, ville, departement, region, pays) VALUES
(1, 'Carrefour Paris Bercy', '15', 'Rue de Bercy', '75012', 'Paris', '75', 'Île-de-France', 'France'),
(2, 'Auchan Lyon Part-Dieu', '102', 'Cours Lafayette', '69003', 'Lyon', '69', 'Auvergne-Rhône-Alpes', 'France'),
(3, 'Villa Martin - Cannes', '45', 'Boulevard de la Croisette', '06400', 'Cannes', '06', 'Provence-Alpes-Côte d''Azur', 'France'),
(4, 'E.Leclerc Marseille', '78', 'Avenue du Prado', '13008', 'Marseille', '13', 'Provence-Alpes-Côte d''Azur', 'France'),
(5, 'Centre Commercial Confluence', '112', 'Cours Charlemagne', '69002', 'Lyon', '69', 'Auvergne-Rhône-Alpes', 'France'),
(6, 'Stade de France', '93200', 'Avenue du Président Wilson', '93210', 'Saint-Denis', '93', 'Île-de-France', 'France'),
(7, 'Résidence Dubois', '12', 'Avenue Victor Hugo', '75016', 'Paris', '75', 'Île-de-France', 'France');

-- =====================================================
-- 10. TARIFS MISSION
-- =====================================================
INSERT INTO tarifs_mission (id, type_mission, prix_unitaireht, tauxtva, majoration_nuit, majoration_weekend, majoration_dimanche, majoration_ferie) VALUES
(1, 'SURVEILLANCE', 15.00, 20.00, 25.00, 30.00, 50.00, 75.00),
(2, 'GARDE_DU_CORPS', 25.00, 20.00, 30.00, 35.00, 60.00, 80.00),
(3, 'RONDIER', 14.00, 20.00, 20.00, 25.00, 45.00, 70.00),
(4, 'SSIAP_1', 18.00, 20.00, 28.00, 32.00, 55.00, 78.00),
(5, 'SSIAP_2', 22.00, 20.00, 32.00, 36.00, 60.00, 85.00),
(6, 'SSIAP_3', 28.00, 20.00, 35.00, 40.00, 65.00, 90.00),
(7, 'CQP_APS', 16.00, 20.00, 24.00, 28.00, 50.00, 72.00),
(8, 'SECURITE_EVENEMENTIELLE', 20.00, 20.00, 30.00, 35.00, 58.00, 82.00),
(9, 'TELESURVEILLANCE', 13.00, 20.00, 18.00, 22.00, 40.00, 65.00);

-- =====================================================
-- 11. DEVIS
-- =====================================================
INSERT INTO devis (id, reference_devis, description, statut, entreprise_id, client_id, date_creation, date_validite, conditions_generales, montantht, montanttva, montantttc) VALUES
(1, 'DEV-2025-001', 'Surveillance magasin Carrefour Paris Bercy - 3 mois', 'VALIDE_PAR_CLIENT', 1, 1, '2025-09-01', '2025-12-01', 'Conditions générales de vente Boulevard Security. Paiement à 30 jours.', 45000.00, 9000.00, 54000.00),
(2, 'DEV-2025-002', 'Sécurité centre commercial Auchan Lyon - 6 mois', 'ACCEPTE_PAR_ENTREPRISE', 2, 2, '2025-09-05', '2025-12-05', 'Prestations 24h/24 - 7j/7. Tarif horaire négocié.', 120000.00, 24000.00, 144000.00),
(3, 'DEV-2025-003', 'Sécurité événementielle villa privée - 1 mois', 'VALIDE_PAR_CLIENT', 1, 3, '2025-10-01', '2025-11-30', 'Surveillance nocturne et week-end.', 8000.00, 1600.00, 9600.00),
(4, 'DEV-2025-004', 'Surveillance E.Leclerc Marseille - 4 mois', 'EN_ATTENTE', 3, 4, '2025-10-05', '2025-12-31', 'Devis en attente de validation.', 60000.00, 12000.00, 72000.00),
(5, 'DEV-2025-005', 'Sécurité résidence Paris 16e - 2 mois', 'ACCEPTE_PAR_ENTREPRISE', 1, 5, '2025-10-10', '2025-12-10', 'Surveillance diurne uniquement.', 15000.00, 3000.00, 18000.00);

-- =====================================================
-- 12. MISSIONS
-- =====================================================
INSERT INTO missions (id, titre, description, date_debut, date_fin, heure_debut, heure_fin, statut_mission, type_mission, site_id, tarif_mission_id, nombre_agents, quantite, montantht, montanttva, montantttc, devis_id) VALUES
(1, 'Surveillance Carrefour - Novembre', 'Surveillance quotidienne du magasin pendant les heures d''ouverture', '2025-11-01', '2025-11-30', '08:00:00', '20:00:00', 'PLANIFIEE', 'SURVEILLANCE', 1, 1, 2, 360, 15000.00, 3000.00, 18000.00, 1),
(2, 'Surveillance Auchan - Décembre', 'Surveillance 24/7 centre commercial Lyon Part-Dieu', '2025-11-01', '2025-12-31', '00:00:00', '23:59:59', 'PLANIFIEE', 'SURVEILLANCE', 2, 1, 2, 744, 40000.00, 8000.00, 48000.00, 2),
(3, 'Sécurité Villa Cannes - Novembre', 'Protection villa privée nuits et week-ends', '2025-11-01', '2025-11-30', '18:00:00', '08:00:00', 'PLANIFIEE', 'GARDE_DU_CORPS', 3, 2, 1, 168, 8000.00, 1600.00, 9600.00, 3),
(4, 'Surveillance E.Leclerc', 'Surveillance magasin et parking', '2025-11-15', '2026-01-15', '06:00:00', '22:00:00', 'EN_ATTENTE_DE_VALIDATION_DEVIS', 'SURVEILLANCE', 4, 1, 1, 480, 20000.00, 4000.00, 24000.00, 4),
(5, 'Ronde Résidence Paris', 'Rondes de sécurité quotidiennes résidence de standing', '2025-11-01', '2025-12-31', '20:00:00', '06:00:00', 'PLANIFIEE', 'RONDIER', 7, 3, 2, 372, 7500.00, 1500.00, 9000.00, 5),
(6, 'SSIAP Stade de France', 'Service SSIAP événement sportif', '2025-11-20', '2025-11-20', '14:00:00', '23:00:00', 'PLANIFIEE', 'SSIAP_2', 6, 5, 2, 18, 5000.00, 1000.00, 6000.00, NULL),
(7, 'Surveillance Confluence', 'Surveillance centre commercial', '2025-10-15', '2025-11-15', '10:00:00', '22:00:00', 'EN_COURS', 'SURVEILLANCE', 5, 1, 2, 372, 12000.00, 2400.00, 14400.00, NULL);

-- =====================================================
-- 13. ASSOCIATION MISSIONS - AGENTS
-- =====================================================
INSERT INTO mission_agents (mission_id, agent_id) VALUES
-- Mission 1: Carrefour (Sophie + Lucas)
(1, 10), (1, 15),
-- Mission 2: Auchan Lyon (Thomas + Pierre)
(2, 11), (2, 17),
-- Mission 3: Villa Cannes (Camille)
(3, 16),
-- Mission 4: Leclerc (Julie)
(4, 12),
-- Mission 5: Résidence Paris (Lucas + Sophie)
(5, 15), (5, 10),
-- Mission 6: Stade France (Émilie + Thomas)
(6, 14), (6, 11),
-- Mission 7: Confluence (Pierre + Thomas)
(7, 17), (7, 11);

-- =====================================================
-- 13. CONTRATS
-- =====================================================
INSERT INTO contrats (id, reference_contrat, date_signature, devis_id, duree_mois, tacite_reconduction, preavis_mois) VALUES
(1, 'CNT-2025-001', '2025-09-15', 1, 3, true, 1),
(2, 'CNT-2025-002', '2025-10-15', 3, 1, false, 0),
(3, 'CNT-2025-003', '2025-10-20', 5, 2, true, 1);

-- =====================================================
-- 14. ARTICLES DE CONTRAT
-- =====================================================
INSERT INTO article_contrat (id, numero, titre, contenu, contrat_id) VALUES
(1, 1, 'Objet du contrat', 'Le présent contrat a pour objet la fourniture de services de surveillance et de sécurité pour le site du client.', 1),
(2, 2, 'Durée du contrat', 'Le contrat est conclu pour une durée de 3 mois à compter de la date de signature, renouvelable par tacite reconduction.', 1),
(3, 3, 'Modalités de paiement', 'Le règlement s''effectue par virement bancaire à 30 jours fin de mois.', 1),
(4, 1, 'Objet du contrat', 'Fourniture de services de sécurité pour événement privé.', 2),
(5, 2, 'Durée', 'Prestation limitée à 1 mois non renouvelable.', 2),
(6, 1, 'Objet', 'Services de surveillance pour résidence privée.', 3),
(7, 2, 'Durée et reconduction', 'Contrat de 2 mois renouvelable tacitement avec préavis de 1 mois.', 3);

-- =====================================================
-- 15. CONTRATS DE TRAVAIL
-- =====================================================
INSERT INTO contrats_de_travail (id, reference_contrat, type_contrat, date_debut, date_fin, description, salaire_de_base, periodicite_salaire, agent_id, entreprise_id, mission_id, created_at, updated_at) VALUES
(1, 'CDT-2025-001', 'CDD', '2025-11-01', '2025-11-30', 'Contrat pour mission Carrefour Paris Bercy', 2200.00, 'MENSUEL', 10, 1, 1, '2025-09-20 10:00:00', '2025-09-20 10:00:00'),
(2, 'CDT-2025-002', 'CDD', '2025-11-01', '2025-11-30', 'Contrat pour mission Carrefour Paris Bercy', 2200.00, 'MENSUEL', 15, 1, 1, '2025-09-20 10:05:00', '2025-09-20 10:05:00'),
(3, 'CDT-2025-003', 'CDD', '2025-11-01', '2025-12-31', 'Contrat surveillance Auchan Lyon', 2400.00, 'MENSUEL', 11, 2, 2, '2025-09-21 09:00:00', '2025-09-21 09:00:00'),
(4, 'CDT-2025-004', 'CDD', '2025-11-01', '2025-12-31', 'Contrat surveillance Auchan Lyon', 2400.00, 'MENSUEL', 17, 2, 2, '2025-09-21 09:05:00', '2025-09-21 09:05:00'),
(5, 'CDT-2025-005', 'CDD', '2025-11-01', '2025-11-30', 'Contrat sécurité villa Cannes', 2800.00, 'MENSUEL', 16, 1, 3, '2025-10-05 14:00:00', '2025-10-05 14:00:00'),
(6, 'CDT-2025-006', 'CDI', '2025-11-01', NULL, 'Contrat CDI surveillance résidence Paris', 2300.00, 'MENSUEL', 10, 1, 5, '2025-10-12 11:00:00', '2025-10-12 11:00:00'),
(7, 'CDT-2025-007', 'CDD', '2025-11-20', '2025-11-20', 'Contrat événementiel Stade de France', 18.50, 'HORAIRE', 14, 1, 6, '2025-10-10 15:00:00', '2025-10-10 15:00:00'),
(8, 'CDT-2025-008', 'CDD', '2025-11-20', '2025-11-20', 'Contrat événementiel Stade de France', 18.50, 'HORAIRE', 11, 1, 6, '2025-10-10 15:05:00', '2025-10-10 15:05:00');

-- =====================================================
-- 16. ARTICLES DE CONTRAT DE TRAVAIL
-- =====================================================
INSERT INTO article_contrat_travail (id, libelle, contenu, contrat_travail_id) VALUES
(1, 'Clause de confidentialité', 'L''agent s''engage à respecter la plus stricte confidentialité sur toutes les informations dont il aura connaissance dans le cadre de sa mission.', 1),
(2, 'Clause de non-concurrence', 'L''agent s''interdit pendant la durée du contrat et 6 mois après de travailler pour un concurrent direct.', 1),
(3, 'Horaires de travail', 'L''agent effectuera 35 heures hebdomadaires du lundi au vendredi de 8h à 16h avec 1h de pause déjeuner.', 1),
(4, 'Clause de mobilité', 'L''agent accepte d''être affecté sur différents sites dans la zone Île-de-France selon les besoins du service.', 2),
(5, 'Équipements de protection', 'L''employeur fournit gratuitement l''ensemble des équipements de protection individuelle nécessaires.', 3),
(6, 'Formation continue', 'L''agent bénéficiera de formations régulières pour maintenir ses compétences et certifications à jour.', 4),
(7, 'Discrétion absolue', 'Compte tenu du caractère privé de la mission, l''agent s''engage à une discrétion absolue sur l''identité du client et les lieux surveillés.', 5),
(8, 'Permanence', 'L''agent sera en permanence joignable pendant les heures de service via le téléphone professionnel fourni.', 6);

-- =====================================================
-- 17. FACTURES
-- =====================================================
INSERT INTO factures (id, reference_facture, date_emission, statut, montantht, montanttva, montantttc, devis_id, entreprise_id, client_id) VALUES
(1, 'FACT-2025-001', '2025-10-01', 'PAYEE', 15000.00, 3000.00, 18000.00, 1, 1, 1),
(2, 'FACT-2025-002', '2025-10-15', 'EN_ATTENTE', 20000.00, 4000.00, 24000.00, 2, 2, 2),
(3, 'FACT-2025-003', '2025-11-01', 'PAYEE', 8000.00, 1600.00, 9600.00, 3, 1, 3),
(4, 'FACT-2025-004', '2025-11-05', 'EN_ATTENTE', 7500.00, 1500.00, 9000.00, 5, 1, 5);

-- =====================================================
-- 18. ASSOCIATION FACTURES - MISSIONS
-- =====================================================
INSERT INTO facture_missions (facture_id, mission_id) VALUES
(1, 1),
(2, 2),
(3, 3),
(4, 5);

-- =====================================================
-- Réactiver les contraintes de clés étrangères
-- =====================================================
SET FOREIGN_KEY_CHECKS = 1;

SELECT 'Données de test insérées avec succès!' as message;
SELECT '8 agents créés (IDs 10-17)' as agents;
SELECT '5 clients créés' as clients;
SELECT '7 missions créées' as missions;
SELECT '8 contrats de travail créés' as contrats;
