import { useState } from "react";
import { Card, Button } from "../components/ui";
import { useSettings, useUpdateSettings } from "../hooks/useSettings";

const ideThemes = ["Ping Dark", "Monokai", "Solarized", "GitHub Light"];
const languages = ["Python"];

export default function Settings() {
  const settings = useSettings();
  const updateSettings = useUpdateSettings();
  const [saved, setSaved] = useState(false);

  if (settings.isLoading) return <Card>Chargement des réglages...</Card>;
  if (settings.isError) return <Card>Impossible de charger les réglages.</Card>;

  const data = settings.data ?? {
    theme: ideThemes[0],
    defaultLanguage: languages[0],
    notificationsEnabled: true,
    soundEnabled: true,
  };

  const save = async () => {
    await updateSettings.mutateAsync(data);
    setSaved(true);
    window.setTimeout(() => setSaved(false), 1500);
  };

  return (
    <div className="flex flex-col gap-7">
      <h1 className="font-display text-4xl font-bold">Réglages</h1>

      <Card>
        <h2 className="font-display text-lg font-bold text-(--color-green)">Environnement IDE</h2>
        <div className="mt-5 flex flex-col gap-5">
          <div>
            <label className="text-sm text-(--color-text-soft)">Thème de l'éditeur</label>
            <select
              value={data.theme}
              onChange={(e) => updateSettings.mutate({ theme: e.target.value })}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
            >
              {ideThemes.map((t) => (
                <option key={t}>{t}</option>
              ))}
            </select>
          </div>
          <div>
            <label className="text-sm text-(--color-text-soft)">Langage par défaut</label>
            <select
              value={data.defaultLanguage}
              onChange={(e) => updateSettings.mutate({ defaultLanguage: e.target.value })}
              className="mt-2 w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
            >
              {languages.map((l) => (
                <option key={l}>{l}</option>
              ))}
            </select>
          </div>
        </div>
      </Card>

      <Card>
        <h2 className="font-display text-lg font-bold">Notifications</h2>
        <div className="mt-5 flex flex-col gap-4">
          <ToggleRow
            label="Rappels de série"
            description="Reçois un rappel quotidien pour garder ta série."
            value={data.notificationsEnabled}
            onChange={(value) => updateSettings.mutate({ notificationsEnabled: value })}
          />
          <ToggleRow
            label="Sons de l'interface"
            description="Active les sons lors de la complétion d'un exercice."
            value={data.soundEnabled}
            onChange={(value) => updateSettings.mutate({ soundEnabled: value })}
          />
        </div>
      </Card>

      <div className="flex items-center gap-3">
        <Button variant="solid" onClick={save} disabled={updateSettings.isPending}>
          {updateSettings.isPending ? "Enregistrement..." : "Enregistrer les modifications"}
        </Button>
        {saved && <p className="text-sm text-(--color-green)">Réglages enregistrés</p>}
      </div>
    </div>
  );
}

function ToggleRow({
  label,
  description,
  value,
  onChange,
}: {
  label: string;
  description: string;
  value: boolean;
  onChange: (v: boolean) => void;
}) {
  return (
    <div className="flex items-center justify-between gap-4">
      <div>
        <p className="text-sm font-medium">{label}</p>
        <p className="text-xs text-(--color-text-mute)">{description}</p>
      </div>
      <button
        type="button"
        aria-pressed={value}
        onClick={() => onChange(!value)}
        className={`relative h-6 w-11 shrink-0 rounded-full transition-colors ${
          value ? "bg-(--color-green)" : "bg-(--color-surface-elevated)"
        }`}
      >
        <span
          className={`absolute left-0.5 top-0.5 size-5 rounded-full bg-(--color-bg) transition-transform ${
            value ? "translate-x-5" : "translate-x-0"
          }`}
        />
      </button>
    </div>
  );
}
