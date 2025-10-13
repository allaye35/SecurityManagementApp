// src/services/RapportService.js
import api from "./api";

const API_URL = "/rapports";

const RapportService = {
    
    getAllRapports: () =>
        api.get(API_URL),

    getRapportById: id =>
        api.get(`${API_URL}/${id}`),

    createRapport: rapport =>
        api.post(API_URL, rapport),

    updateRapport: (id, rapport) =>
        api.put(`${API_URL}/${id}`, rapport),

    deleteRapport: id =>
        api.delete(`${API_URL}/${id}`)
};

export default RapportService;
