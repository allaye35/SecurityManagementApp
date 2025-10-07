import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import Select from "react-select";
import FactureService from "../../services/FactureService";
import ClientService from "../../services/ClientService";
import EntrepriseService from "../../services/EntrepriseService";
import DevisService from "../../services/DevisService";
import MissionService from "../../services/MissionService";
import TarifMissionService from "../../services/TarifMissionService";
import "../../styles/FactureForm.css";

const STATUTS = ["EN_ATTENTE", "PAYEE", "EN_RETARD"];

export default function FactureForm() {
    const { id } = useParams();
    const isEdit = Boolean(id);
    const navigate = useNavigate();

    const [dto, setDto] = useState({
        referenceFacture: "",
        dateEmission: new Date().toISOString().split('T')[0],
        statut: STATUTS[0],
        montantHT: 0,
        montantTVA: 0,
        montantTTC: 0,
        devisId: "",
        entrepriseId: "",
        clientId: "",
        missionIds: [],
        dateDebut: "",
        dateFin: ""
    });
    
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);
    
    const [clients, setClients] = useState([]);
    const [entreprises, setEntreprises] = useState([]);
    const [devis, setDevis] = useState([]);
    const [missions, setMissions] = useState([]);
    const [missionsOptions, setMissionsOptions] = useState([]);
    const [selectedMissions, setSelectedMissions] = useState([]);
    const [clientEntreprises, setClientEntreprises] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    
    const [calculatedData, setCalculatedData] = useState({
        totalHT: 0,
        totalTVA: 0,
        totalTTC: 0,
        detailsMissions: []
    });

    useEffect(() => {
        setLoading(true);
        
        Promise.all([
            ClientService.getAll(),
            EntrepriseService.getAllEntreprises(),
            DevisService.getAll(),
            MissionService.getAllMissions()
        ])
        .then(([clientsRes, entreprisesRes, devisRes, missionsRes]) => {
            setClients(clientsRes.data || []);
            setEntreprises(entreprisesRes.data || []);
            setDevis(devisRes.data || []);
            setMissions(missionsRes.data || []);
            
            console.log("Entreprises chargées:", entreprisesRes.data);
            console.log("Missions chargées:", missionsRes.data);
        })
        .catch(err => {
            console.error("Erreur lors du chargement des données:", err);
            setError("Erreur lors du chargement des données de référence");
        })
        .finally(() => setLoading(false));

        if (isEdit) {
            setLoading(true);
            FactureService.getById(id)
                .then(({ data }) => {
                    setDto({
                        referenceFacture: data.referenceFacture,
                        dateEmission: data.dateEmission,
                        statut: data.statut,
                        montantHT: data.montantHT || 0,
                        montantTVA: data.montantTVA || 0,
                        montantTTC: data.montantTTC || 0,
                        devisId: data.devisId || "",
                        entrepriseId: data.entrepriseId,
                        clientId: data.clientId,
                        missionIds: data.missionIds || [],
                        dateDebut: data.dateDebut || "",
                        dateFin: data.dateFin || ""
                    });
                    
                    if (data.clientId) {
                        loadClientData(data.clientId, data.missionIds);
                    }
                })
                .catch(err => {
                    console.error("Erreur lors du chargement de la facture:", err);
                    setError("Impossible de charger la facture.");
                })
                .finally(() => setLoading(false));
        } else {
            const today = new Date();
            const referenceFacture = `FACT-${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${Math.random().toString(36).substring(2, 6).toUpperCase()}`;
            setDto(prev => ({ ...prev, referenceFacture }));
        }
    }, [id, isEdit]);

    const loadClientData = (clientId, selectedMissionIds = []) => {
        if (!clientId) return;
        setLoading(true);
        setError("");
        let filteredMissions = missions.filter(mission =>
            mission.clientId === parseInt(clientId) || mission.devis?.clientId === parseInt(clientId)
        );
        if (dto.devisId) {
            filteredMissions = filteredMissions.filter(mission =>
                mission.devisId === Number(dto.devisId)
            );
        }
        if (dto.dateDebut && dto.dateFin) {
            const start = new Date(dto.dateDebut);
            const end = new Date(dto.dateFin);
            filteredMissions = filteredMissions.filter(mission => {
                if (!mission.dateDebut || !mission.dateFin) return false;
                const missionStart = new Date(mission.dateDebut);
                const missionEnd = new Date(mission.dateFin);
                return (missionStart <= end && missionEnd >= start);
            });
        }
        const options = filteredMissions.map(mission => ({
            value: mission.id,
            label: mission.titre || `Mission #${mission.id} - ${mission.typeMission || "N/A"}`,
            mission: mission
        }));
        setMissionsOptions(options);
        if (selectedMissionIds && selectedMissionIds.length > 0) {
            const selected = options.filter(option => 
                selectedMissionIds.includes(option.value)
            );
            setSelectedMissions(selected);
            calculateAmounts(selected.map(option => option.mission));
        }
        if (!dto.dateDebut || !dto.dateFin) {
            setMissionsOptions(options);
        }
        const clientEntreprisesMock = entreprises;
        setClientEntreprises(clientEntreprisesMock);
        if (clientEntreprisesMock.length === 1) {
            setDto(prev => ({ ...prev, entrepriseId: clientEntreprisesMock[0].id }));
        }
        setLoading(false);
    };

    const filterMissionsByDate = (allMissions, startDate, endDate) => {
        if (!startDate || !endDate || !allMissions.length) return;
        const start = new Date(startDate);
        const end = new Date(endDate);
        const filteredMissions = allMissions.filter(mission => {
            if (!mission.dateDebut || !mission.dateFin) return false;
            const missionStart = new Date(mission.dateDebut);
            const missionEnd = new Date(mission.dateFin);
            return (missionStart <= end && missionEnd >= start);
        });
        const filteredOptions = filteredMissions.map(mission => ({
            value: mission.id,
            label: mission.titre || `Mission #${mission.id} - ${mission.typeMission || "N/A"}`,
            mission: mission
        }));
        setSelectedMissions(filteredOptions);
        setMissionsOptions(filteredOptions);
        calculateAmounts(filteredMissions);
    };

    const calculateAmounts = async (selectedMissionsList) => {
        if (!selectedMissionsList || selectedMissionsList.length === 0) {
            setCalculatedData({
                totalHT: 0,
                totalTVA: 0,
                totalTTC: 0,
                detailsMissions: []
            });
            setDto(prev => ({
                ...prev,
                montantHT: 0,
                montantTVA: 0,
                montantTTC: 0
            }));
            return;
        }
        const missionDetails = await Promise.all(
            selectedMissionsList.map(async mission => {
                if (mission.tarif) return mission;
                if (mission.tarifId) {
                    try {
                        const tarifResponse = await TarifMissionService.getById(mission.tarifId);
                        const tarif = tarifResponse.data;
                        const montantHT = mission.montantHT || 
                            (tarif.prixUnitaireHT * (mission.quantite || 1) * (mission.nombreAgents || 1));
                        const tauxTVA = tarif.tauxTVA || 0.2;
                        const montantTVA = mission.montantTVA || (montantHT * tauxTVA);
                        const montantTTC = mission.montantTTC || (montantHT + montantTVA);
                        return {
                            ...mission,
                            tarif,
                            montantHT,
                            montantTVA,
                            montantTTC
                        };
                    } catch (err) {
                        console.error("Erreur lors du chargement du tarif:", err);
                        return {
                            ...mission,
                            montantHT: mission.montantHT || 0,
                            montantTVA: mission.montantTVA || 0,
                            montantTTC: mission.montantTTC || 0
                        };
                    }
                }
                return {
                    ...mission,
                    montantHT: mission.montantHT || 0,
                    montantTVA: mission.montantTVA || 0,
                    montantTTC: mission.montantTTC || 0
                };
            })
        );
        const totalHT = missionDetails.reduce(
            (sum, mission) => sum + parseFloat(mission.montantHT || 0), 0
        );
        const totalTVA = missionDetails.reduce(
            (sum, mission) => sum + parseFloat(mission.montantTVA || 0), 0
        );
        const totalTTC = missionDetails.reduce(
            (sum, mission) => sum + parseFloat(mission.montantTTC || 0), 0
        );
        setCalculatedData({
            totalHT,
            totalTVA,
            totalTTC,
            detailsMissions: missionDetails
        });
        setDto(prev => ({
            ...prev,
            montantHT: totalHT,
            montantTVA: totalTVA,
            montantTTC: totalTTC
        }));
    };

    const handleChange = e => {
        const { name, value } = e.target;
        setDto(d => ({ ...d, [name]: value }));
        if (name === "clientId" && value) {
            loadClientData(value);
            setDto(d => ({ 
                ...d, 
                entrepriseId: "",
                missionIds: []
            }));
            setSelectedMissions([]);
        }
        if (name === "devisId" && value) {
            loadDevisInfo(value);
        }
        if ((name === "dateDebut" || name === "dateFin") && missions.length > 0) {
            const startDate = name === "dateDebut" ? value : dto.dateDebut;
            const endDate = name === "dateFin" ? value : dto.dateFin;
            if (startDate && endDate) {
                filterMissionsByDate(missions, startDate, endDate);
            }
        }
    };
    
    const handleMissionsChange = (selectedOptions) => {
        setSelectedMissions(selectedOptions || []);
        
        const selectedIds = selectedOptions ? selectedOptions.map(option => option.value) : [];
        setDto(prev => ({ ...prev, missionIds: selectedIds }));
        
        const selectedMissionsList = selectedOptions ? 
            selectedOptions.map(option => option.mission) : [];
        calculateAmounts(selectedMissionsList);
    };

    const handleSearchChange = (e) => {
        setSearchTerm(e.target.value.toLowerCase());
    };

    const handleSubmit = e => {
        e.preventDefault();
        setError("");
        setLoading(true);
        
        if (!dto.clientId) {
            setError("Veuillez sélectionner un client");
            setLoading(false);
            return;
        }
        
        if (!dto.entrepriseId) {
            setError("Veuillez sélectionner une entreprise");
            setLoading(false);
            return;
        }
        
        if (dto.missionIds.length === 0) {
            setError("Veuillez sélectionner au moins une mission");
            setLoading(false);
            return;
        }
        
        const payload = {
            referenceFacture: dto.referenceFacture,
            dateEmission: dto.dateEmission,
            statut: dto.statut,
            montantHT: Number(dto.montantHT),
            montantTVA: Number(dto.montantTVA),
            montantTTC: Number(dto.montantTTC),
            devisId: dto.devisId ? Number(dto.devisId) : undefined,
            entrepriseId: Number(dto.entrepriseId),
            clientId: Number(dto.clientId),
            missionIds: dto.missionIds.map(id => Number(id)),
            dateDebut: dto.dateDebut || undefined,
            dateFin: dto.dateFin || undefined
        };
        
        const call = isEdit
            ? FactureService.update(id, payload)
            : FactureService.create(payload);

        call
            .then(() => navigate("/factures"))
            .catch(err => setError(err.response?.data?.message || "Erreur serveur"))
            .finally(() => setLoading(false));
    };

    const loadDevisInfo = (devisId) => {
        if (!devisId) return;
        setLoading(true);
        DevisService.getById(devisId)
            .then(({ data }) => {
                setDto(prev => ({
                    ...prev,
                    montantHT: data.montantHT || prev.montantHT,
                    montantTVA: data.montantTVA || prev.montantTVA,
                    montantTTC: data.montantTTC || prev.montantTTC,
                    clientId: data.clientId || prev.clientId,
                    entrepriseId: data.entrepriseId || prev.entrepriseId
                }));
                if (data.clientId && data.clientId !== dto.clientId) {
                    loadClientData(data.clientId);
                }
            })
            .catch(err => {
                console.error("Erreur lors du chargement des informations du devis:", err);
                setError("Erreur lors du chargement des informations du devis");
            })
            .finally(() => setLoading(false));
    };

    const filteredClients = searchTerm 
        ? clients.filter(client => 
            (client.nom && client.nom.toLowerCase().includes(searchTerm)) || 
            (client.prenom && client.prenom.toLowerCase().includes(searchTerm)) || 
            (client.email && client.email.toLowerCase().includes(searchTerm)) ||
            (client.telephone && client.telephone?.includes(searchTerm))
        )
        : clients;

    return (
        <div className="facture-form">
            <h2>{isEdit ? "Modifier" : "Créer"} une facture</h2>
            {error && <p className="error">{error}</p>}
            {loading && <p className="loading">Chargement en cours...</p>}

            <form onSubmit={handleSubmit}>
                <div className="search-section">
                    <label>
                        Rechercher un client
                        <input
                            type="text"
                            value={searchTerm}
                            onChange={handleSearchChange}
                            placeholder="Nom, prénom, email ou téléphone"
                            className="form-control"
                        />
                    </label>
                </div>

                <div className="form-grid">
                    <div className="form-column">
                        <label>
                            Client <span className="required">*</span>
                            <select
                                name="clientId"
                                value={dto.clientId}
                                onChange={handleChange}
                                required
                                disabled={loading}
                            >
                                <option value="">Sélectionnez un client</option>
                                {filteredClients.map(client => (
                                    <option key={client.id} value={client.id}>
                                        {client.nom} {client.prenom || ''} - {client.email || ''}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Entreprise <span className="required">*</span>
                            <select
                                name="entrepriseId"
                                value={dto.entrepriseId}
                                onChange={handleChange}
                                required
                                disabled={loading || !dto.clientId}
                            >
                                <option value="">Sélectionnez une entreprise</option>
                                {(clientEntreprises.length > 0 
                                    ? clientEntreprises 
                                    : entreprises
                                ).map(entreprise => (
                                    <option key={entreprise.id} value={entreprise.id}>
                                        {entreprise.nom} - {entreprise.siren || 'N/A'}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <div className="date-range">
                            <label>
                                Période de facturation - Début
                                <input
                                    type="date"
                                    name="dateDebut"
                                    value={dto.dateDebut}
                                    onChange={handleChange}
                                    className="form-control"
                                    disabled={loading || !dto.clientId}
                                />
                            </label>

                            <label>
                                Période de facturation - Fin
                                <input
                                    type="date"
                                    name="dateFin"
                                    value={dto.dateFin}
                                    onChange={handleChange}
                                    className="form-control"
                                    disabled={loading || !dto.clientId || !dto.dateDebut}
                                    min={dto.dateDebut}
                                />
                            </label>
                        </div>

                        <label>
                            Réf. facture <span className="required">*</span>
                            <input
                                name="referenceFacture"
                                value={dto.referenceFacture}
                                onChange={handleChange}
                                required
                                disabled={loading}
                            />
                        </label>

                        <label>
                            Date émission <span className="required">*</span>
                            <input
                                type="date"
                                name="dateEmission"
                                value={dto.dateEmission}
                                onChange={handleChange}
                                required
                                disabled={loading}
                            />
                        </label>

                        <label>
                            Statut <span className="required">*</span>
                            <select
                                name="statut"
                                value={dto.statut}
                                onChange={handleChange}
                                required
                                disabled={loading}
                            >
                                {STATUTS.map(s => (
                                    <option key={s} value={s}>{s}</option>
                                ))}
                            </select>
                        </label>
                    </div>

                    <div className="form-column">
                        <label>
                            Devis associé
                            <select
                                name="devisId"
                                value={dto.devisId}
                                onChange={handleChange}
                                disabled={loading}
                            >
                                <option value="">Sélectionnez un devis (optionnel)</option>
                                {devis
                                    .filter(d => !dto.clientId || d.clientId === Number(dto.clientId))
                                    .map(d => (
                                        <option key={d.id} value={d.id}>
                                            {d.referenceDevis || `Devis #${d.id}`} - {d.montantTTC}€ TTC
                                        </option>
                                    ))}
                            </select>
                        </label>

                        <label>
                            Missions <span className="required">*</span>
                            <div className="select-mission-container">
                                <Select
                                    isMulti
                                    name="missions"
                                    value={selectedMissions}
                                    onChange={handleMissionsChange}
                                    options={missionsOptions}
                                    classNamePrefix="select"
                                    placeholder="Sélectionnez les missions à facturer"
                                    isDisabled={loading || !dto.clientId}
                                    noOptionsMessage={() => "Aucune mission disponible"}
                                />
                                <small className="help-text">
                                    {dto.dateDebut && dto.dateFin 
                                        ? "Les missions dans la période sélectionnée sont automatiquement incluses" 
                                        : "Sélectionnez une période pour filtrer les missions automatiquement"}
                                </small>
                            </div>
                        </label>

                        <div className="montants-section">
                            <h3>Montants calculés automatiquement</h3>
                            <div className="montant-item">
                                <label>Montant HT</label>
                                <div className="montant-value">{Number(dto.montantHT || 0).toFixed(2)} €</div>
                            </div>

                            <div className="montant-item">
                                <label>Montant TVA</label>
                                <div className="montant-value">{Number(dto.montantTVA || 0).toFixed(2)} €</div>
                            </div>

                            <div className="montant-item">
                                <label>Montant TTC</label>
                                <div className="montant-value">{Number(dto.montantTTC || 0).toFixed(2)} €</div>
                            </div>
                        </div>
                    </div>
                </div>

                {Array.isArray(calculatedData.detailsMissions) && calculatedData.detailsMissions.length > 0 && (
                    <div className="missions-details">
                        <h3>Détails des missions sélectionnées</h3>
                        <table className="missions-table">
                            <thead>
                                <tr>
                                    <th>Mission</th>
                                    <th>Type</th>
                                    <th>Période</th>
                                    <th>Agents</th>
                                    <th>Quantité</th>
                                    <th>Montant HT</th>
                                    <th>TVA</th>
                                    <th>Montant TTC</th>
                                </tr>
                            </thead>
                            <tbody>
                                {calculatedData.detailsMissions.filter(m => !!m).map(mission => (
                                    <tr key={mission.id || Math.random()}>
                                        <td>{mission.titre || `Mission #${mission.id || ''}`}</td>
                                        <td>{mission.typeMission || "N/A"}</td>
                                        <td>{mission.dateDebut ? new Date(mission.dateDebut).toLocaleDateString() : "N/A"} - {mission.dateFin ? new Date(mission.dateFin).toLocaleDateString() : "N/A"}</td>
                                        <td>{mission.nombreAgents != null ? mission.nombreAgents : 1}</td>
                                        <td>{mission.quantite != null ? mission.quantite : 1}</td>
                                        <td>{Number(mission.montantHT || 0).toFixed(2)} €</td>
                                        <td>{Number(mission.montantTVA || 0).toFixed(2)} €</td>
                                        <td>{Number(mission.montantTTC || 0).toFixed(2)} €</td>
                                    </tr>
                                ))}
                            </tbody>
                            <tfoot>
                                <tr>
                                    <th colSpan="5">Total</th>
                                    <th>{Number(calculatedData.totalHT || 0).toFixed(2)} €</th>
                                    <th>{Number(calculatedData.totalTVA || 0).toFixed(2)} €</th>
                                    <th>{Number(calculatedData.totalTTC || 0).toFixed(2)} €</th>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                )}

                <div className="form-actions">
                    <button 
                        type="submit" 
                        className="btn-submit"
                        disabled={loading || selectedMissions.length === 0}
                    >
                        {loading ? "Traitement en cours..." : (isEdit ? "Mettre à jour" : "Créer")}
                    </button>
                    <button
                        type="button"
                        className="btn-cancel"
                        onClick={() => navigate("/factures")}
                        disabled={loading}
                    >
                        Annuler
                    </button>
                </div>
            </form>
        </div>
    );
}
