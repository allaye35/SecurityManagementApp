// src/components/geolocalisations/GeolocalisationDetail.js
import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import GeolocalisationService from "../../services/GeolocalisationService";
import "../../styles/GeolocalisationDetail.css";
import "leaflet/dist/leaflet.css";

export default function GeolocalisationDetail() {
    const { id } = useParams();
    const navigate = useNavigate();
    const [geo, setGeo]       = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState(null);

    useEffect(() => {
        GeolocalisationService.getGeolocalisationById(id)
            .then(({ data }) => setGeo(data))
            .catch(() => setError("❌ Impossible de récupérer cette géolocalisation."))
            .finally(() => setLoading(false));
    }, [id]);

    if (loading) return (
        <div className="detail-container">
            <div className="loader">
                <div className="spinner"></div>
                <p>⏳ Chargement des détails…</p>
            </div>
        </div>
    );
    
    if (error) return (
        <div className="detail-container">
            <div className="error-box">
                <h2>❌ Erreur</h2>
                <p>{error}</p>
                <button className="btn-back" onClick={() => navigate("/geolocalisations")}>
                    ← Retour à la liste
                </button>
            </div>
        </div>
    );

    return (
        <div className="detail-container">
            {}
            <div className="detail-header">
                <div className="header-content">
                    <h1>📍 Géolocalisation #{geo.id}</h1>
                    <span className="precision-badge">
                        📡 Précision : {geo.gpsPrecision} m
                    </span>
                </div>
                <div className="header-actions">
                    <button
                        className="btn-edit"
                        onClick={() => navigate(`/geolocalisations/edit/${geo.id}`)}
                    >
                        ✏️ Modifier
                    </button>
                    <button
                        className="btn-delete"
                        onClick={() => {
                            if (window.confirm("Voulez-vous vraiment supprimer cette géolocalisation ?")) {
                                GeolocalisationService.deleteGeolocalisation(geo.id)
                                    .then(() => navigate("/geolocalisations"))
                                    .catch(() => alert("Erreur lors de la suppression."));
                            }
                        }}
                    >
                        🗑️ Supprimer
                    </button>
                </div>
            </div>

            {}
            <div className="map-section">
                <h3>🗺️ Localisation sur la carte</h3>
                <MapContainer
                    center={[geo.position.latitude, geo.position.longitude]}
                    zoom={15}
                    className="map-preview"
                    scrollWheelZoom={true}
                >
                    <TileLayer 
                        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    />
                    <Marker position={[geo.position.latitude, geo.position.longitude]}>
                        <Popup>
                            <strong>Géolocalisation #{geo.id}</strong><br />
                            Précision: {geo.gpsPrecision} m
                        </Popup>
                    </Marker>
                </MapContainer>
            </div>

            {}
            <div className="info-section">
                <h3>📌 Coordonnées GPS</h3>
                <div className="info-grid">
                    <div className="info-card">
                        <div className="info-icon">🌐</div>
                        <div className="info-content">
                            <label>Latitude</label>
                            <p className="info-value">{geo.position.latitude.toFixed(6)}°</p>
                        </div>
                    </div>
                    <div className="info-card">
                        <div className="info-icon">🌐</div>
                        <div className="info-content">
                            <label>Longitude</label>
                            <p className="info-value">{geo.position.longitude.toFixed(6)}°</p>
                        </div>
                    </div>
                    <div className="info-card">
                        <div className="info-icon">📡</div>
                        <div className="info-content">
                            <label>Précision GPS</label>
                            <p className="info-value">{geo.gpsPrecision} mètre{geo.gpsPrecision > 1 ? "s" : ""}</p>
                        </div>
                    </div>
                </div>
            </div>

            {}
            <div className="missions-section">
                <div className="section-header">
                    <h3>🎯 Missions rattachées</h3>
                    <span className="mission-count">{geo.missions.length} mission{geo.missions.length > 1 ? "s" : ""}</span>
                </div>
                {geo.missions.length > 0 ? (
                    <div className="missions-grid">
                        {geo.missions.map(m => (
                            <div key={m.id} className="mission-card">
                                <div className="mission-header">
                                    <h4>{m.titre}</h4>
                                    <span className={`status-badge ${m.statutMission.toLowerCase()}`}>
                                        {m.statutMission}
                                    </span>
                                </div>
                                <div className="mission-details">
                                    <div className="mission-detail-item">
                                        <span className="detail-icon">📅</span>
                                        <span className="detail-label">Début :</span>
                                        <span className="detail-value">{m.dateDebut}</span>
                                    </div>
                                    <div className="mission-detail-item">
                                        <span className="detail-icon">📅</span>
                                        <span className="detail-label">Fin :</span>
                                        <span className="detail-value">{m.dateFin}</span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="no-missions">
                        <p>📭 Aucune mission assignée à cette géolocalisation.</p>
                    </div>
                )}
            </div>

            {}
            <div className="footer-actions">
                <button className="btn-back" onClick={() => navigate("/geolocalisations")}>
                    ← Retour à la liste
                </button>
            </div>
        </div>
    );
}
