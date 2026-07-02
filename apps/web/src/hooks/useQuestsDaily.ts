import { useQuery } from "@tanstack/react-query";
import { claimQuest, getDailyQuests } from "../api/quests";
import { useMutation, useQueryClient } from "@tanstack/react-query";

export function useQuestsDaily() {
  return useQuery({
    queryKey: ["questsDaily"],
    queryFn: getDailyQuests,
  });
}

export function useClaimQuest() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: claimQuest,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["questsDaily"] });
      queryClient.invalidateQueries({ queryKey: ["userStats"] });
    },
  });
}
