// src/components/plannings/PlanningStats.js
import React from 'react';
import { Card, Row, Col, Badge } from 'react-bootstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { 
  faUsers, faTasks, faCalendarCheck, faClock,
  faCheckCircle, faHourglassHalf, faExclamationTriangle
} from '@fortawesome/free-solid-svg-icons';

const PlanningStats = ({ planning, missions, agents }) => {
  const getMissionStats = () => {
    const stats = {
      total: missions.length,
      active: 0,
      pending: 0,
      completed: 0
    };

    const now = new Date();
    
    missions.forEach(mission => {
      if (!mission.dateDebut || !mission.dateFin) {
        stats.pending++;
        return;
      }
      
      const debut = new Date(mission.dateDebut);
      const fin = new Date(mission.dateFin);
      
      if (now < debut) stats.pending++;
      else if (now > fin) stats.completed++;
      else stats.active++;
    });

    return stats;
  };

  const formatDuration = () => {
    if (missions.length === 0) return 'Aucune mission';
    
    const datesDebut = missions.map(m => m.dateDebut).filter(Boolean);
    const datesFin = missions.map(m => m.dateFin).filter(Boolean);
    
    if (datesDebut.length === 0 || datesFin.length === 0) return 'Durée non définie';
    
    const debut = new Date(Math.min(...datesDebut.map(d => new Date(d))));
    const fin = new Date(Math.max(...datesFin.map(d => new Date(d))));
    
    const diffTime = Math.abs(fin - debut);
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return `${diffDays} jour${diffDays > 1 ? 's' : ''}`;
  };

  const missionStats = getMissionStats();

  return (
    <Row className="mb-4">
      <Col md={3} sm={6} className="mb-3">
        <Card className="text-center h-100 stats-card">
          <Card.Body>
            <FontAwesomeIcon icon={faUsers} size="2x" className="text-info mb-2" />
            <h4 className="mb-1">{agents.length}</h4>
            <small className="text-muted">Agent{agents.length > 1 ? 's' : ''}</small>
          </Card.Body>
        </Card>
      </Col>
      
      <Col md={3} sm={6} className="mb-3">
        <Card className="text-center h-100 stats-card">
          <Card.Body>
            <FontAwesomeIcon icon={faTasks} size="2x" className="text-success mb-2" />
            <h4 className="mb-1">{missionStats.total}</h4>
            <small className="text-muted">Mission{missionStats.total > 1 ? 's' : ''}</small>
          </Card.Body>
        </Card>
      </Col>
      
      <Col md={3} sm={6} className="mb-3">
        <Card className="text-center h-100 stats-card">
          <Card.Body>
            <FontAwesomeIcon icon={faCheckCircle} size="2x" className="text-primary mb-2" />
            <h4 className="mb-1">{missionStats.active}</h4>
            <small className="text-muted">En cours</small>
          </Card.Body>
        </Card>
      </Col>
      
      <Col md={3} sm={6} className="mb-3">
        <Card className="text-center h-100 stats-card">
          <Card.Body>
            <FontAwesomeIcon icon={faClock} size="2x" className="text-warning mb-2" />
            <h4 className="mb-1">{formatDuration()}</h4>
            <small className="text-muted">Durée totale</small>
          </Card.Body>
        </Card>
      </Col>
    </Row>
  );
};

export default PlanningStats;
