import { apiFetch } from "./client";

export interface LoginResponse {
  token: string;
}

export function login(loginValue: string, password: string) {
  return apiFetch<LoginResponse>("/api/user/login", {
    method: "POST",
    body: JSON.stringify({ login: loginValue, password }),
  });
}
