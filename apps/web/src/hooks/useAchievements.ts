import { useQuery } from "@tanstack/react-query";
import { getAchievements } from "../api/achievements";

export function useAchievements() {
  return useQuery({
    queryKey: ["achievements"],
    queryFn: getAchievements,
  });
}
