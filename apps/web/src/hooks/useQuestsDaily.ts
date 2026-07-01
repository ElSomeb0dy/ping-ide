import { useQuery } from "@tanstack/react-query";
import { getDailyQuests } from "../api/quests";

export function useQuestsDaily() {
  return useQuery({
    queryKey: ["questsDaily"],
    queryFn: getDailyQuests,
  });
}
