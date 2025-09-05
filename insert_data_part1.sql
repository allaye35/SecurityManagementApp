-- Insertion des entreprises
INSERT INTO entreprises (nom, adresse, email, telephone, statut, forme_juridique, numero_siret) VALUES 
('Sécuritech Pro', '15 Avenue des Champs, 75008 Paris', 'contact@securitech.com', '0145789654', 'ACTIF', 'SARL', '12345678901234'),
('Protection Plus', '22 Rue de la Défense, 92400 Courbevoie', 'info@protectionplus.fr', '0147852963', 'ACTIF', 'SAS', '23456789012345'),
('Secure Services', '8 Boulevard Saint-Germain, 75005 Paris', 'admin@secureservices.com', '0142857396', 'ACTIF', 'EURL', '34567890123456');

-- Insertion des clients
INSERT INTO clients (nom, prenom, email, telephone, adresse, date_naissance, type_client, entreprise_id) VALUES 
('Martin', 'Claire', 'claire.martin@email.com', '0612345678', '12 Rue de la République, 69001 Lyon', '1985-04-12', 'PARTICULIER', NULL),
('Dubois', 'Paul', 'paul.dubois@email.com', '0623456789', '34 Avenue Victor Hugo, 13001 Marseille', '1978-11-25', 'PARTICULIER', NULL),
('Bernard', 'Sophie', 'sophie.bernard@email.com', '0634567890', '56 Boulevard Voltaire, 75011 Paris', '1990-07-08', 'PARTICULIER', NULL),
('TechCorp', NULL, 'contact@techcorp.fr', '0145678901', '123 Route de la Technologie, 92100 Boulogne', NULL, 'ENTREPRISE', 1),
('MegaStore', NULL, 'security@megastore.com', '0156789012', '456 Avenue du Commerce, 75015 Paris', NULL, 'ENTREPRISE', 2),
('InnovLab', NULL, 'admin@innovlab.fr', '0167890123', '789 Rue de l''Innovation, 69003 Lyon', NULL, 'ENTREPRISE', 3);

-- Insertion des zones de travail
INSERT INTO zones_de_travail (nom, description, superficie, niveau_risque) VALUES 
('Zone Alpha', 'Zone d''entrée principale et accueil', 150.50, 'FAIBLE'),
('Zone Beta', 'Zone de stockage et entrepôts', 850.25, 'MOYEN'),
('Zone Gamma', 'Zone technique et serveurs', 200.75, 'ELEVE'),
('Zone Delta', 'Zone VIP et bureaux de direction', 300.00, 'MOYEN'),
('Zone Echo', 'Zone parking souterrain', 1200.00, 'FAIBLE'),
('Zone Foxtrot', 'Zone laboratoire R&D', 180.90, 'ELEVE');

-- Insertion des sites
INSERT INTO sites (nom, adresse, type_site, zone_id) VALUES 
('Site Central Paris', '15 Place Vendôme, 75001 Paris', 'BUREAU', 1),
('Entrepôt Logistique Nord', '128 Rue de l''Industrie, 59000 Lille', 'ENTREPOT', 2),
('Centre Technique Lyon', '45 Avenue des Sciences, 69007 Lyon', 'CENTRE_TECHNIQUE', 3),
('Siège Social Marseille', '67 Boulevard Michelet, 13008 Marseille', 'BUREAU', 4),
('Parking Central', '12 Rue du Parking, 75002 Paris', 'PARKING', 5),
('Laboratoire Innovation', '89 Rue Newton, 69003 Lyon', 'LABORATOIRE', 6);
