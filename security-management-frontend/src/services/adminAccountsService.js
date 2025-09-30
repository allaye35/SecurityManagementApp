// src/services/adminAccountsService.js
import api from "./api";

const adminAccountsService = {
  getPendingAgents:  () => api.get("/admin/accounts/pending/agents").then(r=>r.data),
  getPendingClients: () => api.get("/clients/pending").then(r=>r.data), // Corriger l'endpoint
  approveAgent:  (id) => api.post(`/admin/accounts/agents/${id}/approve`).then(r=>r.data),
  approveClient: (id) => api.post(`/clients/${id}/approve`).then(r=>r.data), // Corriger l'endpoint
  
  // Nouvelle méthode pour changer le rôle d'un client (utilise l'endpoint spécialisé)
  changeClientRole: (id, newRole) => api.put(`/clients/${id}/role`, { role: newRole }).then(r=>r.data),
};

export default adminAccountsService;
