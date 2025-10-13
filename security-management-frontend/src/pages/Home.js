import React from "react";
import { Link } from "react-router-dom";
import logo from "../assets/logo.jpg";
import { useAuth } from "../context/AuthContext";
import "../styles/Home.css";

export default function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div className="home-container">
      <header className="home-header">
        <img src={logo} alt="Boulevard Sécurité Logo" className="logo" />
        <h1>Bienvenue sur Boulevard Sécurité</h1>
        <p>
          Votre solution pour la gestion des agents de sécurité, missions, plannings,
          clients, entreprises, rapports, sites et géolocalisations.
        </p>
      </header>

      {}
      {!isAuthenticated && (
        <div className="home-cta">
          <Link to="/login" className="btn-primary">Se connecter</Link>
          <span className="cta-sep">ou</span>
          <Link to="/register" className="btn-link">Créer un compte</Link>
        </div>
      )}
    </div>
  );
}