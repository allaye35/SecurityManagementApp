// src/components/plannings/AgentInfoCard.js
import React from 'react';
import { Card, Badge, Button, OverlayTrigger, Tooltip } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { 
  faUser, faIdCard, faEnvelope, faPhone, 
  faBriefcase, faCalendarAlt, faEye
} from '@fortawesome/free-solid-svg-icons';

const AgentInfoCard = ({ agent, showDetailLink = false }) => {
  const getInitials = (nom, prenom) => {
    const nomInitial = nom ? nom.charAt(0).toUpperCase() : '';
    const prenomInitial = prenom ? prenom.charAt(0).toUpperCase() : '';
    return `${prenomInitial}${nomInitial}`;
  };

  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'actif':
      case 'active':
        return 'success';
      case 'inactif':
      case 'inactive':
        return 'danger';
      case 'en congé':
      case 'conge':
        return 'warning';
      default:
        return 'secondary';
    }
  };

  return (
    <Card className="agent-info-card h-100">
      <Card.Body className="p-3">
        <div className="d-flex align-items-center mb-3">
          <div className="agent-avatar-large">
            {getInitials(agent.nom, agent.prenom)}
          </div>
          <div className="ms-3 flex-grow-1">
            <h6 className="agent-name-large mb-1">
              {agent.prenom} {agent.nom}
            </h6>
            <div className="d-flex align-items-center gap-2">
              <Badge bg="primary" className="agent-id-badge">
                <FontAwesomeIcon icon={faIdCard} className="me-1" />
                #{agent.id}
              </Badge>
              {agent.status && (
                <Badge bg={getStatusColor(agent.status)} className="status-badge">
                  {agent.status}
                </Badge>
              )}
            </div>
          </div>
        </div>

        <div className="agent-details">
          {agent.email && (
            <div className="agent-detail-item">
              <FontAwesomeIcon icon={faEnvelope} className="detail-icon" />
              <span className="detail-text">{agent.email}</span>
            </div>
          )}
          
          {agent.telephone && (
            <div className="agent-detail-item">
              <FontAwesomeIcon icon={faPhone} className="detail-icon" />
              <span className="detail-text">{agent.telephone}</span>
            </div>
          )}
          
          {agent.poste && (
            <div className="agent-detail-item">
              <FontAwesomeIcon icon={faBriefcase} className="detail-icon" />
              <span className="detail-text">{agent.poste}</span>
            </div>
          )}
          
          {agent.dateEmbauche && (
            <div className="agent-detail-item">
              <FontAwesomeIcon icon={faCalendarAlt} className="detail-icon" />
              <span className="detail-text">
                Embauché le {new Date(agent.dateEmbauche).toLocaleDateString('fr-FR')}
              </span>
            </div>
          )}
        </div>

        {showDetailLink && (
          <div className="mt-3 text-center">
            <OverlayTrigger
              placement="top"
              overlay={<Tooltip>Voir le profil complet</Tooltip>}
            >
              <Button variant="outline-primary" size="sm">
                <FontAwesomeIcon icon={faEye} className="me-1" />
                Voir profil
              </Button>
            </OverlayTrigger>
          </div>
        )}
      </Card.Body>
    </Card>
  );
};

export default AgentInfoCard;
