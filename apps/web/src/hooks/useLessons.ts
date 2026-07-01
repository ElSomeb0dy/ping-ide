import { useQuery } from "@tanstack/react-query";
import { getLessons } from "../api/lessons";

export function useLessons() {
  return useQuery({
    queryKey: ["lessons"],
    queryFn: getLessons,
  });
}
