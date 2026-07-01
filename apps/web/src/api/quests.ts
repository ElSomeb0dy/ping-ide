import { apiFetch } from "./client";

export interface QuestResponse {
  id: string;
  code: string;
  title: string;
  description: string;
  xpReward: number;
  type: string;
  criteria: number;
  date: string | null;
  progressCurrent: number;
  progressTarget: number;
  status: string;
}

export function getDailyQuests() {
  return apiFetch<QuestResponse[]>("/api/quests/daily");
}

export function claimQuest(id: string) {
  return apiFetch<QuestResponse>(`/api/quests/${id}/claim`, {
    method: "POST",
  });
}
