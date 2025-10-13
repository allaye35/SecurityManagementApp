import api from "./api";

const PATH = "/jours-feries";

const JoursFeriesService = {
    
    getByAnnee: (annee) => api.get(`${PATH}?annee=${annee}`),
};

export default JoursFeriesService;