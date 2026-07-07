import { Link, useParams, Navigate } from "react-router-dom";
import { ArrowLeft, Lock, Check } from "lucide-react";
import { Card, ProgressBar, Button, XpBadge } from "../components/ui";
import { useLesson } from "../hooks/useLesson";

export default function LessonDetail() {
  const { lessonId } = useParams();
  const lessonQuery = useLesson(lessonId);

  if (lessonQuery.isLoading) return <Card>Chargement de la leçon...</Card>;
  if (lessonQuery.isError) return <Navigate to="/lessons" replace />;

  const lesson = lessonQuery.data;
  if (!lesson) return <Navigate to="/lessons" replace />;

  const done = lesson.completedExercises;
  const total = lesson.totalExercises;
  const remaining = total - done;

  return (
    <div className="flex flex-col gap-7">
      <Link to="/lessons">
        <Button variant="ghost" size="sm">
          <ArrowLeft className="size-3.5" /> Retour
        </Button>
      </Link>

      <div>
        <h1 className="font-display text-4xl font-bold">{lesson.title}</h1>
        <p className="mt-2 text-(--color-text-soft)">{lesson.description}</p>
      </div>

      <Card>
        <p className="text-sm text-(--color-text-soft)">
          Continue comme ça, tu as déjà terminé {done} exercices sur {total}, il n'en reste plus que{" "}
          {remaining}.
        </p>
        <div className="mt-4">
          <ProgressBar value={done} max={total || 1} />
        </div>
      </Card>

      <div className="grid grid-cols-2 gap-4 sm:grid-cols-3 lg:grid-cols-4">
        {lesson.exercises.map((ex, i) => {
          const locked = ex.status === "LOCKED";
          const completed = ex.status === "COMPLETED";
          return (
            <div
              key={ex.id}
              className={`flex flex-col rounded-2xl border p-4 ${
                locked ? "border-(--color-border)" : "border-(--color-border-hover)"
              }`}
            >
              <div className="flex items-center gap-2">
                <div className="grid size-8 shrink-0 place-items-center rounded-full border border-(--color-border-hover) bg-(--color-surface-elevated) text-xs font-semibold">
                  {locked ? <Lock className="size-3.5" /> : completed ? <Check className="size-4 text-(--color-green)" /> : i + 1}
                </div>
                <p className="text-sm font-semibold">{ex.title}</p>
              </div>
              <p className="mt-2 flex-1 text-xs text-(--color-text-mute)">{ex.statementMd}</p>
              <div className="mt-3 flex items-center justify-between gap-2">
                <XpBadge xp={ex.xpReward} />
                {!locked &&
                  (completed ? (
                    <Link to={`/lessons/${lesson.id}/exercises/${ex.id}`}>
                      <Button variant="ghost" size="sm">
                        Refaire
                      </Button>
                    </Link>
                  ) : (
                    <Link to={`/lessons/${lesson.id}/exercises/${ex.id}`}>
                      <Button variant="outline" size="sm">
                        Essayer
                      </Button>
                    </Link>
                  ))}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
