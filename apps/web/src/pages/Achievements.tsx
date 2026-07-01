import { useState } from "react";
import { Lock } from "lucide-react";
import { Card, Pill, XpBadge } from "../components/ui";
import { useAchievements } from "../hooks/useAchievements";

const filters = ["Tous les succès", "Débloqués", "Verrouillés"] as const;
type Filter = (typeof filters)[number];

export default function Achievements() {
  const [filter, setFilter] = useState<Filter>("Tous les succès");
  const achievements = useAchievements();

  if (achievements.isLoading) return <Card>Chargement des succès...</Card>;
  if (achievements.isError) return <Card>Impossible de charger les succès.</Card>;

  const visible = (achievements.data ?? []).filter((a) => {
    if (filter === "Débloqués") return a.unlocked;
    if (filter === "Verrouillés") return !a.unlocked;
    return true;
  });

  return (
    <div className="flex flex-col gap-7">
      <h1 className="font-display text-4xl font-bold">Succès</h1>

      <div className="flex gap-2">
        {filters.map((f) => (
          <button
            key={f}
            onClick={() => setFilter(f)}
            className={`rounded-full border px-4 py-1.5 text-sm font-medium transition-colors ${
              filter === f
                ? "border-(--color-green) text-(--color-green) bg-(--color-green-soft)"
                : "border-(--color-border-hover) text-(--color-text-soft) hover:text-(--color-text)"
            }`}
          >
            {f}
          </button>
        ))}
      </div>

      <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {visible.map((a) => {
          const locked = !a.unlocked;
          return (
            <Card key={a.id} highlight={!locked} className={locked ? "opacity-60" : ""}>
              <div className="flex items-start gap-3">
                <div className="grid size-11 shrink-0 place-items-center rounded-full bg-(--color-surface-elevated)">
                  {locked && <Lock className="size-4 text-(--color-text-mute)" />}
                </div>
                <div className="min-w-0 flex-1">
                  <p className="font-semibold">{a.title}</p>
                  <p className="mt-0.5 text-xs text-(--color-text-mute)">{a.description}</p>
                  {locked && <p className="mt-1 text-xs text-(--color-text-mute) italic">Objectif: {a.criteriaValue}</p>}
                </div>
              </div>

              <div className="mt-4 flex items-center justify-between gap-2">
                {locked ? (
                  <Pill tone="muted">+{a.xpReward} xp</Pill>
                ) : (
                  <XpBadge xp={a.xpReward} />
                )}
                {a.unlocked && (
                  <p className="text-xs text-(--color-text-mute)">Débloqué</p>
                )}
              </div>
            </Card>
          );
        })}
      </div>
    </div>
  );
}
