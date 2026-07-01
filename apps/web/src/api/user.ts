import { apiFetch } from "./client";

export interface UserResponse {
  id: string;
  login: string;
  displayName: string;
  isAdmin: boolean;
  avatar: string;
}

export interface UserStatsResponse {
  level: number;
  xp: number;
  xpToNextLevel: number;
  currentStreak: number;
  longestStreak: number;
  lessonsCompleted: number;
  exercisesSolved: number;
  achievementsUnlocked: number;
}

export interface UserSettingsResponse {
  theme: string;
  defaultLanguage: string;
  notificationsEnabled: boolean;
  soundEnabled: boolean;
}

export function getUser(id: string) {
  return apiFetch<UserResponse>(`/api/user/${id}`);
}

export function getUserStats(id: string) {
  return apiFetch<UserStatsResponse>(`/api/user/${id}/stats`);
}

export function getUserSettings(id: string) {
  return apiFetch<UserSettingsResponse>(`/api/user/${id}/settings`);
}

export function updateUserSettings(id: string, settings: Partial<UserSettingsResponse>) {
  return apiFetch<UserSettingsResponse>(`/api/user/${id}/settings`, {
    method: "PUT",
    body: JSON.stringify(settings),
  });
}
