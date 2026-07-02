import { Link } from "react-router-dom";
import { Flame, Check, Gift, Trophy } from "lucide-react";
import { Card, ProgressBar, Button, XpBadge } from "../components/ui";
import XpRing from "../components/XpRing";
import { useAchievements } from "../hooks/useAchievements";
import { useLessons } from "../hooks/useLessons";
import { useClaimQuest, useQuestsDaily } from "../hooks/useQuestsDaily";
import { useUserStats } from "../hooks/useUserStats";
import { useUser } from "../hooks/useUser";

export default function Dashboard() {
  const stats = useUserStats();
  const lessons = useLessons();
  const quests = useQuestsDaily();
  const claimQuest = useClaimQuest();
  const achievements = useAchievements();
  const user = useUser();

  if (stats.isLoading || lessons.isLoading || quests.isLoading || achievements.isLoading || user.isLoading) {
    return <Card>Chargement...</Card>;
  }

  const displayName = user.data?.displayName ?? "Apprenant";
  const userStats = stats.data;
  const continueLesson = lessons.data?.find((l) => l.status === "IN_PROGRESS")
    ?? lessons.data?.find((l) => l.status !== "LOCKED");
  const recentAchievements = (achievements.data ?? [])
    .filter((a) => a.unlocked)
    .sort((a, b) => new Date(b.unlockedAt ?? 0).getTime() - new Date(a.unlockedAt ?? 0).getTime())
    .slice(0, 3);

  return (
    <div className="flex flex-col gap-8">
      <h1 className="font-display text-4xl font-bold">Bon retour, {displayName} !</h1>

      <Card className="flex items-center justify-between">
        <div className="flex items-center gap-5">
          <div className="flex size-14 items-center justify-center rounded-full border border-(--color-border-hover) bg-(--color-surface-elevated) text-xl font-semibold">
            {displayName.slice(0, 1).toUpperCase()}
          </div>
          <div>
            <p className="font-display text-2xl font-bold">{displayName}</p>
            <p className="flex items-center gap-1.5 text-sm font-medium text-(--color-orange)">
              <Flame className="size-4" />
              {userStats?.currentStreak ?? 0} jours de série
            </p>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <div className="relative grid place-items-center">
            <XpRing value={userStats?.xp ?? 0} max={userStats?.xpToNextLevel ?? 50} />
          </div>
          <div>
            <p className="font-display text-lg font-bold">Niveau {userStats?.level ?? 1}</p>
            <p className="text-sm text-(--color-text-soft)">
              {userStats?.xp ?? 0} / {userStats?.xpToNextLevel ?? 50} XP
            </p>
          </div>
        </div>
      </Card>

      {continueLesson && (
        <Card>
          <div className="flex items-start justify-between">
            <div>
              <p className="text-sm text-(--color-text-mute)">Continue où tu en étais</p>
              <h2 className="mt-1 font-display text-2xl font-bold">{continueLesson.title}</h2>
            </div>
            <Link to={`/lessons/${continueLesson.id}`}>
              <Button variant="outline">Reprendre</Button>
            </Link>
          </div>
          <div className="mt-5">
            <ProgressBar
              value={continueLesson.completedExercises}
              max={continueLesson.totalExercises || 1}
              showLabel={false}
            />
            <p className="mt-2 text-right text-xs font-medium text-(--color-text-soft)">
              {continueLesson.percentage}%
            </p>
          </div>
        </Card>
      )}

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-[1.4fr_1fr]">
        <Card>
          <h2 className="font-display text-2xl font-bold">Quêtes du jour</h2>
          <div className="mt-5 grid grid-cols-1 gap-3 sm:grid-cols-2">
            {(quests.data ?? []).length === 0 && (
              <div className="rounded-xl border border-(--color-border) p-4 text-sm text-(--color-text-soft)">
                Aucune quête disponible pour le moment.
              </div>
            )}
            {(quests.data ?? []).map((q) => {
              const progressTarget = q.progressTarget || q.criteria || 1;
              const progressCurrent = Math.min(q.progressCurrent ?? 0, progressTarget);
              const isCompleted = q.status === "COMPLETED";
              const isClaimed = q.status === "CLAIMED";

              return (
                <div
                  key={q.id}
                  className={`rounded-xl border p-4 ${
                    isClaimed
                      ? "border-(--color-border) opacity-50"
                      : "border-(--color-border-hover)"
                  }`}
                >
                  <div className="flex items-start justify-between gap-2">
                    <p className="text-sm font-medium">{q.title}</p>
                    {isClaimed ? (
                      <span className="inline-flex items-center gap-1 rounded-full border border-(--color-border-hover) px-2.5 py-1 text-xs text-(--color-text-soft)">
                        <Check className="size-3" /> Réclamée
                      </span>
                    ) : isCompleted ? (
                      <button
                        onClick={() => claimQuest.mutate(q.id)}
                        disabled={claimQuest.isPending}
                        className="inline-flex items-center gap-1 rounded-full border border-(--color-green) px-2.5 py-1 text-xs font-medium text-(--color-green) transition-colors hover:bg-(--color-green-soft) disabled:opacity-50"
                      >
                        <Gift className="size-3" /> Réclamer
                      </button>
                    ) : (
                      <XpBadge xp={q.xpReward} />
                    )}
                  </div>
                  <p className="mt-2 text-xs text-(--color-text-mute)">{q.description}</p>
                  <div className="mt-4">
                    <ProgressBar value={progressCurrent} max={progressTarget} showLabel={false} />
                    <p className="mt-2 text-right text-xs text-(--color-text-soft)">
                      {progressCurrent} / {progressTarget}
                    </p>
                  </div>
                </div>
              );
            })}
          </div>
        </Card>

        <Card>
          <h2 className="font-display text-2xl font-bold">Succès récents</h2>
          <div className="mt-5 flex flex-col gap-3">
            {recentAchievements.length === 0 && (
              <div className="rounded-xl border border-(--color-border) p-4 text-sm text-(--color-text-soft)">
                Aucun succès débloqué pour l'instant.
              </div>
            )}
            {recentAchievements.map((a) => (
              <div
                key={a.id}
                className="flex items-center gap-3 rounded-xl border border-(--color-border) p-3"
              >
                <div className="grid size-10 shrink-0 place-items-center rounded-full bg-(--color-surface-elevated)">
                  <Trophy className="size-4 text-(--color-green)" />
                </div>
                <div className="min-w-0">
                  <p className="truncate text-sm font-semibold">{a.title}</p>
                  <p className="text-xs text-(--color-text-mute)">Débloqué</p>
                </div>
              </div>
            ))}
          </div>
        </Card>
      </div>
    </div>
  );
}
