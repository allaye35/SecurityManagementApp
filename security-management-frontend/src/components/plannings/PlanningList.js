// src/components/plannings/PlanningList.js
import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import PlanningService from "../../services/PlanningService";
import MissionService from "../../services/MissionService";
import AgentService from "../../services/AgentService";
import {
  Table, Button, Container, Row, Col, Form, Card, Badge, InputGroup, Alert, Modal, Tooltip, OverlayTrigger
} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCalendarPlus, faEye, faEdit, faTrash, faPlus, faTimes, faSearch, faFilter, 
  faUsers, faTasks, faCalendarAlt, faExclamationTriangle, faTable, faThLarge
} from "@fortawesome/free-solid-svg-icons";
import "../../styles/PlanningList.css";

export default function PlanningList() {
  const [plannings, setPlannings] = useState([]);
  const [missions, setMissions] = useState([]);
  const [agents, setAgents] = useState([]); // Nouvel état pour les agents
  const [selection, setSelection] = useState({}); // { [planningId]: missionId }
  const [filters, setFilters] = useState({ agent: "", mission: "", d1: "", d2: "" });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [planningToDelete, setPlanningToDelete] = useState(null);
  const [viewMode, setViewMode] = useState('table'); // 'table' ou 'cards'

  // Map rapide missionId -> mission
  const missionById = useMemo(() => {
    const map = new Map();
    missions.forEach(m => map.set(m.id, m));
    return map;
  }, [missions]);

  // Map rapide agentId -> agent
  const agentById = useMemo(() => {
    const map = new Map();
    agents.forEach(a => map.set(a.id, a));
    return map;
  }, [agents]);

  useEffect(() => {
    loadAll();
  }, []);

  function loadAll() {
    setLoading(true);
    setError("");

    Promise.all([
      PlanningService.getAllPlannings(), 
      MissionService.getAllMissions(),
      AgentService.getAllAgents()
    ])
      .then(([plRes, miRes, agRes]) => {
        if (Array.isArray(plRes.data)) {
          setPlannings(plRes.data);
        } else {
          console.error("Plannings non reçus sous forme de tableau :", plRes.data);
          setPlannings([]);
          setError("Erreur lors du chargement des plannings");
        }

        if (Array.isArray(miRes.data)) {
          setMissions(miRes.data);
        } else {
          console.error("Missions non reçues sous forme de tableau :", miRes.data);
          setMissions([]);
          setError(prev => prev ? prev + " et des missions" : "Erreur lors du chargement des missions");
        }

        if (Array.isArray(agRes.data)) {
          setAgents(agRes.data);
        } else {
          console.error("Agents non reçus sous forme de tableau :", agRes.data);
          setAgents([]);
          setError(prev => prev ? prev + " et des agents" : "Erreur lors du chargement des agents");
        }
      })
      .catch(err => {
        console.error("Erreur lors du chargement des données :", err);
        setError("Erreur de connexion au serveur");
        setPlannings([]);
        setMissions([]);
        setAgents([]);
      })
      .finally(() => setLoading(false));
  }

  // Helpers d'affichage: reconstruit les missions/agents d'un planning
  const getMissionObjects = (p) => {
    // Si le backend renvoie déjà p.missions (objet), on s'en sert;
    // sinon on reconstruit via p.missionIds.
    if (Array.isArray(p?.missions)) return p.missions;
    const ids = Array.isArray(p?.missionIds) ? p.missionIds : [];
    return ids.map(id => missionById.get(id)).filter(Boolean);
  };

  const getAgentsForPlanning = (p) => {
    const mis = getMissionObjects(p);
    // Pour chaque mission, récupérer les agents via agentIds
    const allAgents = [];
    mis.forEach(mission => {
      if (Array.isArray(mission?.agents)) {
        // Si la mission a déjà les objets agents complets
        allAgents.push(...mission.agents);
      } else if (Array.isArray(mission?.agentIds)) {
        // Si la mission a seulement les agentIds, récupérer les objets agents
        const missionAgents = mission.agentIds
          .map(agentId => agentById.get(agentId))
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

  // Suppression planning
  const confirmDelete = (id) => { setPlanningToDelete(id); setShowDeleteModal(true); };

  const deletePlanning = () => {
    setShowDeleteModal(false);
    if (!planningToDelete) return;

    setLoading(true);
    PlanningService.deletePlanning(planningToDelete)
      .then(loadAll)
      .catch(err => {
        setError("Erreur lors de la suppression du planning");
        console.error("Erreur lors de la suppression :", err);
        setLoading(false);
      });
  };

  // Ajouter / retirer une mission
  const addMission = (planningId) => {
    const mid = Number(selection[planningId]); // IMPORTANT: en nombre
    if (!mid) {
      setError("Veuillez sélectionner une mission à ajouter");
      setTimeout(() => setError(""), 3000);
      return;
    }

    // Empêche les doublons côté UI
    const current = plannings.find(p => p.id === planningId);
    const currentIds = Array.isArray(current?.missionIds)
      ? current.missionIds
      : getMissionObjects(current).map(m => m.id);

    if (currentIds.includes(mid)) {
      setError("Cette mission est déjà assignée à ce planning");
      setTimeout(() => setError(""), 3000);
      return;
    }

    setLoading(true);
    PlanningService.addMissionToPlanning(planningId, mid)
      .then(() => {
        loadAll();
        setSelection({ ...selection, [planningId]: "" }); // reset le select
      })
      .catch(err => {
        setError("Erreur lors de l'ajout de la mission");
        console.error("Erreur lors de l'ajout de mission :", err);
        setLoading(false);
      });
  };

  const removeMission = (planningId, missionId) => {
    if (!window.confirm("Êtes-vous sûr de vouloir retirer cette mission du planning ?")) return;

    setLoading(true);
    PlanningService.removeMissionFromPlanning(planningId, missionId)
      .then(loadAll)
      .catch(err => {
        setError("Erreur lors du retrait de la mission");
        console.error("Erreur lors du retrait de mission :", err);
        setLoading(false);
      });
  };

  // Filtrage
  const runFilter = async () => {
    const { agent, mission, d1, d2 } = filters;
    setLoading(true);
    setError("");

    try {
      if (agent) {
        const data = await PlanningService.getPlanningsByAgent(agent);
        setPlannings(Array.isArray(data) ? data : []);
      } else if (mission) {
        const data = await PlanningService.getPlanningsByMission(mission);
        setPlannings(Array.isArray(data) ? data : []);
      } else if (d1 && d2) {
        if (new Date(d1) > new Date(d2)) {
          setError("La date de début doit être antérieure à la date de fin");
          setLoading(false);
          return;
        }
        const data = await PlanningService.getPlanningsByDateRange(d1, d2);
        setPlannings(Array.isArray(data) ? data : []);
      } else {
        loadAll();
        return;
      }
      setLoading(false);
    } catch (err) {
      console.error("Erreur lors du filtrage :", err);
      setPlannings([]);
      setError("Erreur lors du filtrage des plannings");
      setLoading(false);
    }
  };

  const resetFilters = () => {
    setFilters({ agent: "", mission: "", d1: "", d2: "" });
    loadAll();
  };

  return (
    <Container fluid className="planning-list-container">
      <Card className="planning-card fade-in">
        <Card.Header className="planning-header">
          <div className="d-flex justify-content-between align-items-center">
            <h2 className="planning-title">
              <FontAwesomeIcon icon={faCalendarPlus} className="me-3" /> 
              Gestion des Plannings
            </h2>
            <Link to="/plannings/create">
              <Button className="new-planning-btn">
                <FontAwesomeIcon icon={faPlus} className="me-2" /> 
                Nouveau Planning
              </Button>
            </Link>
            
            {/* Boutons de changement de vue */}
            <div className="d-flex gap-2 ms-3">
              <OverlayTrigger
                placement="bottom"
                overlay={<Tooltip>Vue tableau</Tooltip>}
              >
                <Button
                  variant={viewMode === 'table' ? 'light' : 'outline-light'}
                  size="sm"
                  onClick={() => setViewMode('table')}
                >
                  <FontAwesomeIcon icon={faTable} />
                </Button>
              </OverlayTrigger>
              <OverlayTrigger
                placement="bottom"
                overlay={<Tooltip>Vue cartes</Tooltip>}
              >
                <Button
                  variant={viewMode === 'cards' ? 'light' : 'outline-light'}
                  size="sm"
                  onClick={() => setViewMode('cards')}
                >
                  <FontAwesomeIcon icon={faThLarge} />
                </Button>
              </OverlayTrigger>
            </div>
          </div>
        </Card.Header>

        <Card.Body className="p-4">
          {error && (
            <Alert variant="danger" dismissible onClose={() => setError("")} className="fade-in">
              <FontAwesomeIcon icon={faExclamationTriangle} className="me-2" />
              {error}
            </Alert>
          )}

          {/* Filtres améliorés */}
          <Card className="filters-card slide-in">
            <Card.Header className="filters-header">
              <h5 className="mb-0">
                <FontAwesomeIcon icon={faFilter} className="me-2 text-primary" /> 
                Filtres de recherche
              </h5>
            </Card.Header>
            <Card.Body className="filter-controls">
              <Form>
                <Row className="mb-3">
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label className="fw-semibold">
                        <FontAwesomeIcon icon={faUsers} className="me-1 text-info" />
                        ID Agent
                      </Form.Label>
                      <Form.Control
                        className="filter-input"
                        placeholder="Ex: 123"
                        value={filters.agent}
                        onChange={(e) => setFilters({ ...filters, agent: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label className="fw-semibold">
                        <FontAwesomeIcon icon={faTasks} className="me-1 text-success" />
                        ID Mission
                      </Form.Label>
                      <Form.Control
                        className="filter-input"
                        placeholder="Ex: 456"
                        value={filters.mission}
                        onChange={(e) => setFilters({ ...filters, mission: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label className="fw-semibold">
                        <FontAwesomeIcon icon={faCalendarAlt} className="me-1 text-warning" />
                        Date de début
                      </Form.Label>
                      <Form.Control
                        className="filter-input"
                        type="date"
                        value={filters.d1}
                        onChange={(e) => setFilters({ ...filters, d1: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label className="fw-semibold">
                        <FontAwesomeIcon icon={faCalendarAlt} className="me-1 text-warning" />
                        Date de fin
                      </Form.Label>
                      <Form.Control
                        className="filter-input"
                        type="date"
                        value={filters.d2}
                        onChange={(e) => setFilters({ ...filters, d2: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                </Row>
                <div className="d-flex justify-content-end filter-buttons">
                  <Button variant="outline-secondary" onClick={resetFilters} className="filter-btn">
                    <FontAwesomeIcon icon={faTimes} className="me-2" />
                    Réinitialiser
                  </Button>
                  <Button variant="primary" onClick={runFilter} className="filter-btn">
                    <FontAwesomeIcon icon={faSearch} className="me-2" /> 
                    Rechercher
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>

          {/* Tableau des plannings amélioré */}
          {loading ? (
            <div className="loading-container">
              <div className="loading-spinner"></div>
              <h5 className="text-muted">Chargement des plannings...</h5>
              <p className="text-muted">Veuillez patienter</p>
            </div>
          ) : plannings.length === 0 ? (
            <div className="empty-state">
              <FontAwesomeIcon icon={faCalendarPlus} className="empty-state-icon" />
              <h4>Aucun planning trouvé</h4>
              <p className="text-muted">
                Créez votre premier planning ou modifiez vos critères de recherche.
              </p>
              <Link to="/plannings/create">
                <Button variant="primary" size="lg">
                  <FontAwesomeIcon icon={faPlus} className="me-2" />
                  Créer un planning
                </Button>
              </Link>
            </div>
          ) : (
            <>
              {viewMode === 'table' ? (
                // Vue tableau
                <div className="planning-table">
                  <Table responsive hover className="align-middle mb-0">
                    <thead className="table-header">
                      <tr>
                        <th className="text-center">ID</th>
                        <th>Date de création</th>
                        <th>Dernière modification</th>
                        <th className="text-center">Agents assignés</th>
                        <th style={{ minWidth: '350px' }}>Missions</th>
                        <th className="text-center">Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {plannings.map((p, index) => {
                        const missionObjs = getMissionObjects(p);
                        const agents = getAgentsForPlanning(p);

                        return (
                          <tr key={p.id} className="table-row planning-row-card fade-in" style={{ animationDelay: `${index * 0.1}s` }}>
                            <td className="text-center">
                              <Badge bg="primary" className="fs-6">#{p.id}</Badge>
                            </td>
                            <td>
                              <div className="d-flex flex-column">
                                <span className="fw-semibold">
                                  {new Date(p.dateCreation).toLocaleDateString('fr-FR')}
                                </span>
                                <small className="text-muted">
                                  {new Date(p.dateCreation).toLocaleTimeString('fr-FR')}
                                </small>
                              </div>
                            </td>
                            <td>
                              <div className="d-flex flex-column">
                                <span className="fw-semibold">
                                  {new Date(p.dateModification).toLocaleDateString('fr-FR')}
                                </span>
                                <small className="text-muted">
                                  {new Date(p.dateModification).toLocaleTimeString('fr-FR')}
                                </small>
                              </div>
                            </td>

                            {/* Agents améliorés */}
                            <td>
                              <div className="text-center">
                                {agents.length === 0 ? (
                                  <Badge className="no-agent-badge">
                                    <FontAwesomeIcon icon={faUsers} className="me-1" />
                                    Aucun agent
                                  </Badge>
                                ) : (
                                  <div>
                                    <div className="mb-2">
                                      <Badge bg="info" className="fs-6">
                                        {agents.length} agent{agents.length > 1 ? 's' : ''}
                                      </Badge>
                                    </div>
                                    <div style={{ maxHeight: '100px', overflowY: 'auto' }}>
                                      {agents.map((a) => (
                                        <OverlayTrigger
                                          key={`agent-${p.id}-${a.id}`}
                                          placement="top"
                                          overlay={<Tooltip>Agent #{a.id}</Tooltip>}
                                        >
                                          <Badge className="agent-badge">
                                            {a.nom} {a.prenom}
                                          </Badge>
                                        </OverlayTrigger>
                                      ))}
                                    </div>
                                  </div>
                                )}
                              </div>
                            </td>

                            {/* Missions considérablement améliorées */}
                            <td>
                              <div className="mission-container">
                                <div className="mb-3">
                                  {missionObjs.length === 0 ? (
                                    <div className="text-center">
                                      <Badge className="no-mission-badge">
                                        <FontAwesomeIcon icon={faTasks} className="me-1" />
                                        Aucune mission assignée
                                      </Badge>
                                    </div>
                                  ) : (
                                    <div>
                                      <div className="d-flex justify-content-between align-items-center mb-2">
                                        <small className="text-muted fw-semibold">
                                          <FontAwesomeIcon icon={faTasks} className="me-1" />
                                          {missionObjs.length} mission{missionObjs.length > 1 ? 's' : ''} assignée{missionObjs.length > 1 ? 's' : ''}
                                        </small>
                                      </div>
                                      {missionObjs.map((m) => (
                                        <div className="mission-item slide-in" key={`mis-${m.id}`}>
                                          <div className="d-flex align-items-center flex-grow-1">
                                            <OverlayTrigger
                                              placement="top"
                                              overlay={
                                                <Tooltip>
                                                  <div>
                                                    <strong>Mission #{m.id}</strong><br/>
                                                    Titre: {m.titre}<br/>
                                                    {m.description && `Description: ${m.description.substring(0, 50)}...`}
                                                  </div>
                                                </Tooltip>
                                              }
                                            >
                                              <Badge className="mission-badge">
                                                <FontAwesomeIcon icon={faTasks} className="me-1" />
                                                {m.titre}
                                              </Badge>
                                            </OverlayTrigger>
                                          </div>
                                          <OverlayTrigger
                                            placement="top"
                                            overlay={<Tooltip>Retirer cette mission</Tooltip>}
                                          >
                                            <Button
                                              className="remove-mission-btn"
                                              size="sm"
                                              onClick={() => removeMission(p.id, m.id)}
                                            >
                                              <FontAwesomeIcon icon={faTimes} />
                                            </Button>
                                          </OverlayTrigger>
                                        </div>
                                      ))}
                                    </div>
                                  )}
                                </div>

                                {/* Sélecteur de mission amélioré */}
                                <div className="mission-selector">
                                  <Form.Select
                                    className="mission-select"
                                    value={selection[p.id] ?? ""}
                                    onChange={(e) =>
                                      setSelection({ ...selection, [p.id]: Number(e.target.value) })
                                    }
                                  >
                                    <option value="">— Ajouter une mission —</option>
                                    {missions
                                      .filter(m => !missionObjs.some(existing => existing.id === m.id))
                                      .map((m) => (
                                        <option key={m.id} value={m.id}>
                                          #{m.id} - {m.titre}
                                        </option>
                                      ))}
                                  </Form.Select>
                                  <OverlayTrigger
                                    placement="top"
                                    overlay={<Tooltip>Ajouter la mission sélectionnée</Tooltip>}
                                  >
                                    <Button 
                                      className="add-mission-btn" 
                                      onClick={() => addMission(p.id)}
                                      disabled={!selection[p.id]}
                                    >
                                      <FontAwesomeIcon icon={faPlus} />
                                    </Button>
                                  </OverlayTrigger>
                                </div>
                              </div>
                            </td>

                            {/* Actions améliorées */}
                            <td>
                              <div className="action-buttons">
                                <OverlayTrigger
                                  placement="top"
                                  overlay={<Tooltip>Voir les détails</Tooltip>}
                                >
                                  <Link to={`/plannings/${p.id}`}>
                                    <Button className="action-btn view-btn" size="sm">
                                      <FontAwesomeIcon icon={faEye} />
                                    </Button>
                                  </Link>
                                </OverlayTrigger>
                                <OverlayTrigger
                                  placement="top"
                                  overlay={<Tooltip>Modifier le planning</Tooltip>}
                                >
                                  <Link to={`/plannings/edit/${p.id}`}>
                                    <Button className="action-btn edit-btn" size="sm">
                                      <FontAwesomeIcon icon={faEdit} />
                                    </Button>
                                  </Link>
                                </OverlayTrigger>
                                <OverlayTrigger
                                  placement="top"
                                  overlay={<Tooltip>Supprimer le planning</Tooltip>}
                                >
                                  <Button 
                                    className="action-btn delete-btn" 
                                    size="sm" 
                                    onClick={() => confirmDelete(p.id)}
                                  >
                                    <FontAwesomeIcon icon={faTrash} />
                                  </Button>
                                </OverlayTrigger>
                              </div>
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </Table>
                </div>
              ) : (
                // Vue cartes
                <Row className="g-4">
                  {plannings.map((p, index) => {
                    const missionObjs = getMissionObjects(p);
                    const agents = getAgentsForPlanning(p);

                    return (
                      <Col key={p.id} lg={6} xl={4} className="fade-in" style={{ animationDelay: `${index * 0.1}s` }}>
                        <Card className="h-100 planning-card-view">
                          <Card.Header className="d-flex justify-content-between align-items-center">
                            <div>
                              <h5 className="mb-0">
                                <Badge bg="primary">Planning #{p.id}</Badge>
                              </h5>
                            </div>
                            <div className="action-buttons">
                              <OverlayTrigger
                                placement="top"
                                overlay={<Tooltip>Voir les détails</Tooltip>}
                              >
                                <Link to={`/plannings/${p.id}`}>
                                  <Button className="action-btn view-btn" size="sm">
                                    <FontAwesomeIcon icon={faEye} />
                                  </Button>
                                </Link>
                              </OverlayTrigger>
                              <OverlayTrigger
                                placement="top"
                                overlay={<Tooltip>Modifier</Tooltip>}
                              >
                                <Link to={`/plannings/edit/${p.id}`}>
                                  <Button className="action-btn edit-btn" size="sm">
                                    <FontAwesomeIcon icon={faEdit} />
                                  </Button>
                                </Link>
                              </OverlayTrigger>
                              <OverlayTrigger
                                placement="top"
                                overlay={<Tooltip>Supprimer</Tooltip>}
                              >
                                <Button 
                                  className="action-btn delete-btn" 
                                  size="sm" 
                                  onClick={() => confirmDelete(p.id)}
                                >
                                  <FontAwesomeIcon icon={faTrash} />
                                </Button>
                              </OverlayTrigger>
                            </div>
                          </Card.Header>
                          
                          <Card.Body>
                            {/* Informations de dates */}
                            <div className="mb-3">
                              <small className="text-muted d-block">
                                <FontAwesomeIcon icon={faCalendarAlt} className="me-1" />
                                Créé le {new Date(p.dateCreation).toLocaleDateString('fr-FR')}
                              </small>
                              <small className="text-muted d-block">
                                <FontAwesomeIcon icon={faCalendarAlt} className="me-1" />
                                Modifié le {new Date(p.dateModification).toLocaleDateString('fr-FR')}
                              </small>
                            </div>

                            {/* Agents */}
                            <div className="mb-3">
                              <h6 className="mb-2">
                                <FontAwesomeIcon icon={faUsers} className="me-1 text-info" />
                                Agents ({agents.length})
                              </h6>
                              {agents.length === 0 ? (
                                <Badge className="no-agent-badge">
                                  Aucun agent assigné
                                </Badge>
                              ) : (
                                <div>
                                  {agents.map((a) => (
                                    <Badge key={`agent-card-${p.id}-${a.id}`} className="agent-badge">
                                      {a.nom} {a.prenom}
                                    </Badge>
                                  ))}
                                </div>
                              )}
                            </div>

                            {/* Missions */}
                            <div className="mb-3">
                              <h6 className="mb-2">
                                <FontAwesomeIcon icon={faTasks} className="me-1 text-success" />
                                Missions ({missionObjs.length})
                              </h6>
                              {missionObjs.length === 0 ? (
                                <Badge className="no-mission-badge">
                                  Aucune mission assignée
                                </Badge>
                              ) : (
                                <div className="missions-list" style={{ maxHeight: '200px', overflowY: 'auto' }}>
                                  {missionObjs.map((m) => (
                                    <div key={`mission-card-${m.id}`} className="d-flex justify-content-between align-items-center mb-2 p-2 bg-light rounded">
                                      <div>
                                        <Badge className="mission-badge">
                                          <FontAwesomeIcon icon={faTasks} className="me-1" />
                                          {m.titre}
                                        </Badge>
                                        {m.description && (
                                          <div>
                                            <small className="text-muted">
                                              {m.description.substring(0, 50)}...
                                            </small>
                                          </div>
                                        )}
                                      </div>
                                      <Button
                                        className="remove-mission-btn"
                                        size="sm"
                                        onClick={() => removeMission(p.id, m.id)}
                                      >
                                        <FontAwesomeIcon icon={faTimes} />
                                      </Button>
                                    </div>
                                  ))}
                                </div>
                              )}
                            </div>

                            {/* Ajouter une mission */}
                            <div className="mission-selector">
                              <Form.Select
                                className="mission-select mb-2"
                                value={selection[p.id] ?? ""}
                                onChange={(e) =>
                                  setSelection({ ...selection, [p.id]: Number(e.target.value) })
                                }
                              >
                                <option value="">— Ajouter une mission —</option>
                                {missions
                                  .filter(m => !missionObjs.some(existing => existing.id === m.id))
                                  .map((m) => (
                                    <option key={m.id} value={m.id}>
                                      #{m.id} - {m.titre}
                                    </option>
                                  ))}
                              </Form.Select>
                              <Button 
                                className="add-mission-btn w-100" 
                                onClick={() => addMission(p.id)}
                                disabled={!selection[p.id]}
                              >
                                <FontAwesomeIcon icon={faPlus} className="me-2" />
                                Ajouter la mission
                              </Button>
                            </div>
                          </Card.Body>
                        </Card>
                      </Col>
                    );
                  })}
                </Row>
              )}
            </>
          )}
        </Card.Body>
      </Card>

      {/* Modal de confirmation de suppression amélioré */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} className="custom-modal">
        <Modal.Header closeButton>
          <Modal.Title>
            <FontAwesomeIcon icon={faExclamationTriangle} className="me-2 text-warning" />
            Confirmation de suppression
          </Modal.Title>
        </Modal.Header>
        <Modal.Body className="text-center py-4">
          <FontAwesomeIcon icon={faTrash} className="text-danger mb-3" size="3x" />
          <h5>Êtes-vous sûr de vouloir supprimer ce planning ?</h5>
          <p className="text-muted">
            Cette action est irréversible et supprimera définitivement le planning #{planningToDelete}.
          </p>
        </Modal.Body>
        <Modal.Footer className="justify-content-center">
          <Button variant="outline-secondary" onClick={() => setShowDeleteModal(false)}>
            <FontAwesomeIcon icon={faTimes} className="me-2" />
            Annuler
          </Button>
          <Button variant="danger" onClick={deletePlanning}>
            <FontAwesomeIcon icon={faTrash} className="me-2" />
            Supprimer définitivement
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}
