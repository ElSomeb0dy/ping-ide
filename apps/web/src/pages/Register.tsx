import { useState, type FormEvent } from "react";
import { useMutation } from "@tanstack/react-query";
import { Link, Navigate, useNavigate } from "react-router-dom";
import { Zap } from "lucide-react";
import { register, type RegisterPayload } from "../api/auth";
import { Button, Card } from "../components/ui";
import { useAuth } from "../context/AuthContext";

function errorMessage(error: unknown) {
  if (!(error instanceof Error)) {
    return "Inscription impossible";
  }

  try {
    const parsed = JSON.parse(error.message) as { message?: string };
    return parsed.message ?? error.message;
  } catch {
    return error.message || "Inscription impossible";
  }
}

export default function Register() {
  const auth = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState<RegisterPayload>({
    username: "",
    email: "",
    password: "",
    confirmPassword: "",
  });
  const [clientError, setClientError] = useState<string | null>(null);

  const mutation = useMutation({
    mutationFn: register,
    onSuccess: (response) => {
      auth.loginWithToken(response.token);
      navigate("/", { replace: true });
    },
  });

  if (auth.isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const updateField =
    (field: keyof RegisterPayload) =>
    (event: React.ChangeEvent<HTMLInputElement>) => {
      setForm((current) => ({ ...current, [field]: event.target.value }));
    };

  const onSubmit = (event: FormEvent) => {
    event.preventDefault();
    setClientError(null);

    if (form.password !== form.confirmPassword) {
      setClientError("Les mots de passe ne correspondent pas.");
      return;
    }

    mutation.mutate(form);
  };

  const inlineError = clientError ?? (mutation.error ? errorMessage(mutation.error) : null);

  return (
    <main className="grid h-screen place-items-center overflow-y-auto bg-(--color-bg) px-6 py-10">
      <Card className="w-full max-w-md">
        <div className="flex items-center gap-2">
          <Zap className="size-7 text-(--color-green)" strokeWidth={2.5} />
          <h1 className="font-display text-3xl font-bold text-(--color-green)">Ping</h1>
        </div>

        <form onSubmit={onSubmit} className="mt-8 flex flex-col gap-5">
          <div>
            <label htmlFor="username" className="text-sm text-(--color-text-soft)">
              Identifiant
            </label>
            <input
              id="username"
              value={form.username}
              onChange={updateField("username")}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
              autoComplete="username"
              placeholder="prenom.nom"
              required
            />
          </div>

          <div>
            <label htmlFor="email" className="text-sm text-(--color-text-soft)">
              Email
            </label>
            <input
              id="email"
              type="email"
              value={form.email}
              onChange={updateField("email")}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
              autoComplete="email"
              required
            />
          </div>

          <div>
            <label htmlFor="password" className="text-sm text-(--color-text-soft)">
              Mot de passe
            </label>
            <input
              id="password"
              type="password"
              value={form.password}
              onChange={updateField("password")}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
              autoComplete="new-password"
              required
            />
          </div>

          <div>
            <label htmlFor="confirmPassword" className="text-sm text-(--color-text-soft)">
              Confirmer le mot de passe
            </label>
            <input
              id="confirmPassword"
              type="password"
              value={form.confirmPassword}
              onChange={updateField("confirmPassword")}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
              autoComplete="new-password"
              required
            />
          </div>

          {inlineError && (
            <p className="rounded-lg border border-(--color-orange-dim) bg-(--color-orange-soft) px-3 py-2 text-sm text-(--color-orange)">
              {inlineError}
            </p>
          )}

          <Button variant="solid" disabled={mutation.isPending}>
            {mutation.isPending ? "Création..." : "Créer le compte"}
          </Button>
        </form>

        <p className="mt-6 text-sm text-(--color-text-soft)">
          Déjà un compte ?{" "}
          <Link to="/login" className="font-medium text-(--color-green) hover:underline">
            Se connecter
          </Link>
        </p>
      </Card>
    </main>
  );
}
