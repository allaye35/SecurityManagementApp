import api from "./api";

const PATH = "/factures";

const FactureService = {
    
    getAll: () => api.get(PATH),

    getById: (id) => api.get(`${PATH}/${id}`),

    create: (factureData) => api.post(PATH, factureData),

    update: (id, factureData) => api.put(`${PATH}/${id}`, factureData),

    delete: (id) => api.delete(`${PATH}/${id}`),

    getByClient: (clientId) => api.get(`${PATH}/client/${clientId}`),

    createFromDevis: (devisId) => api.post(`${PATH}/from-devis/${devisId}`),

    createForPeriod: (periodeData) => api.post(`${PATH}/periode`, periodeData),

    previewFacture: (periodeData) => api.post(`${PATH}/preview`, periodeData),

    getPdf: (id) => api.get(`${PATH}/${id}/pdf`, {
        responseType: 'blob',
        headers: {
            'Accept': 'application/pdf'
        }
    })
};

export default FactureService;
