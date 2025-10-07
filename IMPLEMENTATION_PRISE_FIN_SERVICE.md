# Implémentation des endpoints Prise/Fin de Service

## Date: 6 octobre 2025
## Branche: feature/amelioration-pointages

## Changements effectués

### 1. Modèle Pointage (Entity)
**Fichier**: `SecurityManagementApp/src/main/java/com/boulevardsecurity/securitymanagementapp/model/Pointage.java`

**Nouveaux champs ajoutés**:
- `finService` (Date) - Date de fin de service (null = agent toujours en service)
- `agentId` (Long) - ID de l'agent (pas de relation directe, on récupère via mission)

```java
@Temporal(TemporalType.TIMESTAMP)
private Date finService;

// ID de l'agent (pas de relation directe, on récupère via mission)
private Long agentId;
```

**Justification**: 
- Pas de relation `@ManyToOne` vers AgentDeSecurite car on peut récupérer les agents via la mission
- `finService = null` indique qu'un agent est actuellement en service

---

### 2. Service Interface
**Fichier**: `SecurityManagementApp/src/main/java/com/boulevardsecurity/securitymanagementapp/service/PointageService.java`

**Nouvelles méthodes**:
```java
PointageDto enregistrerPriseDeService(PointageCreateDto dto);
PointageDto enregistrerFinDeService(PointageCreateDto dto);
List<AgentDeSecuriteDto> getAgentsEnService(Long idMission);
```

---

### 3. Service Implementation
**Fichier**: `SecurityManagementApp/src/main/java/com/boulevardsecurity/securitymanagementapp/service/impl/PointageServiceImpl.java`

#### a) `enregistrerPriseDeService()`
**Validations**:
1. Vérifier que la mission existe
2. Vérifier que l'agent est assigné à la mission
3. Vérifier que l'agent n'est pas déjà en service pour cette mission
4. Validation GPS: position à max 100m de la géolocalisation de la mission

**Logique**:
```java
// Créer le pointage de prise de service
Pointage pointage = mapper.toEntity(dto);
pointage.setDatePointage(new Date());
pointage.setMission(mission);
pointage.setAgentId(dto.getAgentId());
pointage.setFinService(null); // Explicitement null pour indiquer "en service"
```

#### b) `enregistrerFinDeService()`
**Validations**:
1. Vérifier que la mission existe
2. Vérifier que l'agent est assigné à la mission
3. Trouver le pointage actif (finService = null) de l'agent pour cette mission

**Logique**:
```java
// Mettre à jour le pointage existant avec la fin de service
Pointage pointage = priseDeService.get();
pointage.setFinService(new Date());
```

#### c) `getAgentsEnService()`
**Logique**:
```java
// Récupérer les IDs des agents en service
List<Long> agentIdsEnService = pointageRepository.findByMissionId(idMission).stream()
    .filter(p -> p.getFinService() == null && p.getAgentId() != null)
    .map(Pointage::getAgentId)
    .distinct()
    .collect(Collectors.toList());

// Récupérer les agents de la mission qui sont en service
return mission.getAgents().stream()
    .filter(agent -> agentIdsEnService.contains(agent.getId()))
    .map(agentMapper::toDto)
    .collect(Collectors.toList());
```

---

### 4. Controller
**Fichier**: `SecurityManagementApp/src/main/java/com/boulevardsecurity/securitymanagementapp/controller/PointageController.java`

**Nouveaux endpoints**:

#### POST `/api/pointages/prise-service`
```java
@PostMapping("/prise-service")
public ResponseEntity<?> priseDeService(@RequestBody PointageCreateDto dto) {
    try {
        PointageDto pointage = service.enregistrerPriseDeService(dto);
        return ResponseEntity.ok(pointage);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(404)
            .body(Map.of("error", e.getMessage()));
    }
}
```

#### POST `/api/pointages/fin-service`
```java
@PostMapping("/fin-service")
public ResponseEntity<?> finDeService(@RequestBody PointageCreateDto dto) {
    try {
        PointageDto pointage = service.enregistrerFinDeService(dto);
        return ResponseEntity.ok(pointage);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest()
            .body(Map.of("error", e.getMessage()));
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(404)
            .body(Map.of("error", e.getMessage()));
    }
}
```

