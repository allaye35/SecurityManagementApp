import React, { useState } from "react";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

export default function PasswordResetRequestPage() {
  const [email, setEmail] = useState("");
  const [msg, setMsg] = useState(""); const [err, setErr] = useState("");

  const submit = async (e) => {
    e.preventDefault(); setErr(""); setMsg("");
    try {
      await AuthService.requestPasswordReset(email);
      setMsg("Si un compte existe, un email de réinitialisation a été envoyé.");
    } catch {
      setErr("Impossible d’envoyer la demande.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Mot de passe oublié</h2>
      <form onSubmit={submit} className="auth-form">
        <input type="email" placeholder="Votre email" value={email} onChange={e=>setEmail(e.target.value)} required />
        {err && <p className="auth-error">{err}</p>}
        {msg && <p className="auth-ok">{msg}</p>}
        <button type="submit">Envoyer le lien</button>
      </form>
    </div>
  );
}
