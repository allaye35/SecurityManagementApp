# 📝 Code Complet à Copier dans GeolocalisationList.js

## ⚠️ Instructions

1. **Ouvrez** le fichier `security-management-frontend/src/components/geolocalisations/GeolocalisationList.js`
2. **Sélectionnez tout** le contenu (Ctrl+A)
3. **Supprimez** tout
4. **Copiez** le code ci-dessous
5. **Collez** dans le fichier vide
6. **Sauvegardez** (Ctrl+S)

## 📄 Code Complet

```javascript
import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { 
    Container, Row, Col, Card, Button, Badge, Alert, 
    Spinner, InputGroup, FormControl, ListGroup, Modal
} from "react-bootstrap";
import { 
    FaMapMarkedAlt, FaPlus, FaSearch, FaTrash, FaEdit, 
    FaEye, FaSatelliteDish, FaMapPin, FaBriefcase
} from "react-icons/fa";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import L from "leaflet";
import GeolocalisationService from "../../services/GeolocalisationService";
import "../../styles/GeolocalisationList.css";
import "../../styles/GeolocalisationListImproved.css";
import "leaflet/dist/leaflet.css";

// Fix des icônes Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
    iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
    iconUrl: require("leaflet/dist/images/marker-icon.png"),
    shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

export default function GeolocalisationList() {
    const [geolocs, setGeolocs] = useState([]);
    const [filteredGeolocs, setFilteredGeolocs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [selectedGeo, setSelectedGeo] = useState(null);

    useEffect(() => {
        loadGeolocs();
    }, []);

    const loadGeolocs = () => {
        setLoading(true);
        GeolocalisationService.getAllGeolocalisations()
            .then(({ data }) => {
                setGeolocs(data);
                setFilteredGeolocs(data);
            })
            .catch(() => setError("Impossible de charger les géolocalisations."))
            .finally(() => setLoading(false));
    };

    // Filtrage par recherche
    useEffect(() => {
        if (!searchTerm.trim()) {
            setFilteredGeolocs(geolocs);
        } else {
            const filtered = geolocs.filter(g => 
                g.id.toString().includes(searchTerm) ||
                g.missions?.some(m => 
                    m.titre?.toLowerCase().includes(searchTerm.toLowerCase())
                )
            );
            setFilteredGeolocs(filtered);
        }
    }, [searchTerm, geolocs]);

    const handleDeleteClick = (geo) => {
        setSelectedGeo(geo);
        setShowDeleteModal(true);
    };

    const confirmDelete = () => {
        if (!selectedGeo) return;
        GeolocalisationService.deleteGeolocalisation(selectedGeo.id)
            .then(() => {
                setGeolocs(g => g.filter(x => x.id !== selectedGeo.id));
                setShowDeleteModal(false);
                setSelectedGeo(null);
            })
            .catch(() => alert("Échec de la suppression."));
    };

    // Statistiques
    const totalGeolocs = geolocs.length;
    const geoWithMissions = geolocs.filter(g => g.missions?.length > 0).length;
    const avgPrecision = geolocs.length > 0 
        ? (geolocs.reduce((sum, g) => sum + g.gpsPrecision, 0) / geolocs.length).toFixed(1)
        : 0;

    if (loading) {
        return (
            <Container className="text-center mt-5">
                <Spinner animation="border" variant="primary" />
                <p className="mt-3">Chargement des géolocalisations...</p>
            </Container>
        );
    }

    if (error) {
        return (
            <Container className="mt-4">
                <Alert variant="danger">
                    <Alert.Heading>Erreur</Alert.Heading>
                    {error}
                </Alert>
            </Container>
        );
    }

    if (!geolocs.length) {
        return (
            <Container className="mt-5">
                <Card className="text-center shadow-sm">
                    <Card.Body className="p-5">
                        <FaMapMarkedAlt size={64} className="text-muted mb-3" />
                        <h3>Aucune géolocalisation</h3>
                        <p className="text-muted">Commencez par créer votre première géolocalisation GPS.</p>
                        <Link to="/geolocalisations/create">
                            <Button variant="primary" size="lg" className="mt-3">
                                <FaPlus className="me-2" /> Créer une géolocalisation
                            </Button>
                        </Link>
                    </Card.Body>
                </Card>
            </Container>
        );
    }

    return (
        <Container fluid className="py-4">
            {/* Header avec titre et bouton */}
            <Row className="mb-4">
                <Col>
                    <div className="d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <div>
                            <h2 className="mb-1">
                                <FaMapMarkedAlt className="me-2 text-primary" />
                                Mes Géolocalisations GPS
                            </h2>
                            <p className="text-muted mb-0">
                                Gérez vos points de géolocalisation et leurs missions associées
                            </p>
                        </div>
                        <Link to="/geolocalisations/create">
                            <Button variant="primary" size="lg" className="shadow-sm">
                                <FaPlus className="me-2" /> Nouvelle géolocalisation
                            </Button>
                        </Link>
                    </div>
                </Col>
            </Row>

            {/* Statistiques */}
            <Row className="mb-4">
                <Col md={4} className="mb-3 mb-md-0">
                    <Card className="shadow-sm border-0 bg-primary text-white">
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 className="mb-0 opacity-75">Total</h6>
                                    <h2 className="mb-0">{totalGeolocs}</h2>
                                </div>
                                <FaMapMarkedAlt size={48} className="opacity-50" />
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4} className="mb-3 mb-md-0">
                    <Card className="shadow-sm border-0 bg-success text-white">
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 className="mb-0 opacity-75">Avec missions</h6>
                                    <h2 className="mb-0">{geoWithMissions}</h2>
                                </div>
                                <FaBriefcase size={48} className="opacity-50" />
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
                <Col md={4}>
                    <Card className="shadow-sm border-0 bg-info text-white">
                        <Card.Body>
                            <div className="d-flex justify-content-between align-items-center">
                                <div>
                                    <h6 className="mb-0 opacity-75">Précision moyenne</h6>
                                    <h2 className="mb-0">{avgPrecision} m</h2>
                                </div>
                                <FaSatelliteDish size={48} className="opacity-50" />
                            </div>
                        </Card.Body>
                    </Card>
                </Col>
            </Row>

            {/* Barre de recherche */}
            <Row className="mb-4">
                <Col md={6} className="mb-3 mb-md-0">
                    <InputGroup size="lg">
                        <InputGroup.Text>
                            <FaSearch />
                        </InputGroup.Text>
                        <FormControl
                            placeholder="Rechercher par ID ou nom de mission..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                        />
                    </InputGroup>
                </Col>
                <Col md={6} className="text-md-end align-self-center">
                    <Badge bg="secondary" className="p-2 fs-6">
                        {filteredGeolocs.length} résultat{filteredGeolocs.length > 1 ? 's' : ''}
                    </Badge>
                </Col>
            </Row>

            {/* Liste des géolocalisations */}
            <Row>
                {filteredGeolocs.map(g => (
                    <Col key={g.id} lg={6} xl={4} className="mb-4">
                        <Card className="h-100 shadow-sm border-0 hover-card">
                            <Card.Header className="bg-gradient bg-primary text-white">
                                <div className="d-flex justify-content-between align-items-center">
                                    <h5 className="mb-0">
                                        <FaMapPin className="me-2" />
                                        Géo #{g.id}
                                    </h5>
                                    <Badge bg="light" text="dark">
                                        <FaSatelliteDish className="me-1" />
                                        {g.gpsPrecision} m
                                    </Badge>
                                </div>
                            </Card.Header>

                            <Card.Body className="p-0">
                                {/* Mini carte */}
                                <div style={{ height: "200px", width: "100%" }}>
                                    <MapContainer
                                        center={[g.position.latitude, g.position.longitude]}
                                        zoom={13}
                                        style={{ height: "100%", width: "100%" }}
                                        scrollWheelZoom={false}
                                        dragging={false}
                                        zoomControl={false}
                                    >
                                        <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
                                        <Marker position={[g.position.latitude, g.position.longitude]}>
                                            <Popup>Géo #{g.id}</Popup>
                                        </Marker>
                                    </MapContainer>
                                </div>

                                {/* Coordonnées */}
                                <div className="p-3 bg-light">
                                    <Row className="small">
                                        <Col xs={6}>
                                            <strong>📍 Latitude:</strong><br />
                                            {g.position.latitude.toFixed(6)}
                                        </Col>
                                        <Col xs={6}>
                                            <strong>📍 Longitude:</strong><br />
                                            {g.position.longitude.toFixed(6)}
                                        </Col>
                                    </Row>
                                </div>

                                {/* Missions */}
                                <div className="p-3">
                                    <h6 className="mb-2">
                                        <FaBriefcase className="me-2 text-primary" />
                                        Missions rattachées
                                    </h6>
                                    {g.missions?.length ? (
                                        <ListGroup variant="flush">
                                            {g.missions.slice(0, 2).map(m => (
                                                <ListGroup.Item key={m.id} className="px-0 py-2">
                                                    <div className="d-flex justify-content-between align-items-start">
                                                        <div className="flex-grow-1">
                                                            <strong className="d-block">{m.titre}</strong>
                                                            <small className="text-muted">
                                                                Du {m.dateDebut} au {m.dateFin}
                                                            </small>
                                                        </div>
                                                        <Badge bg={m.statutMission === 'PLANIFIEE' ? 'info' : 'success'}>
                                                            {m.statutMission}
                                                        </Badge>
                                                    </div>
                                                </ListGroup.Item>
                                            ))}
                                            {g.missions.length > 2 && (
                                                <ListGroup.Item className="px-0 text-center">
                                                    <small className="text-muted">
                                                        + {g.missions.length - 2} autre{g.missions.length - 2 > 1 ? 's' : ''} mission{g.missions.length - 2 > 1 ? 's' : ''}
                                                    </small>
                                                </ListGroup.Item>
                                            )}
                                        </ListGroup>
                                    ) : (
                                        <p className="text-muted small mb-0">
                                            <em>Aucune mission assignée</em>
                                        </p>
                                    )}
                                </div>
                            </Card.Body>

                            <Card.Footer className="bg-white border-top">
                                <div className="d-flex gap-2">
                                    <Link to={`/geolocalisations/${g.id}`} className="flex-fill">
                                        <Button variant="outline-primary" size="sm" className="w-100">
                                            <FaEye className="me-1" /> Voir
                                        </Button>
                                    </Link>
                                    <Link to={`/geolocalisations/edit/${g.id}`} className="flex-fill">
                                        <Button variant="outline-secondary" size="sm" className="w-100">
                                            <FaEdit className="me-1" /> Modifier
                                        </Button>
                                    </Link>
                                    <Button 
                                        variant="outline-danger" 
                                        size="sm"
                                        onClick={() => handleDeleteClick(g)}
                                    >
                                        <FaTrash />
                                    </Button>
                                </div>
                            </Card.Footer>
                        </Card>
                    </Col>
                ))}
            </Row>

            {/* Modal de confirmation de suppression */}
            <Modal show={showDeleteModal} onHide={() => setShowDeleteModal(false)} centered>
                <Modal.Header closeButton>
                    <Modal.Title>Confirmer la suppression</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <p>Voulez-vous vraiment supprimer la géolocalisation <strong>#{selectedGeo?.id}</strong> ?</p>
                    {selectedGeo?.missions?.length > 0 && (
                        <Alert variant="warning">
                            <strong>Attention !</strong> Cette géolocalisation est associée à {selectedGeo.missions.length} mission{selectedGeo.missions.length > 1 ? 's' : ''}.
                        </Alert>
                    )}
                </Modal.Body>
                <Modal.Footer>
                    <Button variant="secondary" onClick={() => setShowDeleteModal(false)}>
                        Annuler
                    </Button>
                    <Button variant="danger" onClick={confirmDelete}>
                        <FaTrash className="me-2" />
                        Supprimer
                    </Button>
                </Modal.Footer>
            </Modal>
        </Container>
    );
}
```

## ✅ Vérifications Après Copie

1. **Le fichier ne doit pas avoir d'erreurs de syntaxe**
2. **Tous les imports sont présents en haut**
3. **Le composant se termine bien par `}`**
4. **Sauvegardez le fichier**

## 🎨 Fichier CSS Déjà Créé

Le fichier `GeolocalisationListImproved.css` a déjà été créé dans `src/styles/`.
Il contient les styles pour :
- Effet hover sur les cartes
- Animations
- Design responsive
- Dégradés de couleur

## 📦 Packages Nécessaires

Si vous n'avez pas encore ces packages, installez-les :

```bash
npm install react-bootstrap bootstrap react-icons react-leaflet leaflet
```

Et ajoutez dans `index.js` ou `App.js` :

```javascript
import 'bootstrap/dist/css/bootstrap.min.css';
```

## 🚀 C'est Prêt !

Après avoir copié le code et sauvegardé, rechargez votre page et profitez de la nouvelle interface améliorée ! 🎉
