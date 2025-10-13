// src/services/ZoneService.js
import api, { plain } from "./api";

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
  
  getAllPublic: () => plain.get("/zones"),
  
  getAllZones: () => plain.get("/zones"),

  getByIdPublic: (id) => plain.get(`/zones/${id}`),

  searchByNamePublic: (nom) =>
    plain.get("/zones/recherche", { params: { nom } }),

  searchByTypePublic: (typeZone) => plain.get(`/zones/type/${typeZone}`),

  getAll: () => api.get("/zones"),

  getById: (id) => api.get(`/zones/${id}`),

  create: (dto) => {
    const data = normalizeZoneDto(dto);
    return api.post("/zones", data);
  },

  createWithAgents: (dto) => {
    const data = normalizeZoneDto(dto);
    return api.post("/zones", data);
  },

  update: (id, dto) => api.put(`/zones/${id}`, dto),

  remove: (id) => api.delete(`/zones/${id}`),

  searchByName: (nom) => api.get("/zones/recherche", { params: { nom } }),
  searchByType: (typeZone) => api.get(`/zones/type/${typeZone}`),

  getAgentsForZone: (zoneId) => api.get(`/zones/${zoneId}/agents`),

  assignAgentToZone: (zoneId, agentId) =>
    api.put(`/agents/${agentId}/zone/${zoneId}`),

  removeAgentFromZone: (zoneId, agentId) =>
    api.delete(`/zones/${zoneId}/agents/${agentId}`),

  assignMultipleAgentsToZone: (zoneId, agentIds) => {
    const list = Array.isArray(agentIds) ? agentIds : [agentIds];
    return api.put(`/zones/${zoneId}/agents`, { agentIds: list });
  },
};

export default ZoneService;
