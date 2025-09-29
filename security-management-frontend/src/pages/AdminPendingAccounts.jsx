// src/pages/AdminPendingAccounts.jsx
import React, { useEffect, useState } from "react";
import adminSvc from "../services/adminAccountsService";

export default function AdminPendingAccounts() {
  const [agents, setAgents] = useState([]);
  const [clients, setClients] = useState([]);
  const [err, setErr] = useState("");

  const refresh = async () => {
    setErr("");
    try {
      const [a,c] = await Promise.all([
        adminSvc.getPendingAgents(),
        adminSvc.getPendingClients()
      ]);
      setAgents(a); setClients(c);
    } catch { setErr("Impossible de charger la liste."); }
  };

  useEffect(()=>{ refresh(); },[]);

  const okAgent  = async (id)=>{ try{ await adminSvc.approveAgent(id);  refresh(); } catch{} };
  const okClient = async (id)=>{ try{ await adminSvc.approveClient(id); refresh(); } catch{} };

  return (
    <div style={{padding:20}}>
      <h2>Comptes à valider</h2>
      {err && <p style={{color:"#b91c1c"}}>{err}</p>}

      <h3 style={{marginTop:16}}>Agents</h3>
      {agents.length===0 ? <p>Aucun agent en attente.</p> : (
        <table className="table">
          <thead><tr><th>Nom</th><th>Email</th><th>Action</th></tr></thead>
          <tbody>
            {agents.map(a=>(
              <tr key={a.id}>
                <td>{a.nom} {a.prenom}</td>
                <td>{a.email}</td>
                <td><button className="btn btn-success btn-sm" onClick={()=>okAgent(a.id)}>Approuver</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <h3 style={{marginTop:24}}>Clients</h3>
      {clients.length===0 ? <p>Aucun client en attente.</p> : (
        <table className="table">
          <thead><tr><th>Nom/Entreprise</th><th>Email</th><th>Action</th></tr></thead>
          <tbody>
            {clients.map(c=>(
              <tr key={c.id}>
                <td>{c.nom || c.representant || "—"}</td>
                <td>{c.email}</td>
                <td><button className="btn btn-success btn-sm" onClick={()=>okClient(c.id)}>Approuver</button></td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
