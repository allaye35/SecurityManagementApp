// src/components/plannings/MissionTimeline.js
import React from 'react';
import { Card, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { 
  faCalendarAlt, faClock, faMapMarkerAlt, 
  faCheckCircle, faHourglassHalf, faExclamationTriangle
} from '@fortawesome/free-solid-svg-icons';

const MissionTimeline = ({ missions }) => {
  const sortedMissions = [...missions].sort((a, b) => {
    const dateA = a.dateDebut ? new Date(a.dateDebut) : new Date();
    const dateB = b.dateDebut ? new Date(b.dateDebut) : new Date();
    return dateA - dateB;
  });

  const getStatusInfo = (mission) => {
    if (!mission.dateDebut || !mission.dateFin) {
      return { 
        status: 'pending', 
        label: 'À planifier', 
        icon: faHourglassHalf,
        color: '#ffc107'
      };
    }
    
    const now = new Date();
    const debut = new Date(mission.dateDebut);
    const fin = new Date(mission.dateFin);
    
    if (now < debut) {
      return { 
        status: 'upcoming', 
        label: 'À venir', 
        icon: faHourglassHalf,
        color: '#17a2b8'
      };
    }
    if (now > fin) {
      return { 
        status: 'completed', 
        label: 'Terminée', 
        icon: faCheckCircle,
        color: '#28a745'
      };
    }
    return { 
      status: 'active', 
      label: 'En cours', 
      icon: faCheckCircle,
      color: '#007bff'
    };
  };

  const formatDate = (dateString) => {
    if (!dateString) return 'Non définie';
    return new Date(dateString).toLocaleDateString('fr-FR', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  };

  if (missions.length === 0) {
    return (
      <Card className="timeline-card">
        <Card.Header className="timeline-header">
          <h5 className="mb-0">
            <FontAwesomeIcon icon={faCalendarAlt} className="me-2" />
            Timeline des missions
          </h5>
        </Card.Header>
        <Card.Body className="text-center py-5">
          <FontAwesomeIcon icon={faExclamationTriangle} size="3x" className="text-warning mb-3" />
          <h6>Aucune mission dans ce planning</h6>
          <p className="text-muted">Ajoutez des missions pour voir la timeline</p>
        </Card.Body>
      </Card>
    );
  };

  return (
    <Card className="timeline-card">
      <Card.Header className="timeline-header">
        <h5 className="mb-0">
          <FontAwesomeIcon icon={faCalendarAlt} className="me-2" />
          Timeline des missions
        </h5>
      </Card.Header>
      <Card.Body className="timeline-body">
        <div className="timeline">
          {sortedMissions.map((mission, index) => {
            const statusInfo = getStatusInfo(mission);
            return (
              <div key={mission.id} className="timeline-item" style={{ animationDelay: `${index * 0.1}s` }}>
                <div className="timeline-marker" style={{ backgroundColor: statusInfo.color }}>
                  <FontAwesomeIcon icon={statusInfo.icon} />
                </div>
                <div className="timeline-content">
                  <div className="timeline-card-content">
                    <div className="d-flex justify-content-between align-items-start mb-2">
                      <h6 className="timeline-title">
                        <Badge bg="primary" className="me-2">#{mission.id}</Badge>
                        {mission.titre}
                      </h6>
                      <Badge 
                        className="status-badge"
                        style={{ backgroundColor: statusInfo.color }}
                      >
                        {statusInfo.label}
                      </Badge>
                    </div>
                    
                    {mission.description && (
                      <p className="timeline-description">
                        {mission.description.length > 100 
                          ? `${mission.description.substring(0, 100)}...` 
                          : mission.description}
                      </p>
                    )}
                    
                    <div className="timeline-details">
                      <div className="timeline-detail-item">
                        <FontAwesomeIcon icon={faCalendarAlt} className="timeline-icon" />
                        <span>
                          {mission.dateDebut ? formatDate(mission.dateDebut) : 'Début non défini'}
                          {mission.dateFin && ` → ${formatDate(mission.dateFin)}`}
                        </span>
                      </div>
                      
                      {mission.lieu && (
                        <div className="timeline-detail-item">
                          <FontAwesomeIcon icon={faMapMarkerAlt} className="timeline-icon" />
                          <span>{mission.lieu}</span>
                        </div>
                      )}
                      
                      {mission.duree && (
                        <div className="timeline-detail-item">
                          <FontAwesomeIcon icon={faClock} className="timeline-icon" />
                          <span>{mission.duree}h</span>
                        </div>
                      )}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </Card.Body>
    </Card>
  );
};

export default MissionTimeline;
