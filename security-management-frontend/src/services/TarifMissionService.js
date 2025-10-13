import api from "./api";

const PATH = "/tarifs";

const TarifMissionService = {
    
    getAll: () => api.get(PATH),

    getById: (id) => api.get(`${PATH}/${id}`),

    getByType: (type) => api.get(`${PATH}/type/${type}`),

    create: (dto) => api.post(PATH, dto),

    update: (id, dto) => api.put(`${PATH}/${id}`, dto),

    delete: (id) => api.delete(`${PATH}/${id}`),

    associateMission: (tarifId, missionId) => api.post(`${PATH}/${tarifId}/missions/${missionId}`),

    removeMission: (tarifId, missionId) => api.delete(`${PATH}/${tarifId}/missions/${missionId}`),

    getMissions: (tarifId) => api.get(`${PATH}/${tarifId}/missions`),

};

export default TarifMissionService;
