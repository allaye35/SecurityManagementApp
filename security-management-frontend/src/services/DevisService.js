import api from "./api";

const PATH = "/devis";

const DevisService = {
    
    getAll: () => api.get(PATH),

    getDisponibles: () => api.get(`${PATH}/disponibles`),

    getById: (id) => api.get(`${PATH}/${id}`),

    create: (dto) => api.post(PATH, dto),

    update: (id, dto) => api.put(`${PATH}/${id}`, dto),

    delete: (id) => api.delete(`${PATH}/${id}`),

    search: (ref) => api.get(`${PATH}/search`, { params: { reference: ref } }),

    addMissions: (devisId, missionIds) => api.post(`${PATH}/${devisId}/missions`, missionIds)
};

export default DevisService;