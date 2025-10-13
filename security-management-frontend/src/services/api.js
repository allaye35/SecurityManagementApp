// src/services/api.js
import axios from "axios";
import { tokenService } from "./auth/tokenService";

const BASE_URL = process.env.REACT_APP_API_BASE || "http://localhost:8080/api";

export const plain = axios.create({
  baseURL: BASE_URL,
  withCredentials: false,
});

const api = axios.create({
  baseURL: BASE_URL,
  withCredentials: false,
});

api.interceptors.request.use((config) => {
  const access = tokenService.getAccess();
  if (access) {
    config.headers = config.headers ?? {};
    config.headers.Authorization = `Bearer ${access}`;
  }
  return config;
});

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
        return Promise.reject(error);
      }

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
