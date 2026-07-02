import { useQuery } from "@tanstack/react-query";
import { getUser } from "../api/user";
import { useAuth } from "../context/AuthContext";

export function useCurrentUser() {
  const auth = useAuth();

  return useQuery({
    queryKey: ["user", auth.user?.id],
    queryFn: () => getUser(auth.user!.id),
    enabled: Boolean(auth.user?.id),
  });
}
