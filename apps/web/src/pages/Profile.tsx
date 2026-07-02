import { Flame, Trophy, Star, GraduationCap } from "lucide-react";
import { Card } from "../components/ui";
import XpRing from "../components/XpRing";
import { useAuth } from "../context/AuthContext";
import { useCurrentUser } from "../hooks/useCurrentUser";
import { useSettings } from "../hooks/useSettings";
import { useUserStats } from "../hooks/useUserStats";

export default function Profile() {
  const auth = useAuth();
  const statsQuery = useUserStats();
  const settingsQuery = useSettings();
  const currentUser = useCurrentUser();

  if (statsQuery.isLoading || currentUser.isLoading) return <Card>Chargement du profil...</Card>;

  const userStats = statsQuery.data;
  const displayName = currentUser.data?.displayName || currentUser.data?.login || "Apprenant";
  const stats = [
    { label: "Leçons terminées", value: userStats?.lessonsCompleted ?? 0, icon: GraduationCap },
    { label: "Exercices résolus", value: userStats?.exercisesSolved ?? 0, icon: Star },
    { label: "Succès débloqués", value: userStats?.achievementsUnlocked ?? 0, icon: Trophy },
    { label: "Jours de série", value: userStats?.currentStreak ?? 0, icon: Flame },
  ];

  return (
    <div className="flex flex-col gap-7">
      <h1 className="font-display text-4xl font-bold">Profil</h1>

      <Card className="flex flex-wrap items-center justify-between gap-6">
        <div className="flex items-center gap-5">
          <div className="grid size-16 place-items-center rounded-full border border-(--color-border-hover) bg-(--color-surface-elevated) text-2xl font-semibold">
            {displayName.slice(0, 1).toUpperCase()}
          </div>
          <div>
            <p className="font-display text-2xl font-bold">{displayName}</p>
            <p className="text-sm text-(--color-text-soft)">Membre depuis Janvier 2026</p>
          </div>
        </div>
        <div className="flex items-center gap-4">
          <XpRing value={userStats?.xp ?? 0} max={userStats?.xpToNextLevel ?? 50} size={56} />
          <div>
            <p className="font-display text-lg font-bold">Niveau {userStats?.level ?? 1}</p>
            <p className="text-sm text-(--color-text-soft)">
              {userStats?.xp ?? 0} / {userStats?.xpToNextLevel ?? 50} XP
            </p>
          </div>
        </div>
      </Card>

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-4">
        {stats.map(({ label, value, icon: Icon }) => (
          <Card key={label} className="text-center">
            <Icon className="mx-auto size-5 text-(--color-green)" />
            <p className="mt-3 font-display text-2xl font-bold">{value}</p>
            <p className="mt-1 text-xs text-(--color-text-mute)">{label}</p>
          </Card>
        ))}
      </div>

      <Card>
        <h2 className="font-display text-xl font-bold">Informations</h2>
        <div className="mt-4 flex flex-col divide-y divide-(--color-border)">
          {[
            { label: "Identifiant", value: currentUser.data?.login ?? "-" },
            { label: "Rôle", value: auth.isAdmin ? "Admin" : "Apprenant" },
            { label: "Langage préféré", value: settingsQuery.data?.defaultLanguage ?? "Python" },
          ].map((row) => (
            <div key={row.label} className="flex items-center justify-between py-3 text-sm">
              <span className="text-(--color-text-mute)">{row.label}</span>
              <span className="font-medium">{row.value}</span>
            </div>
          ))}
        </div>
      </Card>
    </div>
  );
}
