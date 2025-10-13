import React from "react";
import { Link } from "react-router-dom";
import { FaUserShield, FaBuilding } from "react-icons/fa";
import logo from "../assets/logo.jpg";
import "../styles/Home.css";

export default function RegisterChooserPage() {
  return (
    <div className="chooser-wrapper">
      <div className="chooser-card">
        <img src={logo} alt="Boulevard Sécurité" className="chooser-logo" />
        <h1 className="chooser-title">Créer un compte</h1>
        <p className="chooser-subtitle">Choisissez votre profil pour continuer.</p>

        <div className="chooser-grid">
          {}
          <Link
            to="/register/agent"
            className="card-select card-select--button"
            role="button"
            aria-label="Créer un compte agent de sécurité"
          >
            <div className="card-select__icon">
              <FaUserShield />
            </div>
            <div className="card-select__content">
              <div className="card-select__title">Je suis un agent</div>
              <div className="card-select__desc">
                Créer un compte agent de sécurité
              </div>
            </div>
            <span className="card-select__cta">Choisir</span>
          </Link>

          {}
          <Link
            to="/register/client"
            className="card-select card-select--button"
            role="button"
            aria-label="Créer un compte client (particulier / entreprise)"
          >
            <div className="card-select__icon">
              <FaBuilding />
            </div>
            <div className="card-select__content">
              <div className="card-select__title">Je suis un client</div>
              <div className="card-select__desc">
                Créer un compte client (particulier / entreprise)
              </div>
            </div>
            <span className="card-select__cta">Choisir</span>
          </Link>
        </div>

        <p className="chooser-foot">
          Déjà un compte ? <Link to="/login">Connexion</Link>
        </p>
      </div>
    </div>
  );
}
