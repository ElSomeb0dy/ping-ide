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

  const queryKey = ["settings", auth.user?.id];

  return useMutation({
    mutationFn: (settings: Partial<UserSettingsResponse>) => updateUserSettings(auth.user!.id, settings),
    onMutate: async (settings) => {
      await queryClient.cancelQueries({ queryKey });
      const previous = queryClient.getQueryData<UserSettingsResponse>(queryKey);
      if (previous) {
        queryClient.setQueryData(queryKey, { ...previous, ...settings });
      }
      return { previous };
    },
    onError: (_err, _settings, context) => {
      if (context?.previous) {
        queryClient.setQueryData(queryKey, context.previous);
      }
    },
    onSuccess: (updated) => {
      queryClient.setQueryData(queryKey, updated);
    },
  });
}
