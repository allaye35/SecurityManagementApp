import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import ZoneService from "../../services/ZoneService";
import AgentService from "../../services/AgentService";
import ZoneForm from "./ZoneForm";

const ZoneEdit = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [data, setData] = useState({
        nom: "",
        typeZone: "VILLE",
        codePostal: "",
        ville: "",
        departement: "",
        region: "",
        pays: ""
    });
    const [error, setError] = useState(null);
    const [agents, setAgents] = useState([]);
    const [selectedAgents, setSelectedAgents] = useState([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const zoneResponse = await ZoneService.getById(id);
                setData(zoneResponse.data);
                
                const agentsResponse = await AgentService.getAllAgents();
                setAgents(agentsResponse.data);
                
                const zoneAgentsResponse = await ZoneService.getAgentsForZone(id);
                const zoneAgents = zoneAgentsResponse.data;
                
                setSelectedAgents(zoneAgents.map(agent => agent.id));
                
                setLoading(false);
            } catch (err) {
                console.error("Erreur lors du chargement des données:", err);
                setError("Impossible de charger les données nécessaires.");
                setLoading(false);
            }
        };

        fetchData();
    }, [id]);

    const handleSubmit = async e => {
        e.preventDefault();
        setError(null);
        try {
            await ZoneService.update(id, data);
            
            const zoneAgentsResponse = await ZoneService.getAgentsForZone(id);
            const currentAgents = zoneAgentsResponse.data.map(agent => agent.id);
            
            const agentsToAdd = selectedAgents.filter(agentId => !currentAgents.includes(agentId));
            const agentsToRemove = currentAgents.filter(agentId => !selectedAgents.includes(agentId));
            
            if (agentsToAdd.length > 0) {
                await Promise.all(agentsToAdd.map(agentId => 
                    ZoneService.assignAgentToZone(id, agentId)
                ));
            }
            
            if (agentsToRemove.length > 0) {
                await Promise.all(agentsToRemove.map(agentId => 
                    ZoneService.removeAgentFromZone(id, agentId)
                ));
            }
            
            navigate("/zones");
        } catch (err) {
            console.error("Erreur lors de la mise à jour de la zone:", err);
            setError("Échec de la mise à jour. Vérifiez la console pour plus de détails.");
        }
    };

    if (loading) {
        return <div className="loading">Chargement...</div>;
    }

    return (
        <ZoneForm
            title="Modifier"
            data={data}
            setData={setData}
            onSubmit={handleSubmit}
            error={error}
            agents={agents}
            selectedAgents={selectedAgents}
            setSelectedAgents={setSelectedAgents}
        />
    );
};

export default ZoneEdit;
