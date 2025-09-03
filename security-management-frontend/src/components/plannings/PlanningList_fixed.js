// src/components/plannings/PlanningList.js
import { useEffect, useState, useMemo } from "react";
import { Link } from "react-router-dom";
import PlanningService from "../../services/PlanningService";
import MissionService from "../../services/MissionService";
import AgentService from "../../services/AgentService";
import {
  Table, Button, Container, Row, Col, Form, Card, Badge, InputGroup, Alert, Modal
} from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCalendarPlus, faEye, faEdit, faTrash, faPlus, faTimes, faSearch, faFilter
} from "@fortawesome/free-solid-svg-icons";

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
    <Container fluid className="py-4">
      <Card className="shadow">
        <Card.Header className="bg-primary text-white">
          <h2 className="mb-0 d-flex justify-content-between align-items-center">
            <span>
              <FontAwesomeIcon icon={faCalendarPlus} className="me-2" /> Plannings
            </span>
            <Link to="/plannings/create">
              <Button variant="light">
                <FontAwesomeIcon icon={faPlus} className="me-1" /> Nouveau planning
              </Button>
            </Link>
          </h2>
        </Card.Header>

        <Card.Body>
          {error && (
            <Alert variant="danger" dismissible onClose={() => setError("")}>
              {error}
            </Alert>
          )}

          {/* Filtres */}
          <Card className="mb-4 border-light">
            <Card.Header className="bg-light">
              <FontAwesomeIcon icon={faFilter} className="me-2" /> Filtres
            </Card.Header>
            <Card.Body>
              <Form>
                <Row className="mb-3">
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>ID Agent</Form.Label>
                      <Form.Control
                        placeholder="Identifiant de l'agent"
                        value={filters.agent}
                        onChange={(e) => setFilters({ ...filters, agent: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>ID Mission</Form.Label>
                      <Form.Control
                        placeholder="Identifiant de la mission"
                        value={filters.mission}
                        onChange={(e) => setFilters({ ...filters, mission: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>Date de début</Form.Label>
                      <Form.Control
                        type="date"
                        value={filters.d1}
                        onChange={(e) => setFilters({ ...filters, d1: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={3}>
                    <Form.Group>
                      <Form.Label>Date de fin</Form.Label>
                      <Form.Control
                        type="date"
                        value={filters.d2}
                        onChange={(e) => setFilters({ ...filters, d2: e.target.value })}
                      />
                    </Form.Group>
                  </Col>
                </Row>
                <div className="d-flex justify-content-end gap-2">
                  <Button variant="secondary" onClick={resetFilters}>
                    Réinitialiser
                  </Button>
                  <Button variant="primary" onClick={runFilter}>
                    <FontAwesomeIcon icon={faSearch} className="me-2" /> Rechercher
                  </Button>
                </div>
              </Form>
            </Card.Body>
          </Card>

          {/* Tableau des plannings */}
          {loading ? (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">Chargement...</span>
              </div>
              <p className="mt-2">Chargement des plannings...</p>
            </div>
          ) : plannings.length === 0 ? (
            <Alert variant="info">Aucun planning trouvé. Créez-en un nouveau ou modifiez vos filtres.</Alert>
          ) : (
            <Table responsive hover striped className="align-middle">
              <thead className="table-light">
                <tr>
                  <th>ID</th>
                  <th>Créé le</th>
                  <th>Dernière modification</th>
                  <th>Agents</th>
                  <th>Missions</th>
                  <th className="text-center">Actions</th>
                </tr>
              </thead>
              <tbody>
                {plannings.map((p) => {
                  const missionObjs = getMissionObjects(p);
                  const agents = getAgentsForPlanning(p);

                  return (
                    <tr key={p.id}>
                      <td>{p.id}</td>
                      <td>{new Date(p.dateCreation).toLocaleString()}</td>
                      <td>{new Date(p.dateModification).toLocaleString()}</td>

                      {/* Agents */}
                      <td>
                        {agents.length === 0 ? (
                          <Badge bg="warning">Aucun agent</Badge>
                        ) : (
                          agents.map((a) => (
                            <Badge bg="info" className="me-1 mb-1" key={`agent-${p.id}-${a.id}`}>
                              {a.nom} {a.prenom}
                            </Badge>
                          ))
                        )}
                      </td>

                      {/* Missions */}
                      <td>
                        <div className="mb-2">
                          {missionObjs.length === 0 ? (
                            <Badge bg="warning">Aucune mission</Badge>
                          ) : (
                            missionObjs.map((m) => (
                              <div className="d-flex align-items-center mb-1" key={`mis-${m.id}`}>
                                <Badge bg="success" className="me-2">{m.titre}</Badge>
                                <Button
                                  variant="danger"
                                  size="sm"
                                  onClick={() => removeMission(p.id, m.id)}
                                >
                                  <FontAwesomeIcon icon={faTimes} />
                                </Button>
                              </div>
                            ))
                          )}
                        </div>

                        <InputGroup size="sm">
                          <Form.Select
                            value={selection[p.id] ?? ""}
                            onChange={(e) =>
                              setSelection({ ...selection, [p.id]: Number(e.target.value) })
                            }
                          >
                            <option value="">— Sélectionner une mission —</option>
                            {missions.map((m) => (
                              <option key={m.id} value={m.id}>
                                {m.titre}
                              </option>
                            ))}
                          </Form.Select>
                          <Button variant="outline-primary" onClick={() => addMission(p.id)}>
                            <FontAwesomeIcon icon={faPlus} />
                          </Button>
                        </InputGroup>
                      </td>

                      {/* Actions */}
                      <td>
                        <div className="d-flex justify-content-center gap-2">
                          <Link to={`/plannings/${p.id}`}>
                            <Button variant="info" size="sm">
                              <FontAwesomeIcon icon={faEye} />
                            </Button>
                          </Link>
                          <Link to={`/plannings/edit/${p.id}`}>
                            <Button variant="warning" size="sm">
                              <FontAwesomeIcon icon={faEdit} />
                            </Button>
                          </Link>
                          <Button variant="danger" size="sm" onClick={() => confirmDelete(p.id)}>
                            <FontAwesomeIcon icon={faTrash} />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </Table>
          )}
        </Card.Body>
      </Card>

      {/* Modal de confirmation de suppression */}
      <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Confirmation de suppression</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          Êtes-vous sûr de vouloir supprimer ce planning ? Cette action est irréversible.
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
            Annuler
          </Button>
          <Button variant="danger" onClick={deletePlanning}>
            Supprimer
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
}
