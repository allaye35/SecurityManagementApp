import React, { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import { 
  Container, Row, Col, Card, Form, Button, Spinner, 
  Alert, InputGroup, Badge
} from "react-bootstrap";
import { 
  FaMapMarkedAlt, FaPlus, FaSatelliteDish, FaCrosshairs,
  FaBuilding, FaArrowLeft, FaTimes, FaCheck 
} from "react-icons/fa";

import { MapContainer, TileLayer, Marker, useMap } from "react-leaflet";
import L from "leaflet";

import GeolocalisationService from "../../services/GeolocalisationService";
import MissionService from "../../services/MissionService";
import SiteService from "../../services/SiteService";
import "../../styles/GeolocalisationForm.css";
import "leaflet/dist/leaflet.css";

delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: require("leaflet/dist/images/marker-icon-2x.png"),
  iconUrl: require("leaflet/dist/images/marker-icon.png"),
  shadowUrl: require("leaflet/dist/images/marker-shadow.png"),
});

function MapUpdater({ center }) {
  const map = useMap();
  useEffect(() => {
    map.setView(center, 15);
  }, [center, map]);
  return null;
}

export default function CreateGeolocalisation() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    gpsPrecision: 5,
    latitude: 48.8566,
    longitude: 2.3522,
    missionId: "",
  });
  const [missions, setMissions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [siteAddress, setSiteAddress] = useState("");
  const [geocoding, setGeocoding] = useState(false);
  const [coordsLocked, setCoordsLocked] = useState(false);

  useEffect(() => {
    MissionService.getAllMissions()
      .then(({ data }) => setMissions(data))
      .catch(() => setError("Impossible de charger la liste des missions."));
  }, []);

  useEffect(() => {
    if (!form.missionId) {
      setSiteAddress("");
      setCoordsLocked(false);
      return;
    }
    (async () => {
      try {
        setGeocoding(true);
        setError(null);
        
        console.log("🔍 Récupération de la mission ID:", form.missionId);
        const { data: mission } = await MissionService.getMissionById(form.missionId);
        console.log("📦 Mission récupérée:", mission);
        
        const siteId = mission.site_mission || mission.siteId || mission.site?.id || mission.site_id;
        console.log("🏢 Site ID trouvé:", siteId);
        
        if (!siteId) {
          console.error("❌ Aucun site_id trouvé dans la mission:", Object.keys(mission));
          setError("Cette mission n'a pas de site associé.");
          setSiteAddress("");
          setCoordsLocked(false);
          return;
        }

        console.log("🔍 Récupération du site ID:", siteId);
        const { data: site } = await SiteService.getSiteById(siteId);
        console.log("🏢 Site récupéré:", site);
        
        const address = site.adresse || site.address || site.rue || site.voie;
        console.log("📍 Adresse trouvée:", address);
        
        if (!address) {
          console.error("❌ Aucune adresse trouvée dans le site:", Object.keys(site));
          setError(`Le site "${site.nom || site.name || 'inconnu'}" n'a pas d'adresse renseignée.`);
          setSiteAddress("");
          setCoordsLocked(false);
          return;
        }

        setSiteAddress(address);

        console.log("🌍 Géocodage de l'adresse:", address);
        const resp = await fetch(
          `https:
        );
        const results = await resp.json();
        console.log("📍 Résultats du géocodage:", results);
        
        if (!results.length) {
          setError(`Impossible de géocoder l'adresse "${address}". Veuillez vérifier l'adresse ou saisir manuellement les coordonnées.`);
          setCoordsLocked(false);
          return;
        }

        const coords = {
          latitude: parseFloat(results[0].lat),
          longitude: parseFloat(results[0].lon)
        };
        console.log("✅ Coordonnées calculées:", coords);

        setForm(f => ({
          ...f,
          ...coords
        }));
        setCoordsLocked(true);
      } catch (err) {
        console.error("❌ Erreur lors du géocodage:", err);
        console.error("Détails de l'erreur:", err.response || err.message);
        setError(`Erreur lors du géocodage: ${err.message || 'Erreur inconnue'}`);
        setCoordsLocked(false);
      } finally {
        setGeocoding(false);
      }
    })();
  }, [form.missionId]);

  const handleChange = e => {
    const { name, value } = e.target;
    setForm(f =>
      name === "missionId"
        ? { ...f, [name]: value }
        : { ...f, [name]: parseFloat(value) || value }
    );
  };

  const handleGetCurrentPosition = () => {
    if (!navigator.geolocation) {
      setError("La géolocalisation n'est pas supportée par votre navigateur.");
      return;
    }

    setGeocoding(true);
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setForm(f => ({
          ...f,
          latitude: position.coords.latitude,
          longitude: position.coords.longitude,
        }));
        setCoordsLocked(true);
        setSiteAddress("Position actuelle");
        setGeocoding(false);
      },
      () => {
        setError("Impossible d'obtenir votre position actuelle.");
        setGeocoding(false);
      }
    );
  };

  const handleUnlockCoords = () => {
    setCoordsLocked(false);
    setForm(f => ({ ...f, missionId: "" }));
    setSiteAddress("");
  };

  const handleSubmit = async e => {
    e.preventDefault();
    setError(null);
    setLoading(true);
    try {
      const { data: created } = await GeolocalisationService.createGeolocalisation({
        gpsPrecision: form.gpsPrecision,
        latitude: form.latitude,
        longitude: form.longitude,
      });
      if (form.missionId) {
        await GeolocalisationService.addMission(created.id, form.missionId);
      }
      navigate("/geolocalisations");
    } catch {
      setError("Échec de la création. Vérifiez les champs et réessayez.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container fluid className="py-4">
      <Card className="shadow-sm border-0 rounded-lg">
        <Card.Header className="bg-gradient bg-primary text-white">
          <Row className="align-items-center">
            <Col>
              <h5 className="mb-0">
                <FaMapMarkedAlt className="me-2" /> Création d'une nouvelle géolocalisation
              </h5>
            </Col>
            <Col xs="auto">
              <Link 
                to="/geolocalisations" 
                className="btn btn-light btn-sm d-flex align-items-center shadow-sm"
              >
                <FaArrowLeft className="me-1" /> Retour
              </Link>
            </Col>
          </Row>
        </Card.Header>
        
        <Card.Body className="p-4">
          {error && (
            <Alert variant="danger" className="d-flex align-items-center mb-4">
              <FaTimes className="me-2" />
              {error}
            </Alert>
          )}
          
          <Form onSubmit={handleSubmit}>
            <Row className="mb-4">
              <Col md={6}>
                <Form.Group className="mb-3">
                  <Form.Label className="d-flex align-items-center fw-bold">
                    <FaSatelliteDish className="me-2 text-primary" /> Précision GPS (mètres)
                  </Form.Label>
                  <InputGroup>
                    <Form.Control
                      type="number"
                      name="gpsPrecision"
                      value={form.gpsPrecision}
                      onChange={handleChange}
                      min="0"
                      step="0.1"
                      required
                      className="shadow-sm"
                    />
                    <InputGroup.Text>mètres</InputGroup.Text>
                  </InputGroup>
                </Form.Group>
                
                <Form.Group className="mb-4">
                  <Form.Label className="d-flex align-items-center fw-bold">
                    <FaBuilding className="me-2 text-primary" /> Associer à une mission (facultatif)
                  </Form.Label>
                  <Form.Select 
                    name="missionId" 
                    value={form.missionId} 
                    onChange={handleChange}
                    className="shadow-sm"
                    disabled={geocoding}
                  >
                    <option value="">— Aucune —</option>
                    {missions.map(m => (
                      <option key={m.id} value={m.id}>
                        {m.titre} ({m.dateDebut} → {m.dateFin})
                      </option>
                    ))}
                  </Form.Select>
                  <Form.Text className="text-muted">
                    La sélection d'une mission géocodera automatiquement son site.
                  </Form.Text>
                </Form.Group>

                {geocoding && (
                  <Alert variant="info" className="d-flex align-items-center mb-3">
                    <Spinner size="sm" animation="border" className="me-2" />
                    Géocodage de l'adresse en cours...
                  </Alert>
                )}

                {siteAddress && (
                  <Alert variant="success" className="mb-3">
                    <strong>Adresse du site :</strong> {siteAddress}
                  </Alert>
                )}

                <Row>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label className="d-flex align-items-center fw-bold">
                        <FaCrosshairs className="me-2 text-danger" /> Latitude
                      </Form.Label>
                      <Form.Control
                        type="number"
                        name="latitude"
                        value={form.latitude}
                        onChange={handleChange}
                        step="0.000001"
                        required
                        className="shadow-sm"
                        readOnly={coordsLocked}
                        disabled={coordsLocked}
                      />
                    </Form.Group>
                  </Col>
                  <Col md={6}>
                    <Form.Group className="mb-3">
                      <Form.Label className="d-flex align-items-center fw-bold">
                        <FaCrosshairs className="me-2 text-danger" /> Longitude
                      </Form.Label>
                      <Form.Control
                        type="number"
                        name="longitude"
                        value={form.longitude}
                        onChange={handleChange}
                        step="0.000001"
                        required
                        className="shadow-sm"
                        readOnly={coordsLocked}
                        disabled={coordsLocked}
                      />
                    </Form.Group>
                  </Col>
                </Row>

                {coordsLocked && (
                  <div className="mb-3">
                    <Button 
                      variant="outline-warning" 
                      size="sm"
                      onClick={handleUnlockCoords}
                      className="d-flex align-items-center"
                    >
                      <FaTimes className="me-1" /> Modifier manuellement les coordonnées
                    </Button>
                  </div>
                )}

                {!form.missionId && !coordsLocked && (
                  <div className="mb-3">
                    <Button 
                      variant="outline-info" 
                      size="sm"
                      onClick={handleGetCurrentPosition}
                      disabled={geocoding}
                      className="d-flex align-items-center"
                    >
                      <FaCrosshairs className="me-1" /> Utiliser ma position actuelle
                    </Button>
                  </div>
                )}
                
                <div className="mt-4 mb-3 d-flex justify-content-between align-items-center">
                  <Badge bg="info" className="py-2 px-3 d-flex align-items-center">
                    <FaMapMarkedAlt className="me-1" /> Coordonnées actuelles
                  </Badge>
                  <div className="text-muted small">
                    <strong>Lat:</strong> {form.latitude.toFixed(6)} | <strong>Long:</strong> {form.longitude.toFixed(6)}
                  </div>
                </div>
              </Col>

              <Col md={6}>
                <div className="shadow-sm rounded overflow-hidden border mb-3" style={{ height: "350px" }}>
                  <MapContainer
                    center={[form.latitude, form.longitude]}
                    zoom={15}
                    style={{ height: "100%", width: "100%" }}
                  >
                    <TileLayer url="https:
                    <Marker position={[form.latitude, form.longitude]} />
                    <MapUpdater center={[form.latitude, form.longitude]} />
                  </MapContainer>
                </div>
                <div className="bg-light p-3 rounded">
                  <p className="mb-0 small text-muted">
                    <strong>Note :</strong> La position sur la carte est mise à jour automatiquement selon les coordonnées.
                  </p>
                </div>
              </Col>
            </Row>

            <hr className="my-4" />

            <div className="d-flex justify-content-between gap-3">
              <Button 
                variant="outline-secondary" 
                onClick={() => navigate(-1)}
                disabled={loading}
                className="d-flex align-items-center"
              >
                <FaArrowLeft className="me-1" /> Annuler
              </Button>
              <Button 
                variant="primary" 
                type="submit" 
                disabled={loading}
                className="d-flex align-items-center"
              >
                {loading ? (
                  <>
                    <Spinner size="sm" animation="border" className="me-2" /> Création en cours...
                  </>
                ) : (
                  <>
                    <FaCheck className="me-1" /> Créer la géolocalisation
                  </>
                )}
              </Button>
            </div>
          </Form>
        </Card.Body>
      </Card>
    </Container>
  );
}