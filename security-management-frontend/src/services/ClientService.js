import api from "./api";

// Le chemin correct sans duplication de "/api" car baseURL contient déjà "http://localhost:8080/api"
const PATH = "/clients";

const ClientService = {
    
    getAll: () => api.get(PATH),

    getById: (id) => api.get(`${PATH}/${id}`),

    getByEmail: (email) => api.get(`${PATH}/email/${email}`).then(response => response.data),

    getByNom: (nom) => api.get(`${PATH}/nom/${nom}`).then(response => response.data),

    create: (dto) => api.post(PATH, dto).then(response => response.data),

    update: (id, dto) => api.put(`${PATH}/${id}`, dto).then(response => response.data),
      
    delete: (id) => api.delete(`${PATH}/${id}`).then(response => response.data),

    getByEntreprise: (entrepriseId) => api.get(`${PATH}/entreprise/${entrepriseId}`).then(response => response.data),

    getStatistiques: () => api.get(`${PATH}/statistiques`).then(response => response.data),

    calculerChiffreAffaires: (clientId, dateDebut, dateFin) => 
        api.get(`${PATH}/${clientId}/chiffre-affaires`, { params: { dateDebut, dateFin } }).then(response => response.data),

    getHistoriqueFactures: (clientId) => api.get(`${PATH}/${clientId}/factures`).then(response => response.data),

    getEntreprises: (clientId) => api.get(`${PATH}/${clientId}/entreprises`).then(response => response.data),
      
    associerEntreprise: (clientId, entrepriseId) => 
        api.post(`${PATH}/${clientId}/entreprises/${entrepriseId}`).then(response => response.data),

    dissocierEntreprise: (clientId, entrepriseId) => 
        api.delete(`${PATH}/${clientId}/entreprises/${entrepriseId}`).then(response => response.data),

    calculerRentabilite: (clientId) => api.get(`${PATH}/${clientId}/rentabilite`).then(response => response.data),

    getMissionsActives: (clientId) => api.get(`${PATH}/${clientId}/missions/actives`).then(response => response.data),

    getContratsEnCours: (clientId) => api.get(`${PATH}/${clientId}/contrats/en-cours`).then(response => response.data)
};

export default ClientService;
