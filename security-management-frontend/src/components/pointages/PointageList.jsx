import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import PointageService from "../../services/PointageService";
import { 
    Container, Row, Col, Card, Table, Button, Badge, 
    Spinner, Alert, Form, InputGroup, OverlayTrigger, 
    Tooltip, Nav, Pagination
} from "react-bootstrap";
import { 
    FaUserClock, FaPlus, FaSearch, FaFilter, FaEye, 
    FaPencilAlt, FaTrashAlt, FaExclamationTriangle,
    FaCalendarAlt, FaCheck, FaTimes, FaClock, FaMapMarkerAlt,
    FaSignInAlt, FaSignOutAlt, FaUser, FaBriefcase, FaBuilding
} from "react-icons/fa";

export default function PointageList() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showNotification, setShowNotification] = useState(false);
    const [notification, setNotification] = useState({ message: "", variant: "success" });
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage, setItemsPerPage] = useState(5); 
    const navigate = useNavigate();

    const loadData = () => {
        setLoading(true);
        setError("");
        PointageService.getAll()
            .then(({ data }) => {
                console.log("Pointages chargés:", data);
                
                if (data && data.length > 0) {
                    console.log("Structure du premier pointage:", data[0]);
                    console.log("Mission du premier pointage:", data[0].mission);
                    console.log("Latitude:", data[0].latitude, "Longitude:", data[0].longitude);
                }
                setItems(data);
                setLoading(false);
            })
            .catch((err) => {
                console.error("Erreur lors du chargement des pointages:", err);
                console.error("Détails de l'erreur:", err.response);
                const errorMessage = err.response?.data?.message || err.message || "Erreur de chargement des pointages";
                setError(errorMessage);
                setLoading(false);
                showTemporaryNotification(`Erreur: ${errorMessage}`, 'danger');
            });
    };

    useEffect(() => {
        loadData();
    }, []);

    const showTemporaryNotification = (message, variant = 'success') => {
        setNotification({ message, variant });
        setShowNotification(true);
        setTimeout(() => setShowNotification(false), 5000);
    };

    const handleDelete = (id) => {
        if (!window.confirm("Êtes-vous sûr de vouloir supprimer ce pointage ?")) return;
        
        PointageService.delete(id)
            .then(() => {
                setItems(prev => prev.filter(x => x.id !== id));
                showTemporaryNotification("Pointage supprimé avec succès", "success");
            })
            .catch(() => showTemporaryNotification("Échec de la suppression", "danger"));
    };

    const filteredItems = (items || []).filter(p => {
        if (!p) return false;
        try {
            
            const agentName = p.agentNom && p.agentPrenom 
                ? `${p.agentPrenom} ${p.agentNom}`.trim() 
                : (p.agent ? `${p.agent.prenom || ''} ${p.agent.nom || ''}`.trim() : '');
            
            const missionInfo = p.missionTitre || (p.mission ? `${p.mission.titre || ''} ${p.mission.description || ''}` : '');
            const siteInfo = p.mission?.site ? `${p.mission.site.nom || ''} ${p.mission.site.adresse || ''}` : '';
            
            const searchString = `${p.id || ''} ${agentName} ${p.datePointage ? new Date(p.datePointage).toLocaleString() : ''} ${missionInfo} ${siteInfo}`
                .toLowerCase();
            return searchTerm === '' || searchString.includes(searchTerm.toLowerCase());
        } catch (error) {
            console.error("Erreur lors du filtrage du pointage:", p, error);
            return false;
        }
    });

const totalPages = Math.ceil(filteredItems.length / itemsPerPage);
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems = filteredItems.slice(indexOfFirstItem, indexOfLastItem);

useEffect(() => {
        setCurrentPage(1);
    }, [searchTerm]);

    const handlePageChange = (pageNumber) => {
        setCurrentPage(pageNumber);
        window.scrollTo({ top: 0, behavior: 'smooth' });
    };

    const handleItemsPerPageChange = (e) => {
        setItemsPerPage(parseInt(e.target.value));
        setCurrentPage(1);
    };

