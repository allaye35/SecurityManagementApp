import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import GeolocalisationService from "../../services/GeolocalisationService";
import "../../styles/GeolocalisationList.css";

export default function GeolocalisationList() {
    const [geolocs, setGeolocs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error,   setError]   = useState(null);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(6); // 6 géolocalisations par page

    useEffect(() => {
        GeolocalisationService.getAllGeolocalisations()
            .then(({ data }) => setGeolocs(data))
            .catch(() => setError("Impossible de charger les géolocalisations."))
            .finally(() => setLoading(false));
    }, []);

    const handleDelete = id => {
        if (!window.confirm("Voulez-vous vraiment supprimer cette géolocalisation ?")) return;
        GeolocalisationService.deleteGeolocalisation(id)
            .then(() => setGeolocs(g => g.filter(x => x.id !== id)))
            .catch(() => alert("Échec de la suppression."));
    };

    // Calcul de la pagination
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentGeolocs = geolocs.slice(indexOfFirstItem, indexOfLastItem);
    const totalPages = Math.ceil(geolocs.length / itemsPerPage);

    const paginate = (pageNumber) => setCurrentPage(pageNumber);
    const nextPage = () => {
        if (currentPage < totalPages) setCurrentPage(currentPage + 1);
    };
    const prevPage = () => {
        if (currentPage > 1) setCurrentPage(currentPage - 1);
    };

    if (loading) return <div className="spinner" />;
    if (error)   return <p className="error">{error}</p>;

    if (!geolocs.length) {
        return (
            <div className="geo-empty">
                <p>Aucune géolocalisation n’a été trouvée.</p>
                <Link to="/geolocalisations/create" className="btn-add">
                    ➕ Ajouter une géolocalisation
                </Link>
            </div>
        );
    }

    return (
        <div className="geo-container">
            <div className="geo-header">
                <h2>📍 Mes Géolocalisations GPS</h2>
                <Link to="/geolocalisations/create" className="btn-add">
                    ➕ Nouvelle géolocalisation
                </Link>
            </div>

            {/* Informations de pagination */}
            <div className="pagination-info">
                <p>
                    Affichage de {indexOfFirstItem + 1} à {Math.min(indexOfLastItem, geolocs.length)} sur {geolocs.length} géolocalisations
                </p>
            </div>

            <div className="geo-grid">
                {currentGeolocs.map(g => (
                    <div key={g.id} className="geo-card">
                        <div className="geo-card-header">
                            <h3>📍 Géolocalisation #{g.id}</h3>
                            <span className="precision">
                                📡 Précision : {g.gpsPrecision} m
                            </span>
                        </div>

                        <p className="coords">
                            📌 <strong>Latitude</strong> : {g.position.latitude.toFixed(5)} <br/>
                            📌 <strong>Longitude</strong> : {g.position.longitude.toFixed(5)}
                        </p>

                        <div className="missions">
                            <strong>🎯 Missions rattachées ({g.missions?.length || 0}) :</strong>
                            {g.missions?.length ? (
                                <ul>
                                    {g.missions.map(m => (
                                        <li key={m.id}>
                                            <div className="mission-item">
                                                <strong>{m.titre}</strong>
                                                <span className={`badge ${m.statutMission === 'PLANIFIEE' ? 'badge-info' : 'badge-success'}`}>
                                                    {m.statutMission}
                                                </span>
                                            </div>
                                            <small className="mission-dates">
                                                📅 Du {m.dateDebut} au {m.dateFin}
                                            </small>
                                        </li>
                                    ))}
                                </ul>
                            ) : (
                                <p className="no-missions">— Aucune mission assignée —</p>
                            )}
                        </div>

                        <div className="geo-actions">
                            <Link to={`/geolocalisations/${g.id}`} className="btn" title="Voir détails">
                                🔍 Voir
                            </Link>
                            <Link to={`/geolocalisations/edit/${g.id}`} className="btn" title="Modifier">
                                ✏️ Modifier
                            </Link>
                            <button
                                onClick={() => handleDelete(g.id)}
                                className="btn btn-delete"
                                title="Supprimer"
                            >
                                🗑️ Supprimer
                            </button>
                        </div>
                    </div>
                ))}
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
                <div className="pagination">
                    <button 
                        onClick={prevPage} 
                        disabled={currentPage === 1}
                        className="pagination-btn"
                    >
                        ⬅️ Précédent
                    </button>

                    <div className="pagination-numbers">
                        {[...Array(totalPages)].map((_, index) => (
                            <button
                                key={index + 1}
                                onClick={() => paginate(index + 1)}
                                className={`pagination-number ${currentPage === index + 1 ? 'active' : ''}`}
                            >
                                {index + 1}
                            </button>
                        ))}
                    </div>

                    <button 
                        onClick={nextPage} 
                        disabled={currentPage === totalPages}
                        className="pagination-btn"
                    >
                        Suivant ➡️
                    </button>
                </div>
            )}
        </div>
    );
}
