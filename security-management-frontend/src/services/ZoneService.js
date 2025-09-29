// src/services/ZoneService.js
import api, { plain } from "./api";

/**
 * Normalise le payload pour la création/màj d'une zone.
 * - Garantit que agentIds est un tableau.
 * - Clone l'objet pour éviter toute mutation.
 */
function normalizeZoneDto(dto = {}) {
  const data = { ...dto };
  if (data.agentIds && !Array.isArray(data.agentIds)) {
    data.agentIds = [data.agentIds];
  } else if (!data.agentIds) {
    data.agentIds = [];
  }
  return data;
}

const ZoneService = {
  // ───────────────────────────
  // LECTURE PUBLIQUE (sans auth)
  // ───────────────────────────
  /** Liste toutes les zones (publique) */
  getAllPublic: () => plain.get("/zones"),
  /** Alias rétro-compat pour ton code : getAllZones() */
  getAllZones: () => plain.get("/zones"),

  /** Détail d'une zone (publique) */
  getByIdPublic: (id) => plain.get(`/zones/${id}`),

  /** Recherche publique par nom */
  searchByNamePublic: (nom) =>
    plain.get("/zones/recherche", { params: { nom } }),

  /** Recherche publique par type */
  searchByTypePublic: (typeZone) => plain.get(`/zones/type/${typeZone}`),

  // ───────────────────────────
  // LECTURE / ÉCRITURE AUTH
  // ───────────────────────────
  /** Liste toutes les zones (authentifié) */
  getAll: () => api.get("/zones"),

  /** Détail d'une zone (authentifié) */
  getById: (id) => api.get(`/zones/${id}`),

  /** Crée une zone (authentifié) */
  create: (dto) => {
    const data = normalizeZoneDto(dto);
    // console.log("ZoneService.create payload:", data);
    return api.post("/zones", data);
  },

  /** Variante explicite pour créer et lier des agents (authentifié) */
  createWithAgents: (dto) => {
    const data = normalizeZoneDto(dto);
    // console.log("ZoneService.createWithAgents payload:", data);
    return api.post("/zones", data);
  },

  /** Met à jour une zone (authentifié) */
  update: (id, dto) => api.put(`/zones/${id}`, dto),

  /** Supprime une zone (authentifié) */
  remove: (id) => api.delete(`/zones/${id}`),

  // ── filtres (authentifié) ─────────────────────────
  searchByName: (nom) => api.get("/zones/recherche", { params: { nom } }),
  searchByType: (typeZone) => api.get(`/zones/type/${typeZone}`),

  // ── relations Agents <-> Zone (authentifié) ───────
  /** Récupère les agents de la zone */
  getAgentsForZone: (zoneId) => api.get(`/zones/${zoneId}/agents`),

  /** Assigne 1 agent à une zone (selon tes contrôleurs) */
  assignAgentToZone: (zoneId, agentId) =>
    api.put(`/agents/${agentId}/zone/${zoneId}`),

  /** Retire 1 agent d'une zone */
  removeAgentFromZone: (zoneId, agentId) =>
    api.delete(`/zones/${zoneId}/agents/${agentId}`),

  /** Assigne plusieurs agents d'un coup */
  assignMultipleAgentsToZone: (zoneId, agentIds) => {
    const list = Array.isArray(agentIds) ? agentIds : [agentIds];
    return api.put(`/zones/${zoneId}/agents`, { agentIds: list });
  },
};

export default ZoneService;
