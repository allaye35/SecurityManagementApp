// src/services/api.js
import axios from "axios";
import { tokenService } from "./auth/tokenService";

// ----- Base URL de l’API Spring -----
// Tu peux changer ici ou via .env => REACT_APP_API_URL
const BASE_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";

// Instance SANS auth pour login/refresh/verify, etc.
export const plain = axios.create({
  baseURL: BASE_URL,
  withCredentials: false,
});

// Instance AVEC auth pour toutes les routes protégées
const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: false,
});

// --- Intercepteur: ajoute le Bearer token à chaque requête ---
api.interceptors.request.use((config) => {
  const access = tokenService.getAccess();
  if (access) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${access}`;
  }
  return config;
});

// --- Intercepteur: si 401, tente un refresh une seule fois puis rejoue la requête ---
let isRefreshing = false;
let pending = [];

const runPending = (token) => {
  pending.forEach(({ resolve, reject, config }) => {
    if (token) {
      config.headers = config.headers ?? {};
      config.headers.Authorization = `Bearer ${token}`;
      resolve(api(config));
    } else {
      reject(new axios.Cancel("Refresh token failed"));
    }
  });
  pending = [];
};

api.interceptors.response.use(
  (res) => res,
  async (error) => {
    const original = error?.config;
    const status = error?.response?.status;

    if (status === 401 && !original?._retry) {
      original._retry = true;

      const refreshToken = tokenService.getRefresh();
      if (!refreshToken) {
        // pas de refresh => logout côté appelant
        return Promise.reject(error);
      }

      // File d’attente si un refresh est déjà en cours
      if (isRefreshing) {
        return new Promise((resolve, reject) => {
          pending.push({ resolve, reject, config: original });
        });
      }

      try {
        isRefreshing = true;
        const { data } = await plain.post("/auth/refresh", { refreshToken });
        const newAccess = data?.accessToken;
        if (newAccess) {
          tokenService.setTokens({ accessToken: newAccess });
          original.headers = original.headers ?? {};
          original.headers.Authorization = `Bearer ${newAccess}`;
          runPending(newAccess);
          return api(original);
        }
        runPending(null);
        return Promise.reject(error);
      } catch (e) {
        runPending(null);
        return Promise.reject(e);
      } finally {
        isRefreshing = false;
      }
    }

    return Promise.reject(error);
  }
);

export default api;
