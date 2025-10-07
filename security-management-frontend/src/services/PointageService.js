// src/services/PointageService.js
import api from "./api";

const API = "/pointages";

const PointageService = {
    getAll: () => api.get(API),
    getById: (id) => api.get(`${API}/${id}`),
    create: (dto) => api.post(API, dto),
    update: (id, dto) => api.put(`${API}/${id}`, dto),
    delete: (id) => api.delete(`${API}/${id}`),
    
    // Nouvelles méthodes pour la prise et fin de service
    priseDeService: (dto) => api.post(`${API}/prise-service`, dto),
    finDeService: (dto) => api.post(`${API}/fin-service`, dto),
    
    // Obtenir les pointages par mission
    getByMission: (missionId) => api.get(`${API}/mission/${missionId}`),
    
    // Obtenir les pointages par agent
    getByAgent: (agentId) => api.get(`${API}/agent/${agentId}`),
    
    // Obtenir les agents en service pour une mission
    getAgentsEnService: (missionId) => api.get(`${API}/mission/${missionId}/agents-en-service`),
};

export default PointageService;