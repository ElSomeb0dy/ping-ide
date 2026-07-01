import { useQuery } from "@tanstack/react-query";
import { getUserStats } from "../api/user";
import { useAuth } from "../context/AuthContext";

export function useUserStats() {
  const auth = useAuth();

  return useQuery({
    queryKey: ["userStats", auth.user?.id],
    queryFn: () => getUserStats(auth.user!.id),
    enabled: Boolean(auth.user?.id),
  });
}
