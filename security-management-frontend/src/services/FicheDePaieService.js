import api from "./api";

const PATH = "/fiches-paie";

const FicheDePaieService = {
    getAll: ()             => api.get(PATH),
    getById: id            => api.get(`${PATH}/${id}`),
    create: dto            => api.post(PATH, dto),
    update: (id, dto)      => api.put(`${PATH}/${id}`, dto),
    remove: id             => api.delete(`${PATH}/${id}`)
};

export default FicheDePaieService;