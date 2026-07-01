import { useMutation, useQuery } from "@tanstack/react-query";
import { executeCode, getExercise, submitExercise } from "../api/exercises";

export function useExercise(id: string | undefined) {
  return useQuery({
    queryKey: ["exercise", id],
    queryFn: () => getExercise(id!),
    enabled: Boolean(id),
  });
}

export function useExecuteCode() {
  return useMutation({
    mutationFn: ({ language, code, stdin }: { language: string; code: string; stdin?: string }) =>
      executeCode(language, code, stdin),
  });
}

export function useSubmitExercise() {
  return useMutation({
    mutationFn: ({ id, language, code }: { id: string; language: string; code: string }) =>
      submitExercise(id, language, code),
  });
}
