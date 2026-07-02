import { apiFetch } from "./client";

export interface LoginResponse {
  token: string;
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export function login(loginValue: string, password: string) {
  return apiFetch<LoginResponse>("/api/user/login", {
    method: "POST",
    body: JSON.stringify({ login: loginValue, password }),
  });
}

export function register(data: RegisterPayload) {
  return apiFetch<LoginResponse>("/api/user/register", {
    method: "POST",
    body: JSON.stringify(data),
  });
}
