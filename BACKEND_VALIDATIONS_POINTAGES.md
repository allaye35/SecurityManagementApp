# Spécifications Backend - Validation des Pointages

## Endpoints à implémenter

### 1. POST `/api/pointages/prise-service`
**Fonction** : Enregistrer la prise de service d'un agent

**Payload reçu** :
```json
{
  "missionId": 41,
  "agentId": 25,
  "datePointage": "2025-10-06T17:30:00.000Z",
  "latitude": 43.660422,
  "longitude": 7.205711,
  "estPresent": true,
  "estRetard": false
}
```

**Validations à effectuer** :

#### Validation 1 : Mission existe
```java
Mission mission = missionRepository.findById(missionId)
    .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, 
        "❌ Mission non trouvée avec l'ID: " + missionId
    ));
```

#### Validation 2 : Agent existe et est assigné à la mission
```java
AgentDeSecurite agent = agentRepository.findById(agentId)
    .orElseThrow(() -> new ResponseStatusException(
        HttpStatus.NOT_FOUND, 
        "❌ Agent non trouvé avec l'ID: " + agentId
    ));

if (!mission.getAgents().contains(agent)) {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "❌ L'agent n'est pas assigné à cette mission"
    );
}
```

#### Validation 3 : Prise de service avant le début de la mission (PERMIS)
```java
LocalDate today = LocalDate.now();
LocalDate dateDebut = mission.getDateDebut();

if (dateDebut != null && today.isBefore(dateDebut)) {
    // ⚠️ AVERTISSEMENT mais autoriser quand même
    // Logger ou envoyer une notification
    log.warn("⚠️ Prise de service anticipée: La mission {} commence le {}, " +
             "mais l'agent {} pointe aujourd'hui ({})", 
             mission.getId(), dateDebut, agent.getId(), today);
    
    // Optionnel : marquer comme "anticipé" dans le pointage
    pointage.setCommentaire("Prise de service anticipée");
}
```

#### Validation 4 : Site de la mission (optionnel mais recommandé)
```java
Site site = mission.getSite();

if (site == null) {
    // ⚠️ Mission sans site - Autoriser mais logger
    log.warn("⚠️ Mission {} n'a pas de site assigné. " +
             "Impossible de valider la géolocalisation", mission.getId());
    pointage.setCommentaire("Mission sans site - Géolocalisation non vérifiée");
} else {
    // Validation 5 : Géolocalisation
    validerGeolocalisation(site, latitude, longitude);
}
```

#### Validation 5 : Géolocalisation proche du site
```java
private void validerGeolocalisation(Site site, double latAgent, double lonAgent) {
    GeolocalisationGPS geoSite = site.getGeolocalisation();
    
    if (geoSite == null) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            "⚠️ Le site n'a pas de géolocalisation configurée. " +
            "Veuillez configurer la position GPS du site d'abord."
        );
    }
    
    double latSite = geoSite.getLatitude();
    double lonSite = geoSite.getLongitude();
    
    // Calculer la distance en mètres (formule de Haversine)
    double distance = calculerDistance(latAgent, lonAgent, latSite, lonSite);
    
    // Rayon acceptable : 100 mètres par défaut
    double rayonAcceptable = 100.0; // mètres
    
    if (distance > rayonAcceptable) {
        throw new ResponseStatusException(
            HttpStatus.BAD_REQUEST,
            String.format(
                "❌ Vous êtes trop loin du site ! " +
                "Distance actuelle: %.0f mètres. " +
                "Distance maximale autorisée: %.0f mètres. " +
                "Veuillez vous rapprocher du site avant de pointer.",
                distance, rayonAcceptable
            )
        );
    }
    
    // ✅ Position valide
    log.info("✅ Agent à {} mètres du site (dans le rayon autorisé de {} m)", 
             Math.round(distance), rayonAcceptable);
}

// Formule de Haversine pour calculer la distance entre 2 points GPS
private double calculerDistance(double lat1, double lon1, double lat2, double lon2) {
    final int R = 6371; // Rayon de la Terre en km
    
    double latDistance = Math.toRadians(lat2 - lat1);
    double lonDistance = Math.toRadians(lon2 - lon1);
    
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    
    double distance = R * c * 1000; // Convertir en mètres
    return distance;
}
```

#### Validation 6 : Agent déjà en service ?
```java
// Vérifier si l'agent n'est pas déjà en service (a déjà pointé sans pointer la fin)
List<Pointage> pointagesActifs = pointageRepository
    .findByMissionAndAgentAndEnService(mission, agent);

if (!pointagesActifs.isEmpty()) {
    throw new ResponseStatusException(
        HttpStatus.CONFLICT,
        "❌ Cet agent est déjà en service ! " +
        "Veuillez d'abord enregistrer la fin de service avant de reprendre."
    );
}
```

