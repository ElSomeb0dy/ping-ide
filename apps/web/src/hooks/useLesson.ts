import { useQuery } from "@tanstack/react-query";
import { getLesson } from "../api/lessons";

export function useLesson(id: string | undefined) {
  return useQuery({
    queryKey: ["lesson", id],
    queryFn: () => getLesson(id!),
    enabled: Boolean(id),
  });
}
