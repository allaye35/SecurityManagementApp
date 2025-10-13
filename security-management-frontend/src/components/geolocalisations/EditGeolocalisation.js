import React, { useEffect, useState } from "react";
import { useNavigate, useParams, Link } from "react-router-dom";
import { 
  Container, Row, Col, Card, Form, Button, Spinner, 
  Alert, InputGroup, Badge 
} from "react-bootstrap";
import { 
  FaMapMarkedAlt, FaSatelliteDish, FaCrosshairs,
  FaArrowLeft, FaTimes, FaSave, FaExclamationTriangle
} from "react-icons/fa";

import { MapContainer, TileLayer, Marker } from "react-leaflet";
import L from "leaflet";

import GeolocalisationService from "../../services/GeolocalisationService";
import "../../styles/GeolocalisationForm.css";
import "leaflet/dist/leaflet.css";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
  iconUrl: require("leaflet/dist/images/marker-icon.png"),
  shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

const EditGeolocalisation = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [geolocalisation, setGeolocalisation] = useState({
    gpsPrecision: "",
    position: { latitude: 48.8566, longitude: 2.3522 },
    missions: []
  });
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    setLoading(true);
    GeolocalisationService.getGeolocalisationById(id)
      .then((response) => {
        setGeolocalisation(response.data);
        setLoading(false);
      })
      .catch((error) => {
        console.error("Erreur de chargement", error);
        setError("Impossible de charger les données de cette géolocalisation.");
        setLoading(false);
      });
  }, [id]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "latitude" || name === "longitude") {
      setGeolocalisation({
        ...geolocalisation,
        position: {
          ...geolocalisation.position,
          [name]: parseFloat(value) || value,
        },
      });
    } else {
      setGeolocalisation({ 
        ...geolocalisation, 
        [name]: name === "gpsPrecision" ? parseFloat(value) || value : value 
      });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setSaving(true);
    setError(null);
    
    GeolocalisationService.updateGeolocalisation(id, geolocalisation)
      .then(() => {
        navigate("/geolocalisations");
      })
      .catch((error) => {
        console.error("Erreur de modification", error);
        setError("Échec de la mise à jour. Vérifiez les champs et réessayez.");
        setSaving(false);
      });
  };

  return (
    <Container fluid className="edit-geo-container py-4">
      <div className="edit-geo-wrapper">
        {}
        <div className="edit-header">
          <div className="header-content">
            <h1>
              <FaMapMarkedAlt className="me-3" />
              Modification de la géolocalisation #{id}
            </h1>
            <p className="header-subtitle">
              Ajustez les coordonnées GPS et la précision de cette géolocalisation
            </p>
          </div>
          <Link 
            to="/geolocalisations" 
            className="btn-header-back"
          >
            <FaArrowLeft className="me-2" /> Retour à la liste
          </Link>
        </div>

        {error && (
          <Alert variant="danger" className="error-alert">
            <FaExclamationTriangle className="me-2" />
            {error}
          </Alert>
        )}
        
        {loading ? (
          <div className="loading-container">
            <Spinner animation="border" variant="primary" className="spinner-large" />
            <p className="loading-text">Chargement des données...</p>
          </div>
        ) : (
          <Form onSubmit={handleSubmit}>
            <Row className="g-4">
              {}
              <Col lg={6}>
                <Card className="info-card">
                  <Card.Body>
                    <h3 className="card-title">
                      <FaSatelliteDish className="me-2" />
                      Informations GPS
                    </h3>

                    <Form.Group className="form-group-modern mb-4">
                      <Form.Label className="form-label-modern">
                        <FaSatelliteDish className="me-2 text-primary" /> 
                        Précision GPS
                      </Form.Label>
                      <InputGroup className="input-group-modern">
                        <Form.Control
                          type="number"
                          name="gpsPrecision"
                          value={geolocalisation.gpsPrecision || ""}
                          onChange={handleChange}
                          min="0"
                          step="0.1"
                          required
                          placeholder="Ex: 5.0"
                        />
                        <InputGroup.Text className="unit-badge">mètres</InputGroup.Text>
                      </InputGroup>
                      <Form.Text className="form-hint">
                        La précision indique la marge d'erreur de la géolocalisation
                      </Form.Text>
                    </Form.Group>

                    <Row>
                      <Col md={6}>
                        <Form.Group className="form-group-modern mb-4">
                          <Form.Label className="form-label-modern">
                            <FaCrosshairs className="me-2 text-danger" /> 
                            Latitude
                          </Form.Label>
                          <Form.Control
                            type="number"
                            name="latitude"
                            value={geolocalisation.position?.latitude || ""}
                            onChange={handleChange}
                            step="0.000001"
                            required
                            placeholder="Ex: 48.856613"
                            className="input-modern"
                          />
                          <Form.Text className="form-hint">
                            Entre -90° et 90°
                          </Form.Text>
                        </Form.Group>
                      </Col>
                      <Col md={6}>
                        <Form.Group className="form-group-modern mb-4">
                          <Form.Label className="form-label-modern">
                            <FaCrosshairs className="me-2 text-danger" /> 
                            Longitude
                          </Form.Label>
                          <Form.Control
                            type="number"
                            name="longitude"
                            value={geolocalisation.position?.longitude || ""}
                            onChange={handleChange}
                            step="0.000001"
                            required
                            placeholder="Ex: 2.352222"
                            className="input-modern"
                          />
                          <Form.Text className="form-hint">
                            Entre -180° et 180°
                          </Form.Text>
                        </Form.Group>
                      </Col>
                    </Row>

                    <div className="coords-display">
                      <div className="coords-label">📍 Coordonnées actuelles</div>
                      <div className="coords-values">
                        <span className="coord-item">
                          <strong>Lat:</strong> {geolocalisation.position?.latitude?.toFixed(6) || "-"}°
                        </span>
                        <span className="coord-separator">|</span>
                        <span className="coord-item">
                          <strong>Long:</strong> {geolocalisation.position?.longitude?.toFixed(6) || "-"}°
                        </span>
                      </div>
                    </div>

                    {geolocalisation.missions && geolocalisation.missions.length > 0 && (
                      <div className="missions-box">
                        <h6 className="missions-title">
                          🎯 Missions associées ({geolocalisation.missions.length})
                        </h6>
                        <div className="missions-badges">
                          {geolocalisation.missions.map(mission => (
                            <Badge key={mission.id} className="mission-badge">
                              {mission.titre}
                            </Badge>
                          ))}
                        </div>
                      </div>
                    )}
                  </Card.Body>
                </Card>
              </Col>

              {}
              <Col lg={6}>
                <Card className="map-card">
                  <Card.Body>
                    <h3 className="card-title">
                      🗺️ Aperçu de la localisation
                    </h3>
                    
                    {geolocalisation.position && (
                      <div className="map-wrapper">
                        <MapContainer
                          center={[
                            geolocalisation.position.latitude || 48.8566, 
                            geolocalisation.position.longitude || 2.3522
                          ]}
                          zoom={15}
                          style={{ height: "100%", width: "100%" }}
                          scrollWheelZoom={true}
                        >
                          <TileLayer url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png" />
                          <Marker position={[
                            geolocalisation.position.latitude || 48.8566, 
                            geolocalisation.position.longitude || 2.3522
                          ]} />
                        </MapContainer>
                      </div>
                    )}
                    
                    <div className="map-note">
                      <strong>💡 Note :</strong> La carte affiche la position actuellement enregistrée. 
                      Les modifications seront visibles après enregistrement.
                    </div>
                  </Card.Body>
                </Card>
              </Col>
            </Row>

            {}
            <div className="action-buttons">
              <Button 
                variant="outline-secondary" 
                onClick={() => navigate("/geolocalisations")}
                disabled={saving}
                className="btn-cancel"
              >
                <FaTimes className="me-2" /> Annuler
              </Button>
              <Button 
                variant="success" 
                type="submit" 
                disabled={saving}
                className="btn-save"
              >
                {saving ? (
                  <>
                    <Spinner size="sm" animation="border" className="me-2" /> 
                    Enregistrement...
                  </>
                ) : (
                  <>
                    <FaSave className="me-2" /> Enregistrer les modifications
                  </>
                )}
              </Button>
            </div>
          </Form>
        )}
      </div>
    </Container>
  );
};

export default EditGeolocalisation;
