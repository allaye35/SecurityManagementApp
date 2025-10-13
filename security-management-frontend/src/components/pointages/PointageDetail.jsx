import React, { useEffect, useState } from "react";
import { useParams, Link, useNavigate } from "react-router-dom";
import PointageService from "../../services/PointageService";
import "../../styles/PointageDetail.css";

export default function PointageDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [pt, setPt] = useState(null);
  const [error, setError] = useState("");

  useEffect(() => {
    let mounted = true;
    PointageService.getById(id)
      .then(({ data }) => {
        if (!mounted) return;
        console.log("Pointage chargé:", data);
        setPt(data);
      })
      .catch((err) => {
        console.error("Erreur chargement pointage:", err);
        if (!mounted) return;
        setError("Impossible de charger le pointage");
      });
    return () => {
      mounted = false;
    };
  }, [id]);

  if (error) return <p className="error">{error}</p>;
  if (!pt) return <p>Chargement…</p>;

const latFromRoot = pt?.latitude;
  const lngFromRoot = pt?.longitude;
  const latFromObj = pt?.positionActuelle?.latitude;
  const lngFromObj = pt?.positionActuelle?.longitude;

  const lat = Number.isFinite(latFromRoot)
    ? latFromRoot
    : Number.isFinite(latFromObj)
    ? latFromObj
    : undefined;

  const lng = Number.isFinite(lngFromRoot)
    ? lngFromRoot
    : Number.isFinite(lngFromObj)
    ? lngFromObj
    : undefined;

  const hasLatLng = Number.isFinite(lat) && Number.isFinite(lng);
  const position = hasLatLng ? `${lat}, ${lng}` : "Position non disponible";

const missionId = pt?.missionId ?? pt?.mission?.id ?? "Non spécifiée";
  const missionTitre = pt?.missionTitre ?? pt?.mission?.titre ?? null;

const agentName =
    pt?.agentNom && pt?.agentPrenom
      ? `${pt.agentPrenom} ${pt.agentNom}`
      : (pt?.agent?.prenom && pt?.agent?.nom
        ? `${pt.agent.prenom} ${pt.agent.nom}`
        : "Agent non spécifié");

  return (
    <div className="pointage-detail-container">
      <div className="detail-header">
        <div className="header-title">
          <h2>📍 Détail du Pointage #{pt?.id ?? "—"}</h2>
          <div className="status-badges">
            {pt?.estPresent ? (
              <span className="badge badge-success">✓ Présent</span>
            ) : (
              <span className="badge badge-danger">✗ Absent</span>
            )}
            {pt?.estRetard && (
              <span className="badge badge-warning">⏰ En Retard</span>
            )}
          </div>
        </div>
        <button onClick={() => navigate("/pointages")} className="btn-back">
          ← Retour à la liste
        </button>
      </div>

      <div className="detail-content">
        {}
        <div className="detail-section">
          <h3>🕐 Informations Temporelles</h3>
          <div className="detail-grid">
            <div className="detail-item">
              <span className="detail-label">Date et Heure</span>
              <span className="detail-value">
                {pt?.datePointage 
                  ? new Date(pt.datePointage).toLocaleString('fr-FR', {
                      weekday: 'long',
                      year: 'numeric',
                      month: 'long',
                      day: 'numeric',
                      hour: '2-digit',
                      minute: '2-digit',
                      second: '2-digit'
                    })
                  : "Non disponible"}
              </span>
            </div>
          </div>
        </div>

        {}
        <div className="detail-section">
          <h3>📊 Statut du Pointage</h3>
          <div className="detail-grid">
            <div className="detail-item">
              <span className="detail-label">Présence</span>
              <span className={`detail-value ${pt?.estPresent ? 'text-success' : 'text-danger'}`}>
                {pt?.estPresent ? "✓ Oui" : "✗ Non"}
              </span>
            </div>
            <div className="detail-item">
              <span className="detail-label">Retard</span>
              <span className={`detail-value ${pt?.estRetard ? 'text-warning' : 'text-success'}`}>
                {pt?.estRetard ? "⚠️ Oui" : "✓ Non"}
              </span>
            </div>
          </div>
        </div>

        {}
        <div className="detail-section">
          <h3>📍 Localisation</h3>
          <div className="detail-grid">
            <div className="detail-item full-width">
              <span className="detail-label">Position GPS</span>
              <span className="detail-value">
                {hasLatLng ? (
                  <>
                    <span className="coordinates">
                      Lat: {lat?.toFixed(6)} | Long: {lng?.toFixed(6)}
                    </span>
                    <a 
                      href={`https:
                      target="_blank"
                      rel="noopener noreferrer"
                      className="map-link"
                    >
                      🗺️ Voir sur la carte
                    </a>
                  </>
                ) : (
                  "Position non disponible"
                )}
              </span>
            </div>
          </div>
        </div>

        {}
        <div className="detail-section">
          <h3>👥 Mission et Agent</h3>
          <div className="detail-grid">
            <div className="detail-item">
              <span className="detail-label">Mission ID</span>
              <span className="detail-value mission-id">#{missionId}</span>
            </div>
            {missionTitre && (
              <div className="detail-item">
                <span className="detail-label">Titre de la Mission</span>
                <span className="detail-value">{missionTitre}</span>
              </div>
            )}
            <div className="detail-item">
              <span className="detail-label">Agent</span>
              <span className="detail-value agent-name">{agentName}</span>
            </div>
            {pt?.agentId && (
              <div className="detail-item">
                <span className="detail-label">Agent ID</span>
                <span className="detail-value">#{pt.agentId}</span>
              </div>
            )}
          </div>
        </div>
      </div>

      {}
      <div className="detail-actions">
        <Link to={`/pointages/edit/${id}`} className="btn btn-primary">
          ✏️ Modifier le pointage
        </Link>
        <button 
          onClick={() => window.print()} 
          className="btn btn-secondary"
        >
          🖨️ Imprimer
        </button>
      </div>
    </div>
  );
}