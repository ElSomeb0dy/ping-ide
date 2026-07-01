import { apiFetch } from "./client";
import type { AchievementResponse } from "./achievements";
import type { ExerciseResponse } from "./lessons";

export interface ExecuteResponse {
  stdout: string;
  stderr: string;
  exitCode: number;
  timedOut: boolean;
}

export interface SubmitExerciseResponse {
  status: string;
  testResults: Array<{
    input: string;
    expected: string;
    actual: string;
    passed: boolean;
  }>;
  xpAwarded: number;
  leveledUp: boolean;
  newLevel: number;
  newAchievements: AchievementResponse[];
  questUpdates: unknown[];
}

export function getExercise(id: string) {
  return apiFetch<ExerciseResponse>(`/api/exercises/${id}`);
}

export function executeCode(language: string, code: string, stdin = "") {
  return apiFetch<ExecuteResponse>("/api/execute", {
    method: "POST",
    body: JSON.stringify({ language, code, stdin }),
  });
}

export function submitExercise(id: string, language: string, code: string) {
  return apiFetch<SubmitExerciseResponse>(`/api/exercises/${id}/submit`, {
    method: "POST",
    body: JSON.stringify({ language, code }),
  });
}
