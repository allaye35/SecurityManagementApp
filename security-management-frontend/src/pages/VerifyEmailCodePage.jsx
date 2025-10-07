// src/pages/VerifyEmailCodePage.jsx
import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

const clean = (s) => (s ?? "").toString().trim();

export default function VerifyEmailCodePage() {
  const [email, setEmail] = useState("");
  const [code, setCode]   = useState("");
  const [ok, setOk]       = useState("");
  const [err, setErr]     = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const submit = async (e) => {
    e.preventDefault();
    if (loading) return;
    setErr(""); setOk(""); setLoading(true);

    try {
      await AuthService.verifyEmailByCode(clean(email), clean(code));
      setOk("Email vérifié ✅. Redirection vers la connexion…");
      setTimeout(() => navigate("/login", { replace: true }), 2000);
    } catch (ex) {
      setErr(ex?.response?.data?.message ?? "Code invalide ou expiré.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Vérifier l’email par code</h2>

      <form onSubmit={submit} className="auth-form">
        <input
          type="email"
          placeholder="Votre email"
          value={email}
          onChange={(e)=>setEmail(e.target.value)}
          required
        />

        <input
          inputMode="numeric"
          pattern="\d{6}"
          maxLength={6}
          placeholder="Code reçu (6 chiffres)"
          value={code}
          onChange={(e)=>setCode(e.target.value.replace(/\D/g,''))}
          required
        />

        {err && (
          <p className="auth-error">
            {err}
            {" "}
            {}
            <Link to="/login">Se connecter</Link>
          </p>
        )}
        {ok && (
          <p className="auth-ok">
            {ok}{" "}
            <Link to="/login">Aller à la connexion maintenant</Link>
          </p>
        )}

        <button type="submit" disabled={loading}>
          {loading ? "Validation…" : "Valider"}
        </button>
      </form>

      {}
      <p className="auth-link" style={{ marginTop: 12 }}>
        <Link to="/verify-email/resend">Renvoyer l’email de vérification</Link>
        {" "}&nbsp;·&nbsp;{" "}
        <Link to="/login">Aller à la connexion</Link>
      </p>
    </div>
  );
}
