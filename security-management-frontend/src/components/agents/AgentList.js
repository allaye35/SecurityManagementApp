// src/components/agents/AgentList.js
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { 
    Container, Row, Col, Table, Button, Card, Form, 
    InputGroup, Badge, Dropdown, Modal, Alert, Spinner
} from "react-bootstrap";
import { 
    BsSearch, BsPersonPlus, BsEye, BsPencil, BsTrash, 
    BsPersonBadge, BsCalendarCheck, BsShield, BsDiagram2, 
    BsGeoAlt, BsCreditCard2Front, BsAward, BsThreeDots,
    BsCheckCircle
} from "react-icons/bs";
import AgentService from "../../services/AgentService";
import adminAccountsService from "../../services/adminAccountsService";
import "../../styles/AgentList.css";

const AgentList = () => {
    const [agents, setAgents] = useState([]);
    const [filteredAgents, setFilteredAgents] = useState([]);
    const [filter, setFilter] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [showRoleModal, setShowRoleModal] = useState(false);
    const [selectedAgent, setSelectedAgent] = useState(null);
    const [selectedRole, setSelectedRole] = useState("");
    const [showConfirmDelete, setShowConfirmDelete] = useState(false);
    const [message, setMessage] = useState({ text: "", type: "" });
    const [showConfirmValidation, setShowConfirmValidation] = useState(false);
    // États pour la pagination
    const [currentPage, setCurrentPage] = useState(1);
    const agentsPerPage = 10;

    // Options de rôles
    const roles = ["AGENT_SECURITE", "CHEF_EQUIPE", "MANAGER", "ADMIN"];

    const loadAgents = () => {
        setLoading(true);
        AgentService.getAllAgents()
            .then(res => {
                console.log("Agents data received:", res.data); // Debug temporaire
                // Debug pour voir la structure d'un agent
                if (res.data.length > 0) {
                    console.log("Premier agent:", res.data[0]);
                }
                setAgents(res.data);
                setFilteredAgents(res.data);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur lors du chargement des agents:", err);
                setError("Impossible de charger la liste des agents.");
                setLoading(false);
            });
    };

    useEffect(() => {
        loadAgents();
    }, []);

    useEffect(() => {
        if (filter) {
            const term = filter.toLowerCase();
            const filtered = agents.filter(a => 
                a.nom?.toLowerCase().includes(term) ||
                a.prenom?.toLowerCase().includes(term) ||
                a.email?.toLowerCase().includes(term) ||
                a.telephone?.toLowerCase().includes(term) ||
                a.statut?.toLowerCase().includes(term) ||
                a.role?.toLowerCase().includes(term)
            );
            setFilteredAgents(filtered);
        } else {
            setFilteredAgents(agents);
        }
        // Réinitialiser à la première page lors du filtrage
        setCurrentPage(1);
    }, [filter, agents]);

    const handleDeleteAgent = (agent) => {
        setSelectedAgent(agent);
        setShowConfirmDelete(true);
    };

    const confirmDelete = () => {
        AgentService.deleteAgent(selectedAgent.id)
            .then(() => {
                setAgents(agents.filter(a => a.id !== selectedAgent.id));
                setMessage({ text: `Agent ${selectedAgent.nom} ${selectedAgent.prenom} supprimé avec succès`, type: "success" });
                setShowConfirmDelete(false);
            })
            .catch(err => {
                console.error("Erreur lors de la suppression:", err);
                setMessage({ text: "Une erreur est survenue lors de la suppression", type: "danger" });
                setShowConfirmDelete(false);
            });
    };

    const handleChangeRole = (agent) => {
        setSelectedAgent(agent);
        setSelectedRole(agent.role);
        setShowRoleModal(true);
    };

    const confirmChangeRole = () => {
        AgentService.changeRole(selectedAgent.id, selectedRole)
            .then(res => {
                const updatedAgents = agents.map(a => 
                    a.id === selectedAgent.id ? res.data : a
                );
                setAgents(updatedAgents);
                setMessage({ text: `Rôle modifié avec succès pour ${selectedAgent.nom}`, type: "success" });
                setShowRoleModal(false);
            })
            .catch(err => {
                console.error("Erreur lors du changement de rôle:", err);
                setMessage({ text: "Une erreur est survenue lors du changement de rôle", type: "danger" });
                setShowRoleModal(false);
            });
    };

    const handleValidateAccount = (agent) => {
        setSelectedAgent(agent);
        setShowConfirmValidation(true);
    };

    const confirmValidateAccount = () => {
        adminAccountsService.approveAgent(selectedAgent.id)
            .then(() => {
                // Recharger la liste des agents après validation
                loadAgents();
                setMessage({ 
                    text: `Compte de ${selectedAgent.nom} ${selectedAgent.prenom} validé avec succès`, 
                    type: "success" 
                });
                setShowConfirmValidation(false);
            })
            .catch(err => {
                console.error("Erreur lors de la validation du compte:", err);
                setMessage({ 
                    text: "Une erreur est survenue lors de la validation du compte", 
                    type: "danger" 
                });
                setShowConfirmValidation(false);
            });
    };

    const formatDate = (dateString) => {
        if (!dateString) return "–";
        const date = new Date(dateString);
        return new Intl.DateTimeFormat('fr-FR').format(date);
    };

    const getStatusBadge = (agent) => {
        console.log(`Agent ${agent.nom} - adminApproved:`, agent.adminApproved, typeof agent.adminApproved); // Debug
        
        // Si le compte n'est pas approuvé par l'admin
        // Considérer false, 0, null, undefined comme non approuvé
        const isApproved = agent.adminApproved === true || agent.adminApproved === 1;
        
        if (!isApproved) {
            return <Badge bg="warning">En attente validation admin</Badge>;
        }
        
        // Si approuvé, utiliser le statut normal
        const status = agent.statut;
        if (!status) return <Badge bg="secondary">Non défini</Badge>;
        
        const statusColors = {
            EN_SERVICE: "success",
            EN_CONGE: "info",
            ABSENT: "warning",
            SUSPENDU: "danger",
            HORS_SERVICE: "secondary",
            "Non défini": "secondary"
        };
        
        const statusLabels = {
            EN_SERVICE: "EN SERVICE",
            EN_CONGE: "EN CONGE",
            ABSENT: "ABSENT",
            SUSPENDU: "SUSPENDU",
            HORS_SERVICE: "HORS SERVICE",
            "Non défini": "Non défini"
        };
        
        return (
            <Badge bg={statusColors[status] || "secondary"}>
                {statusLabels[status] || status}
            </Badge>
        );
    };

    const getRoleBadge = (role) => {
        if (!role) return <Badge bg="secondary">Non défini</Badge>;
        
        const roleColors = {
            AGENT_SECURITE: "primary",
            CHEF_EQUIPE: "success",
            MANAGER: "warning",
            ADMIN: "danger"
        };
        
        return (
            <Badge bg={roleColors[role] || "secondary"}>
                {role.replace("_", " ")}
            </Badge>
        );
    };

    if (error) return <Alert variant="danger">{error}</Alert>;

    // Calculs pour la pagination
    const indexOfLastAgent = currentPage * agentsPerPage;
    const indexOfFirstAgent = indexOfLastAgent - agentsPerPage;
    const currentAgents = filteredAgents.slice(indexOfFirstAgent, indexOfLastAgent);
    const totalPages = Math.ceil(filteredAgents.length / agentsPerPage);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
    };

    return (
        <Container fluid className="agent-list-container my-4">
            {/* En-tête avec titre et boutons d'action */}
            <Card className="mb-4 border-0 shadow-sm">
                <Card.Body>
                    <Row className="align-items-center mb-3">
                        <Col>
                            <h2 className="text-primary mb-0">
                                <BsShield className="me-2" />
                                Liste des Agents de Sécurité
                            </h2>
                        </Col>
                        <Col xs="auto">
                            <Link to="/agents/create">
                                <Button variant="primary" className="d-flex align-items-center">
                                    <BsPersonPlus className="me-2" /> Nouvel Agent
                                </Button>
                            </Link>
                        </Col>
                    </Row>
                    
                    {/* Barre de recherche */}
                    <Row>
                        <Col md={6} lg={4}>
                            <InputGroup>
                                <InputGroup.Text>
                                    <BsSearch />
                                </InputGroup.Text>
                                <Form.Control
                                    type="text" 
                                    placeholder="Rechercher par nom, email, statut..."
                                    value={filter}
                                    onChange={(e) => setFilter(e.target.value)}
                                />
                            </InputGroup>
                        </Col>
                        <Col>
                            {message.text && (
                                <Alert 
                                    variant={message.type} 
                                    dismissible 
                                    onClose={() => setMessage({text: "", type: ""})}
                                >
                                    {message.text}
                                </Alert>
                            )}
                        </Col>
                    </Row>
                </Card.Body>
            </Card>

            {/* Tableau des agents */}
            <Card className="shadow-sm">
                <Card.Body className="p-0">
                    {loading ? (
                        <div className="text-center py-5">
                            <Spinner animation="border" variant="primary" />
                            <p className="mt-2">Chargement des agents...</p>
                        </div>
                    ) : (
                        <div className="table-responsive">
                            <Table hover className="agent-table mb-0">
                                <thead className="bg-light">
                                    <tr>
                                        <th className="text-center">#</th>
                                        <th>Nom / Prénom</th>
                                        <th>Contact</th>
                                        <th>Statut</th>
                                        <th>Rôle</th>
                                        <th>Zones</th>
                                        <th>Documents</th>
                                        <th className="text-center">Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {currentAgents.map((agent, index) => (
                                        <tr key={agent.id}>
                                            <td className="text-center">{indexOfFirstAgent + index + 1}</td>
                                            <td>
                                                <div className="d-flex align-items-center">
                                                    <div className="avatar-circle">
                                                        {agent.prenom?.[0]}{agent.nom?.[0]}
                                                    </div>
                                                    <div className="ms-3">
                                                        <div className="fw-bold">{agent.nom} {agent.prenom}</div>
                                                        <small className="text-muted">
                                                            Né(e) le: {formatDate(agent.dateNaissance)}
                                                        </small>
                                                    </div>
                                                </div>
                                            </td>
                                            <td>
                                                <div><small><i className="bi bi-envelope"></i> {agent.email}</small></div>
                                                <div><small><i className="bi bi-telephone"></i> {agent.telephone || "–"}</small></div>
                                            </td>
                                            <td>{getStatusBadge(agent)}</td>
                                            <td>{getRoleBadge(agent.role)}</td>
                                            <td className="text-center">
                                                {(agent.zonesDeTravailIds?.length > 0) ? (
                                                    <Badge bg="info" pill>{agent.zonesDeTravailIds.length}</Badge>
                                                ) : (
                                                    <Badge bg="light" text="dark" pill>0</Badge>
                                                )}
                                            </td>
                                            <td>
                                                <div className="d-flex gap-2">
                                                    <Badge bg={agent.diplomesSSIAPIds?.length > 0 ? "success" : "light"} 
                                                           text={agent.diplomesSSIAPIds?.length > 0 ? "white" : "dark"} 
                                                           title="Diplômes SSIAP">
                                                        <BsAward className="me-1" />
                                                        {agent.diplomesSSIAPIds?.length || 0}
                                                    </Badge>
                                                    <Badge bg={agent.cartesProfessionnelles?.length > 0 ? "primary" : "light"}
                                                           text={agent.cartesProfessionnelles?.length > 0 ? "white" : "dark"}
                                                           title="Cartes Pro">
                                                        <BsCreditCard2Front className="me-1" />
                                                        {agent.cartesProfessionnelles?.length || 0}
                                                    </Badge>
                                                </div>
                                            </td>
                                            <td>
                                                <div className="d-flex justify-content-center gap-1">
                                                    <Link to={`/agents/${agent.id}`}>
                                                        <Button variant="outline-primary" size="sm" title="Voir le détail">
                                                            <BsEye />
                                                        </Button>
                                                    </Link>
                                                    <Link to={`/agents/edit/${agent.id}`}>
                                                        <Button variant="outline-secondary" size="sm" title="Modifier">
                                                            <BsPencil />
                                                        </Button>
                                                    </Link>
                                                    <Dropdown>
                                                        <Dropdown.Toggle variant="outline-dark" size="sm" id={`dropdown-${agent.id}`}>
                                                            <BsThreeDots />
                                                        </Dropdown.Toggle>
                                                        <Dropdown.Menu>
                                                            <Dropdown.Item onClick={() => handleChangeRole(agent)}>
                                                                <BsPersonBadge className="me-2" />Changer le rôle
                                                            </Dropdown.Item>
                                                            {(agent.adminApproved !== true && agent.adminApproved !== 1) && (
                                                                <Dropdown.Item onClick={() => handleValidateAccount(agent)}>
                                                                    <BsCheckCircle className="me-2" />Valider le compte
                                                                </Dropdown.Item>
                                                            )}
                                                            <Link to={`/agents/${agent.id}/planning`} className="dropdown-item">
                                                                <BsCalendarCheck className="me-2" />Voir le planning
                                                            </Link>
                                                            <Dropdown.Item onClick={() => handleDeleteAgent(agent)}>
                                                                <BsTrash className="me-2" />Supprimer
                                                            </Dropdown.Item>
                                                        </Dropdown.Menu>
                                                    </Dropdown>
                                                </div>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </Table>
                            
                            {filteredAgents.length === 0 && !loading && (
                                <div className="text-center py-5">
                                    <p className="text-muted">Aucun agent ne correspond à votre recherche</p>
                                </div>
                            )}
                        </div>
                    )}
                </Card.Body>
                
                {/* Pagination */}
                {totalPages > 1 && (
                    <Card.Footer>
                        <div className="d-flex justify-content-between align-items-center">
                            <div className="text-muted">
                                Affichage de {indexOfFirstAgent + 1} à {Math.min(indexOfLastAgent, filteredAgents.length)} sur {filteredAgents.length} agent(s)
                            </div>
                            <div className="d-flex justify-content-center align-items-center">
                                <Button
                                    variant="outline-primary"
                                    size="sm"
                                    className="me-2"
                                    disabled={currentPage === 1}
                                    onClick={() => handlePageChange(currentPage - 1)}
                                >
                                    Précédent
                                </Button>
                                {[...Array(totalPages)].map((_, idx) => (
                                    <Button
                                        key={idx + 1}
                                        variant={currentPage === idx + 1 ? "primary" : "outline-primary"}
                                        size="sm"
                                        className="mx-1"
                                        onClick={() => handlePageChange(idx + 1)}
                                    >
                                        {idx + 1}
                                    </Button>
                                ))}
                                <Button
                                    variant="outline-primary"
                                    size="sm"
                                    className="ms-2"
                                    disabled={currentPage === totalPages}
                                    onClick={() => handlePageChange(currentPage + 1)}
                                >
                                    Suivant
                                </Button>
                            </div>
                        </div>
                    </Card.Footer>
                )}
                
                {/* Footer sans pagination si une seule page */}
                {totalPages <= 1 && (
                    <Card.Footer className="d-flex justify-content-between align-items-center">
                        <div className="text-muted">
                            Total: {filteredAgents.length} agent(s)
                        </div>
                    </Card.Footer>
                )}
            </Card>

            {/* Modal pour changer le rôle */}
            <Modal show={showRoleModal} onHide={() => setShowRoleModal(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Changer le rôle</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedAgent && (
                        <>
                            <p>Changer le rôle de l'agent: <strong>{selectedAgent.nom} {selectedAgent.prenom}</strong></p>
                            <Form.Group>
                                <Form.Label>Sélectionner un rôle</Form.Label>
                                <Form.Select
                                    value={selectedRole}
                                    onChange={(e) => setSelectedRole(e.target.value)}
                                >
                                    {roles.map(role => (
                                        <option key={role} value={role}>{role.replace("_", " ")}</option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowRoleModal(false)}>
                        Annuler
                    </Button>
                    <Button variant="primary" onClick={confirmChangeRole}>
                        Confirmer
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Modal de confirmation de suppression */}
            <Modal show={showConfirmDelete} onHide={() => setShowConfirmDelete(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Confirmer la suppression</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedAgent && (
                        <p>
                            Êtes-vous sûr de vouloir supprimer l'agent
                            <strong> {selectedAgent.nom} {selectedAgent.prenom} </strong>?
                            Cette action est irréversible.
                        </p>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowConfirmDelete(false)}>
                        Annuler
                    </Button>
                    <Button variant="danger" onClick={confirmDelete}>
                        Supprimer
                    </Button>
                </Modal.Footer>
            </Modal>

            {/* Modal de confirmation de validation de compte */}
            <Modal show={showConfirmValidation} onHide={() => setShowConfirmValidation(false)}>
                <Modal.Header closeButton>
                    <Modal.Title>Valider le compte</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    {selectedAgent && (
                        <div>
                            <p>
                                Êtes-vous sûr de vouloir valider le compte de l'agent
                                <strong> {selectedAgent.nom} {selectedAgent.prenom} </strong>?
                            </p>
                            <p className="text-info">
                                <small>
                                    <strong>Note:</strong> Cette action activera définitivement son compte et lui permettra d'accéder à l'application. 
                                    L'agent pourra alors se connecter avec ses identifiants.
                                </small>
                            </p>
                        </div>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowConfirmValidation(false)}>
                        Annuler
                    </Button>
                    <Button variant="success" onClick={confirmValidateAccount}>
                        Valider le compte
                    </Button>
                </Modal.Footer>
            </Modal>        </Container>
    );
};

export default AgentList;
