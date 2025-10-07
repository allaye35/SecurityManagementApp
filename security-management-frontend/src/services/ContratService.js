import api from "./api";

const RESOURCE = "/contrats";

const ContratService = {
  
  getAll:         ()   => api.get(RESOURCE),
  getById:        id   => api.get(`${RESOURCE}/${id}`),
  getByReference: ref  => api.get(`${RESOURCE}/ref/${ref}`),

  create: data => api.post(RESOURCE, data),

  update: (id, data) => api.put(`${RESOURCE}/${id}`, data),

  remove: id => api.delete(`${RESOURCE}/${id}`),
};

export default ContratService;
