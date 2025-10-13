// src/services/AgentService.js
import api from "./api";

const AgentService = {
  
  getAllAgents: () => api.get("/agents"),

  getAgentById: (id) => api.get(`/agents/${id}`),

  getAgentByEmail: (email) => api.get(`/agents/search?email=${email}`),

  createAgent: (agent) => api.post("/agents", agent),

  updateAgent: (id, agent) => api.put(`/agents/${id}`, agent),

  deleteAgent: (id) => api.delete(`/agents/${id}`),

  assignZone: (agentId, zoneId) => api.put(`/agents/${agentId}/zone/${zoneId}`),

  addDisponibilite: (agentId, dispo) => api.post(`/agents/${agentId}/disponibilites`, dispo),

  assignDisponibiliteExistante: (agentId, disponibiliteId) => 
    api.put(`/agents/${agentId}/disponibilites/${disponibiliteId}`),

  addCarte: (agentId, carte) => api.post(`/agents/${agentId}/cartesProfessionnelles`, carte),

  assignCarteExistante: (agentId, carteId) => 
    api.put(`/agents/${agentId}/cartesProfessionnelles/${carteId}`),

  addDiplome: (agentId, diplome) => api.post(`/agents/${agentId}/diplomesSsiap`, diplome),

  assignDiplomeExistant: (agentId, diplomeId) => 
    api.put(`/agents/${agentId}/diplomesSsiap/${diplomeId}`),

  changeRole: (agentId, role) => api.put(`/agents/${agentId}/role?role=${role}`),

  getPlanning: (agentId) => api.get(`/agents/${agentId}/planning`)
};

export default AgentService;
