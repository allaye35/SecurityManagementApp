// src/routes/ProtectedRoute.jsx
import { Navigate, Outlet, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function ProtectedRoute({ roles }) {
  const { isAuthenticated, hasRole, user } = useAuth();
  const location = useLocation();

  // Vérification 1: L'utilisateur doit être authentifié
  if (!isAuthenticated || !user) {
    // Sauvegarder la page demandée pour redirection après connexion
    return <Navigate to="/login" replace state={{ from: location }} />;
  }

  // Vérification 2: Si des rôles spécifiques sont requis, les vérifier
  if (roles?.length && !hasRole(roles)) {
    // Rediriger vers une page d'erreur ou la page d'accueil
    return <Navigate to="/unauthorized" replace />;
  }

  // L'utilisateur est authentifié et autorisé
  return <Outlet />;
}
