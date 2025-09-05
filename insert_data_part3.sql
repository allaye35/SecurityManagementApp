-- Insertion des clients (particuliers et entreprises)
INSERT INTO clients (nom, prenom, email, telephone, adresse, password, type_client, role) VALUES 
('Martin', 'Claire', 'claire.martin@email.com', '0612345678', '12 Rue de la République, 69001 Lyon', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'PARTICULIER', 'CLIENT'),
('Dubois', 'Paul', 'paul.dubois@email.com', '0623456789', '34 Avenue Victor Hugo, 13001 Marseille', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'PARTICULIER', 'CLIENT'),
('Bernard', 'Sophie', 'sophie.bernard@email.com', '0634567890', '56 Boulevard Voltaire, 75011 Paris', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'PARTICULIER', 'CLIENT'),
('TechCorp', NULL, 'contact@techcorp.fr', '0145678901', '123 Route de la Technologie, 92100 Boulogne', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'ENTREPRISE', 'CLIENT'),
('MegaStore', NULL, 'security@megastore.com', '0156789012', '456 Avenue du Commerce, 75015 Paris', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'ENTREPRISE', 'CLIENT'),
('InnovLab', NULL, 'admin@innovlab.fr', '0167890123', '789 Rue de l''Innovation, 69003 Lyon', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', 'ENTREPRISE', 'CLIENT');

-- Insertion des entreprises (table séparée pour les prestataires)
INSERT INTO entreprises (nom, email, telephone, rue, ville, code_postal, pays, representant_prestataire, siret_prestataire) VALUES 
('Sécuritech Pro', 'contact@securitech.com', '0145789654', '15 Avenue des Champs', 'Paris', '75008', 'France', 'Jean Dupont', '12345678901234'),
('Protection Plus', 'info@protectionplus.fr', '0147852963', '22 Rue de la Défense', 'Courbevoie', '92400', 'France', 'Marie Leroy', '23456789012345'),
('Secure Services', 'admin@secureservices.com', '0142857396', '8 Boulevard Saint-Germain', 'Paris', '75005', 'France', 'Pierre Martin', '34567890123456');

-- Insertion des zones de travail (géographiques)
INSERT INTO zones_de_travail (nom, ville, departement, region, code_postal, pays, type_zone) VALUES 
('Zone Paris Centre', 'Paris', '75', 'Île-de-France', '75001', 'France', 'VILLE'),
('Zone Lyon Métropole', 'Lyon', '69', 'Auvergne-Rhône-Alpes', '69000', 'France', 'VILLE'),
('Zone Marseille Sud', 'Marseille', '13', 'Provence-Alpes-Côte d''Azur', '13000', 'France', 'VILLE'),
('Zone Courbevoie', 'Courbevoie', '92', 'Île-de-France', '92400', 'France', 'VILLE'),
('Zone Département Nord', NULL, '59', 'Hauts-de-France', NULL, 'France', 'DEPARTEMENT'),
('Zone Région PACA', NULL, NULL, 'Provence-Alpes-Côte d''Azur', NULL, 'France', 'REGION');

-- Insertion des sites
INSERT INTO sites (nom, rue, ville, code_postal, departement, region, pays, numero) VALUES 
('Site Central Paris', 'Place Vendôme', 'Paris', '75001', '75', 'Île-de-France', 'France', '15'),
('Entrepôt Logistique Nord', 'Rue de l''Industrie', 'Lille', '59000', '59', 'Hauts-de-France', 'France', '128'),
('Centre Technique Lyon', 'Avenue des Sciences', 'Lyon', '69007', '69', 'Auvergne-Rhône-Alpes', 'France', '45'),
('Siège Social Marseille', 'Boulevard Michelet', 'Marseille', '13008', '13', 'Provence-Alpes-Côte d''Azur', 'France', '67'),
('Parking Central', 'Rue du Parking', 'Paris', '75002', '75', 'Île-de-France', 'France', '12'),
('Laboratoire Innovation', 'Rue Newton', 'Lyon', '69003', '69', 'Auvergne-Rhône-Alpes', 'France', '89');

-- Insertion des agents de sécurité
INSERT INTO agents_de_securite (nom, prenom, email, telephone, adresse, password, date_naissance, role, statut) VALUES 
('Durand', 'Pierre', 'pierre.durand@boulevardsecurity.com', '0612345679', '23 Rue de la Sécurité, 75003 Paris', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '1990-03-15', 'AGENT_SECURITE', 'EN_SERVICE'),
('Moreau', 'Anne', 'anne.moreau@boulevardsecurity.com', '0623456780', '45 Avenue de la Protection, 69002 Lyon', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '1987-09-22', 'AGENT_SECURITE', 'EN_SERVICE'),
('Leroy', 'Marc', 'marc.leroy@boulevardsecurity.com', '0634567891', '67 Boulevard de la Garde, 13003 Marseille', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '1992-12-08', 'AGENT_SECURITE', 'EN_SERVICE'),
('Petit', 'Julie', 'julie.petit@boulevardsecurity.com', '0645678902', '89 Rue de la Surveillance, 92200 Neuilly', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '1988-06-18', 'AGENT_SECURITE', 'EN_CONGE'),
('Roux', 'Thomas', 'thomas.roux@boulevardsecurity.com', '0656789013', '12 Place de la Vigilance, 59000 Lille', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '1985-11-12', 'AGENT_SECURITE', 'EN_SERVICE'),
('Garcia', 'Laura', 'laura.garcia@boulevardsecurity.com', '0667890124', '34 Chemin de la Ronde, 33000 Bordeaux', '$2a$10$N.zmdr9k7uOCQb376NoUnuTsJJZbMZY8VoktAZFMM7L0.7nOInqJu', '1991-04-25', 'AGENT_SECURITE', 'HORS_SERVICE');
