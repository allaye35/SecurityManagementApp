import api from "./api";

const API = "/pointages";

const PointageService = {
    getAll: () => api.get(API),
    getById: (id) => api.get(`${API}/${id}`),
    create: (dto) => api.post(API, dto),
    update: (id, dto) => api.put(`${API}/${id}`, dto),
    delete: (id) => api.delete(`${API}/${id}`),
    
    priseDeService: (dto) => api.post(`${API}/prise-service`, dto),
    finDeService: (dto) => api.post(`${API}/fin-service`, dto),
    
    getByMission: (missionId) => api.get(`${API}/mission/${missionId}`),
    
    getByAgent: (agentId) => api.get(`${API}/agent/${agentId}`),
    
    getAgentsEnService: (missionId) => api.get(`${API}/mission/${missionId}/agents-en-service`),
};

export default PointageService;