#### Validation 7 : Retard (optionnel)
```java
LocalTime heureDebut = mission.getHeureDebut();
LocalTime heureActuelle = LocalTime.now();

boolean estEnRetard = false;
if (heureDebut != null && heureActuelle.isAfter(heureDebut.plusMinutes(15))) {
    estEnRetard = true;
    log.warn("⏰ Agent {} en retard pour la mission {}. " +
             "Heure prévue: {}, Heure réelle: {}", 
             agent.getId(), mission.getId(), heureDebut, heureActuelle);
}

pointage.setEstRetard(estEnRetard);
```

---

### 2. POST `/api/pointages/fin-service`
**Fonction** : Enregistrer la fin de service d'un agent

**Validations supplémentaires** :

#### Validation : Agent doit être en service
```java
List<Pointage> pointagesActifs = pointageRepository
    .findByMissionAndAgentAndEnService(mission, agent);

if (pointagesActifs.isEmpty()) {
    throw new ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "❌ Impossible d'enregistrer la fin de service. " +
        "Cet agent n'a pas de prise de service active pour cette mission."
    );
}

// Marquer le pointage de début comme "terminé"
Pointage pointageDebut = pointagesActifs.get(0);
pointageDebut.setFinService(new Date());
```

---

### 3. GET `/api/pointages/mission/{missionId}/agents-en-service`
**Fonction** : Récupérer la liste des agents actuellement en service

**Logique** :
```java
@GetMapping("/mission/{missionId}/agents-en-service")
public List<AgentDeSecuriteDto> getAgentsEnService(@PathVariable Long missionId) {
    Mission mission = missionRepository.findById(missionId)
        .orElseThrow(() -> new ResponseStatusException(
            HttpStatus.NOT_FOUND, 
            "Mission non trouvée"
        ));
    
    // Récupérer les pointages actifs (prise de service sans fin de service)
    List<Pointage> pointagesActifs = pointageRepository
        .findByMissionAndFinServiceIsNull(mission);
    
    // Extraire les agents
    return pointagesActifs.stream()
        .map(p -> p.getAgent())
        .distinct()
        .map(agentMapper::toDto)
        .collect(Collectors.toList());
}
```

---

## Repository à ajouter

```java
public interface PointageRepository extends JpaRepository<Pointage, Long> {
    
    // Trouver les pointages actifs (en service)
    @Query("SELECT p FROM Pointage p WHERE p.mission = :mission " +
           "AND p.agent = :agent AND p.finService IS NULL")
    List<Pointage> findByMissionAndAgentAndEnService(
        @Param("mission") Mission mission, 
        @Param("agent") AgentDeSecurite agent
    );
    
    // Trouver tous les pointages actifs d'une mission
    @Query("SELECT p FROM Pointage p WHERE p.mission = :mission " +
           "AND p.finService IS NULL")
    List<Pointage> findByMissionAndFinServiceIsNull(
        @Param("mission") Mission mission
    );
    
    // Pointages par mission
    List<Pointage> findByMission(Mission mission);
    
    // Pointages par agent
    List<Pointage> findByAgent(AgentDeSecurite agent);
}
```

---

## Modèle Pointage à modifier

```java
@Entity
@Table(name = "pointages")
public class Pointage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date datePointage;  // Date/heure de prise de service
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date finService;    // 👈 NOUVEAU : Date/heure de fin de service
    
    private boolean estPresent;
    private boolean estRetard;
    
    @Embedded
    private GeoPoint positionActuelle;
    
    @ManyToOne
    @JoinColumn(name = "mission_id")
    private Mission mission;
    
    @ManyToOne  // 👈 AJOUTER si pas déjà là
    @JoinColumn(name = "agent_id")
    private AgentDeSecurite agent;
    
    private String commentaire;  // 👈 NOUVEAU : Pour les messages d'avertissement
    
    // Getters, setters, constructeurs...
}
```

---

## Messages d'erreur explicites

| Code | Message |
|------|---------|
| 404 | ❌ Mission non trouvée avec l'ID: {id} |
| 404 | ❌ Agent non trouvé avec l'ID: {id} |
| 400 | ❌ L'agent n'est pas assigné à cette mission |
| 400 | ❌ Vous êtes trop loin du site ! Distance: {distance}m. Maximum autorisé: 100m |
| 400 | ⚠️ Le site n'a pas de géolocalisation configurée |
| 409 | ❌ Cet agent est déjà en service ! Veuillez d'abord pointer la fin de service |
| 400 | ❌ Impossible de finir le service. Aucune prise de service active trouvée |

---

## Cas d'usage

### Cas 1 : Prise de service normale ✅
- Mission planifiée
- Agent assigné à la mission
- Site avec géolocalisation
- Agent sur le site (< 100m)
- → **Succès**

### Cas 2 : Prise de service anticipée ⚠️
- Mission commence demain
- Agent pointe aujourd'hui
- → **Autoriser avec avertissement**

### Cas 3 : Mission sans site ⚠️
- Pas de validation géographique possible
- → **Autoriser avec commentaire**

### Cas 4 : Agent trop loin du site ❌
- Distance > 100m
- → **Refuser avec message explicite**

### Cas 5 : Fin de service sans prise ❌
- Aucun pointage actif trouvé
- → **Refuser**
