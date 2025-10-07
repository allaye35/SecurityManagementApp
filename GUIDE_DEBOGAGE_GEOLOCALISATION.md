# 🐛 Guide de Débogage - Géolocalisation

## 📊 Logs de la Console

Avec les nouvelles améliorations, la console du navigateur affichera des logs détaillés pour vous aider à identifier le problème :

### Étapes de débogage :

1. **Ouvrez la console du navigateur** (F12)
2. **Sélectionnez une mission** dans le formulaire
3. **Observez les logs** qui apparaissent :

```
🔍 Récupération de la mission ID: 1
📦 Mission récupérée: { ... }
🏢 Site ID trouvé: 5
🔍 Récupération du site ID: 5
🏢 Site récupéré: { ... }
📍 Adresse trouvée: "123 Rue de Paris"
🌍 Géocodage de l'adresse: "123 Rue de Paris"
📍 Résultats du géocodage: [{ lat: "48.8566", lon: "2.3522", ... }]
✅ Coordonnées calculées: { latitude: 48.8566, longitude: 2.3522 }
```

## ❌ Messages d'Erreur Possibles

### 1. "Cette mission n'a pas de site associé"
**Cause** : La mission n'a aucun ID de site dans ses propriétés
**Solution** : 
- Vérifier dans la BDD si la colonne `site_mission` ou `site_id` est remplie
- Associer un site à la mission via l'interface d'administration

**Log dans la console** :
```
❌ Aucun site_id trouvé dans la mission: ["id", "titre", "dateDebut", ...]
```

### 2. "Le site 'XXX' n'a pas d'adresse renseignée"
**Cause** : Le site existe mais n'a pas d'adresse
**Solution** :
- Aller dans la gestion des sites
- Modifier le site et renseigner l'adresse complète
- Formats d'adresse supportés :
  - `site.adresse`
  - `site.address`
  - `site.rue`
  - `site.voie`

**Log dans la console** :
```
❌ Aucune adresse trouvée dans le site: ["id", "nom", "codePostal", ...]
```

### 3. "Impossible de géocoder l'adresse 'XXX'"
**Cause** : L'API Nominatim n'a pas trouvé l'adresse
**Solution** :
- Vérifier que l'adresse est correcte et complète
- Ajouter le code postal et la ville : "123 Rue de Paris, 75001 Paris, France"
- Ou saisir manuellement les coordonnées

**Log dans la console** :
```
📍 Résultats du géocodage: []
```

### 4. "Erreur lors du géocodage: XXX"
**Cause** : Erreur technique (API, réseau, etc.)
**Solution** :
- Vérifier la connexion internet
- Attendre quelques secondes (limite de requêtes de l'API)
- Réessayer

## 🔧 Propriétés Vérifiées

### Pour le Site ID (dans la mission) :
- `mission.site_mission` ✅
- `mission.siteId` ✅
- `mission.site.id` ✅
- `mission.site_id` ✅

### Pour l'Adresse (dans le site) :
- `site.adresse` ✅
- `site.address` ✅
- `site.rue` ✅
- `site.voie` ✅

## 📝 Exemple de Données Attendues

### Mission (réponse API) :
```json
{
  "id": 1,
  "titre": "Surveillance nuit",
  "dateDebut": "2025-01-15",
  "dateFin": "2025-01-20",
  "site_mission": 5,  // ← L'ID du site
  ...
}
```

### Site (réponse API) :
```json
{
  "id": 5,
  "nom": "Centre Commercial Rosny",
  "adresse": "Avenue du Général de Gaulle, 93110 Rosny-sous-Bois, France",  // ← L'adresse complète
  "codePostal": "93110",
  "ville": "Rosny-sous-Bois",
  ...
}
```

## 🎯 Actions Recommandées

1. **Ouvrir la console du navigateur** (F12)
2. **Sélectionner une mission**
3. **Lire les logs** pour identifier l'étape qui échoue
4. **Corriger les données** dans la base de données si nécessaire
5. **Réessayer**

## 💡 Astuce

Si vous voyez dans les logs que le site n'a pas d'adresse, vous pouvez :

1. **Aller dans PhpMyAdmin** ou votre outil de BDD
2. **Ouvrir la table `sites`**
3. **Modifier le site** concerné
4. **Remplir le champ `adresse`** avec une adresse complète
5. **Sauvegarder**
6. **Retourner sur le formulaire** et réessayer

## 🚨 Format d'Adresse Recommandé

Pour un géocodage optimal, utilisez ce format :
```
[Numéro] [Rue], [Code Postal] [Ville], [Pays]
```

Exemples :
- ✅ "123 Avenue des Champs-Élysées, 75008 Paris, France"
- ✅ "10 Rue de la Paix, 75002 Paris"
- ❌ "Champs-Élysées" (trop imprécis)
- ❌ "Paris" (trop imprécis)
