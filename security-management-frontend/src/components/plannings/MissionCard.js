// src/components/plannings/MissionCard.js
import React from 'react';
import { Card, Badge, Button, OverlayTrigger, Tooltip } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { 
  faTasks, faMapMarkerAlt, faCalendarAlt, faClock, 
  faUsers, faInfoCircle, faTimes
} from '@fortawesome/free-solid-svg-icons';

const MissionCard = ({ mission, onRemove, canRemove = true, agents = [] }) => {
  const formatDate = (dateString) => {
    if (!dateString) return 'Non définie';
    return new Date(dateString).toLocaleDateString('fr-FR');
  };

  const getStatusColor = (status) => {
    switch (status?.toLowerCase()) {
      case 'active':
      case 'en cours':
        return 'success';
      case 'pending':
      case 'en attente':
        return 'warning';
      case 'completed':
      case 'terminé':
        return 'info';
      case 'cancelled':
      case 'annulé':
        return 'danger';
      default:
        return 'secondary';
    }
  };

  return (
    <Card className="mission-card h-100 shadow-sm">
      <Card.Header className="d-flex justify-content-between align-items-center bg-light">
        <div className="d-flex align-items-center">
          <FontAwesomeIcon icon={faTasks} className="text-primary me-2" />
          <strong>Mission #{mission.id}</strong>
        </div>
        {canRemove && onRemove && (
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip>Retirer cette mission</Tooltip>}
          >
            <Button
              variant="outline-danger"
              size="sm"
              onClick={() => onRemove(mission.id)}
              className="remove-mission-btn"
            >
              <FontAwesomeIcon icon={faTimes} />
            </Button>
          </OverlayTrigger>
        )}
      </Card.Header>
      
      <Card.Body className="p-3">
        <Card.Title className="h6 mb-3">
          {mission.titre || 'Mission sans titre'}
        </Card.Title>
        
        {mission.description && (
          <Card.Text className="text-muted small mb-3">
            {mission.description.length > 100 
              ? `${mission.description.substring(0, 100)}...` 
              : mission.description}
          </Card.Text>
        )}

        <div className="mission-details">
          {mission.lieu && (
            <div className="d-flex align-items-center mb-2">
              <FontAwesomeIcon icon={faMapMarkerAlt} className="text-info me-2" />
              <small className="text-muted">{mission.lieu}</small>
            </div>
          )}
          
          {mission.dateDebut && (
            <div className="d-flex align-items-center mb-2">
              <FontAwesomeIcon icon={faCalendarAlt} className="text-warning me-2" />
              <small className="text-muted">
                Début: {formatDate(mission.dateDebut)}
              </small>
            </div>
          )}
          
          {mission.dateFin && (
            <div className="d-flex align-items-center mb-2">
              <FontAwesomeIcon icon={faCalendarAlt} className="text-warning me-2" />
              <small className="text-muted">
                Fin: {formatDate(mission.dateFin)}
              </small>
            </div>
          )}

          {mission.duree && (
            <div className="d-flex align-items-center mb-2">
              <FontAwesomeIcon icon={faClock} className="text-secondary me-2" />
              <small className="text-muted">
                Durée: {mission.duree}h
              </small>
            </div>
          )}

          {agents.length > 0 && (
            <div className="d-flex align-items-center mb-2">
              <FontAwesomeIcon icon={faUsers} className="text-success me-2" />
              <small className="text-muted">
                {agents.length} agent{agents.length > 1 ? 's' : ''} assigné{agents.length > 1 ? 's' : ''}
              </small>
            </div>
          )}
        </div>

        <div className="d-flex justify-content-between align-items-center mt-3">
          {mission.statut && (
            <Badge bg={getStatusColor(mission.statut)} className="mission-status-badge">
              {mission.statut}
            </Badge>
          )}
          
          {mission.priorite && (
            <Badge 
              bg={mission.priorite === 'haute' ? 'danger' : 
                  mission.priorite === 'moyenne' ? 'warning' : 'secondary'}
              className="ms-2"
            >
              Priorité {mission.priorite}
            </Badge>
          )}
        </div>
      </Card.Body>
      
      {mission.notes && (
        <Card.Footer className="bg-light">
          <OverlayTrigger
            placement="top"
            overlay={<Tooltip>{mission.notes}</Tooltip>}
          >
            <small className="text-muted d-flex align-items-center">
              <FontAwesomeIcon icon={faInfoCircle} className="me-1" />
              Notes disponibles
            </small>
          </OverlayTrigger>
        </Card.Footer>
      )}
    </Card>
  );
};

export default MissionCard;
