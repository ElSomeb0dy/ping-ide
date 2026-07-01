import { apiFetch } from "./client";

export interface ExerciseResponse {
  id: string;
  lessonId: string;
  slug: string;
  title: string;
  statementMd: string;
  examplesJson: string;
  difficulty: string;
  xpReward: number;
  order: number;
  allowedLanguages: string;
  starterCodeJson: string;
  status: "LOCKED" | "AVAILABLE" | "COMPLETED";
}

export interface LessonResponse {
  id: string;
  slug: string;
  title: string;
  description: string;
  order: number;
  difficulty: string;
  icon: string;
  prerequisiteLessonId: string | null;
  status: "LOCKED" | "IN_PROGRESS" | "COMPLETED";
  completedExercises: number;
  totalExercises: number;
  percentage: number;
  exercises: ExerciseResponse[];
}

export function getLessons() {
  return apiFetch<LessonResponse[]>("/api/lessons");
}

export function getLesson(id: string) {
  return apiFetch<LessonResponse>(`/api/lessons/${id}`);
}
