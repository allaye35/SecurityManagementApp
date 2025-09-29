import React, { useState } from "react";
import { Link } from "react-router-dom";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

const STATUTS = ["EN_SERVICE", "EN_CONGE", "ABSENT"];

export default function RegisterAgentPage() {
  const [form, setForm] = useState({
    nom: "", prenom: "", email: "",
    telephone: "", adresse: "",
    dateNaissance: "", statut: "EN_SERVICE",
    password: "", confirm: "",
  });

  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");

  const onField = (e) =>
    setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const submit = async (e) => {
    e.preventDefault(); setErr(""); setMsg("");
    if (form.password !== form.confirm) {
      setErr("Les mots de passe ne correspondent pas."); return;
    }

    const dto = {
      nom: form.nom.trim(),
      prenom: form.prenom.trim(),
      email: form.email.trim(),
      password: form.password,
      telephone: form.telephone?.trim() || undefined,
      adresse: form.adresse?.trim() || undefined,
      dateNaissance: form.dateNaissance || undefined,
      statut: form.statut || undefined,
    };

    try {
      await AuthService.registerAgent(dto);
      setMsg(
        "Compte créé. Nous vous avons envoyé un email avec un LIEN de vérification " +
        "et un CODE à 6 chiffres. Cliquez sur le lien OU saisissez le code."
      );
      // ⛔️ Plus de redirection automatique : on laisse l’utilisateur choisir.
    } catch (ex) {
      const apiMsg =
        ex?.response?.data?.message ||
        (ex?.response?.status === 409 ? "Email (ou téléphone) déjà utilisé." : null);
      setErr(apiMsg || "Erreur lors de l’inscription.");
    }
  };

  return (
    <div className="auth-container">
      <h2>Créer un compte agent</h2>

      <form onSubmit={submit} className="auth-form" autoComplete="off">
        <input name="nom" placeholder="Nom" value={form.nom} onChange={onField} required />
        <input name="prenom" placeholder="Prénom" value={form.prenom} onChange={onField} required />
        <input type="email" name="email" placeholder="Email" value={form.email} onChange={onField} required />

        <input name="telephone" placeholder="Téléphone (optionnel)" value={form.telephone} onChange={onField} />
        <input name="adresse" placeholder="Adresse (optionnel)" value={form.adresse} onChange={onField} />

        <label className="auth-label">Date de naissance</label>
        <input type="date" name="dateNaissance" value={form.dateNaissance} onChange={onField} />

        <label className="auth-label">Statut</label>
        <select name="statut" value={form.statut} onChange={onField}>
          {STATUTS.map((s) => <option key={s} value={s}>{s.replace("_", " ")}</option>)}
        </select>

        <input type="password" name="password" placeholder="Mot de passe" value={form.password} onChange={onField} required />
        <input type="password" name="confirm" placeholder="Confirmer le mot de passe" value={form.confirm} onChange={onField} required />

        {err && <p className="auth-error">{err}</p>}

        <button type="submit">S’inscrire</button>
      </form>

      {msg && (
        <div style={{ marginTop: 14 }}>
          <p className="auth-ok">{msg}</p>
          <div style={{ display: "flex", gap: 8, flexWrap: "wrap", justifyContent: "center" }}>
            <Link className="btn-link" to="/verify-email/code">Saisir un code reçu</Link>
            <Link className="btn-link" to="/verify-email/resend">Renvoyer l’email</Link>
            <Link className="btn-link" to="/login">Aller à la connexion</Link>
          </div>
        </div>
      )}

      <p className="auth-link" style={{ marginTop: 10 }}>
        Déjà un compte ? <Link to="/login">Connexion</Link>
      </p>
    </div>
  );
}
