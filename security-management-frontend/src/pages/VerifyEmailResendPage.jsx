import React, { useState } from "react";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

export default function VerifyEmailResendPage() {
  const [email, setEmail] = useState("");
  const [msg, setMsg]     = useState("");
  const [err, setErr]     = useState("");

  const submit = async (e) => {
    e.preventDefault(); setErr(""); setMsg("");
    try {
      await AuthService.resendVerifyEmail(email.trim());
      setMsg("Si un compte existe, un nouvel email de vérification a été envoyé.");
    } catch (ex) {
      setErr(ex?.response?.data?.message ?? "Impossible d’envoyer l’email.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Renvoyer l’email de vérification</h2>
      <form onSubmit={submit} className="auth-form">
        <input
          type="email"
          placeholder="Votre email"
          value={email}
          onChange={e=>setEmail(e.target.value)}
          required
        />
        {err && <p className="auth-error">{err}</p>}
        {msg && <p className="auth-ok">{msg}</p>}
        <button type="submit">Envoyer</button>
      </form>
    </div>
  );
}