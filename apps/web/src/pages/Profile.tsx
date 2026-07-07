import { useState } from "react";
import { Flame, Trophy, Star, GraduationCap, Pencil } from "lucide-react";
import { Card, Button } from "../components/ui";
import XpRing from "../components/XpRing";
import { useAuth } from "../context/AuthContext";
import { useSettings } from "../hooks/useSettings";
import { useUserStats } from "../hooks/useUserStats";
import { useUser, useUpdateUser } from "../hooks/useUser";

export default function Profile() {
  const auth = useAuth();
  const statsQuery = useUserStats();
  const settingsQuery = useSettings();
  const userQuery = useUser();
  const updateUser = useUpdateUser();
  const [editingAvatar, setEditingAvatar] = useState(false);
  const [avatarInput, setAvatarInput] = useState("");
  const [dragging, setDragging] = useState(false);
  const [editingName, setEditingName] = useState(false);
  const [nameInput, setNameInput] = useState("");
  const [saveError, setSaveError] = useState<string | null>(null);

  if (statsQuery.isLoading) return <Card>Chargement du profil...</Card>;

  const userStats = statsQuery.data;
  const displayName = userQuery.data?.displayName ?? "Apprenant";
  const avatar = userQuery.data?.avatar;

  const startEditingAvatar = () => {
    setAvatarInput(avatar ?? "");
    setSaveError(null);
    setEditingAvatar(true);
  };

  const saveAvatar = async () => {
    setSaveError(null);
    try {
      await updateUser.mutateAsync({ avatar: avatarInput });
      setEditingAvatar(false);
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : "Impossible d'enregistrer l'avatar");
    }
  };

  const readFile = (file: File) => {
    if (!file.type.startsWith("image/")) return;
    const reader = new FileReader();
    reader.onload = () => setAvatarInput(reader.result as string);
    reader.readAsDataURL(file);
  };

  const startEditingName = () => {
    setNameInput(displayName);
    setSaveError(null);
    setEditingName(true);
  };

  const saveName = async () => {
    if (!nameInput.trim()) {
      setEditingName(false);
      return;
    }
    setSaveError(null);
    try {
      await updateUser.mutateAsync({ displayName: nameInput.trim() });
      setEditingName(false);
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : "Impossible d'enregistrer le nom");
    }
  };
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
            <div className="group relative">
              <div className="grid size-16 place-items-center overflow-hidden rounded-full border border-(--color-border-hover) bg-(--color-surface-elevated) text-2xl font-semibold">
                {avatar ? (
                    <img src={avatar} alt={displayName} className="size-full object-cover" />
                ) : (
                    displayName.slice(0, 1).toUpperCase()
                )}
              </div>
              <button
                  onClick={startEditingAvatar}
                  className="absolute -bottom-1 -right-1 grid size-6 place-items-center rounded-full border border-(--color-border-hover) bg-(--color-surface-elevated) text-(--color-text-soft) hover:text-(--color-green)"
                  title="Modifier l'avatar"
                  aria-label="Modifier l'avatar"
              >
                <Pencil className="size-3.5" />
              </button>
            </div>
            <div>
              {editingName ? (
                  <div className="flex items-center gap-2">
                    <input
                        autoFocus
                        type="text"
                        aria-label="Nom d'affichage"
                        value={nameInput}
                        onChange={(e) => setNameInput(e.target.value)}
                        onKeyDown={(e) => {
                          if (e.key === "Enter") saveName();
                          if (e.key === "Escape") setEditingName(false);
                        }}
                        className="rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3 py-1.5 font-display text-xl font-bold outline-none"
                    />
                    <Button variant="solid" onClick={saveName} disabled={updateUser.isPending}>
                      {updateUser.isPending ? "Enregistrement..." : "OK"}
                    </Button>
                    <Button variant="ghost" onClick={() => setEditingName(false)}>
                      Annuler
                    </Button>
                  </div>
              ) : (
                  <button onClick={startEditingName} className="group/name flex items-center gap-2">
                    <p className="font-display text-2xl font-bold">{displayName}</p>
                    <Pencil className="size-3.5 text-(--color-text-mute) opacity-0 group-hover/name:opacity-100" />
                  </button>
              )}
              <p className="text-sm text-(--color-text-soft)">Membre depuis Janvier 2026</p>
              {editingName && saveError && (
                <p className="mt-1 text-xs text-(--color-orange)">{saveError}</p>
              )}
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

        {editingAvatar && (
            <Card>
              <h2 className="font-display text-lg font-bold">Modifier l'avatar</h2>
              <div className="mt-4 flex flex-col gap-3">
                <div
                    onDragOver={(e) => {
                      e.preventDefault();
                      setDragging(true);
                    }}
                    onDragLeave={() => setDragging(false)}
                    onDrop={(e) => {
                      e.preventDefault();
                      setDragging(false);
                      const file = e.dataTransfer.files[0];
                      if (file) readFile(file);
                    }}
                    className={`flex flex-col items-center justify-center gap-2 rounded-lg border-2 border-dashed p-6 text-center text-sm transition-colors ${
                        dragging
                            ? "border-(--color-green) bg-(--color-green-soft) text-(--color-green)"
                            : "border-(--color-border-hover) text-(--color-text-soft)"
                    }`}
                >
                  {avatarInput ? (
                      <img src={avatarInput} alt="Aperçu" className="size-16 rounded-full object-cover" />
                  ) : null}
                  <p>Glisse-dépose une image ici</p>
                  <label className="cursor-pointer text-(--color-green) underline">
                    ou choisis un fichier
                    <input
                        type="file"
                        accept="image/*"
                        className="hidden"
                        onChange={(e) => {
                          const file = e.target.files?.[0];
                          if (file) readFile(file);
                        }}
                    />
                  </label>
                </div>
                <label htmlFor="avatar-url" className="text-sm text-(--color-text-soft)">
                  Ou une URL d'image
                </label>
                <input
                    id="avatar-url"
                    type="text"
                    value={avatarInput.startsWith("data:") ? "" : avatarInput}
                    onChange={(e) => setAvatarInput(e.target.value)}
                    placeholder="https://..."
                    className="w-full rounded-lg border border-(--color-border-hover) bg-(--color-surface-elevated) px-3.5 py-2.5 text-sm outline-none"
                />
                {saveError && (
                  <p className="text-sm text-(--color-orange)">{saveError}</p>
                )}
                <div className="flex items-center gap-3">
                  <Button variant="solid" onClick={saveAvatar} disabled={updateUser.isPending}>
                    {updateUser.isPending ? "Enregistrement..." : "Enregistrer"}
                  </Button>
                  <Button variant="ghost" onClick={() => setEditingAvatar(false)}>
                    Annuler
                  </Button>
                </div>
              </div>
            </Card>
        )}

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
              { label: "Identifiant", value: auth.user?.id ?? "-" },
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
