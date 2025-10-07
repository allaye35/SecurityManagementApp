import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import DiplomeService from "../../services/DiplomeService";
import AgentService from "../../services/AgentService";
import DiplomeForm from "./DiplomeForm";

const DiplomeCreate = () => {
    const navigate = useNavigate();
    const [data, setData] = useState({
        agentId: "",
        niveau: "SSIAP_1",
        dateObtention: "",
        dateExpiration: ""
    });
    const [agents, setAgents] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        setLoading(true);
        AgentService.getAllAgents()
            .then(response => {
                const sortedAgents = [...response.data].sort((a, b) => 
                    `${a.nom} ${a.prenom}`.localeCompare(`${b.nom} ${b.prenom}`)
                );
                setAgents(sortedAgents);
                setLoading(false);
            })
            .catch(err => {
                console.error("Erreur lors du chargement des agents:", err);
                setError("Impossible de charger la liste des agents.");
                setLoading(false);
            });
    }, []);

    const handleSubmit = async e => {
        e.preventDefault();
        setError(null);
        setIsSubmitting(true);
        
        if (!data.agentId) {
            setError("Veuillez sélectionner un agent");
            setIsSubmitting(false);
            return;
        }
        
        try {
            const payload = {
                agentId: Number(data.agentId),
                niveau: data.niveau,
                dateObtention: data.dateObtention || null,
                dateExpiration: data.dateExpiration || null
            };
            
            await DiplomeService.create(payload);
            
            navigate("/diplomes-ssiap");
        } catch (err) {
            console.error("Erreur lors de la création:", err);
            
            if (err.response) {
                setError(`Échec de la création: ${err.response.data?.message || `Erreur ${err.response.status}`}`);
            } else if (err.request) {
                setError("Aucune réponse du serveur. Vérifiez votre connexion internet.");
            } else {
                setError(`Erreur: ${err.message || "Une erreur inconnue s'est produite"}`);
            }
            setIsSubmitting(false);
        }
    };

    return (
        <DiplomeForm
            title="Créer"
            data={data}
            setData={setData}
            onSubmit={handleSubmit}
            error={error}
            agents={agents}
            isSubmitting={isSubmitting}
        />
    );
};

export default DiplomeCreate;
