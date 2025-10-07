// src/pages/AdminPendingAccounts.jsx
import React, { useEffect, useState } from "react";
import ClientService from "../services/ClientService";
import AgentService from "../services/AgentService";
import adminSvc from "../services/adminAccountsService";
import "../styles/AdminPendingAccounts.css";

export default function AdminPendingAccounts() {
  const [agents, setAgents] = useState([]);
  const [clients, setClients] = useState([]);
  const [activeTab, setActiveTab] = useState("agents");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [filter, setFilter] = useState("all");

  // --- pagination (ajoute ceci avec les autres useState) ---
  const PAGE_SIZE = 8;
  const [pageAgents, setPageAgents] = useState(1);
  const [pageClients, setPageClients] = useState(1);

  const loadData = async () => {
    setLoading(true);
    setError("");
    try {
      // Utiliser directement les services admin pour récupérer les comptes en attente
      const [pendingClients, pendingAgents] = await Promise.all([
        adminSvc.getPendingClients(),
        adminSvc.getPendingAgents()
      ]);

      setClients(pendingClients);
      setAgents(pendingAgents);
      
      // Réinitialiser la pagination après refresh des données
      setPageAgents(1);
      setPageClients(1);
    } catch (err) {
      setError("Erreur lors du chargement des comptes en attente");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  // Filtrer les données selon le filtre sélectionné
  const filteredAgents = agents.filter(agent => {
    if (filter === "all") return true;
    // Vous pouvez ajouter d'autres filtres ici selon vos besoins
    return true;
  });

  const filteredClients = clients.filter(client => {
    if (filter === "all") return true;
    if (filter === "particulier") return client.typeClient === "PARTICULIER";
    if (filter === "entreprise") return client.typeClient === "ENTREPRISE";
    return true;
  });

  // --- pages & tranches visibles ---
  const pagedAgents = filteredAgents.slice((pageAgents - 1) * PAGE_SIZE, pageAgents * PAGE_SIZE);
  const pagedClients = filteredClients.slice((pageClients - 1) * PAGE_SIZE, pageClients * PAGE_SIZE);
  const pagesAgents = Math.max(1, Math.ceil(filteredAgents.length / PAGE_SIZE));
  const pagesClients = Math.max(1, Math.ceil(filteredClients.length / PAGE_SIZE));

  // quand le filtre change
  useEffect(() => {
    setPageAgents(1);
    setPageClients(1);
  }, [filter]);

  // quand on bascule d'onglet
  useEffect(() => {
    if (activeTab === "agents") setPageAgents(1);
    if (activeTab === "clients") setPageClients(1);
  }, [activeTab]);

  const handleApproveAgent = async (agentId) => {
    try {
      setLoading(true);
      await adminSvc.approveAgent(agentId);
      await loadData(); // Recharger les données
    } catch (err) {
      setError("Erreur lors de l'approbation de l'agent");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleApproveClient = async (clientId) => {
    try {
      setLoading(true);
      await adminSvc.approveClient(clientId);
      await loadData(); // Recharger les données
    } catch (err) {
      setError("Erreur lors de l'approbation du client");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleRejectAgent = async (agentId) => {
    if (!window.confirm("Êtes-vous sûr de vouloir rejeter ce compte agent ?")) return;
    
    try {
      setLoading(true);
      await adminSvc.rejectAgent(agentId);
      await loadData(); // Recharger les données
    } catch (err) {
      setError("Erreur lors du rejet de l'agent");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleRejectClient = async (clientId) => {
    if (!window.confirm("Êtes-vous sûr de vouloir rejeter ce compte client ?")) return;
    
    try {
      setLoading(true);
      await adminSvc.rejectClient(clientId);
      await loadData(); // Recharger les données
    } catch (err) {
      setError("Erreur lors du rejet du client");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  const getClientDisplayName = (client) => {
    if (client.typeClient === "PARTICULIER") {
      return `${client.prenom || ""} ${client.nom || ""}`.trim() || "Particulier";
    } else {
      return client.representant || client.siege || "Entreprise";
    }
  };

  return (
    <div className="admin-pending-accounts">
      <div className="page-header">
        <h2>🟡 Validation des comptes</h2>
        <p className="page-description">
          Gérez les demandes d'inscription en attente d'approbation administrative.
        </p>
        <button 
          className="btn btn-primary refresh-btn" 
          onClick={loadData}
          disabled={loading}
        >
          <i className="fas fa-sync-alt"></i> Actualiser
        </button>
      </div>

      {error && (
        <div className="alert alert-danger">
          <i className="fas fa-exclamation-triangle"></i> {error}
        </div>
      )}

      {/* Filtres */}
      <div className="filters-section">
        <div className="filter-group">
          <label>🔍 Filtre par statut :</label>
          <select 
            value={filter} 
            onChange={(e) => setFilter(e.target.value)}
            className="form-control"
          >
            <option value="all">Tous les comptes en attente</option>
            {activeTab === "clients" && (
              <>
                <option value="particulier">Clients particuliers</option>
                <option value="entreprise">Clients entreprises</option>
              </>
            )}
          </select>
        </div>
      </div>

      {/* Onglets */}
      <div className="tabs-container">
        <button
          className={`tab-button ${activeTab === "agents" ? "active" : ""}`}
          onClick={() => setActiveTab("agents")}
        >
          👮‍♂️ Agents en attente ({filteredAgents.length})
        </button>
        <button
          className={`tab-button ${activeTab === "clients" ? "active" : ""}`}
          onClick={() => setActiveTab("clients")}
        >
          👤 Clients en attente ({filteredClients.length})
        </button>
      </div>

      {/* Contenu des onglets */}
      <div className="tab-content">
        {loading ? (
          <div className="loading-overlay">
            <i className="fas fa-spinner fa-spin"></i> Chargement...
          </div>
        ) : (
          <>
            {/* Onglet Agents */}
            {activeTab === "agents" && (
              <div className="agents-section">
                {pagedAgents.length === 0 ? (
                  <div className="empty-state">
                    <i className="fas fa-user-shield"></i>
                    <h4>Aucun agent en attente</h4>
                    <p>Tous les agents ont été traités ou aucune demande n'est en attente.</p>
                  </div>
                ) : (
                  <div className="pending-cards">
                    {pagedAgents.map(agent => (
                      <div key={agent.id} className="pending-card">
                        <div className="card-header">
                          <h4>{agent.prenom} {agent.nom}</h4>
                          <span className="badge badge-warning">👮‍♂️ Agent</span>
                        </div>
                        <div className="card-body">
                          <div className="info-row">
                            <strong>Email:</strong> {agent.email}
                          </div>
                          <div className="info-row">
                            <strong>Téléphone:</strong> {agent.telephone || "Non renseigné"}
                          </div>
                          <div className="info-row">
                            <strong>Date d'inscription:</strong> {new Date(agent.dateCreation).toLocaleDateString()}
                          </div>
                          {agent.adresse && (
                            <div className="info-row">
                              <strong>Adresse:</strong> {agent.adresse}
                            </div>
                          )}
                        </div>
                        <div className="card-actions">
                          <button
                            className="btn btn-success"
                            onClick={() => handleApproveAgent(agent.id)}
                            disabled={loading}
                          >
                            <i className="fas fa-check"></i> Approuver
                          </button>
                          <button
                            className="btn btn-danger"
                            onClick={() => handleRejectAgent(agent.id)}
                            disabled={loading}
                          >
                            <i className="fas fa-times"></i> Rejeter
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                {/* Pagination Agents */}
                {activeTab === "agents" && filteredAgents.length > PAGE_SIZE && (
                  <div className="pagination">
                    <button 
                      disabled={pageAgents === 1} 
                      onClick={() => setPageAgents(p => p - 1)}
                      className="btn btn-outline"
                    >
                      <i className="fas fa-chevron-left"></i> Précédent
                    </button>
                    <span className="pagination-info">
                      Page {pageAgents} sur {pagesAgents} ({filteredAgents.length} agents)
                    </span>
                    <button 
                      disabled={pageAgents === pagesAgents} 
                      onClick={() => setPageAgents(p => p + 1)}
                      className="btn btn-outline"
                    >
                      Suivant <i className="fas fa-chevron-right"></i>
                    </button>
                  </div>
                )}
              </div>
            )}

            {/* Onglet Clients */}
            {activeTab === "clients" && (
              <div className="clients-section">
                {pagedClients.length === 0 ? (
                  <div className="empty-state">
                    <i className="fas fa-users"></i>
                    <h4>Aucun client en attente</h4>
                    <p>Tous les clients ont été traités ou aucune demande n'est en attente.</p>
                  </div>
                ) : (
                  <div className="pending-cards">
                    {pagedClients.map(client => (
                      <div key={client.id} className="pending-card">
                        <div className="card-header">
                          <h4>{getClientDisplayName(client)}</h4>
                          <span className={`badge ${client.typeClient === "PARTICULIER" ? "badge-info" : "badge-primary"}`}>
                            {client.typeClient === "PARTICULIER" ? "👤 Particulier" : "🏢 Entreprise"}
                          </span>
                        </div>
                        <div className="card-body">
                          <div className="info-row">
                            <strong>Email:</strong> {client.email}
                          </div>
                          <div className="info-row">
                            <strong>Téléphone:</strong> {client.telephone || "Non renseigné"}
                          </div>
                          <div className="info-row">
                            <strong>Date d'inscription:</strong> {new Date(client.dateCreation).toLocaleDateString()}
                          </div>
                          {client.typeClient === "ENTREPRISE" && client.siret && (
                            <div className="info-row">
                              <strong>SIRET:</strong> {client.siret}
                            </div>
                          )}
                          {client.adresse && (
                            <div className="info-row">
                              <strong>Adresse:</strong> {client.adresse}
                            </div>
                          )}
                        </div>
                        <div className="card-actions">
                          <button
                            className="btn btn-success"
                            onClick={() => handleApproveClient(client.id)}
                            disabled={loading}
                          >
                            <i className="fas fa-check"></i> Approuver
                          </button>
                          <button
                            className="btn btn-danger"
                            onClick={() => handleRejectClient(client.id)}
                            disabled={loading}
                          >
                            <i className="fas fa-times"></i> Rejeter
                          </button>
                        </div>
                      </div>
                    ))}
                  </div>
                )}

                {/* Pagination Clients */}
                {activeTab === "clients" && filteredClients.length > PAGE_SIZE && (
                  <div className="pagination">
                    <button 
                      disabled={pageClients === 1} 
                      onClick={() => setPageClients(p => p - 1)}
                      className="btn btn-outline"
                    >
                      <i className="fas fa-chevron-left"></i> Précédent
                    </button>
                    <span className="pagination-info">
                      Page {pageClients} sur {pagesClients} ({filteredClients.length} clients)
                    </span>
                    <button 
                      disabled={pageClients === pagesClients} 
                      onClick={() => setPageClients(p => p + 1)}
                      className="btn btn-outline"
                    >
                      Suivant <i className="fas fa-chevron-right"></i>
                    </button>
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </div>
    </div>
  );
}