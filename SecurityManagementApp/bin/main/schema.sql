-- Création du schéma de base de données pour SecurityManagementApp
-- Ce script sera exécuté avant data.sql

-- Désactiver les vérifications de clés étrangères temporairement
SET FOREIGN_KEY_CHECKS = 0;

-- Suppression des tables existantes (dans l'ordre inverse des dépendances)
DROP TABLE IF EXISTS notifications;
DROP TABLE IF EXISTS lignes_cotisation;
DROP TABLE IF EXISTS fiches_de_paie;
DROP TABLE IF EXISTS rapports;
DROP TABLE IF EXISTS pointages;
DROP TABLE IF EXISTS plannings;
DROP TABLE IF EXISTS factures;
DROP TABLE IF EXISTS lignes_facture;
DROP TABLE IF EXISTS contrats_de_travail;
DROP TABLE IF EXISTS articles_contrat_travail;
DROP TABLE IF EXISTS devis;
DROP TABLE IF EXISTS lignes_devis;
DROP TABLE IF EXISTS missions;
DROP TABLE IF EXISTS mission_zones;
DROP TABLE IF EXISTS geolocalisations;
DROP TABLE IF EXISTS cartes_pro;
DROP TABLE IF EXISTS diplomes;
DROP TABLE IF EXISTS disponibilites;
DROP TABLE IF EXISTS agents_de_securite;
DROP TABLE IF EXISTS zones_de_travail;
DROP TABLE IF EXISTS sites;
DROP TABLE IF EXISTS tarifs;
DROP TABLE IF EXISTS articles;
DROP TABLE IF EXISTS contrats;
DROP TABLE IF EXISTS clients;
DROP TABLE IF EXISTS entreprises;
DROP TABLE IF EXISTS administrateurs;

-- Réactiver les vérifications de clés étrangères
SET FOREIGN_KEY_CHECKS = 1;