#### GET `/api/pointages/mission/{idMission}/agents-en-service`
```java
@GetMapping("/mission/{idMission}/agents-en-service")
public ResponseEntity<?> getAgentsEnService(@PathVariable Long idMission) {
    try {
        List<AgentDeSecuriteDto> agents = service.getAgentsEnService(idMission);
        return ResponseEntity.ok(agents);
    } catch (NoSuchElementException e) {
        return ResponseEntity.status(404)
            .body(Map.of("error", e.getMessage()));
    }
}
```

---

## Architecture et Design Decisions

### Pas de relation directe Agent ↔ Pointage
**Raison**: On peut récupérer la liste des agents via la mission concernée
- `pointage.agentId` stocke uniquement l'ID
- Pour obtenir l'agent complet: `mission.getAgents()` puis filtrer par ID

### Détection agent en service
**Critère**: `finService == null`
- Prise de service: `finService = null`
- Fin de service: `finService = new Date()`

### Validation géolocalisation
**Source**: Géolocalisation de la mission (pas du site)
- Mission peut avoir sa propre géolocalisation
- Tolérance: 100 mètres
- Formule Haversine pour calcul de distance GPS

---

## Tests à effectuer

### 1. Prise de Service
```bash
POST http://localhost:8080/api/pointages/prise-service
Content-Type: application/json

{
  "missionId": 1,
  "agentId": 2,
  "datePointage": "2025-10-06T14:30:00",
  "estPresent": true,
  "estRetard": false,
  "positionActuelle": {
    "latitude": 43.125681,
    "longitude": 5.928494
  }
}
```

**Cas de succès**: 
- Status 200
- Retourne PointageDto avec `finService = null`

**Cas d'erreur**:
- 404: Mission introuvable
- 400: Agent non affecté à la mission
- 400: Agent déjà en service
- 400: Position GPS trop éloignée

### 2. Fin de Service
```bash
POST http://localhost:8080/api/pointages/fin-service
Content-Type: application/json

{
  "missionId": 1,
  "agentId": 2,
  "datePointage": "2025-10-06T22:30:00",
  "estPresent": true,
  "estRetard": false,
  "positionActuelle": {
    "latitude": 43.125681,
    "longitude": 5.928494
  }
}
```

**Cas de succès**: 
- Status 200
- Retourne PointageDto avec `finService = date actuelle`

**Cas d'erreur**:
- 404: Mission introuvable
- 400: Agent non affecté à la mission
- 400: Aucune prise de service active trouvée

### 3. Liste des Agents en Service
```bash
GET http://localhost:8080/api/pointages/mission/1/agents-en-service
```

**Cas de succès**: 
- Status 200
- Retourne `List<AgentDeSecuriteDto>` des agents actuellement en service

**Cas d'erreur**:
- 404: Mission introuvable

---

## Migration Base de Données

### Ajout des colonnes
```sql
ALTER TABLE pointages 
ADD COLUMN fin_service DATETIME NULL,
ADD COLUMN agent_id BIGINT NULL;
```

### Contraintes (optionnel)
```sql
-- Index pour améliorer les performances
CREATE INDEX idx_pointages_agent_id ON pointages(agent_id);
CREATE INDEX idx_pointages_fin_service ON pointages(fin_service);
CREATE INDEX idx_pointages_mission_agent ON pointages(mission_id, agent_id);
```

---

## Frontend

Le frontend est déjà configuré avec:
- `PointageService.priseDeService(dto)` - Appel POST `/prise-service`
- `PointageService.finDeService(dto)` - Appel POST `/fin-service`
- `PointageService.getAgentsEnService(missionId)` - Appel GET `/agents-en-service`

**Fichier**: `security-management-frontend/src/services/PointageService.js`

---

## État de la compilation

✅ **Aucune erreur de compilation** dans les fichiers modifiés:
- Pointage.java
- PointageService.java
- PointageServiceImpl.java
- PointageController.java

Les warnings restants sont dans d'autres fichiers non liés aux pointages.

---

## Prochaines étapes

1. **Tester les endpoints** avec Postman ou le frontend
2. **Vérifier la migration DB** (colonnes finService et agentId)
3. **Tester les validations**:
   - Agent non assigné à la mission
   - Double prise de service
   - Fin de service sans prise de service active
   - Validation GPS > 100m
4. **Intégration frontend complète**
