// src/services/auth/AuthService.js
import api, { plain } from "../api";
import { tokenService } from "./tokenService";

const AuthService = {
  async login(email, password) {
    const { data } = await plain.post("/auth/login", { email, password });
    tokenService.setTokens({
      accessToken: data.accessToken,
      refreshToken: data.refreshToken,
    });
    tokenService.setUser({
      id: data.userId,
      role: data.role,
      userType: data.userType,
      email: data.email,
      nom: data.nom,
      prenom: data.prenom,
    });
    return data;
  },

  async refresh() {
    const refreshToken = tokenService.getRefresh();
    if (!refreshToken) return null;
    const { data } = await plain.post("/auth/refresh", { refreshToken });
    if (data?.accessToken) {
      tokenService.setTokens({ accessToken: data.accessToken });
    }
    return data?.accessToken ?? null;
  },

  async logout() {
    try {
      await api.post("/auth/logout");
    } catch {}
    tokenService.clear();
  },

  async registerAgent(dto) {
    const { data } = await plain.post("/auth/register-agent", dto);
    return data;
  },

  async registerClient(dto) {
    const { data } = await plain.post("/auth/register-client", dto);
    return data;
  },

  verifyEmailByToken(token) {
    return plain.get("/auth/verify-email", { params: { token } });
  },

  verifyEmailByCode(email, code) {
    return plain.post("/auth/verify-email/code", { email, code });
  },

  resendVerifyEmail(email) {
    return plain.post("/auth/verify-email/resend", { email });
  },

  requestPasswordReset(email) {
    return plain.post("/auth/password-reset/request", { email });
  },

  confirmPasswordReset(token, newPassword) {
    return plain.post("/auth/password-reset/confirm", { token, newPassword });
  },
};

export default AuthService;
