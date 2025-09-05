-- ============================================
-- DONNÉES FICTIVES POUR SECURITY MANAGEMENT APP
-- ============================================

-- ============================================
-- 1. ADMINISTRATEURS
-- ============================================
INSERT INTO administrateurs (nom, prenom, email, password, telephone, adresse, date_naissance, role) VALUES
('Dupont', 'Jean', 'admin@boulevardsecurity.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0123456789', '123 Rue de l''Administration, 75001 Paris', '1980-05-15', 'ADMIN'),
('Martin', 'Sophie', 'sophie.martin@boulevardsecurity.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0123456790', '456 Avenue de la Sécurité, 75002 Paris', '1985-08-22', 'ADMIN');

-- ============================================
-- 2. ZONES DE TRAVAIL
-- ============================================
INSERT INTO zones_de_travail (nom, type_zone, code_postal, ville, departement, region, pays) VALUES
('Paris Centre', 'VILLE', '75001', 'Paris', 'Paris', 'Île-de-France', 'France'),
('Paris 16ème', 'CODE_POSTAL', '75016', 'Paris', 'Paris', 'Île-de-France', 'France'),
('Hauts-de-Seine', 'DEPARTEMENT', NULL, NULL, 'Hauts-de-Seine', 'Île-de-France', 'France'),
('Île-de-France', 'REGION', NULL, NULL, NULL, 'Île-de-France', 'France'),
('Lyon Centre', 'VILLE', '69001', 'Lyon', 'Rhône', 'Auvergne-Rhône-Alpes', 'France'),
('Marseille', 'VILLE', '13001', 'Marseille', 'Bouches-du-Rhône', 'Provence-Alpes-Côte d''Azur', 'France');

