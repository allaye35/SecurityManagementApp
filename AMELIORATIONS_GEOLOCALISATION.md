# Améliorations de la Géolocalisation

## 📍 Fonctionnalités Ajoutées

### 1. Géocodage Automatique depuis les Missions
- **Avant** : L'utilisateur devait saisir manuellement latitude et longitude
- **Après** : Lors de la sélection d'une mission, l'adresse du site associé est automatiquement géocodée via l'API Nominatim (OpenStreetMap)

### 2. Verrouillage des Coordonnées
- Les champs latitude/longitude sont verrouillés (lecture seule) après un géocodage réussi
- Un bouton "Modifier manuellement les coordonnées" permet de déverrouiller si nécessaire

### 3. Position Actuelle du Navigateur
- Ajout d'un bouton "Utiliser ma position actuelle" 
- Utilise l'API Geolocation du navigateur pour récupérer les coordonnées GPS de l'utilisateur

### 4. Affichage de l'Adresse
- L'adresse du site de la mission sélectionnée est affichée dans une alerte de succès
- Indicateur visuel de géocodage en cours

### 5. Gestion d'Erreurs Améliorée
- Messages d'erreur spécifiques :
  - Mission sans adresse de site
  - Adresse introuvable lors du géocodage
  - Erreur de géolocalisation du navigateur

## 🔧 Modifications Techniques

### Fichiers Modifiés

#### `CreateGeolocalisation.js`
- **Import SiteService** : Ajout de l'import pour récupérer les informations de site
- **Nouveaux états** :
  - `siteAddress` : stocke l'adresse du site
  - `geocoding` : indicateur de géocodage en cours
  - `coordsLocked` : verrouillage des coordonnées

- **Nouvelles fonctions** :
  - `handleGetCurrentPosition()` : géolocalisation via navigateur
  - `handleUnlockCoords()` : déverrouiller les coordonnées

- **useEffect amélioré** :
  - Récupération de la mission via `MissionService.getMissionById()`
  - Extraction du `site_id` (compatible avec `mission.site_mission`, `mission.siteId`, ou `mission.site.id`)
  - Récupération des détails du site via `SiteService.getSiteById()`
  - Géocodage de l'adresse du site
  - Réinitialisation de l'état quand aucune mission n'est sélectionnée
  - Gestion d'erreurs robuste avec messages spécifiques
  - Affichage de l'adresse récupérée

#### `GeolocalisationService.js`
- **Nouvelle méthode** : `addMission(geolocalisationId, missionId)` pour associer une mission à une géolocalisation

## 🎯 Workflow Utilisateur

1. **Avec Mission** :
   - L'utilisateur sélectionne une mission dans la liste déroulante
   - Le système récupère l'ID du site associé à la mission (`site_mission`)
   - Le système récupère les détails du site via l'API SiteService
   - L'adresse du site est extraite et géocodée (convertie en coordonnées GPS)
   - Les coordonnées apparaissent automatiquement dans les champs et sur la carte
   - Les champs latitude/longitude sont verrouillés
   - L'utilisateur peut modifier manuellement si nécessaire via le bouton "Modifier manuellement"

2. **Sans Mission** :
   - L'utilisateur peut cliquer sur "Utiliser ma position actuelle"
   - Ou saisir manuellement les coordonnées latitude/longitude

3. **En cas d'erreur** :
   - Messages clairs indiquant le problème (mission sans site, site sans adresse, géocodage échoué)
   - Possibilité de saisie manuelle en fallback

## 📝 Notes Importantes

- L'API Nominatim a des limites de requêtes (1 requête/seconde)
- La géolocalisation du navigateur nécessite l'autorisation de l'utilisateur
- Les coordonnées sont stockées avec 6 décimales de précision

## 🚀 Prochaines Améliorations Possibles

- Ajout d'un debounce pour éviter trop de requêtes au géocodeur
- Cache des adresses déjà géocodées
- Support de plusieurs fournisseurs de géocodage (Google Maps, Mapbox)
- Validation de la précision GPS avant création
