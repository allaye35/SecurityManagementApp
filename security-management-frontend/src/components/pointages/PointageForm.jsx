import React, { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import PointageService from "../../services/PointageService";
import MissionService from "../../services/MissionService";
import AgentService from "../../services/AgentService";
import { useAuth } from "../../context/AuthContext";
import { Toast, ToastContainer } from "react-bootstrap";
import "../../styles/PointageForm.css";

export default function PointageForm() {
    const { id } = useParams();
    const [searchParams] = useSearchParams();
    const isEdit = Boolean(id);
    const navigate = useNavigate();
    const { user } = useAuth();
    
    const serviceMode = searchParams.get('mode');
    const isServiceMode = serviceMode === 'prise' || serviceMode === 'fin';

    // Bloquer l'accès si mode n'est pas prise ou fin et qu'on n'est pas en édition
    useEffect(() => {
        if (!isEdit && !isServiceMode) {
            navigate('/pointages');
        }
    }, [isEdit, isServiceMode, navigate]);

    const [dto, setDto] = useState({
        datePointage: new Date().toISOString().slice(0, 16),
        estPresent: true,
        estRetard: false,
        positionActuelle: { latitude: "", longitude: "" },
        missionId: "",
        agentId: ""
    });
    const [missions, setMissions] = useState([]);
    const [agents, setAgents] = useState([]);
    const [agentsEnService, setAgentsEnService] = useState([]);
    const [currentUser, setCurrentUser] = useState(null);
    const [selectedMission, setSelectedMission] = useState(null);
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [gettingLocation, setGettingLocation] = useState(false);
    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState("");
    const [toastVariant, setToastVariant] = useState("success");

    useEffect(() => {
        if (user && user.id && !dto.agentId) {
            AgentService.getAllAgents()
                .then(({ data }) => {
                    const agentUtilisateur = data.find(a => a.utilisateur?.id === user.id);
                    if (agentUtilisateur) {
                        console.log("Agent trouvé pour l'utilisateur:", agentUtilisateur);
                        setDto(d => ({ ...d, agentId: agentUtilisateur.id }));
                        setCurrentUser(agentUtilisateur.utilisateur);
                    } else {
                        console.log("Aucun agent trouvé pour cet utilisateur");
                        setCurrentUser(user);
                    }
                })
                .catch(err => {
                    console.error("Erreur lors de la recherche de l'agent:", err);
                    setCurrentUser(user);
                });
        } else {
            setCurrentUser(user);
        }
        
        if (isServiceMode) {
            setTimeout(() => getCurrentPosition(), 500);
        }
    }, [user, isServiceMode]);

    useEffect(() => {
        MissionService.getAllMissions()
            .then(({ data }) => {
                if (isServiceMode) {
                    const missionsActives = data.filter(m => 
                        m.statutMission === 'EN_COURS' || m.statutMission === 'PLANIFIEE'
                    );
                    setMissions(missionsActives);
                } else {
                    setMissions(data);
                }
            })
            .catch(() => setError("Impossible de charger les missions"));
    }, [isServiceMode]);

    useEffect(() => {
        if (dto.missionId) {
            console.log("Chargement des agents pour la mission:", dto.missionId);
            MissionService.getMissionById(dto.missionId)
                .then(({ data }) => {
                    console.log("Données mission reçues:", data);
                    console.log("Type de agentIds:", typeof data.agentIds);
                    console.log("Agent IDs de la mission:", data.agentIds);
                    console.log("Est un tableau?", Array.isArray(data.agentIds));
                    console.log("Valeurs:", data.agentIds);
                    
                    let agentIdsArray = [];
                    if (data.agentIds) {
                        if (Array.isArray(data.agentIds)) {
                            agentIdsArray = data.agentIds;
                        } else if (typeof data.agentIds === 'object') {
                            agentIdsArray = Array.from(Object.values(data.agentIds));
                        } else if (typeof data.agentIds === 'number') {
                            agentIdsArray = [data.agentIds];
                        }
                    }
                    
                    agentIdsArray = agentIdsArray.filter(id => id != null && id !== undefined);
                    
                    console.log("Agent IDs convertis en tableau:", agentIdsArray);
                    
                    setSelectedMission(data);
                    
                    if (serviceMode === 'prise' && data.heureDebut) {
                        const heureActuelle = new Date();
                        const heureDebut = new Date();
                        const [hours, minutes] = data.heureDebut.split(':');
                        heureDebut.setHours(parseInt(hours), parseInt(minutes), 0);
                        
                        const tolerance = 15 * 60 * 1000;
                        const estEnRetard = heureActuelle.getTime() > (heureDebut.getTime() + tolerance);
                        
                        console.log("Calcul retard - Heure début:", data.heureDebut, "Heure actuelle:", heureActuelle.toLocaleTimeString(), "En retard?", estEnRetard);
                        setDto(d => ({ ...d, estRetard: estEnRetard }));
                    }
                    
                    if (agentIdsArray.length > 0) {
                        console.log("Récupération des agents pour les IDs:", agentIdsArray);
                        const agentPromises = agentIdsArray.map(agentId => 
                            AgentService.getAgentById(agentId)
                        );
                        
                        Promise.all(agentPromises)
                            .then(responses => {
                                const agentsList = responses.map(res => res.data);
                                console.log("Liste d'agents récupérée:", agentsList);
                                setAgents(agentsList);
                            })
                            .catch(err => {
                                console.error("Erreur lors de la récupération des agents:", err);
                                setError("Erreur lors du chargement des détails des agents");
                                setAgents([]);
                            });
                    } else {
                        console.log("Aucun agent ID trouvé pour cette mission");
                        setAgents([]);
                    }
                    
                    if (serviceMode === 'fin') {
                        PointageService.getAgentsEnService(dto.missionId)
                            .then(({ data }) => setAgentsEnService(data))
                            .catch(() => setAgentsEnService([]));
                    }
                })
                .catch((err) => {
                    console.error("Erreur lors du chargement de la mission:", err);
                    setError("Impossible de charger les agents de la mission");
                });
        } else {
            setAgents([]);
            setAgentsEnService([]);
        }
    }, [dto.missionId, serviceMode]);

    useEffect(() => {
        if (isEdit) {
            PointageService.getById(id)
                .then(({ data }) => setDto({
                    datePointage: data.datePointage.slice(0, 16),
                    estPresent: data.estPresent,
                    estRetard: data.estRetard,
                    // Le backend renvoie latitude/longitude à la racine, pas dans positionActuelle
                    positionActuelle: { 
                        latitude: data.latitude ?? "", 
                        longitude: data.longitude ?? "" 
                    },
                    missionId: data.missionId ?? "",
                    agentId: data.agentId ?? ""
                }))
                .catch(() => setError("Impossible de charger ce pointage"));
        }
    }, [id, isEdit]);

    const handleChange = e => {
        const { name, value, type, checked } = e.target;
        if (name === "latitude" || name === "longitude") {
            setDto(d => ({
                ...d,
                positionActuelle: { ...d.positionActuelle, [name]: value }
            }));
        } else if (type === "checkbox") {
            setDto(d => ({ ...d, [name]: checked }));
        } else {
            setDto(d => ({ ...d, [name]: value }));
        }
    };

    const getCurrentPosition = () => {
        if (!navigator.geolocation) {
            setError("La géolocalisation n'est pas supportée par votre navigateur");
            return;
        }

        setGettingLocation(true);
        setError("");
        
        navigator.geolocation.getCurrentPosition(
            (pos) => {
                setDto(d => ({
                    ...d,
                    positionActuelle: {
                        latitude: pos.coords.latitude.toFixed(6),
                        longitude: pos.coords.longitude.toFixed(6)
                    }
                }));
                setGettingLocation(false);
                setSuccess("Position GPS obtenue avec succès !");
                setTimeout(() => setSuccess(""), 3000);
            },
            (err) => {
                setGettingLocation(false);
                setError(`Erreur de géolocalisation: ${err.message}`);
            },
            {
                enableHighAccuracy: true,
                timeout: 10000,
                maximumAge: 0
            }
        );
    };

    const getAvailableAgents = () => {
        if (serviceMode === 'fin') {
            return agents.filter(agent => 
                agentsEnService.some(a => a.id === agent.id)
            );
        }
        return agents;
    };

    const handleSubmit = e => {
        e.preventDefault();
        setError("");
        setSuccess("");
        
        // Validation des coordonnées GPS
        const lat = dto.positionActuelle.latitude ? parseFloat(dto.positionActuelle.latitude) : 0.0;
        const lng = dto.positionActuelle.longitude ? parseFloat(dto.positionActuelle.longitude) : 0.0;
        
        if (!dto.positionActuelle.latitude || !dto.positionActuelle.longitude || isNaN(lat) || isNaN(lng)) {
            setError("⚠️ Veuillez obtenir votre position GPS avant de valider le pointage");
            return;
        }
        
        // Le backend attend latitude/longitude à la racine, pas dans positionActuelle
        const payload = {
            datePointage: dto.datePointage ? new Date(dto.datePointage).toISOString() : new Date().toISOString(),
            estPresent: dto.estPresent,
            estRetard: dto.estRetard,
            latitude: lat,
            longitude: lng,
            missionId: parseInt(dto.missionId, 10),
            agentId: parseInt(dto.agentId, 10)
        };
        
        console.log("Payload envoyé au backend:", payload);
        
        let call;
        if (isEdit) {
            call = PointageService.update(id, payload);
        } else if (serviceMode === 'prise') {
            call = PointageService.priseDeService(payload);
        } else if (serviceMode === 'fin') {
            call = PointageService.finDeService(payload);
        } else {
            call = PointageService.create(payload);
        }

        call.then((response) => {
            console.log("Réponse du serveur:", response);
            console.log("Mode de service:", serviceMode);
            console.log("isServiceMode:", isServiceMode);
            
            if (isServiceMode) {
                const message = serviceMode === 'prise' 
                    ? "✅ Prise de service enregistrée avec succès !" 
                    : "✅ Fin de service enregistrée avec succès !";
                
                const variant = serviceMode === 'prise' ? 'success' : 'info';
                
                console.log("Affichage du toast avec message:", message);
                setToastMessage(message);
                setToastVariant(variant);
                setShowToast(true);
                
                // Rediriger vers la liste des pointages après 1.5 secondes
                console.log("Redirection programmée dans 1.5 secondes...");
                setTimeout(() => {
                    console.log("Redirection vers /pointages");
                    navigate("/pointages");
                }, 1500);
            } else {
                console.log("Redirection immédiate vers /pointages");
                navigate("/pointages");
            }
        })
        .catch(err => {
            console.error("Erreur lors de la soumission:", err);
            setError(err.response?.data?.message || "Erreur serveur");
        });
    };

    return (
        <div className="pointage-form">
            <h2>
                {isEdit ? "Modifier un pointage" : 
                 serviceMode === 'prise' ? "🟢 Prise de Service" :
                 serviceMode === 'fin' ? "🔴 Fin de Service" :
                 "Créer un pointage"}
            </h2>
            {error && <p className="error">❌ {error}</p>}
            {success && <p className="success">✅ {success}</p>}
            
            {}
            {isServiceMode && (
                <div className="auto-info">
                    <h4>ℹ️ Informations automatiques</h4>
                    <ul>
                        <li>📅 Date/Heure : <strong>{new Date(dto.datePointage).toLocaleString('fr-FR')}</strong></li>
                        <li>👤 Agent : <strong>{currentUser?.prenom} {currentUser?.nom}</strong> 
                            {!currentUser && <span className="warning"> (Non connecté)</span>}
                        </li>
                        <li>✅ Présence : <strong>Présent</strong></li>
                        <li>⏰ Retard : <strong className={dto.estRetard ? "text-danger" : "text-success"}>
                            {dto.estRetard ? "OUI" : "NON"}
                        </strong>
                        {dto.estRetard && selectedMission?.heureDebut && (
                            <span className="text-muted"> (Heure prévue: {selectedMission.heureDebut})</span>
                        )}
                        </li>
                        {dto.positionActuelle.latitude && (
                            <li>📍 GPS : <strong>{dto.positionActuelle.latitude}, {dto.positionActuelle.longitude}</strong></li>
                        )}
                    </ul>
                </div>
            )}
            
            <form onSubmit={handleSubmit}>
                {}
                {!isServiceMode && (
                    <label>
                        Date & heure *
                        <input
                            type="datetime-local"
                            name="datePointage"
                            value={dto.datePointage}
                            onChange={handleChange}
                            required
                        />
                    </label>
                )}

                {}
                <label>
                    Mission *
                    <select
                        name="missionId"
                        value={dto.missionId}
                        onChange={handleChange}
                        required
                    >
                        <option value="">-- Sélectionnez une mission --</option>
                        {missions.map(mission => (
                            <option key={mission.id} value={mission.id}>
                                {mission.titre} - {mission.site?.nom || 'Sans site'}
                                {isServiceMode && ` (${mission.agents?.length || 0} agent(s))`}
                            </option>
                        ))}
                    </select>
                </label>

                {}
                {dto.missionId && getAvailableAgents().length > 0 && (
                    <label>
                        Agent * {serviceMode === 'fin' && <span className="badge">En service uniquement</span>}
                        <select
                            name="agentId"
                            value={dto.agentId}
                            onChange={handleChange}
                            required
                        >
                            <option value="">-- Sélectionnez un agent --</option>
                            {getAvailableAgents().map(agent => (
                                <option key={agent.id} value={agent.id}>
                                    {agent.utilisateur?.prenom} {agent.utilisateur?.nom} 
                                    ({agent.numeroMatricule || 'N/A'})
                                </option>
                            ))}
                        </select>
                    </label>
                )}
                
                {dto.missionId && agents.length === 0 && (
                    <p className="warning">⚠️ Aucun agent assigné à cette mission</p>
                )}
                
                {dto.missionId && serviceMode === 'fin' && getAvailableAgents().length === 0 && (
                    <p className="warning">ℹ️ Aucun agent actuellement en service pour cette mission</p>
                )}

                {}
                <div className="gps-section">
                    <label>Position GPS *</label>
                    <button 
                        type="button" 
                        onClick={getCurrentPosition}
                        className="btn-gps"
                        disabled={gettingLocation}
                    >
                        {gettingLocation ? "📍 Obtention en cours..." : "📍 Obtenir ma position"}
                    </button>
                    
                    {dto.positionActuelle.latitude && dto.positionActuelle.longitude && (
                        <div className="gps-info">
                            ✅ Position enregistrée : {dto.positionActuelle.latitude}, {dto.positionActuelle.longitude}
                        </div>
                    )}
                    
                    {!isServiceMode && (
                        <div className="gps-manual">
                            <label>
                                Latitude
                                <input
                                    name="latitude"
                                    value={dto.positionActuelle.latitude}
                                    onChange={handleChange}
                                    required
                                    placeholder="Ex: 48.8566"
                                />
                            </label>
                            <label>
                                Longitude
                                <input
                                    name="longitude"
                                    value={dto.positionActuelle.longitude}
                                    onChange={handleChange}
                                    required
                                    placeholder="Ex: 2.3522"
                                />
                            </label>
                        </div>
                    )}
                </div>

                {}
                {!isServiceMode && (
                    <>
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                name="estPresent"
                                checked={dto.estPresent}
                                onChange={handleChange}
                            />
                            Présent
                        </label>
                        <label className="checkbox-label">
                            <input
                                type="checkbox"
                                name="estRetard"
                                checked={dto.estRetard}
                                onChange={handleChange}
                            />
                            Retard
                        </label>
                    </>
                )}

                {}
                <div className="form-actions">
                    <button 
                        type="submit" 
                        className={`btn-submit ${serviceMode === 'prise' ? 'btn-prise' : serviceMode === 'fin' ? 'btn-fin' : ''}`}
                        disabled={!dto.missionId || !dto.agentId || !dto.positionActuelle.latitude}
                    >
                        {isEdit ? "Mettre à jour" :
                         serviceMode === 'prise' ? "✓ Confirmer la prise de service" :
                         serviceMode === 'fin' ? "✓ Confirmer la fin de service" :
                         "Créer"}
                    </button>
                    <button 
                        type="button" 
                        onClick={() => navigate("/pointages")}
                        className="btn-cancel"
                    >
                        Annuler
                    </button>
                </div>
            </form>
            
            {/* Toast de confirmation */}
            <ToastContainer position="top-end" className="p-3" style={{ zIndex: 9999 }}>
                <Toast 
                    show={showToast} 
                    onClose={() => setShowToast(false)} 
                    delay={3000} 
                    autohide
                    bg={toastVariant}
                >
                    <Toast.Header>
                        <strong className="me-auto">
                            {toastVariant === 'success' ? '🟢 Prise de service' : '🔵 Fin de service'}
                        </strong>
                    </Toast.Header>
                    <Toast.Body className="text-white">{toastMessage}</Toast.Body>
                </Toast>
            </ToastContainer>
        </div>
    );
}