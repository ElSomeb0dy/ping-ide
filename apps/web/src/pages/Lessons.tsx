import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { Search, Lock } from "lucide-react";
import { Card, Pill, ProgressBar, Button } from "../components/ui";
import { useLessons } from "../hooks/useLessons";

const filters = ["Toutes", "En cours", "Terminées"] as const;
type Filter = (typeof filters)[number];

export default function Lessons() {
  const [filter, setFilter] = useState<Filter>("Toutes");
  const [query, setQuery] = useState("");
  const lessons = useLessons();
  const inProgress = lessons.data?.find((l) => l.status === "IN_PROGRESS");

  const visible = useMemo(() => {
    return (lessons.data ?? []).filter((l) => {
      const matchesQuery = l.title.toLowerCase().includes(query.toLowerCase());
      const matchesFilter =
        filter === "Toutes" ||
        (filter === "En cours" && l.status === "IN_PROGRESS") ||
        (filter === "Terminées" && l.status === "COMPLETED");
      return matchesQuery && matchesFilter;
    });
  }, [filter, query, lessons.data]);

  if (lessons.isLoading) return <Card>Chargement des leçons...</Card>;
  if (lessons.isError) return <Card>Impossible de charger les leçons.</Card>;

  return (
    <div className="flex flex-col gap-7">
      <h1 className="font-display text-4xl font-bold">Leçons</h1>

      <div className="flex flex-wrap items-center justify-between gap-3">
        <div className="flex items-center gap-2 rounded-full border border-(--color-border-hover) bg-(--color-surface) px-4 py-2 sm:w-80">
          <Search className="size-4 text-(--color-text-mute)" />
          <input
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            placeholder="Rechercher une leçon..."
            aria-label="Rechercher une leçon"
            className="w-full bg-transparent text-sm outline-none placeholder:text-(--color-text-mute)"
          />
        </div>
        {inProgress && (
          <Link to={`/lessons/${inProgress.id}`}>
            <Button variant="outline">Reprendre la leçon en cours</Button>
          </Link>
        )}
      </div>

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

      <div className="flex flex-col gap-4">
        {visible.map((lesson) => {
          const done = lesson.completedExercises;
          const total = lesson.totalExercises;
          const locked = lesson.status === "LOCKED";

          return (
            <Card key={lesson.id} highlight={!locked}>
              <div className="flex items-center justify-between gap-4">
                <div className="flex min-w-0 items-center gap-4">
                  <div className="grid size-12 shrink-0 place-items-center rounded-full border border-(--color-border-hover) bg-(--color-surface-elevated)">
                    {locked && <Lock className="size-4 text-(--color-text-mute)" />}
                  </div>
                  <div className="min-w-0">
                    <h2 className="truncate font-display text-2xl font-bold">{lesson.title}</h2>
                    <div className="mt-2 flex flex-wrap gap-2">
                      <Pill>{lesson.difficulty}</Pill>
                      <Pill>{lesson.icon}</Pill>
                      {lesson.status === "COMPLETED" && <Pill tone="orange">Terminée</Pill>}
                    </div>
                  </div>
                </div>

                {locked ? (
                  <Pill tone="muted">
                    <Lock className="mr-1 inline size-3" /> Verrouillée
                  </Pill>
                ) : (
                  <Link to={`/lessons/${lesson.id}`}>
                    <Button variant="outline">
                      {lesson.status === "COMPLETED" ? "Explorer" : "Reprendre"}
                    </Button>
                  </Link>
                )}
              </div>

              {!locked && (
                <div className="mt-5">
                  <p className="mb-2 text-sm text-(--color-text-soft)">
                    {done}/{total} exercices terminés
                  </p>
                  <ProgressBar value={done} max={total || 1} />
                </div>
              )}
            </Card>
          );
        })}
      </div>
    </div>
  );
}
