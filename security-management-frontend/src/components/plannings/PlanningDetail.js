import { useEffect, useState } from "react";
import { useParams, Link } from "react-router-dom";
import PlanningService from "../../services/PlanningService";
import AgentService from "../../services/AgentService";
import MissionService from "../../services/MissionService";
import PlanningStats from "./PlanningStats";
import MissionTimeline from "./MissionTimeline";
import { Container, Card, Badge, Button, Alert, Spinner, ListGroup, Row, Col, OverlayTrigger, Tooltip } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { 
  faCalendarCheck, faArrowLeft, faEye, faEdit, faUsers, faTasks, 
  faCalendarAlt, faClock, faMapMarkerAlt, faIdCard, faInfoCircle,
  faCheckCircle, faExclamationTriangle, faHourglassHalf
} from "@fortawesome/free-solid-svg-icons";
import "../../styles/PlanningDetail.css";

export default function PlanningDetail() {
    const { id } = useParams();
    const [planning, setPlanning] = useState(null);
    const [agents, setAgents] = useState([]);
    const [missions, setMissions] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState("");

    useEffect(() => {
        setLoading(true);
        
        // Charger le planning, les agents et les missions en parallèle
        Promise.all([
            PlanningService.getPlanningById(id),
            AgentService.getAllAgents(),
            MissionService.getAllMissions()
        ])
        .then(([planningRes, agentsRes, missionsRes]) => {
            setPlanning(planningRes.data);
            setAgents(Array.isArray(agentsRes.data) ? agentsRes.data : []);
            setMissions(Array.isArray(missionsRes.data) ? missionsRes.data : []);
            setLoading(false);
        })
        .catch((error) => {
            console.error("Erreur lors du chargement du planning:", error);
            setErr("Impossible de charger ce planning");
            setLoading(false);
        });
    }, [id]);

    // Helper pour récupérer les objets missions du planning
    const getMissionObjects = () => {
        if (!planning) return [];
        
        // Si le backend renvoie déjà planning.missions (objet), on s'en sert
        if (Array.isArray(planning.missions)) return planning.missions;
        
        // Sinon on reconstruit via planning.missionIds
        const ids = Array.isArray(planning.missionIds) ? planning.missionIds : [];
        return ids.map(id => missions.find(m => m.id === id)).filter(Boolean);
    };

    // Helper pour le statut des missions
    const getMissionStatus = (mission) => {
        if (!mission.dateDebut || !mission.dateFin) return { status: 'pending', label: 'En attente', icon: faHourglassHalf };
        
        const now = new Date();
        const debut = new Date(mission.dateDebut);
        const fin = new Date(mission.dateFin);
        
        if (now < debut) return { status: 'pending', label: 'À venir', icon: faHourglassHalf };
        if (now > fin) return { status: 'completed', label: 'Terminée', icon: faCheckCircle };
        return { status: 'active', label: 'En cours', icon: faCheckCircle };
    };

    // Helper pour formater les dates
    const formatDate = (dateString) => {
        if (!dateString) return 'Non définie';
        return new Date(dateString).toLocaleDateString('fr-FR', {
            day: '2-digit',
            month: 'long',
            year: 'numeric'
        });
    };

    const formatDateTime = (dateString) => {
        if (!dateString) return 'Non définie';
        return new Date(dateString).toLocaleString('fr-FR', {
            day: '2-digit',
            month: 'long',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    // Helper pour récupérer les agents du planning
    const getAgentsForPlanning = () => {
        const missionObjs = getMissionObjects();
        const allAgents = [];
        
        missionObjs.forEach(mission => {
            if (Array.isArray(mission?.agents)) {
                // Si la mission a déjà les objets agents complets
                allAgents.push(...mission.agents);
            } else if (Array.isArray(mission?.agentIds)) {
                // Si la mission a seulement les agentIds, récupérer les objets agents
                const missionAgents = mission.agentIds
                    .map(agentId => agents.find(a => a.id === agentId))
                    .filter(Boolean);
                allAgents.push(...missionAgents);
            }
        });
        
        // Supprimer les doublons basés sur l'ID
        const uniqueAgents = allAgents.filter((agent, index, self) => 
            index === self.findIndex(a => a.id === agent.id)
        );
        
        return uniqueAgents;
    };

    if (err) return (
        <Container className="planning-detail-container">
            <Alert variant="danger" className="error-container">
                <Alert.Heading>
                    <FontAwesomeIcon icon={faExclamationTriangle} className="me-2" />
                    Erreur de chargement
                </Alert.Heading>
                <p className="mb-3">{err}</p>
                <hr />
                <div className="d-flex justify-content-end">
                    <Link to="/plannings">
                        <Button variant="outline-danger" className="back-btn">
                            <FontAwesomeIcon icon={faArrowLeft} className="me-2" />
                            Retour aux plannings
                        </Button>
                    </Link>
                </div>
            </Alert>
        </Container>
    );
    
    if (loading || !planning) return (
        <Container className="planning-detail-container">
            <div className="loading-container">
                <div className="loading-spinner"></div>
                <p className="loading-text">Chargement du planning...</p>
            </div>
        </Container>
    );

    const planningMissions = getMissionObjects();
    const planningAgents = getAgentsForPlanning();

    return (
        <Container fluid className="planning-detail-container">
            <Card className="planning-detail-card fade-in">
                <Card.Header className="planning-detail-header">
                    <div className="d-flex justify-content-between align-items-center">
                        <div className="d-flex align-items-center gap-3">
                            <h1 className="planning-detail-title">
                                <FontAwesomeIcon icon={faCalendarCheck} className="me-3" />
                                Planning
                            </h1>
                            <Badge className="planning-id-badge">
                                #{planning.id}
                            </Badge>
                        </div>
                        <div className="d-flex gap-2">
                            <OverlayTrigger
                                placement="bottom"
                                overlay={<Tooltip>Modifier ce planning</Tooltip>}
                            >
                                <Link to={`/plannings/edit/${planning.id}`}>
                                    <Button className="edit-planning-btn">
                                        <FontAwesomeIcon icon={faEdit} className="me-2" />
                                        Modifier
                                    </Button>
                                </Link>
                            </OverlayTrigger>
                        </div>
                    </div>
                </Card.Header>

                <Card.Body className="p-4">
                    {/* Statistiques rapides */}
                    <PlanningStats 
                        planning={planning} 
                        missions={planningMissions} 
                        agents={planningAgents} 
                    />

                    {/* Section d'informations générales */}
                    <Row className="info-section">
                        <Col lg={6} className="mb-4">
                            <Card className="info-card slide-in-left">
                                <Card.Header className="info-card-header">
                                    <FontAwesomeIcon icon={faInfoCircle} className="me-2 text-primary" />
                                    Informations générales
                                </Card.Header>
                                <Card.Body className="info-card-body">
                                    <div className="info-item">
                                        <div className="info-label">
                                            <FontAwesomeIcon icon={faIdCard} className="info-icon" />
                                            Identifiant
                                        </div>
                                        <div className="info-value">#{planning.id}</div>
                                    </div>
                                    
                                    <div className="info-item">
                                        <div className="info-label">
                                            <FontAwesomeIcon icon={faCalendarAlt} className="info-icon" />
                                            Créé le
                                        </div>
                                        <div className="info-value">
                                            {formatDateTime(planning.dateCreation)}
                                        </div>
                                    </div>
                                    
                                    <div className="info-item">
                                        <div className="info-label">
                                            <FontAwesomeIcon icon={faClock} className="info-icon" />
                                            Modifié le
                                        </div>
                                        <div className="info-value">
                                            {formatDateTime(planning.dateModification)}
                                        </div>
                                    </div>

                                    {planning.description && (
                                        <div className="info-item">
                                            <div className="info-label">
                                                <FontAwesomeIcon icon={faInfoCircle} className="info-icon" />
                                                Description
                                            </div>
                                            <div className="info-value">
                                                {planning.description}
                                            </div>
                                        </div>
                                    )}
                                </Card.Body>
                            </Card>
                        </Col>
                        
                        <Col lg={6} className="mb-4">
                            <Card className="info-card slide-in-right">
                                <Card.Header className="info-card-header">
                                    <FontAwesomeIcon icon={faUsers} className="me-2 text-info" />
                                    Agents impliqués ({planningAgents.length})
                                </Card.Header>
                                <Card.Body className="info-card-body">
                                    {planningAgents.length === 0 ? (
                                        <div className="no-agents-message">
                                            <FontAwesomeIcon icon={faUsers} className="me-2" />
                                            Aucun agent assigné à ce planning
                                        </div>
                                    ) : (
                                        <div className="agents-container">
                                            {planningAgents.map((agent, index) => (
                                                <OverlayTrigger
                                                    key={`agent-${agent.id}`}
                                                    placement="top"
                                                    overlay={
                                                        <Tooltip>
                                                            <div>
                                                                <strong>Agent #{agent.id}</strong><br/>
                                                                {agent.email && `Email: ${agent.email}`}
                                                            </div>
                                                        </Tooltip>
                                                    }
                                                >
                                                    <div 
                                                        className="agent-card"
                                                        style={{ animationDelay: `${index * 0.1}s` }}
                                                    >
                                                        <div className="agent-avatar">
                                                            {agent.prenom?.charAt(0)?.toUpperCase() || 'A'}
                                                        </div>
                                                        <div className="agent-info">
                                                            <div className="agent-name">
                                                                {agent.nom} {agent.prenom}
                                                            </div>
                                                            <div className="agent-id">
                                                                Agent #{agent.id}
                                                            </div>
                                                        </div>
                                                    </div>
                                                </OverlayTrigger>
                                            ))}
                                        </div>
                                    )}
                                </Card.Body>
                            </Card>
                        </Col>
                    </Row>

                    {/* Section des missions */}
                    <Card className="missions-section">
                        <Card.Header className="missions-header">
                            <h4 className="missions-title">
                                <FontAwesomeIcon icon={faTasks} />
                                Missions assignées
                                <Badge className="missions-count">
                                    {planningMissions.length}
                                </Badge>
                            </h4>
                        </Card.Header>
                        
                        <Card.Body className="missions-body">
                            {planningMissions.length === 0 ? (
                                <div className="no-missions-message">
                                    <FontAwesomeIcon icon={faTasks} className="no-missions-icon" />
                                    <h5>Aucune mission assignée</h5>
                                    <p>Ce planning ne contient aucune mission pour le moment.</p>
                                </div>
                            ) : (
                                <ListGroup variant="flush">
                                    {planningMissions.map((mission, index) => {
                                        const status = getMissionStatus(mission);
                                        return (
                                            <ListGroup.Item 
                                                key={mission.id} 
                                                className="mission-item"
                                                style={{ animationDelay: `${index * 0.1}s` }}
                                            >
                                                <div className="mission-content">
                                                    <div className="mission-info">
                                                        <div className="mission-title">
                                                            <Badge className="mission-badge">
                                                                #{mission.id}
                                                            </Badge>
                                                            {mission.titre || 'Mission sans titre'}
                                                        </div>
                                                        
                                                        {mission.description && (
                                                            <div className="mission-description">
                                                                {mission.description}
                                                            </div>
                                                        )}
                                                        
                                                        <div className="mission-dates">
                                                            {mission.dateDebut && (
                                                                <div className="mission-date">
                                                                    <FontAwesomeIcon icon={faCalendarAlt} className="mission-date-icon" />
                                                                    Début: {formatDate(mission.dateDebut)}
                                                                </div>
                                                            )}
                                                            {mission.dateFin && (
                                                                <div className="mission-date">
                                                                    <FontAwesomeIcon icon={faCalendarAlt} className="mission-date-icon" />
                                                                    Fin: {formatDate(mission.dateFin)}
                                                                </div>
                                                            )}
                                                            {mission.lieu && (
                                                                <div className="mission-date">
                                                                    <FontAwesomeIcon icon={faMapMarkerAlt} className="mission-date-icon" />
                                                                    {mission.lieu}
                                                                </div>
                                                            )}
                                                        </div>
                                                        
                                                        <div className="mission-status">
                                                            <Badge className={`status-badge status-${status.status}`}>
                                                                <FontAwesomeIcon icon={status.icon} className="me-1" />
                                                                {status.label}
                                                            </Badge>
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
                                                    </div>
                                                    
                                                    <div className="mission-actions">
                                                        <OverlayTrigger
                                                            placement="top"
                                                            overlay={<Tooltip>Voir les détails de la mission</Tooltip>}
                                                        >
                                                            <Link to={`/missions/${mission.id}`}>
                                                                <Button className="mission-detail-btn">
                                                                    <FontAwesomeIcon icon={faEye} className="me-2" />
                                                                    Détails
                                                                </Button>
                                                            </Link>
                                                        </OverlayTrigger>
                                                    </div>
                                                </div>
                                            </ListGroup.Item>
                                        );
                                    })}
                                </ListGroup>
                            )}
                        </Card.Body>
                    </Card>

                    {/* Timeline des missions */}
                    <MissionTimeline missions={planningMissions} />

                    {/* Navigation */}
                    <div className="navigation-section">
                        <Link to="/plannings">
                            <Button className="back-btn">
                                <FontAwesomeIcon icon={faArrowLeft} className="me-2" />
                                Retour aux plannings
                            </Button>
                        </Link>
                        
                        <div className="d-flex gap-2">
                            <Link to={`/plannings/edit/${planning.id}`}>
                                <Button variant="warning" className="px-4">
                                    <FontAwesomeIcon icon={faEdit} className="me-2" />
                                    Modifier
                                </Button>
                            </Link>
                        </div>
                    </div>
                </Card.Body>
            </Card>
        </Container>
    );
}