-- ============================================
-- 3. AGENTS DE SÉCURITÉ
-- ============================================
INSERT INTO agents_de_securite (nom, prenom, email, password, telephone, adresse, date_naissance, statut, role) VALUES
('Durand', 'Pierre', 'pierre.durand@agent.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0623456789', '789 Rue de la Sécurité, 75003 Paris', '1990-03-10', 'EN_SERVICE', 'AGENT_SECURITE'),
('Bernard', 'Marie', 'marie.bernard@agent.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0623456790', '321 Avenue du Travail, 75004 Paris', '1988-07-18', 'EN_SERVICE', 'AGENT_SECURITE'),
('Petit', 'Antoine', 'antoine.petit@agent.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0623456791', '654 Boulevard de la Protection, 75005 Paris', '1992-11-25', 'EN_CONGE', 'AGENT_SECURITE'),
('Robert', 'Julie', 'julie.robert@agent.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0623456792', '987 Rue de la Vigilance, 75006 Paris', '1987-09-14', 'EN_SERVICE', 'AGENT_SECURITE'),
('Richard', 'Thomas', 'thomas.richard@agent.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0623456793', '147 Place de la Surveillance, 69001 Lyon', '1991-12-03', 'EN_SERVICE', 'AGENT_SECURITE'),
('Moreau', 'Isabelle', 'isabelle.moreau@agent.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '0623456794', '258 Rue de la Garde, 13001 Marseille', '1989-06-07', 'HORS_SERVICE', 'AGENT_SECURITE');

-- ============================================
-- 4. ASSOCIATION AGENTS - ZONES
-- ============================================
INSERT INTO agents_zones (agent_id, zone_id) VALUES
(1, 1), (1, 2), -- Pierre couvre Paris Centre et 16ème
(2, 1), (2, 3), -- Marie couvre Paris Centre et Hauts-de-Seine
(3, 2), (3, 4), -- Antoine couvre Paris 16ème et Île-de-France
(4, 1), (4, 4), -- Julie couvre Paris Centre et Île-de-France
(5, 5), -- Thomas couvre Lyon Centre
(6, 6); -- Isabelle couvre Marseille

-- ============================================
-- 5. CLIENTS PARTICULIERS
-- ============================================
INSERT INTO clients (password, role, type_client, nom, prenom, email, telephone, adresse, code_postal, ville, pays, numero_rue, mode_contact_prefere) VALUES
('$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'CLIENT', 'PARTICULIER', 'Leroy', 'François', 'francois.leroy@email.com', '0601234567', '123 Rue de la Paix', '75001', 'Paris', 'France', '123', 'EMAIL'),
('$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'CLIENT', 'PARTICULIER', 'Roux', 'Catherine', 'catherine.roux@email.com', '0601234568', '456 Avenue des Champs', '75008', 'Paris', 'France', '456', 'TELEPHONE'),
('$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'CLIENT', 'PARTICULIER', 'Blanc', 'Michel', 'michel.blanc@email.com', '0601234569', '789 Boulevard Saint-Germain', '75006', 'Paris', 'France', '789', 'EMAIL');

-- ============================================
-- 6. CLIENTS ENTREPRISES
-- ============================================
INSERT INTO clients (password, role, type_client, siege, representant, numero_siret, email, telephone, adresse, code_postal, ville, pays, numero_rue, mode_contact_prefere) VALUES
('$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'CLIENT', 'ENTREPRISE', 'Paris La Défense', 'Jean-Pierre Directeur', '12345678901234', 'contact@securicorp.fr', '0142345678', '1 Esplanade de la Défense', '92400', 'Courbevoie', 'France', '1', 'EMAIL'),
('$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'CLIENT', 'ENTREPRISE', 'Lyon Part-Dieu', 'Marie Dubois', '98765432109876', 'direction@protectsarl.fr', '0472345679', '100 Cours Lafayette', '69003', 'Lyon', 'France', '100', 'TELEPHONE'),
('$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'CLIENT', 'ENTREPRISE', 'Marseille Vieux-Port', 'Pierre Martin', '11223344556677', 'admin@marseilleguard.com', '0491234567', '50 Quai du Port', '13002', 'Marseille', 'France', '50', 'EMAIL');

-- ============================================
-- 7. SITES
-- ============================================
INSERT INTO sites (nom, numero, rue, code_postal, ville, departement, region, pays) VALUES
('Centre Commercial Rivoli', '100', 'Rue de Rivoli', '75001', 'Paris', 'Paris', 'Île-de-France', 'France'),
('Immeuble de Bureaux La Défense', '15', 'Esplanade Charles de Gaulle', '92400', 'Courbevoie', 'Hauts-de-Seine', 'Île-de-France', 'France'),
('Résidence Privée Neuilly', '25', 'Avenue du Roule', '92200', 'Neuilly-sur-Seine', 'Hauts-de-Seine', 'Île-de-France', 'France'),
('Centre Commercial Part-Dieu', '17', 'Rue du Docteur Bouchut', '69003', 'Lyon', 'Rhône', 'Auvergne-Rhône-Alpes', 'France'),
('Port de Commerce Marseille', '2', 'Boulevard National', '13003', 'Marseille', 'Bouches-du-Rhône', 'Provence-Alpes-Côte d''Azur', 'France'),
('Siège Social TechCorp', '88', 'Avenue des Champs-Élysées', '75008', 'Paris', 'Paris', 'Île-de-France', 'France');

-- ============================================
-- 8. MISSIONS
-- ============================================
INSERT INTO missions (titre, description, date_debut, date_fin, heure_debut, heure_fin, type_mission, statut, site_id, nombre_agents_requis, salaire_horaire) VALUES
('Surveillance Centre Commercial', 'Surveillance générale du centre commercial Rivoli avec contrôle des accès', '2024-09-01', '2024-12-31', '08:00:00', '20:00:00', 'SURVEILLANCE', 'EN_COURS', 1, 2, 15.50),
('Sécurité Immeuble Bureaux', 'Sécurisation de l''immeuble de bureaux 24h/24', '2024-09-15', '2025-03-15', '00:00:00', '23:59:59', 'SURVEILLANCE', 'PLANIFIEE', 2, 3, 18.00),
('Protection VIP', 'Service de garde du corps pour client particulier', '2024-10-01', '2024-10-31', '09:00:00', '18:00:00', 'GARDE_DU_CORPS', 'PLANIFIEE', 3, 1, 25.00),
('SSIAP Centre Lyon', 'Service de sécurité incendie et assistance à personnes', '2024-09-10', '2025-06-10', '06:00:00', '18:00:00', 'SSIAP_1', 'EN_COURS', 4, 2, 16.80),
('Sécurité Portuaire', 'Surveillance du port de commerce de Marseille', '2024-08-01', '2024-11-30', '22:00:00', '06:00:00', 'RONDIER', 'EN_COURS', 5, 2, 17.20),
('Événementiel TechCorp', 'Sécurité pour événement d''entreprise', '2024-11-15', '2024-11-15', '18:00:00', '02:00:00', 'SECURITE_EVENEMENTIELLE', 'PLANIFIEE', 6, 4, 20.00);

-- ============================================
-- 9. AFFECTATION AGENTS AUX MISSIONS
-- ============================================
INSERT INTO missions_agents (mission_id, agent_id) VALUES
(1, 1), (1, 2), -- Mission 1: Pierre et Marie
(2, 2), (2, 4), (2, 1), -- Mission 2: Marie, Julie et Pierre
(3, 4), -- Mission 3: Julie seule (garde du corps)
(4, 5), (4, 1), -- Mission 4: Thomas et Pierre
(5, 6), (5, 5), -- Mission 5: Isabelle et Thomas
(6, 1), (6, 2), (6, 4), (6, 5); -- Mission 6: Pierre, Marie, Julie et Thomas

-- ============================================
-- 10. DEVIS
-- ============================================
INSERT INTO devis (numero_devis, titre, description, date_creation, date_validite, montant_total, statut, client_id) VALUES
('DEV-2024-001', 'Devis Surveillance Centre Commercial', 'Prestation de surveillance pour centre commercial sur 4 mois', '2024-08-15', '2024-10-15', 25600.00, 'ACCEPTE', 4),
('DEV-2024-002', 'Devis Sécurité Immeuble Bureaux', 'Sécurisation 24h/24 d''un immeuble de bureaux sur 6 mois', '2024-08-20', '2024-10-20', 77760.00, 'EN_ATTENTE', 4),
('DEV-2024-003', 'Devis Protection Particulier', 'Service de garde du corps pour particulier sur 1 mois', '2024-09-01', '2024-11-01', 5500.00, 'ACCEPTE', 1),
('DEV-2024-004', 'Devis SSIAP Lyon', 'Service SSIAP pour centre commercial Lyon sur 9 mois', '2024-08-10', '2024-10-10', 72576.00, 'ACCEPTE', 5),
('DEV-2024-005', 'Devis Sécurité Événementielle', 'Sécurité pour événement d''entreprise', '2024-10-01', '2024-12-01', 1280.00, 'EN_ATTENTE', 6);

-- ============================================
-- 11. CONTRATS
-- ============================================
INSERT INTO contrats (numero_contrat, titre, date_debut, date_fin, montant_total, statut, type_contrat, client_id, devis_id) VALUES
('CONT-2024-001', 'Contrat Surveillance Centre Commercial', '2024-09-01', '2024-12-31', 25600.00, 'ACTIF', 'PRESTATION_SERVICE', 4, 1),
('CONT-2024-002', 'Contrat Protection VIP', '2024-10-01', '2024-10-31', 5500.00, 'ACTIF', 'PRESTATION_SERVICE', 1, 3),
('CONT-2024-003', 'Contrat SSIAP Lyon', '2024-09-10', '2025-06-10', 72576.00, 'ACTIF', 'PRESTATION_SERVICE', 5, 4);

-- ============================================
-- 12. DISPONIBILITÉS AGENTS
-- ============================================
INSERT INTO disponibilites (agent_de_securite_id, date_debut, date_fin, heure_debut, heure_fin, est_disponible) VALUES
-- Pierre Durand (agent 1)
(1, '2024-09-01', '2024-09-30', '08:00:00', '18:00:00', true),
(1, '2024-10-01', '2024-10-31', '06:00:00', '14:00:00', true),
-- Marie Bernard (agent 2)
(2, '2024-09-01', '2024-09-30', '14:00:00', '22:00:00', true),
(2, '2024-10-01', '2024-10-31', '08:00:00', '16:00:00', true),
-- Antoine Petit (agent 3) - en congé
(3, '2024-09-15', '2024-09-30', '08:00:00', '18:00:00', false),
-- Julie Robert (agent 4)
(4, '2024-09-01', '2024-12-31', '09:00:00', '17:00:00', true),
-- Thomas Richard (agent 5)
(5, '2024-09-01', '2024-12-31', '06:00:00', '22:00:00', true),
-- Isabelle Moreau (agent 6) - hors service
(6, '2024-08-01', '2024-08-31', '22:00:00', '06:00:00', false);

-- ============================================
-- 13. CARTES PROFESSIONNELLES
-- ============================================
INSERT INTO cartes_professionnelles (numero_carte, date_delivrance, date_expiration, autorite_delivrance, type_carte, agent_de_securite_id) VALUES
('CQP123456789', '2024-01-15', '2029-01-15', 'CNAPS', 'CQP_APS', 1),
('CQP987654321', '2023-06-10', '2028-06-10', 'CNAPS', 'CQP_APS', 2),
('CQP456789123', '2024-03-22', '2029-03-22', 'CNAPS', 'CQP_APS', 3),
('CQP789123456', '2023-11-05', '2028-11-05', 'CNAPS', 'CQP_APS', 4),
('CQP321654987', '2024-02-18', '2029-02-18', 'CNAPS', 'CQP_APS', 5),
('CQP654987321', '2023-08-30', '2028-08-30', 'CNAPS', 'CQP_APS', 6);

-- ============================================
-- 14. DIPLOMES SSIAP
-- ============================================
INSERT INTO diplomes_ssiap (numero_diplome, date_obtention, date_expiration, centre_formation, niveau_ssiap, agent_de_securite_id) VALUES
('SSIAP1-001-2024', '2024-01-20', '2027-01-20', 'Centre de Formation Sécurité Paris', 'SSIAP_1', 1),
('SSIAP1-002-2023', '2023-09-15', '2026-09-15', 'Institut Sécurité Formation', 'SSIAP_1', 4),
('SSIAP1-003-2024', '2024-02-10', '2027-02-10', 'École Sécurité Lyon', 'SSIAP_1', 5),
('SSIAP2-001-2023', '2023-12-05', '2026-12-05', 'Centre Formation Avancée Paris', 'SSIAP_2', 2);

-- ============================================
-- 15. FACTURES
-- ============================================
INSERT INTO factures (numero_facture, date_emission, date_echeance, montant_ht, montant_ttc, taux_tva, statut, statut_paiement, contrat_id) VALUES
('FACT-2024-001', '2024-09-30', '2024-10-30', 6400.00, 7680.00, 20.0, 'EMISE', 'EN_ATTENTE', 1),
('FACT-2024-002', '2024-10-31', '2024-11-30', 5500.00, 6600.00, 20.0, 'EMISE', 'PAYEE', 2),
('FACT-2024-003', '2024-09-30', '2024-10-30', 8064.00, 9676.80, 20.0, 'EMISE', 'EN_ATTENTE', 3);

-- ============================================
-- 16. PLANNINGS
-- ============================================
INSERT INTO plannings (date_planning, heure_debut, heure_fin, mission_id, agent_de_securite_id) VALUES
-- Planning pour Mission 1 (Surveillance Centre Commercial)
('2024-09-01', '08:00:00', '16:00:00', 1, 1), -- Pierre matin
('2024-09-01', '16:00:00', '20:00:00', 1, 2), -- Marie après-midi
('2024-09-02', '08:00:00', '16:00:00', 1, 2), -- Marie matin
('2024-09-02', '16:00:00', '20:00:00', 1, 1), -- Pierre après-midi

-- Planning pour Mission 4 (SSIAP Centre Lyon)
('2024-09-10', '06:00:00', '12:00:00', 4, 5), -- Thomas matin
('2024-09-10', '12:00:00', '18:00:00', 4, 1), -- Pierre après-midi
('2024-09-11', '06:00:00', '12:00:00', 4, 1), -- Pierre matin
('2024-09-11', '12:00:00', '18:00:00', 4, 5); -- Thomas après-midi

-- ============================================
-- 17. POINTAGES
-- ============================================
INSERT INTO pointages (date_pointage, heure_arrivee, heure_depart, heures_travaillees, planning_id, agent_de_securite_id) VALUES
(CURRENT_DATE - 1, '08:00:00', '16:00:00', 8.0, 1, 1),
(CURRENT_DATE - 1, '16:00:00', '20:00:00', 4.0, 2, 2),
(CURRENT_DATE, '08:00:00', '16:00:00', 8.0, 3, 2),
(CURRENT_DATE, '06:00:00', '12:00:00', 6.0, 5, 5);

-- ============================================
-- 18. RAPPORTS D'INTERVENTION
-- ============================================
INSERT INTO rapports_intervention (titre, description, date_rapport, heure_rapport, status, mission_id, agent_de_securite_id) VALUES
('Rapport de surveillance quotidien', 'Aucun incident signalé durant la journée. Contrôles d''accès effectués conformément aux procédures.', CURRENT_DATE - 1, '16:00:00', 'FINALISE', 1, 1),
('Incident détection fumée', 'Déclenchement alarme incendie secteur B. Intervention immédiate, fausse alerte confirmée par maintenance.', CURRENT_DATE - 1, '14:30:00', 'FINALISE', 4, 5),
('Contrôle nocturne', 'Ronde de sécurité effectuée. Vérification des accès et éclairages. Tout en ordre.', CURRENT_DATE, '02:00:00', 'EN_COURS', 5, 6);

-- ============================================
-- 19. NOTIFICATIONS
-- ============================================
INSERT INTO gestionnaire_notifications (titre, message, date_creation, est_lu, type_notification, agent_de_securite_id, client_id) VALUES
('Nouvelle mission assignée', 'Vous avez été assigné à la mission de surveillance du centre commercial Rivoli.', CURRENT_DATE - 2, true, 'MISSION', 1, NULL),
('Rappel formation SSIAP', 'Votre formation SSIAP arrive à expiration dans 6 mois. Pensez à la renouveler.', CURRENT_DATE - 1, false, 'FORMATION', 1, NULL),
('Devis accepté', 'Votre devis DEV-2024-003 pour la protection VIP a été accepté.', CURRENT_DATE - 3, true, 'CONTRAT', NULL, 1),
('Facture émise', 'Une nouvelle facture FACT-2024-002 a été émise pour votre contrat.', CURRENT_DATE - 1, false, 'FACTURATION', NULL, 1),
('Mission terminée', 'La mission de sécurité événementielle s''est terminée avec succès.', CURRENT_DATE, false, 'MISSION', 2, NULL);

-- ============================================
-- 20. CONTRATS DE TRAVAIL AGENTS
-- ============================================
INSERT INTO contrats_de_travail (numero_contrat, date_debut, date_fin, salaire_base, type_contrat, statut, agent_de_securite_id) VALUES
('CDT-2024-001', '2024-01-01', '2024-12-31', 1800.00, 'CDD', 'ACTIF', 1),
('CDT-2024-002', '2024-01-15', NULL, 1750.00, 'CDI', 'ACTIF', 2),
('CDT-2024-003', '2024-02-01', '2024-11-30', 1650.00, 'CDD', 'SUSPENDU', 3),
('CDT-2024-004', '2024-01-10', NULL, 1900.00, 'CDI', 'ACTIF', 4),
('CDT-2024-005', '2024-03-01', NULL, 1850.00, 'CDI', 'ACTIF', 5),
('CDT-2024-006', '2024-01-20', '2024-08-31', 1700.00, 'CDD', 'TERMINE', 6);

-- ============================================
-- 21. FICHES DE PAIE
-- ============================================
INSERT INTO fiches_de_paie (mois, annee, salaire_brut, cotisations_sociales, salaire_net, heures_travaillees, heures_supplementaires, primes, contrat_de_travail_id) VALUES
(8, 2024, 1800.00, 450.00, 1350.00, 151.67, 0.00, 0.00, 1),
(8, 2024, 1750.00, 437.50, 1312.50, 151.67, 8.00, 120.00, 2),
(8, 2024, 1900.00, 475.00, 1425.00, 151.67, 12.00, 180.00, 4),
(8, 2024, 1850.00, 462.50, 1387.50, 151.67, 5.00, 75.00, 5);

-- ============================================
-- 22. TARIFS MISSIONS
-- ============================================
INSERT INTO tarifs_mission (type_mission, tarif_horaire_standard, tarif_horaire_nuit, tarif_horaire_weekend, tarif_horaire_ferie) VALUES
('SURVEILLANCE', 15.50, 18.00, 17.00, 20.00),
('GARDE_DU_CORPS', 25.00, 30.00, 28.00, 35.00),
('SSIAP_1', 16.80, 19.50, 18.50, 22.00),
('SSIAP_2', 18.50, 21.50, 20.50, 24.00),
('SSIAP_3', 22.00, 25.50, 24.00, 28.00),
('TELESURVEILLANCE', 14.00, 16.50, 15.50, 18.50),
('SECURITE_EVENEMENTIELLE', 20.00, 23.00, 22.00, 26.00),
('RONDIER', 17.20, 20.00, 19.00, 23.00),
('CQP_APS', 15.00, 17.50, 16.50, 19.50);