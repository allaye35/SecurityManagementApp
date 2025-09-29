import React, { useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

export default function PasswordResetConfirmPage() {
  const [params] = useSearchParams();
  const token = params.get("token") || "";
  const [pwd, setPwd] = useState("");
  const [confirm, setConfirm] = useState("");
  const [msg, setMsg] = useState(""); const [err, setErr] = useState("");

  const submit = async (e) => {
    e.preventDefault(); setErr(""); setMsg("");
    if (pwd !== confirm) { setErr("Les mots de passe ne correspondent pas."); return; }
    try {
      await AuthService.confirmPasswordReset(token, pwd);
      setMsg("Mot de passe mis à jour. Vous pouvez vous connecter.");
    } catch (ex) {
      setErr(ex.response?.data?.message ?? "Échec de la mise à jour.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Nouveau mot de passe</h2>
      <form onSubmit={submit} className="auth-form">
        <input type="password" placeholder="Nouveau mot de passe" value={pwd} onChange={e=>setPwd(e.target.value)} required />
        <input type="password" placeholder="Confirmer" value={confirm} onChange={e=>setConfirm(e.target.value)} required />
        {err && <p className="auth-error">{err}</p>}
        {msg && <p className="auth-ok">{msg}</p>}
        <button type="submit">Valider</button>
      </form>
      <p className="auth-link"><Link to="/login">Retour à la connexion</Link></p>
    </div>
  );
}
