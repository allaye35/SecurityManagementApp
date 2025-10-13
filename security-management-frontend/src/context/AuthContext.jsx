import { createContext, useContext, useMemo, useState, useEffect } from "react";
import AuthService from "../services/auth/AuthService";
import { tokenService } from "../services/auth/tokenService";

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => tokenService.getUser());

  const isAuthenticated = !!(tokenService.getAccess() && user);

  useEffect(() => {
    const checkAuth = () => {
      const token = tokenService.getAccess();
      const storedUser = tokenService.getUser();
      
      if (!token || !storedUser) {
        tokenService.clear();
        setUser(null);
      }
    };

    checkAuth();

    const interval = setInterval(checkAuth, 5 * 60 * 1000);
    
    return () => clearInterval(interval);
  }, []);

  const login = async (email, password) => {
    const data = await AuthService.login(email, password);
    const u = {
      id: data.userId,
      role: data.role,
      userType: data.userType,
      email: data.email,
      nom: data.nom,
      prenom: data.prenom,
    };
    tokenService.setUser(u);
    setUser(u);
    return u;
  };

  const logout = async () => {
    try { 
      await AuthService.logout(); 
    } catch {}
    tokenService.clear();
    setUser(null);
  };

  const hasRole = (...roles) => !!user && roles.flat().includes(user.role);

  const value = useMemo(
    () => ({ user, isAuthenticated, hasRole, login, logout }),
    [user, isAuthenticated]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export const useAuth = () => useContext(AuthContext);
