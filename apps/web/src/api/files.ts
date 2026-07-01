import { apiFetch } from "./client";

export interface FSEntryResponse {
  name: string;
  path: string;
  isDirectory: boolean;
}

export function getFolder(path = "") {
  return apiFetch<FSEntryResponse[]>(`/api/folders?path=${encodeURIComponent(path)}`);
}

export async function getFile(path: string) {
  const token = localStorage.getItem("ping.auth.token");
  const response = await fetch(`${import.meta.env.VITE_API_URL ?? "http://localhost:8080"}/api/files?path=${encodeURIComponent(path)}`, {
    headers: token ? { Authorization: `Bearer ${token}` } : undefined,
  });

  if (!response.ok) {
    throw new Error(await response.text());
  }

  return response.text();
}

export function uploadFile(path: string, content: string) {
  return apiFetch<void>(`/api/files/upload?path=${encodeURIComponent(path)}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/octet-stream",
    },
    body: content,
  });
}
