import React, { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import PointageService from "../../services/PointageService";
import MissionService from "../../services/MissionService";
import AgentService from "../../services/AgentService";
import { useAuth } from "../../context/AuthContext";
import "../../styles/PointageForm.css";

export default function PointageForm() {
    const { id } = useParams();
    const [searchParams] = useSearchParams();
    const isEdit = Boolean(id);
    const navigate = useNavigate();
    const { user } = useAuth(); // Récupérer l'utilisateur du contexte
    
    // Mode service : prise ou fin de service
    const serviceMode = searchParams.get('mode'); // 'prise' ou 'fin'
    const isServiceMode = serviceMode === 'prise' || serviceMode === 'fin';

    const [dto, setDto] = useState({
        datePointage: new Date().toISOString().slice(0, 16), // Date/heure actuelle par défaut
        estPresent: true, // Toujours présent si l'agent pointe
        estRetard: false, // Sera calculé automatiquement
        positionActuelle: { latitude: "", longitude: "" },
        missionId: "",
        agentId: "" // Sera rempli avec l'utilisateur connecté
    });
    const [missions, setMissions] = useState([]);
    const [agents, setAgents] = useState([]);
    const [agentsEnService, setAgentsEnService] = useState([]);
    const [currentUser, setCurrentUser] = useState(null); // Utilisateur connecté
    const [selectedMission, setSelectedMission] = useState(null); // Mission sélectionnée pour calcul retard
    const [error, setError] = useState("");
    const [success, setSuccess] = useState("");
    const [gettingLocation, setGettingLocation] = useState(false);

    // Récupérer l'agent de l'utilisateur connecté au montage
    useEffect(() => {
        if (user && user.id && !dto.agentId) {
            // Essayer de trouver l'agent correspondant à l'utilisateur
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
        
        // Obtenir automatiquement la géolocalisation au chargement
        if (isServiceMode) {
            setTimeout(() => getCurrentPosition(), 500); // Petit délai pour éviter les problèmes
        }
    }, [user, isServiceMode]);

    // Charger les missions au montage
    useEffect(() => {
        MissionService.getAllMissions()
            .then(({ data }) => {
                // Si mode service, filtrer les missions actives
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

    // Charger les agents quand une mission est sélectionnée
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
                    
                    // Convertir agentIds en tableau selon le format reçu
                    let agentIdsArray = [];
                    if (data.agentIds) {
                        if (Array.isArray(data.agentIds)) {
                            agentIdsArray = data.agentIds;
                        } else if (typeof data.agentIds === 'object') {
                            // Si c'est un objet (Set en Java devient un objet en JSON)
                            agentIdsArray = Array.from(Object.values(data.agentIds));
                        } else if (typeof data.agentIds === 'number') {
                            // Si c'est un seul ID
                            agentIdsArray = [data.agentIds];
                        }
                    }
                    
                    // Filtrer les valeurs nulles ou undefined
                    agentIdsArray = agentIdsArray.filter(id => id != null && id !== undefined);
                    
                    console.log("Agent IDs convertis en tableau:", agentIdsArray);
                    
                    // Sauvegarder la mission sélectionnée pour le calcul du retard
                    setSelectedMission(data);
                    
                    // Calculer automatiquement le retard si on est en mode prise de service
                    if (serviceMode === 'prise' && data.heureDebut) {
                        const heureActuelle = new Date();
                        const heureDebut = new Date();
                        const [hours, minutes] = data.heureDebut.split(':');
                        heureDebut.setHours(parseInt(hours), parseInt(minutes), 0);
                        
                        // Tolérance de 15 minutes
                        const tolerance = 15 * 60 * 1000; // 15 minutes en millisecondes
                        const estEnRetard = heureActuelle.getTime() > (heureDebut.getTime() + tolerance);
                        
                        console.log("Calcul retard - Heure début:", data.heureDebut, "Heure actuelle:", heureActuelle.toLocaleTimeString(), "En retard?", estEnRetard);
                        setDto(d => ({ ...d, estRetard: estEnRetard }));
                    }
                    
                    // Vérifier si on a des IDs d'agents
                    if (agentIdsArray.length > 0) {
                        console.log("Récupération des agents pour les IDs:", agentIdsArray);
                        // Récupérer les détails complets de chaque agent
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
                    
                    // Si c'est une fin de service, charger les agents en service
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
                    positionActuelle: data.positionActuelle || { latitude: "", longitude: "" },
                    missionId: data.mission?.id ?? "",
                    agentId: data.agent?.id ?? ""
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

    // Obtenir la position GPS
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

    // Filtrer les agents selon le mode
    const getAvailableAgents = () => {
        if (serviceMode === 'fin') {
            // Pour la fin de service, montrer seulement les agents en service
            return agents.filter(agent => 
                agentsEnService.some(a => a.id === agent.id)
            );
        }
        // Pour les autres modes, montrer tous les agents
        return agents;
    };

    const handleSubmit = e => {
        e.preventDefault();
        setError("");
        setSuccess("");
        
        const payload = {
            datePointage: dto.datePointage ? new Date(dto.datePointage).toISOString() : new Date().toISOString(),
            estPresent: dto.estPresent,
            estRetard: dto.estRetard,
            positionActuelle: {
                latitude: parseFloat(dto.positionActuelle.latitude),
                longitude: parseFloat(dto.positionActuelle.longitude)
            },
            missionId: parseInt(dto.missionId, 10),
            agentId: parseInt(dto.agentId, 10)
        };
        
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

        call.then(() => {
            if (isServiceMode) {
                setSuccess(
                    serviceMode === 'prise' 
                        ? "Prise de service enregistrée avec succès !" 
                        : "Fin de service enregistrée avec succès !"
                );
                // Réinitialiser le formulaire après 2 secondes
                setTimeout(() => {
                    setDto({
                        datePointage: "",
                        estPresent: true,
                        estRetard: false,
                        positionActuelle: { latitude: "", longitude: "" },
                        missionId: "",
                        agentId: ""
                    });
                    setSuccess("");
                }, 2000);
            } else {
                navigate("/pointages");
            }
        })
        .catch(err => setError(err.response?.data?.message || "Erreur serveur"));
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
            
            {/* Informations automatiques */}
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
                {/* Date & heure - uniquement en mode création classique */}
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

                {/* Mission */}
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

                {/* Agent - seulement si mission sélectionnée */}
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

                {/* Position GPS */}
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

                {/* Checkboxes - uniquement en mode classique */}
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

                {/* Boutons */}
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
        </div>
    );
}