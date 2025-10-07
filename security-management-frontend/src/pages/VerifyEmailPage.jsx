// src/pages/VerifyEmailPage.jsx
import React, { useEffect, useState } from "react";
import { useSearchParams, Link, useNavigate } from "react-router-dom";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

export default function VerifyEmailPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const [state, setState] = useState({ loading: true, ok: false, msg: "" });

  useEffect(() => {
    const token = params.get("token");
    if (!token) {
      setState({ loading: false, ok: false, msg: "Lien invalide." });
      return;
    }

    AuthService
      .verifyEmailByToken(token)
      .then(() => {
        setState({
          loading: false,
          ok: true,
          msg: "Email vérifié. Vous pouvez vous connecter.",
        });
        setTimeout(() => navigate("/login", { replace: true }), 2000);
      })
      .catch((e) =>
        setState({
          loading: false,
          ok: false,
          msg: e?.response?.data?.message ?? "Vérification impossible.",
        })
      );
  }, [params, navigate]);

  if (state.loading) {
    return (
      <div className="auth-container">
        <p>Vérification en cours…</p>
      </div>
    );
  }

  return (
    <div className="auth-container">
      {state.ok ? (
        <>
          <h2>✅ Succès</h2>
          <p>{state.msg}</p>
          <p>
            <Link to="/login">Aller à la connexion</Link>
          </p>
        </>
      ) : (
        <>
          <h2>❌ Erreur</h2>
          <p>{state.msg}</p>
          <p>
            <Link to="/login">Aller à la connexion</Link>
          </p>
        </>
      )}

      {}
      <div
        style={{
          display: "flex",
          gap: 8,
          flexWrap: "wrap",
          justifyContent: "center",
          marginTop: 8,
        }}
      >
        <Link className="btn-link" to="/verify-email/code">
          Saisir un code reçu
        </Link>
        <Link className="btn-link" to="/verify-email/resend">
          Renvoyer l’email
        </Link>
      </div>
    </div>
  );
}
