import React, { useState, useEffect } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import CarteProService from "../../services/CarteProService";
import AgentService from "../../services/AgentService";
import "../../styles/CarteProDetail.css";

const CarteProDetail = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [carte, setCarte] = useState(null);
    const [agent, setAgent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        setLoading(true);
        CarteProService.getById(id)
            .then(res => {
                setCarte(res.data);
                // Si la carte a un agent associé, récupérer ses informations
                if (res.data.agentId) {
                    return AgentService.getAgentById(res.data.agentId);
                }
                return { data: null };
            })
            .then(agentRes => {
                setAgent(agentRes.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur lors du chargement:", err);
                setError("Impossible de charger les détails de la carte professionnelle");
                setLoading(false);
            });
    }, [id]);

    // Fonction pour déterminer le statut de la carte
    const getCardStatus = (dateFin) => {
        if (!dateFin) return { status: "indefini", label: "Pas de date d'expiration" };
        
        const today = new Date();
        const expirationDate = new Date(dateFin);
        
        if (expirationDate < today) {
            return { status: "expired", label: "Expirée" };
        }
        
        // Calcul de la différence en jours
        const diffTime = expirationDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        if (diffDays <= 90) { // 3 mois
            return { status: "expiring-soon", label: "Expire bientôt" };
        }
        
        return { status: "valid", label: "Valide" };
    };

    // Fonction pour calculer le temps restant avant expiration
    const getRemainingTime = (dateFin) => {
        if (!dateFin) return "Non applicable";
        
        const today = new Date();
        const expirationDate = new Date(dateFin);
        
        if (expirationDate < today) {
            return "Expirée";
        }
        
        // Calcul de la différence en jours
        const diffTime = expirationDate - today;
        const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
        
        if (diffDays < 30) {
            return `${diffDays} jour${diffDays > 1 ? 's' : ''}`;
        } else if (diffDays < 365) {
            const months = Math.floor(diffDays / 30);
            return `${months} mois`;
        } else {
            const years = Math.floor(diffDays / 365);
            const remainingMonths = Math.floor((diffDays % 365) / 30);
            
            if (remainingMonths > 0) {
                return `${years} an${years > 1 ? 's' : ''} et ${remainingMonths} mois`;
            } else {
                return `${years} an${years > 1 ? 's' : ''}`;
            }
        }
    };

    // Fonction pour formater les dates
    const formatDate = (dateString) => {
        if (!dateString) return "Non spécifiée";
        
        const date = new Date(dateString);
        return date.toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric'
        });
    };

    // Fonction pour imprimer la carte professionnelle
    const handlePrint = () => {
        window.print();
    };

    // Fonction pour obtenir la description du type de carte
    const getTypeDescription = (type) => {
        switch(type) {
            case 'AGENT_DE_SECURITE':
                return "Agent de sécurité privée";
            case 'AGENT_DE_GARDIENNAGE':
                return "Agent de gardiennage";
            case 'AGENT_CYNOPHILE':
                return "Agent cynophile (avec chien)";
            case 'AGENT_DE_PROTECTION':
                return "Agent de protection rapprochée";
            case 'AGENT_DE_SURETE_AEROPORTUAIRE':
                return "Agent de sûreté aéroportuaire";
            default:
                return type ? type.replace(/_/g, " ") : "Non spécifié";
        }
    };

    if (loading) return <div className="loading-container"><div className="loading">Chargement des détails...</div></div>;
    if (error) return <div className="error-container"><div className="error">{error}</div></div>;
    if (!carte) return <div className="not-found-container"><div className="not-found">Carte professionnelle non trouvée</div></div>;

    const cardStatus = getCardStatus(carte.dateFin);

    return (
        <div className="carte-pro-detail-container">
            <div className="detail-header">
                <div className="header-title">
                    <h2>Carte Professionnelle</h2>
                    <div className={`status-badge status-${cardStatus.status}`}>
                        {cardStatus.label}
                    </div>
                </div>
                <div className="detail-actions">
                    <Link to="/cartes-professionnelles" className="btn btn-secondary">
                        <i className="fas fa-arrow-left"></i> Retour à la liste
                    </Link>
                    <Link to={`/cartes-professionnelles/edit/${carte.id}`} className="btn btn-primary">
                        <i className="fas fa-edit"></i> Modifier
                    </Link>
                </div>
            </div>

            <div className="detail-content">
                <div className="detail-card">
                    <div className="card-header">
                        <h3>Informations de la carte</h3>
                        <div className="card-badge">{carte.typeCarte && carte.typeCarte.replace(/_/g, " ")}</div>
                    </div>
                    <div className="card-body">
                        <div className="detail-grid">
                            <div className="detail-item">
                                <span className="detail-label">Numéro de carte:</span>
                                <span className="detail-value">{carte.numeroCarte}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Type:</span>
                                <span className="detail-value">
                                    {getTypeDescription(carte.typeCarte)}
                                </span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Date de début:</span>
                                <span className="detail-value">{formatDate(carte.dateDebut)}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Date de fin:</span>
                                <span className="detail-value">{formatDate(carte.dateFin)}</span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Durée de validité:</span>
                                <span className="detail-value">
                                    {Math.round(
                                        (new Date(carte.dateFin) - new Date(carte.dateDebut)) / 
                                        (1000 * 60 * 60 * 24 * 30)
                                    )} mois
                                </span>
                            </div>
                            <div className="detail-item">
                                <span className="detail-label">Temps restant:</span>
                                <span className="detail-value">{getRemainingTime(carte.dateFin)}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="detail-card">
                    <div className="card-header">
                        <h3>Titulaire de la carte</h3>
                        {agent && <Link to={`/agents/${agent.id}`} className="btn btn-sm">Voir profil</Link>}
                    </div>
                    <div className="card-body">
                        {agent ? (
                            <div className="agent-profile">
                                <div className="agent-avatar">
                                    {agent.nom && agent.prenom ? `${agent.nom.charAt(0)}${agent.prenom.charAt(0)}` : "??"}
                                </div>
                                <div className="agent-details">
                                    <div className="detail-grid">
                                        <div className="detail-item">
                                            <span className="detail-label">Nom complet:</span>
                                            <span className="detail-value">{agent.nom} {agent.prenom}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">ID:</span>
                                            <span className="detail-value">{agent.id}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Email:</span>
                                            <span className="detail-value">{agent.email || "Non spécifié"}</span>
                                        </div>
                                        <div className="detail-item">
                                            <span className="detail-label">Téléphone:</span>
                                            <span className="detail-value">{agent.telephone || "Non spécifié"}</span>
                                        </div>
                                        {agent.adresse && (
                                            <div className="detail-item">
                                                <span className="detail-label">Adresse:</span>
                                                <span className="detail-value">{agent.adresse}</span>
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <p className="no-data">Aucun agent associé à cette carte</p>
                        )}
                    </div>
                </div>

                {/* Ajout d'une carte pour les actions avancées */}
                <div className="detail-card actions-card">
                    <div className="card-header">
                        <h3>Actions</h3>
                    </div>
                    <div className="card-body">
                        <div className="action-buttons">
                            <button 
                                className="btn btn-action"
                                onClick={handlePrint}
                            >
                                <i className="fas fa-print"></i> Imprimer la carte
                            </button>
                            
                            <Link to={`/cartes-professionnelles/edit/${id}`} className="btn btn-action">
                                <i className="fas fa-sync-alt"></i> Renouveler la carte
                            </Link>
                            
                            <button 
                                className="btn btn-action btn-danger"
                                onClick={() => {
                                    if(window.confirm("Êtes-vous sûr de vouloir supprimer cette carte professionnelle ?")) {
                                        CarteProService.delete(id)
                                            .then(() => navigate("/cartes-professionnelles"))
                                            .catch(err => alert("Erreur lors de la suppression"));
                                    }
                                }}
                            >
                                <i className="fas fa-trash"></i> Supprimer la carte
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default CarteProDetail;