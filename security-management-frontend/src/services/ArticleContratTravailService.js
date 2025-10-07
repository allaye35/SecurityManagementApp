// src/services/ArticleContratTravailService.js
import api from "./api";

const RESOURCE = "/articles-contrat-travail";

export default {
    getAll: () => api.get(`${RESOURCE}`),
    
    getById: id => api.get(`${RESOURCE}/${id}`),
    
    getByContratTravail: contratId => api.get(`${RESOURCE}/contrat-travail/${contratId}`),
    
    getByContratId: contratId => api.get(`${RESOURCE}/contrat-travail/${contratId}`),
    
    create: article => api.post(RESOURCE, article),
    
    update: (id, article) => api.put(`${RESOURCE}/${id}`, article),
    
    remove: id => api.delete(`${RESOURCE}/${id}`)
};
