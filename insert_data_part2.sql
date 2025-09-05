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

-- Insertion des zones de travail
INSERT INTO zones_de_travail (nom, description, superficie, niveau_risque) VALUES 
('Zone Alpha', 'Zone d''entrée principale et accueil', 150.50, 'FAIBLE'),
('Zone Beta', 'Zone de stockage et entrepôts', 850.25, 'MOYEN'),
('Zone Gamma', 'Zone technique et serveurs', 200.75, 'ELEVE'),
('Zone Delta', 'Zone VIP et bureaux de direction', 300.00, 'MOYEN'),
('Zone Echo', 'Zone parking souterrain', 1200.00, 'FAIBLE'),
('Zone Foxtrot', 'Zone laboratoire R&D', 180.90, 'ELEVE');
