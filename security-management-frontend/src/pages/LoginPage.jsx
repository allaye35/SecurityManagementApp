import React, { useState } from "react";
import { useLocation, useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import logo from "../assets/logo.jpg";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const redirectTo = location.state?.from?.pathname || "/home";

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (loading) return;
    setErr("");
    setLoading(true);

    try {
      await login(email, password);
      navigate(redirectTo, { replace: true });
    } catch (ex) {
      const status = ex?.response?.status;
      const msg = ex?.response?.data?.message || "";

      if (status === 403 && /attente d’approbation|attente d'approbation/i.test(msg)) {
        setErr(<>Votre compte est vérifié mais <b>en attente d’approbation</b> par un administrateur.</>);
      } else if (status === 403) {
        setErr(
          <>
            Adresse email non vérifiée. Consultez votre boîte mail pour activer le compte.
            <br />
            <Link to="/verify-email/code">👉 J’ai un code à saisir</Link>
            &nbsp;·&nbsp;
            <Link to="/verify-email/resend">Renvoyer l’email</Link>
          </>
        );
      } else if (status === 401) {
        setErr("Email ou mot de passe invalide.");
      } else {
        setErr("Impossible de se connecter pour le moment.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        background: "linear-gradient(120deg,#e3f0ff 0%,#f4f7fa 100%)",
      }}
    >
      <div
        style={{
          background: "#fff",
          borderRadius: 22,
          boxShadow: "0 8px 32px #1976d220",
          padding: 44,
          minWidth: 380,
          maxWidth: 440,
          width: "100%",
          position: "relative",
        }}
      >
        <div
          style={{
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
            marginBottom: 18,
          }}
        >
          <img
            src={logo}
            alt="Boulevard Sécurité"
            style={{
              width: 130,
              height: 130,
              borderRadius: "50%",
              objectFit: "cover",
              marginBottom: 12,
              boxShadow: "0 4px 16px #1976d220",
              border: "4px solid #1976d2",
            }}
          />
          <span
            style={{
              fontWeight: 700,
              color: "#1976d2",
              fontSize: 22,
              letterSpacing: 1,
              marginBottom: 2,
            }}
          >
            Boulevard Sécurité
          </span>
        </div>

        <h2
          style={{
            textAlign: "center",
            marginBottom: 28,
            color: "#1976d2",
            letterSpacing: 1,
            fontSize: 26,
          }}
        >
          Connexion
        </h2>

        <form onSubmit={handleSubmit} autoComplete="off">
          <div style={{ marginBottom: 22 }}>
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              type="email"
              placeholder="Adresse email"
              required
              autoComplete="off"
              style={{
                width: "100%",
                padding: "16px 16px",
                borderRadius: 10,
                border: "1.5px solid #b0bec5",
                fontSize: 18,
                outline: "none",
                marginBottom: 6,
                background: "#f8fbff",
              }}
            />
          </div>

          <div style={{ marginBottom: 22 }}>
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              type="password"
              placeholder="Mot de passe"
              required
              autoComplete="new-password"
              style={{
                width: "100%",
                padding: "16px 16px",
                borderRadius: 10,
                border: "1.5px solid #b0bec5",
                fontSize: 18,
                outline: "none",
                marginBottom: 6,
                background: "#f8fbff",
              }}
            />
          </div>

          {err && (
            <div
              style={{
                color: "#d32f2f",
                background: "#ffebee",
                borderRadius: 8,
                padding: "10px 14px",
                marginBottom: 18,
                textAlign: "center",
                fontWeight: 500,
              }}
            >
              {err}
            </div>
          )}

          <button
            type="submit"
            disabled={loading}
            style={{
              width: "100%",
              background: loading ? "#8ab4f8" : "#1976d2",
              color: "#fff",
              border: "none",
              borderRadius: 10,
              padding: "15px 0",
              fontSize: 19,
              fontWeight: 700,
              cursor: loading ? "not-allowed" : "pointer",
              boxShadow: "0 2px 12px #1976d220",
              letterSpacing: 1,
              transition: "background 0.2s",
            }}
          >
            {loading ? "Connexion…" : "Se connecter"}
          </button>
        </form>

        <p style={{ textAlign: "center", marginTop: 12 }}>
          <Link
            to="/password-reset/request"
            style={{ color: "#1976d2", textDecoration: "none", fontWeight: 600 }}
          >
            Mot de passe oublié ?
          </Link>
        </p>

        <p style={{ textAlign: "center", marginTop: 10, fontSize: 16 }}>
          <Link
            to="/register"
            style={{ color: "#1976d2", textDecoration: "none", fontWeight: 600 }}
          >
            Créer un compte
          </Link>
        </p>
      </div>
    </div>
  );
}