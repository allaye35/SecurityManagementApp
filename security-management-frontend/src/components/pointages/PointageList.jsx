import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import PointageService from "../../services/PointageService";
import { 
    Container, Row, Col, Card, Table, Button, Badge, 
    Spinner, Alert, Form, InputGroup, OverlayTrigger, 
    Tooltip, Nav
} from "react-bootstrap";
import { 
    FaUserClock, FaPlus, FaSearch, FaFilter, FaEye, 
    FaPencilAlt, FaTrashAlt, FaExclamationTriangle,
    FaCalendarAlt, FaCheck, FaTimes, FaClock, FaMapMarkerAlt,
    FaSignInAlt, FaSignOutAlt
} from "react-icons/fa";

export default function PointageList() {
    const [items, setItems] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [searchTerm, setSearchTerm] = useState("");
    const [showNotification, setShowNotification] = useState(false);
    const [notification, setNotification] = useState({ message: "", variant: "success" });
    const navigate = useNavigate();

    // Chargement des données
    const loadData = () => {
        setLoading(true);
        PointageService.getAll()
            .then(({ data }) => {
                setItems(data);
                setLoading(false);
            })
            .catch(() => {
                setError("Erreur de chargement des pointages");
                setLoading(false);
                // Afficher une notification d'erreur
                showTemporaryNotification('Erreur lors du chargement des données', 'danger');
            });
    };

    useEffect(() => {
        loadData();
    }, []);

    // Fonction pour afficher une notification temporaire
    const showTemporaryNotification = (message, variant = 'success') => {
        setNotification({ message, variant });
        setShowNotification(true);
        setTimeout(() => setShowNotification(false), 5000);
    };

    // Fonction de suppression
    const handleDelete = (id) => {
        if (!window.confirm("Êtes-vous sûr de vouloir supprimer ce pointage ?")) return;
        
        PointageService.delete(id)
            .then(() => {
                setItems(prev => prev.filter(x => x.id !== id));
                showTemporaryNotification("Pointage supprimé avec succès", "success");
            })
            .catch(() => showTemporaryNotification("Échec de la suppression", "danger"));
    };

    // Filtrage des pointages
    const filteredItems = items.filter(p => {
        const searchString = `${p.id} ${new Date(p.datePointage).toLocaleString()} ${p.mission?.id || ""}`
            .toLowerCase();
        return searchTerm === '' || searchString.includes(searchTerm.toLowerCase());
    });

    // Rendu du tooltip
    const renderTooltip = (text) => (
        <Tooltip id="button-tooltip">
            {text}
        </Tooltip>
    );

    return (
        <Container fluid className="py-4" style={{ maxWidth: '1900px', background: 'linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%)', minHeight: '100vh' }}>
            {/* Notification temporaire */}
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
                            <Link 
                                to="/pointages/create" 
                                className="btn fw-bold shadow-lg text-white px-4 py-3" 
                                style={{ 
                                    fontSize: '1.15rem', 
                                    transition: 'all 0.3s ease',
                                    borderRadius: '12px',
                                    border: 'none',
                                    background: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
                                }}
                            >
                                <FaPlus className="me-2" size={20} /> Nouveau pointage
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
                        
                        {/* Barre de recherche et filtres */}
                        <Row className="mb-5 align-items-center">
                            <Col md={7}>
                                <div className="shadow-sm" style={{ borderRadius: '12px', overflow: 'hidden' }}>
                                    <InputGroup size="lg">
                                        <InputGroup.Text className="bg-white border-0 px-4" style={{ borderRadius: '12px 0 0 12px' }}>
                                            <FaSearch className="text-primary" size={22} />
                                        </InputGroup.Text>
                                        <Form.Control
                                            placeholder="Rechercher un pointage par ID, mission, date..."
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

                        {/* Contenu principal */}
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
                                            <th className="text-center text-white py-4" style={{fontSize: '1.15rem', borderRight: '1px solid rgba(255,255,255,0.1)'}}>
                                                <div className="d-flex align-items-center justify-content-center">
                                                    <div className="bg-white bg-opacity-20 rounded-circle p-2 me-2">
                                                        <FaUserClock size={18} />
                                                    </div>
                                                    <span className="fw-bold">ID</span>
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
                                                    <span className="fw-bold">Position GPS</span>
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
                                        {filteredItems.length === 0 ? (
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
                                            filteredItems.map((p, index) => (
                                                <tr key={p.id} className="hover-row" style={{ 
                                                    borderBottom: '2px solid #f0f0f0',
                                                    background: index % 2 === 0 ? '#ffffff' : '#f8f9fa'
                                                }}>
                                                    <td className="text-center py-4">
                                                        <Badge className="px-4 py-2 shadow-sm" style={{ 
                                                            fontSize: '1.15rem',
                                                            background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                                                            borderRadius: '10px'
                                                        }}>
                                                            #{p.id}
                                                        </Badge>
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
                                                            <div className="bg-danger bg-opacity-10 rounded-circle p-3 me-3">
                                                                <FaMapMarkerAlt className="text-danger" size={22} />
                                                            </div>
                                                            <div style={{ fontSize: '0.95rem' }}>
                                                                <div className="text-dark"><span className="fw-bold">Lat:</span> {p.positionActuelle?.latitude?.toFixed(6) ?? "-"}</div>
                                                                <div className="text-muted"><span className="fw-bold">Lng:</span> {p.positionActuelle?.longitude?.toFixed(6) ?? "-"}</div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td className="py-4">
                                                        <Badge 
                                                            className="d-inline-flex align-items-center px-4 py-3 shadow-sm"
                                                            style={{ 
                                                                fontSize: '1.1rem',
                                                                borderRadius: '10px',
                                                                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
                                                            }}
                                                        >
                                                            <FaUserClock className="me-2" size={18} /> Mission #{p.mission?.id ?? "-"}
                                                        </Badge>
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
                    </div>
                </Card.Body>
            </Card>

            {/* CSS pour les animations et styles spécifiques */}
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
                
                /* Animations pour les badges */
                .badge {
                    animation: fadeIn 0.3s ease-out;
                }
                
                /* Effet glassmorphism pour les cartes */
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
                
                /* Scrollbar personnalisée */
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
            `}</style>
        </Container>
    );
}