const generatePaginationItems = () => {
        const items = [];
        const maxPagesToShow = 5;
        
        if (totalPages <= maxPagesToShow) {
            for (let i = 1; i <= totalPages; i++) {
                items.push(i);
            }
        } else {
            if (currentPage <= 3) {
                for (let i = 1; i <= 4; i++) items.push(i);
                items.push('...');
                items.push(totalPages);
            } else if (currentPage >= totalPages - 2) {
                items.push(1);
                items.push('...');
                for (let i = totalPages - 3; i <= totalPages; i++) items.push(i);
            } else {
                items.push(1);
                items.push('...');
                for (let i = currentPage - 1; i <= currentPage + 1; i++) items.push(i);
                items.push('...');
                items.push(totalPages);
            }
        }
        
        return items;
    };

    const renderTooltip = (text) => (
        <Tooltip id="button-tooltip">
            {text}
        </Tooltip>
    );

    return (
        <Container fluid className="py-4" style={{ maxWidth: '1900px', background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)', minHeight: '100vh' }}>
            {}
            {showNotification && (
                <Alert 
                    variant={notification.variant} 
                    className="position-fixed top-0 end-0 m-4 shadow-lg alert-dismissible fade show" 
                    style={{ 
                        zIndex: 1050, 
                        maxWidth: '450px', 
                        animation: 'fadeIn 0.5s', 
                        fontSize: '1.15rem',
                        borderRadius: '15px',
                        border: 'none'
                    }}
                    onClose={() => setShowNotification(false)}
                    dismissible
                >
                    <div className="d-flex align-items-center p-2">
                        {notification.variant === 'success' && <FaCheck className="me-3" size={24} />}
                        {notification.variant === 'danger' && <FaExclamationTriangle className="me-3" size={24} />}
                        <strong>{notification.message}</strong>
                    </div>
                </Alert>
            )}
            
            <Card className="shadow-xl border-0" style={{ borderRadius: '20px', overflow: 'hidden' }}>
                <Card.Header className="text-white py-4" style={{ 
                    background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                    borderBottom: '4px solid rgba(255,255,255,0.3)'
                }}>
                    <Row className="align-items-center">
                        <Col>
                            <div className="d-flex align-items-center">
                                <div className="bg-white bg-opacity-20 rounded-circle p-3 me-3" style={{ backdropFilter: 'blur(10px)' }}>
                                    <FaUserClock size={36} />
                                </div>
                                <div>
                                    <h2 className="mb-0 fw-bold" style={{ fontSize: '2rem', letterSpacing: '0.5px' }}>
                                        GESTION DES POINTAGES
                                    </h2>
                                    <small className="opacity-75" style={{ fontSize: '0.95rem' }}>
                                        Suivi et contrôle des présences
                                    </small>
                                </div>
                            </div>
                        </Col>
                        <Col xs="auto" className="d-flex gap-3 flex-wrap">
                            <Link 
                                to="/pointages/create?mode=prise" 
                                className="btn btn-success fw-bold shadow-lg px-4 py-3 position-relative overflow-hidden" 
                                style={{ 
                                    fontSize: '1.15rem', 
                                    transition: 'all 0.3s ease',
                                    borderRadius: '12px',
                                    border: 'none',
                                    background: 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)'
                                }}
                            >
                                <FaSignInAlt className="me-2" size={20} /> Prise de Service
                                <span className="position-absolute top-0 end-0 translate-middle badge rounded-pill bg-white text-success" style={{ fontSize: '0.65rem' }}>
                                    Entrée
                                </span>
                            </Link>
                            <Link 
                                to="/pointages/create?mode=fin" 
                                className="btn btn-danger fw-bold shadow-lg px-4 py-3 position-relative overflow-hidden" 
                                style={{ 
                                    fontSize: '1.15rem', 
                                    transition: 'all 0.3s ease',
                                    borderRadius: '12px',
                                    border: 'none',
                                    background: 'linear-gradient(135deg, #eb3349 0%, #f45c43 100%)'
                                }}
                            >
                                <FaSignOutAlt className="me-2" size={20} /> Fin de Service
                                <span className="position-absolute top-0 end-0 translate-middle badge rounded-pill bg-white text-danger" style={{ fontSize: '0.65rem' }}>
                                    Sortie
                                </span>
                            </Link>
                        </Col>
                    </Row>
                </Card.Header>

                <Card.Body className="p-0" style={{ background: '#ffffff' }}>
                    <div className="p-5">
                        {error && (
                            <Alert variant="danger" className="mb-4 d-flex align-items-center py-4 shadow-sm" style={{ 
                                fontSize: '1.15rem',
                                borderRadius: '12px',
                                border: '2px solid #dc3545',
                                background: 'linear-gradient(135deg, #fff5f5 0%, #ffe0e0 100%)'
                            }}>
                                <div className="bg-danger bg-opacity-10 rounded-circle p-3 me-3">
                                    <FaExclamationTriangle className="text-danger" size={28} />
                                </div>
                                <strong>{error}</strong>
                            </Alert>
                        )}
                        
                        {}
                        <Row className="mb-5 align-items-center">
                            <Col md={7}>
                                <div className="shadow-sm" style={{ borderRadius: '12px', overflow: 'hidden' }}>
                                    <InputGroup size="lg">
                                        <InputGroup.Text className="bg-white border-0 px-4" style={{ borderRadius: '12px 0 0 12px' }}>
                                            <FaSearch className="text-primary" size={22} />
                                        </InputGroup.Text>
                                        <Form.Control
                                            placeholder="Rechercher par agent, mission, site, date..."
                                            value={searchTerm}
                                            onChange={(e) => setSearchTerm(e.target.value)}
                                            className="border-0 px-3"
                                            style={{ 
                                                fontSize: '1.1rem',
                                                borderRadius: '0 12px 12px 0'
                                            }}
                                        />
                                    </InputGroup>
                                </div>
                            </Col>
                            <Col md={5} className="d-flex gap-3 justify-content-md-end align-items-center mt-3 mt-md-0">
                                <Button 
                                    variant="outline-primary" 
                                    onClick={loadData} 
                                    title="Actualiser les données"
                                    className="shadow-sm d-flex align-items-center px-4 py-3"
                                    style={{ 
                                        fontSize: '1.05rem',
                                        borderRadius: '12px',
                                        borderWidth: '2px',
                                        fontWeight: '600'
                                    }}
                                >
                                    <FaFilter className="me-2" size={18} /> Actualiser
                                </Button>
                                
                                <Badge 
                                    className="d-flex align-items-center px-4 py-3 shadow-sm"
                                    style={{ 
                                        fontSize: '1.3rem', 
                                        fontWeight: '700',
                                        borderRadius: '12px',
                                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                        color: 'white'
                                    }}
                                >
                                    <FaUserClock className="me-2" size={20} />
                                    {filteredItems.length} pointage{filteredItems.length > 1 ? 's' : ''}
                                </Badge>
                            </Col>
                        </Row>

                        {}
                        {loading ? (
                            <div className="text-center py-5">
                                <div className="mb-4">
                                    <Spinner animation="border" variant="primary" style={{ width: '5rem', height: '5rem', borderWidth: '0.4rem' }} />
                                </div>
                                <h4 className="text-muted fw-bold">Chargement des pointages...</h4>
                                <p className="text-muted">Veuillez patienter</p>
                            </div>
                        ) : (
                            <div className="table-responsive" style={{ borderRadius: '15px', overflow: 'hidden' }}>
                                <Table hover className="align-middle bg-white mb-0" style={{ fontSize: '1.05rem' }}>
                                    <thead style={{ background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)' }}>
                                        <tr>
                                            <th className="text-white py-4" style={{fontSize: '1.15rem', borderRight: '1px solid rgba(255,255,255,0.1)'}}>
                                                <div className="d-flex align-items-center">
                                                    <div className="bg-white bg-opacity-20 rounded-circle p-2 me-2">
                                                        <FaUserClock size={18} />
                                                    </div>
                                                    <span className="fw-bold">Agent</span>
                                                </div>
                                            </th>
                                            <th className="text-white py-4" style={{fontSize: '1.15rem', borderRight: '1px solid rgba(255,255,255,0.1)'}}>
                                                <div className="d-flex align-items-center">
                                                    <div className="bg-white bg-opacity-20 rounded-circle p-2 me-2">
                                                        <FaCalendarAlt size={18} />
                                                    </div>
                                                    <span className="fw-bold">Date & Heure</span>
                                                </div>
                                            </th>
                                            <th className="text-white py-4" style={{fontSize: '1.15rem', borderRight: '1px solid rgba(255,255,255,0.1)'}}>
                                                <div className="d-flex align-items-center">
                                                    <div className="bg-white bg-opacity-20 rounded-circle p-2 me-2">
                                                        <FaCheck size={18} />
                                                    </div>
                                                    <span className="fw-bold">Statut</span>
                                                </div>
                                            </th>
                                            <th className="text-white py-4" style={{fontSize: '1.15rem', borderRight: '1px solid rgba(255,255,255,0.1)'}}>
                                                <div className="d-flex align-items-center">
                                                    <div className="bg-white bg-opacity-20 rounded-circle p-2 me-2">
                                                        <FaMapMarkerAlt size={18} />
                                                    </div>
                                                    <span className="fw-bold">Lieu / Site</span>
                                                </div>
                                            </th>
                                            <th className="text-white py-4" style={{fontSize: '1.15rem', borderRight: '1px solid rgba(255,255,255,0.1)'}}>
                                                <div className="d-flex align-items-center">
                                                    <div className="bg-white bg-opacity-20 rounded-circle p-2 me-2">
                                                        <FaUserClock size={18} />
                                                    </div>
                                                    <span className="fw-bold">Mission</span>
                                                </div>
                                            </th>
                                            <th className="text-center text-white py-4" style={{fontSize: '1.15rem'}}>
                                                <span className="fw-bold">Actions</span>
                                            </th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {currentItems.length === 0 ? (
                                            <tr>
                                                <td colSpan="6" className="text-center py-5">
                                                    <div className="py-5">
                                                        <div className="bg-light rounded-circle d-inline-flex p-4 mb-3">
                                                            <FaExclamationTriangle className="text-muted" size={48} />
                                                        </div>
                                                        <h4 className="text-muted fw-bold">Aucun pointage trouvé</h4>
                                                        <p className="text-muted">Essayez d'ajuster vos critères de recherche</p>
                                                    </div>
                                                </td>
                                            </tr>
                                        ) : (
                                            currentItems.map((p, index) => (
                                                <tr key={p.id} className="hover-row" style={{ 
                                                    borderBottom: '2px solid #f0f0f0',
                                                    background: index % 2 === 0 ? '#ffffff' : '#f8f9fa'
                                                }}>
                                                    <td className="py-4">
                                                        <div className="d-flex align-items-center">
                                                            <div className="bg-primary bg-opacity-10 rounded-circle p-3 me-3 shadow-sm">
                                                                <FaUser className="text-primary" size={22} />
                                                            </div>
                                                            <div>
                                                                <div className="fw-bold text-dark" style={{ fontSize: '1.1rem' }}>
                                                                    {p.agentNom && p.agentPrenom
                                                                        ? `${p.agentPrenom} ${p.agentNom}`
                                                                        : (p.agent?.nom && p.agent?.prenom 
                                                                            ? `${p.agent.prenom} ${p.agent.nom}`
                                                                            : "Agent non spécifié")}
                                                                </div>
                                                                <div className="text-muted" style={{ fontSize: '0.9rem' }}>
                                                                    {p.agentId ? `ID: #${p.agentId}` : `Pointage ID: #${p.id}`}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="py-4">
                                                        <div className="d-flex align-items-center">
                                                            <div className="bg-light rounded-circle p-3 me-3 shadow-sm">
                                                                <FaCalendarAlt className="text-primary" size={24} />
                                                            </div>
                                                            <div>
                                                                <div className="fw-bold text-dark" style={{ fontSize: '1.1rem' }}>
                                                                    {new Date(p.datePointage).toLocaleDateString('fr-FR', { 
                                                                        day: '2-digit', 
                                                                        month: 'long', 
                                                                        year: 'numeric' 
                                                                    })}
                                                                </div>
                                                                <div className="text-muted d-flex align-items-center mt-1" style={{ fontSize: '1rem' }}>
                                                                    <FaClock className="me-2" size={14} />
                                                                    {new Date(p.datePointage).toLocaleTimeString('fr-FR')}
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="py-4">
                                                        <div className="d-flex flex-column gap-2">
                                                            <Badge 
                                                                className="d-inline-flex align-items-center px-3 py-2 shadow-sm"
                                                                style={{ 
                                                                    width: 'fit-content', 
                                                                    fontSize: '1.05rem',
                                                                    borderRadius: '10px',
                                                                    background: p.estPresent 
                                                                        ? 'linear-gradient(135deg, #11998e 0%, #38ef7d 100%)' 
                                                                        : 'linear-gradient(135deg, #eb3349 0%, #f45c43 100%)'
                                                                }}
                                                            >
                                                                {p.estPresent ? (
                                                                    <><FaCheck className="me-2" size={16} /> Présent</>
                                                                ) : (
                                                                    <><FaTimes className="me-2" size={16} /> Absent</>
                                                                )}
                                                            </Badge>
                                                            
                                                            {p.estRetard && (
                                                                <Badge 
                                                                    className="d-inline-flex align-items-center px-3 py-2 shadow-sm"
                                                                    style={{ 
                                                                        width: 'fit-content', 
                                                                        fontSize: '1.05rem',
                                                                        borderRadius: '10px',
                                                                        background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                                                                        color: 'white'
                                                                    }}
                                                                >
                                                                    <FaClock className="me-2" size={16} /> En retard
                                                                </Badge>
                                                            )}
                                                        </div>
                                                    </td>
                                                    <td className="py-4">
                                                        <div className="d-flex align-items-center">
                                                            <div className="bg-success bg-opacity-10 rounded-circle p-3 me-3">
                                                                <FaBuilding className="text-success" size={22} />
                                                            </div>
                                                            <div style={{ fontSize: '0.95rem' }}>
                                                                {p.mission?.site?.nom || p.mission?.site?.adresse || p.siteName ? (
                                                                    <>
                                                                        <div className="fw-bold text-dark" style={{ fontSize: '1.05rem' }}>
                                                                            {p.mission?.site?.nom || p.siteName || "Site"}
                                                                        </div>
                                                                        <div className="text-muted" style={{ fontSize: '0.9rem' }}>
                                                                            {p.mission?.site?.adresse || 
                                                                             (p.latitude != null && p.longitude != null
                                                                                ? `${p.latitude.toFixed(4)}, ${p.longitude.toFixed(4)}`
                                                                                : "Position non disponible")}
                                                                        </div>
                                                                    </>
                                                                ) : p.latitude != null && p.longitude != null ? (
                                                                    <>
                                                                        <div className="text-dark">
                                                                            <span className="fw-bold">Lat:</span> {p.latitude.toFixed(6)}
                                                                        </div>
                                                                        <div className="text-muted">
                                                                            <span className="fw-bold">Lng:</span> {p.longitude.toFixed(6)}
                                                                        </div>
                                                                    </>
                                                                ) : (
                                                                    <div className="text-muted">
                                                                        <FaMapMarkerAlt className="me-2" />
                                                                        Position non disponible
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="py-4">
                                                        <div className="d-flex align-items-center">
                                                            <div className="bg-warning bg-opacity-10 rounded-circle p-3 me-3">
                                                                <FaBriefcase className="text-warning" size={22} />
                                                            </div>
                                                            <div>
                                                                {p.missionTitre || p.mission ? (
                                                                    <>
                                                                        <div className="fw-bold text-dark" style={{ fontSize: '1.05rem' }}>
                                                                            {p.missionTitre || p.mission?.titre || p.mission?.description || `Mission #${p.missionId || p.mission?.id}`}
                                                                        </div>
                                                                        <div className="text-muted" style={{ fontSize: '0.9rem' }}>
                                                                            {p.missionId ? `ID: #${p.missionId}` : (p.mission?.id ? `ID: #${p.mission.id}` : 'Mission')}
                                                                        </div>
                                                                    </>
                                                                ) : (
                                                                    <div className="text-muted">
                                                                        <FaBriefcase className="me-2" />
                                                                        {p.missionId ? `Mission #${p.missionId}` : 'Mission non spécifiée'}
                                                                    </div>
                                                                )}
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="text-center py-4">
                                                        <div className="d-flex justify-content-center gap-2">
                                                            <OverlayTrigger
                                                                placement="top"
                                                                overlay={renderTooltip("Voir les détails")}
                                                            >
                                                                <Link 
                                                                    to={`/pointages/${p.id}`} 
                                                                    className="btn text-white shadow-sm"
                                                                    style={{ 
                                                                        fontSize: '1rem',
                                                                        padding: '0.75rem 1.25rem',
                                                                        borderRadius: '10px',
                                                                        background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                                                        border: 'none'
                                                                    }}
                                                                >
                                                                    <FaEye size={18} />
                                                                </Link>
                                                            </OverlayTrigger>
                                                            <OverlayTrigger
                                                                placement="top"
                                                                overlay={renderTooltip("Modifier")}
                                                            >
                                                                <Link 
                                                                    to={`/pointages/edit/${p.id}`} 
                                                                    className="btn text-white shadow-sm"
                                                                    style={{ 
                                                                        fontSize: '1rem',
                                                                        padding: '0.75rem 1.25rem',
                                                                        borderRadius: '10px',
                                                                        background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
                                                                        border: 'none'
                                                                    }}
                                                                >
                                                                    <FaPencilAlt size={18} />
                                                                </Link>
                                                            </OverlayTrigger>
                                                            <OverlayTrigger
                                                                placement="top"
                                                                overlay={renderTooltip("Supprimer")}
                                                            >
                                                                <Button
                                                                    className="text-white shadow-sm"
                                                                    onClick={() => handleDelete(p.id)}
                                                                    style={{ 
                                                                        fontSize: '1rem',
                                                                        padding: '0.75rem 1.25rem',
                                                                        borderRadius: '10px',
                                                                        background: 'linear-gradient(135deg, #eb3349 0%, #f45c43 100%)',
                                                                        border: 'none'
                                                                    }}
                                                                >
                                                                    <FaTrashAlt size={18} />
                                                                </Button>
                                                            </OverlayTrigger>
                                                        </div>
                                                    </td>
                                                </tr>
                                            ))
                                        )}
                                    </tbody>
                                </Table>
                            </div>
                        )}

                        {}
                        {!loading && (
                            <div className="d-flex flex-column flex-md-row justify-content-between align-items-center mt-4 pt-4 border-top">
                                <div className="mb-3 mb-md-0">
                                    <Form.Group className="d-flex align-items-center">
                                        <Form.Label className="me-3 mb-0 fw-bold" style={{ fontSize: '1.05rem' }}>
                                            Afficher :
                                        </Form.Label>
                                        <Form.Select 
                                            value={itemsPerPage} 
                                            onChange={handleItemsPerPageChange}
                                            style={{ 
                                                width: '100px',
                                                fontSize: '1.05rem',
                                                borderRadius: '10px',
                                                borderWidth: '2px',
                                                borderColor: '#667eea',
                                                fontWeight: '600'
                                            }}
                                        >
                                            <option value={5}>5</option>
                                            <option value={8}>8</option>
                                            <option value={10}>10</option>
                                            <option value={15}>15</option>
                                            <option value={20}>20</option>
                                            <option value={50}>50</option>
                                            <option value={100}>100</option>
                                        </Form.Select>
                                        <span className="ms-3 text-muted" style={{ fontSize: '1.05rem' }}>
                                            {filteredItems.length > 0 ? (
                                                <>
                                                    <strong>{indexOfFirstItem + 1}-{Math.min(indexOfLastItem, filteredItems.length)}</strong> sur <strong>{filteredItems.length}</strong> pointages
                                                </>
                                            ) : (
                                                <strong>0 pointage</strong>
                                            )}
                                        </span>
                                    </Form.Group>
                                </div>

                                <Pagination className="mb-0">
                                    <Pagination.First 
                                        onClick={() => handlePageChange(1)} 
                                        disabled={currentPage === 1 || totalPages === 0}
                                        style={{ fontWeight: '600' }}
                                    />
                                    <Pagination.Prev 
                                        onClick={() => handlePageChange(currentPage - 1)} 
                                        disabled={currentPage === 1 || totalPages === 0}
                                        style={{ fontWeight: '600' }}
                                    />
                                    
                                    {totalPages > 0 && generatePaginationItems().map((page, idx) => {
                                        if (page === '...') {
                                            return <Pagination.Ellipsis key={`ellipsis-${idx}`} disabled />;
                                        }
                                        return (
                                            <Pagination.Item
                                                key={page}
                                                active={page === currentPage}
                                                onClick={() => handlePageChange(page)}
                                                style={{ 
                                                    fontWeight: '600',
                                                    fontSize: '1.05rem'
                                                }}
                                            >
                                                {page}
                                            </Pagination.Item>
                                        );
                                    })}
                                    
                                    <Pagination.Next 
                                        onClick={() => handlePageChange(currentPage + 1)} 
                                        disabled={currentPage === totalPages || totalPages === 0}
                                        style={{ fontWeight: '600' }}
                                    />
                                    <Pagination.Last 
                                        onClick={() => handlePageChange(totalPages)} 
                                        disabled={currentPage === totalPages || totalPages === 0}
                                        style={{ fontWeight: '600' }}
                                    />
                                </Pagination>
                            </div>
                        )}
                    </div>
                </Card.Body>
            </Card>

            {}
            <style>{`
                @keyframes fadeIn {
                    from { opacity: 0; transform: translateY(-10px); }
                    to { opacity: 1; transform: translateY(0); }
                }
                
                @keyframes slideIn {
                    from { opacity: 0; transform: translateX(-20px); }
                    to { opacity: 1; transform: translateX(0); }
                }
                
                .shadow-xl {
                    box-shadow: 0 20px 60px rgba(0,0,0,0.15) !important;
                }
                
                .badge {
                    font-weight: 600;
                    letter-spacing: 0.3px;
                }
                
                th {
                    font-weight: 700;
                    white-space: nowrap;
                    letter-spacing: 0.5px;
                }
                
                .hover-row {
                    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                    cursor: pointer;
                }
                
                .hover-row:hover {
                    background: linear-gradient(135deg, rgba(102, 126, 234, 0.08) 0%, rgba(118, 75, 162, 0.08) 100%) !important;
                    transform: translateX(5px) scale(1.005);
                    box-shadow: -5px 0 15px rgba(102, 126, 234, 0.2), 0 5px 15px rgba(0,0,0,0.1);
                }
                
                .btn {
                    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
                    font-weight: 600;
                    letter-spacing: 0.3px;
                }
                
                .btn:hover {
                    transform: translateY(-3px) scale(1.05);
                    box-shadow: 0 10px 25px rgba(0,0,0,0.2);
                }
                
                .btn:active {
                    transform: translateY(-1px) scale(1.02);
                    box-shadow: 0 5px 15px rgba(0,0,0,0.15);
                }
                
                .table {
                    border-collapse: separate;
                    border-spacing: 0;
                }
                
                .bg-gradient {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                }
                
                .card {
                    animation: slideIn 0.5s ease-out;
                }
                
                .form-control:focus {
                    border-color: #667eea;
                    box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
                }
                
                .input-group-text {
                    background: white;
                }

                .badge {
                    animation: fadeIn 0.3s ease-out;
                }

                .shadow-sm {
                    backdrop-filter: blur(10px);
                }

                @media print {
                    .btn, .nav, .card-header, .form-control, .input-group {
                        display: none !important;
                    }
                }
                
                @media (max-width: 768px) {
                    .table {
                        font-size: 0.9rem;
                    }
                    
                    h2 {
                        font-size: 1.5rem !important;
                    }
                    
                    .btn {
                        font-size: 0.9rem !important;
                        padding: 0.6rem 1.2rem !important;
                    }
                    
                    .hover-row:hover {
                        transform: none;
                    }
                }

                .table-responsive::-webkit-scrollbar {
                    height: 8px;
                }
                
                .table-responsive::-webkit-scrollbar-track {
                    background: #f1f1f1;
                    border-radius: 10px;
                }
                
                .table-responsive::-webkit-scrollbar-thumb {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    border-radius: 10px;
                }
                
                .table-responsive::-webkit-scrollbar-thumb:hover {
                    background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
                }

.pagination {
                    gap: 8px;
                }

                .pagination .page-item .page-link {
                    border-radius: 10px;
                    border: 2px solid #667eea;
                    color: #667eea;
                    font-weight: 600;
                    padding: 0.6rem 1rem;
                    transition: all 0.3s ease;
                    margin: 0 4px;
                }

                .pagination .page-item.active .page-link {
                    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                    border-color: #667eea;
                    color: white;
                    box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
                    transform: scale(1.1);
                }

                .pagination .page-item .page-link:hover:not(.active) {
                    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
                    border-color: #764ba2;
                    color: #764ba2;
                    transform: translateY(-2px);
                    box-shadow: 0 4px 10px rgba(102, 126, 234, 0.2);
                }

                .pagination .page-item.disabled .page-link {
                    border-color: #dee2e6;
                    color: #6c757d;
                    opacity: 0.5;
                }

                .form-select:focus {
                    border-color: #667eea;
                    box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
                }
            `}</style>
        </Container>
    );
}