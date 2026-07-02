import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { getUser, updateUser, type UpdateUserPayload } from "../api/user";
import { useAuth } from "../context/AuthContext";

export function useUser() {
  const auth = useAuth();

  return useQuery({
    queryKey: ["user", auth.user?.id],
    queryFn: () => getUser(auth.user!.id),
    enabled: Boolean(auth.user?.id),
  });
}

export function useUpdateUser() {
  const auth = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: UpdateUserPayload) => updateUser(auth.user!.id, payload),
    onSuccess: (updated) => {
      queryClient.setQueryData(["user", auth.user?.id], updated);
    },
  });
}
