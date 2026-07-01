import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getUserSettings, updateUserSettings, type UserSettingsResponse } from "../api/user";
import { useAuth } from "../context/AuthContext";

export function useSettings() {
  const auth = useAuth();

  return useQuery({
    queryKey: ["settings", auth.user?.id],
    queryFn: () => getUserSettings(auth.user!.id),
    enabled: Boolean(auth.user?.id),
  });
}

export function useUpdateSettings() {
  const auth = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (settings: Partial<UserSettingsResponse>) => updateUserSettings(auth.user!.id, settings),
    onSuccess: () => {
      void queryClient.invalidateQueries({ queryKey: ["settings", auth.user?.id] });
    },
  });
}
