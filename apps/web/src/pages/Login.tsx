import { useState, type FormEvent } from "react";
import { Link, Navigate, useLocation, useNavigate } from "react-router-dom";
import { Zap } from "lucide-react";
import { Button, Card } from "../components/ui";
import { useAuth } from "../context/AuthContext";

export default function Login() {
  const auth = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [loginValue, setLoginValue] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  if (auth.isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  const from = (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? "/";

  const onSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError(null);
    setSubmitting(true);
    try {
      await auth.login(loginValue, password);
      navigate(from, { replace: true });
    } catch (err) {
      setError(err instanceof Error ? err.message : "Connexion impossible");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="grid min-h-screen place-items-center bg-(--color-bg) px-6">
      <Card className="w-full max-w-md">
        <div className="flex items-center gap-2">
          <Zap className="size-7 text-(--color-green)" strokeWidth={2.5} />
          <h1 className="font-display text-3xl font-bold text-(--color-green)">Ping</h1>
        </div>

        <form onSubmit={onSubmit} className="mt-8 flex flex-col gap-5">
          <div>
            <label htmlFor="login" className="text-sm text-(--color-text-soft)">
              Identifiant
            </label>
            <input
              id="login"
              value={loginValue}
              onChange={(event) => setLoginValue(event.target.value)}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
              autoComplete="username"
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
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
              autoComplete="current-password"
              required
            />
          </div>

          {error && (
            <p className="rounded-lg border border-(--color-orange-dim) bg-(--color-orange-soft) px-3 py-2 text-sm text-(--color-orange)">
              {error}
            </p>
          )}

          <Button variant="solid" disabled={submitting}>
            {submitting ? "Connexion..." : "Se connecter"}
          </Button>
        </form>

        <p className="mt-6 text-sm text-(--color-text-soft)">
          Pas encore de compte ?{" "}
          <Link to="/register" className="font-medium text-(--color-green) hover:underline">
            Créer un compte
          </Link>
        </p>
      </Card>
    </main>
  );
}
