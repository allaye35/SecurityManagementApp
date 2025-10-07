// src/pages/AdminUserManagement.jsx
import React, { useEffect, useState } from "react";
import adminSvc from "../services/adminAccountsService";
import ClientService from "../services/ClientService";
import AgentService from "../services/AgentService";
import "../styles/AdminUserManagement.css";

export default function AdminUserManagement() {
  const [users, setUsers] = useState([]);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [filterRole, setFilterRole] = useState("all");
  const [filterStatus, setFilterStatus] = useState("all");
  const [searchTerm, setSearchTerm] = useState("");
  const [showRoleModal, setShowRoleModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState(null);
  const [newRole, setNewRole] = useState("");

  const loadUsers = async () => {
    setLoading(true);
    setError("");
    try {
      const [clientsResp, agentsResp] = await Promise.all([
        ClientService.getAll(),
        AgentService.getAllAgents()
      ]);
      const clients = clientsResp.data;
      const agents = agentsResp.data;

      const allUsers = [
        ...clients.map(c => ({
          ...c,
          type: "client",
          displayName: getClientDisplayName(c),
          status: getClientStatus(c)
        })),
        ...agents.map(a => ({
          ...a,
          type: "agent",
          displayName: `${a.prenom} ${a.nom}`,
          status: getAgentStatus(a)
        }))
      ];

      setUsers(allUsers);
      setFilteredUsers(allUsers);
    } catch (err) {
      setError("Erreur lors du chargement des utilisateurs");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  useEffect(() => {
    let filtered = users;

    if (filterRole !== "all") {
      filtered = filtered.filter(u => u.role === filterRole);
    }

    if (filterStatus !== "all") {
      if (filterStatus === "approved") {
        filtered = filtered.filter(u => u.adminApproved);
      } else if (filterStatus === "pending") {
        filtered = filtered.filter(u => !u.adminApproved && u.emailVerified);
      } else if (filterStatus === "unverified") {
        filtered = filtered.filter(u => !u.emailVerified);
      }
    }

    if (searchTerm) {
      const term = searchTerm.toLowerCase();
      filtered = filtered.filter(u => 
        u.displayName.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        (u.telephone && u.telephone.includes(term))
      );
    }

    setFilteredUsers(filtered);
  }, [users, filterRole, filterStatus, searchTerm]);

  const getClientDisplayName = (client) => {
    if (client.typeClient === "PARTICULIER") {
      return `${client.prenom || ""} ${client.nom || ""}`.trim() || "Particulier";
    } else {
      return client.representant || client.siege || "Entreprise";
    }
  };

  const getClientStatus = (client) => {
    if (!client.emailVerified) return "unverified";
    if (!client.adminApproved) return "pending";
    return "approved";
  };

  const getAgentStatus = (agent) => {
    if (!agent.emailVerified) return "unverified";
    if (!agent.adminApproved) return "pending";
    return "approved";
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case "approved":
        return <span className="badge badge-success">✅ Approuvé</span>;
      case "pending":
        return <span className="badge badge-warning">⏳ En attente</span>;
      case "unverified":
        return <span className="badge badge-danger">❌ Non vérifié</span>;
      default:
        return <span className="badge badge-secondary">❓ Inconnu</span>;
    }
  };

  const getRoleBadge = (role) => {
    const roleColors = {
      ADMIN: "badge-admin",
      CLIENT: "badge-client",
      AGENT_SECURITE: "badge-agent"
    };
    
    const roleNames = {
      ADMIN: "👑 Admin",
      CLIENT: "👤 Client", 
      AGENT_SECURITE: "👮‍♂️ Agent"
    };

    return (
      <span className={`badge ${roleColors[role] || "badge-secondary"}`}>
        {roleNames[role] || role}
      </span>
    );
  };

  const handleRoleChange = (user) => {
    if (user.type !== "client") {
      setError("La modification de rôle n'est disponible que pour les clients");
      return;
    }
    setSelectedUser(user);
    setNewRole(user.role);
    setShowRoleModal(true);
  };

  const handleRoleSubmit = async () => {
    if (!selectedUser || !newRole) return;

    setLoading(true);
    try {
      await adminSvc.changeClientRole(selectedUser.id, newRole);
      await loadUsers();
      setShowRoleModal(false);
      setSelectedUser(null);
    } catch (err) {
      setError("Erreur lors de la modification du rôle");
      console.error("Erreur:", err);
    } finally {
      setLoading(false);
    }
  };

  const getStatsData = () => {
    const stats = {
      total: users.length,
      approved: users.filter(u => u.status === "approved").length,
      pending: users.filter(u => u.status === "pending").length,
      unverified: users.filter(u => u.status === "unverified").length,
      clients: users.filter(u => u.type === "client").length,
      agents: users.filter(u => u.type === "agent").length,
      admins: users.filter(u => u.role === "ADMIN").length
    };
    return stats;
  };

  const stats = getStatsData();

  return (
    <div className="admin-user-management">
      <div className="page-header">
        <h2>👥 Gestion des Utilisateurs & Rôles</h2>
        <p className="page-description">
          Vue d'ensemble et administration des comptes utilisateurs du système.
        </p>
      </div>

      {error && (
        <div className="alert alert-danger">
          <i className="fas fa-exclamation-triangle"></i> {error}
        </div>
      )}

      {}
      <div className="stats-grid">
        <div className="stat-card">
          <div className="stat-icon">👥</div>
          <div className="stat-info">
            <div className="stat-number">{stats.total}</div>
            <div className="stat-label">Total Utilisateurs</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">✅</div>
          <div className="stat-info">
            <div className="stat-number">{stats.approved}</div>
            <div className="stat-label">Approuvés</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">⏳</div>
          <div className="stat-info">
            <div className="stat-number">{stats.pending}</div>
            <div className="stat-label">En Attente</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">👤</div>
          <div className="stat-info">
            <div className="stat-number">{stats.clients}</div>
            <div className="stat-label">Clients</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">👮‍♂️</div>
          <div className="stat-info">
            <div className="stat-number">{stats.agents}</div>
            <div className="stat-label">Agents</div>
          </div>
        </div>
        <div className="stat-card">
          <div className="stat-icon">👑</div>
          <div className="stat-info">
            <div className="stat-number">{stats.admins}</div>
            <div className="stat-label">Administrateurs</div>
          </div>
        </div>
      </div>

      {}
      <div className="filters-section">
        <div className="filters-row">
          <div className="filter-group">
            <label>🔍 Recherche :</label>
            <input
              type="text"
              className="form-control search-input"
              placeholder="Nom, email, téléphone..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
            />
          </div>
          
          <div className="filter-group">
            <label>👤 Rôle :</label>
            <select 
              value={filterRole} 
              onChange={(e) => setFilterRole(e.target.value)}
              className="form-control"
            >
              <option value="all">Tous les rôles</option>
              <option value="ADMIN">Administrateurs</option>
              <option value="CLIENT">Clients</option>
              <option value="AGENT_SECURITE">Agents de sécurité</option>
            </select>
          </div>

          <div className="filter-group">
            <label>📊 Statut :</label>
            <select 
              value={filterStatus} 
              onChange={(e) => setFilterStatus(e.target.value)}
              className="form-control"
            >
              <option value="all">Tous les statuts</option>
              <option value="approved">Approuvés</option>
              <option value="pending">En attente</option>
              <option value="unverified">Non vérifiés</option>
            </select>
          </div>
        </div>
      </div>

      {}
      <div className="users-section">
        <div className="section-header">
          <h3>📋 Utilisateurs ({filteredUsers.length})</h3>
        </div>

        {loading ? (
          <div className="loading-overlay">
            <i className="fas fa-spinner fa-spin"></i> Chargement...
          </div>
        ) : filteredUsers.length === 0 ? (
          <div className="empty-state">
            <i className="fas fa-users"></i>
            <h4>Aucun utilisateur trouvé</h4>
            <p>Aucun utilisateur ne correspond aux critères de filtrage.</p>
          </div>
        ) : (
          <div className="users-table-container">
            <table className="users-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Nom</th>
                  <th>Email</th>
                  <th>Téléphone</th>
                  <th>Type</th>
                  <th>Rôle</th>
                  <th>Statut</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {filteredUsers.map(user => (
                  <tr key={`${user.type}-${user.id}`}>
                    <td>#{user.id}</td>
                    <td className="user-name">
                      <div className="user-name-container">
                        <span className="name">{user.displayName}</span>
                        {user.type === "client" && user.typeClient && (
                          <span className="client-type">({user.typeClient})</span>
                        )}
                      </div>
                    </td>
                    <td className="user-email">{user.email}</td>
                    <td>{user.telephone || "—"}</td>
                    <td>
                      <span className={`badge badge-${user.type}`}>
                        {user.type === "client" ? "👤 Client" : "👮‍♂️ Agent"}
                      </span>
                    </td>
                    <td>{getRoleBadge(user.role)}</td>
                    <td>{getStatusBadge(user.status)}</td>
                    <td>
                      <div className="actions-group">
                        {user.type === "client" && (
                          <button
                            className="btn btn-sm btn-outline"
                            onClick={() => handleRoleChange(user)}
                            disabled={loading}
                            title="Modifier le rôle"
                          >
                            <i className="fas fa-user-cog"></i>
                          </button>
                        )}
                        <button
                          className="btn btn-sm btn-outline"
                          onClick={() => window.open(`/${user.type}s/${user.id}`, '_blank')}
                          title="Voir les détails"
                        >
                          <i className="fas fa-eye"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {}
      {showRoleModal && selectedUser && (
        <div className="modal-overlay" onClick={() => setShowRoleModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h4>⚙️ Modifier le rôle</h4>
              <button 
                className="modal-close"
                onClick={() => setShowRoleModal(false)}
              >
                ×
              </button>
            </div>
            <div className="modal-body">
              <p>
                Modifier le rôle de <strong>{selectedUser.displayName}</strong> :
              </p>
              
              <div className="form-group">
                <label>Nouveau rôle :</label>
                <select
                  value={newRole}
                  onChange={(e) => setNewRole(e.target.value)}
                  className="form-control"
                >
                  <option value="CLIENT">👤 Client</option>
                  <option value="ADMIN">👑 Administrateur</option>
                  <option value="AGENT_SECURITE">👮‍♂️ Agent de sécurité</option>
                </select>
              </div>
              
              <div className="role-warning">
                <i className="fas fa-exclamation-triangle"></i>
                <strong>Attention :</strong> Cette modification changera les permissions de l'utilisateur dans le système.
              </div>
            </div>
            <div className="modal-footer">
              <button 
                className="btn btn-secondary"
                onClick={() => setShowRoleModal(false)}
              >
                Annuler
              </button>
              <button 
                className="btn btn-primary"
                onClick={handleRoleSubmit}
                disabled={loading || newRole === selectedUser.role}
              >
                {loading ? (
                  <><i className="fas fa-spinner fa-spin"></i> Modification...</>
                ) : (
                  <><i className="fas fa-save"></i> Confirmer</>
                )}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}