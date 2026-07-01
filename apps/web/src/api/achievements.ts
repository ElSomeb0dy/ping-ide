import { apiFetch } from "./client";

export interface AchievementResponse {
  id: string;
  code: string;
  title: string;
  description: string;
  icon: string;
  criteriaType: string;
  criteriaValue: number;
  xpReward: number;
  unlocked: boolean;
  unlockedAt: string | null;
}

export function getAchievements() {
  return apiFetch<AchievementResponse[]>("/api/achievements");
}
