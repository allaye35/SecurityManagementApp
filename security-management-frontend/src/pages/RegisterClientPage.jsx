import React, { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import AuthService from "../services/auth/AuthService";
import "../styles/Auth.css";

const TYPES = [
  { value: "PARTICULIER", label: "Particulier" },
  { value: "ENTREPRISE",  label: "Entreprise"  },
];

const MODES = [
  { value: "EMAIL",      label: "Email" },
  { value: "TELEPHONE",  label: "Téléphone" },
  { value: "AUCUN",      label: "Sans préférence" },
];

export default function RegisterClientPage() {
  const [form, setForm] = useState({
    typeClient: "PARTICULIER",

    nom: "", prenom: "",

    siege: "", representant: "", numeroSiret: "",

    email: "", telephone: "", adresse: "", numeroRue: "",
    codePostal: "", ville: "", pays: "",
    modeContactPrefere: "EMAIL",

    password: "", confirm: "",
  });

  const [msg, setMsg] = useState("");
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(false);

  const isEntreprise = useMemo(() => form.typeClient === "ENTREPRISE", [form.typeClient]);

  const onField = (e) => setForm((f) => ({ ...f, [e.target.name]: e.target.value }));

  const submit = async (e) => {
    e.preventDefault();
    if (loading) return;

    setErr(""); setMsg(""); setLoading(true);

    if (form.password !== form.confirm) {
      setErr("Les mots de passe ne correspondent pas.");
      setLoading(false);
      return;
    }
    if (isEntreprise) {
      if (!form.siege?.trim() || !form.representant?.trim()) {
        setErr("Pour une entreprise, 'Siège' et 'Représentant' sont requis.");
        setLoading(false);
        return;
      }
    } else {
      if (!form.nom?.trim() || !form.prenom?.trim()) {
        setErr("Nom et prénom sont requis pour un particulier.");
        setLoading(false);
        return;
      }
    }

    const dto = {
      password: form.password,
      typeClient: form.typeClient,

      nom: isEntreprise ? undefined : form.nom.trim(),
      prenom: isEntreprise ? undefined : form.prenom.trim(),

      siege: isEntreprise ? form.siege.trim() : undefined,
      representant: isEntreprise ? form.representant.trim() : undefined,
      numeroSiret: isEntreprise ? (form.numeroSiret?.trim() || undefined) : undefined,

      email: form.email.trim(),
      telephone: form.telephone?.trim() || undefined,
      adresse: form.adresse?.trim() || undefined,
      numeroRue: form.numeroRue?.trim() || undefined,
      codePostal: form.codePostal?.trim() || undefined,
      ville: form.ville?.trim() || undefined,
      pays: form.pays?.trim() || undefined,
      modeContactPrefere: form.modeContactPrefere || undefined,
    };

    try {
      await AuthService.registerClient(dto);
      setMsg(
        "Compte client créé. Nous vous avons envoyé un email avec un LIEN et un CODE à 6 chiffres. " +
        "Cliquez sur le lien OU saisissez le code pour activer votre compte."
      );
    } catch (ex) {
      const apiMsg = ex?.response?.data?.message
        || (ex?.response?.status === 409 ? "Email déjà utilisé." : null);
      setErr(apiMsg || "Erreur lors de l’inscription.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Créer un compte client</h2>

      <form onSubmit={submit} className="auth-form" autoComplete="off">
        {}
        <label className="auth-label">Type de client</label>
        <select name="typeClient" value={form.typeClient} onChange={onField}>
          {TYPES.map(t => <option key={t.value} value={t.value}>{t.label}</option>)}
        </select>

        {}
        {!isEntreprise && (
          <>
            <input name="nom" placeholder="Nom" value={form.nom} onChange={onField} required />
            <input name="prenom" placeholder="Prénom" value={form.prenom} onChange={onField} required />
          </>
        )}

        {}
        {isEntreprise && (
          <>
            <input name="siege" placeholder="Siège (ex: Société X, Paris)" value={form.siege} onChange={onField} required />
            <input name="representant" placeholder="Représentant légal" value={form.representant} onChange={onField} required />
            <input name="numeroSiret" placeholder="Numéro SIRET (optionnel)" value={form.numeroSiret} onChange={onField} />
          </>
        )}

        {}
        <input type="email" name="email" placeholder="Email" value={form.email} onChange={onField} required />
        <input name="telephone" placeholder="Téléphone (optionnel)" value={form.telephone} onChange={onField} />
        <input name="adresse" placeholder="Adresse (optionnel)" value={form.adresse} onChange={onField} />
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
          <input name="numeroRue" placeholder="N° rue" value={form.numeroRue} onChange={onField} />
          <input name="codePostal" placeholder="Code postal" value={form.codePostal} onChange={onField} />
        </div>
        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 8 }}>
          <input name="ville" placeholder="Ville" value={form.ville} onChange={onField} />
          <input name="pays" placeholder="Pays" value={form.pays} onChange={onField} />
        </div>

        <label className="auth-label">Mode de contact préféré</label>
        <select name="modeContactPrefere" value={form.modeContactPrefere} onChange={onField}>
          {MODES.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
        </select>

        {}
        <input type="password" name="password" placeholder="Mot de passe" value={form.password} onChange={onField} required />
        <input type="password" name="confirm" placeholder="Confirmer le mot de passe" value={form.confirm} onChange={onField} required />

        {err && <p className="auth-error">{err}</p>}
        <button type="submit" disabled={loading}>{loading ? "Création…" : "S’inscrire"}</button>
      </form>

      {msg && (
        <div style={{ marginTop: 14 }}>
          <p className="auth-ok">{msg}</p>
          <div style={{ display:"flex", gap:8, flexWrap:"wrap", justifyContent:"center" }}>
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
