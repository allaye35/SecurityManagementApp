import React, { useEffect, useState } from "react";
import { useNavigate }                 from "react-router-dom";
import DevisService                    from "../../services/DevisService";
import "../../styles/DevisList.css";

export default function DevisList() {
    const [devis, setDevis]     = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError]     = useState("");
    const navigate = useNavigate();
    
    const [stats, setStats] = useState({
        totalHT: 0,
        totalTVA: 0, 
        totalTTC: 0,
        nombreDevis: 0,
        devisParStatut: {},
        nombreAgentsTotal: 0,
        quantiteTotal: 0
    });

    useEffect(() => {
        DevisService.getAll()
            .then(response => {
                if (response && response.data) {
                    if (Array.isArray(response.data)) {
                        setDevis(response.data);
                        
                        const newStats = {
                            totalHT: 0,
                            totalTVA: 0,
                            totalTTC: 0,
                            nombreDevis: response.data.length,
                            devisParStatut: {},
                            nombreAgentsTotal: 0,
                            quantiteTotal: 0
                        };
                        
                        response.data.forEach(d => {
                            newStats.totalHT += d.montantHT || 0;
                            newStats.totalTVA += d.montantTVA || 0;
                            newStats.totalTTC += d.montantTTC || 0;
                            
                            newStats.nombreAgentsTotal += d.nombreAgents || 0;
                            newStats.quantiteTotal += d.quantite || 0;
                            
                            if (d.statut) {
                                newStats.devisParStatut[d.statut] = (newStats.devisParStatut[d.statut] || 0) + 1;
                            }
                        });
                        
                        setStats(newStats);
                    } else {
                        setError("Format de données inattendu");
                    }
                } else {
                    setError("Données non reçues");
                }
            })
            .catch(err => {
                console.error("Erreur lors du chargement des devis:", err);
                setError("Impossible de charger les devis");
            })
            .finally(() => setLoading(false));
    }, []);

    const handleDelete = id => {
        if (!window.confirm("Confirmer la suppression ?")) return;
        DevisService.delete(id)
            .then(() => setDevis(ds => ds.filter(d => d.id !== id)))
            .catch(() => alert("Échec de la suppression"));
    };

    if (loading) return <p>Chargement…</p>;
    if (error)   return <p className="error">{error}</p>;

    return (
        <div className="devis-list">
            <h2>Liste des devis</h2>
            
            {}
            <div className="devis-stats">
                <div className="stat-card">
                    <h3>Résumé</h3>
                    <p><strong>Nombre de devis :</strong> {stats.nombreDevis}</p>
                    <p><strong>Total HT :</strong> {stats.totalHT.toFixed(2)}€</p>
                    <p><strong>Total TVA :</strong> {stats.totalTVA.toFixed(2)}€</p>
                    <p><strong>Total TTC :</strong> {stats.totalTTC.toFixed(2)}€</p>
                </div>
                <div className="stat-card">
                    <h3>Ressources</h3>
                    <p><strong>Agents requis :</strong> {stats.nombreAgentsTotal}</p>
                    <p><strong>Quantité totale :</strong> {stats.quantiteTotal} heures/jours</p>
                </div>
            </div>
            
            <button
                className="btn btn-add"
                onClick={() => navigate("/devis/create")}
            >
                ➕ Nouveau devis
            </button>

            <table className="tbl-devis">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Référence</th>
                    <th>Statut</th>
                    <th># Missions</th>
                    <th>HT (€)</th>
                    <th>TTC (€)</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                {devis.length === 0
                    ? (
                        <tr>
                            <td colSpan="7" className="no-data">
                                Aucun devis disponible
                            </td>
                        </tr>
                    )
                    : devis.map(d => (
                        <tr key={d.id}>
                            <td>{d.id}</td>
                            <td>{d.referenceDevis}</td>
                            <td>{d.statut}</td>
                            <td>{d.missionIds?.length || 0}</td>
                            <td>{(d.montantHT||0).toFixed?.(2) || d.montantHT}</td>
                            <td>{(d.montantTTC||0).toFixed?.(2) || d.montantTTC}</td>
                            <td className="actions">
                                <button onClick={() => navigate(`/devis/${d.id}`)}>👁️</button>
                                <button onClick={() => navigate(`/devis/edit/${d.id}`)}>✏️</button>
                            </td>
                        </tr>
                    ))
                }
                </tbody>
            </table>
        </div>
    );
}
