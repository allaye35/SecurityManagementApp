// src/services/adminAccountsService.js
import api from "./api";

const adminAccountsService = {
  getPendingAgents:  () => api.get("/admin/accounts/pending/agents").then(r=>r.data),
  getPendingClients: () => api.get("/admin/accounts/pending/clients").then(r=>r.data),
  
  approveAgent:  (id) => api.post(`/admin/accounts/agents/${id}/approve`).then(r=>r.data),
  approveClient: (id) => api.post(`/admin/accounts/clients/${id}/approve`).then(r=>r.data),
  
  rejectAgent:  (id, reason = "") => api.post(`/admin/accounts/agents/${id}/reject`, { reason }).then(r=>r.data),
  rejectClient: (id, reason = "") => api.post(`/admin/accounts/clients/${id}/reject`, { reason }).then(r=>r.data),
  
  changeClientRole: (id, newRole) => api.put(`/clients/${id}/role`, { role: newRole }).then(r=>r.data),
};

export default adminAccountsService;
