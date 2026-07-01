import { useEffect, useState } from "react";
import { Link, useParams, Navigate } from "react-router-dom";
import { ArrowLeft, CheckCircle, Play, Terminal } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { Button, Card, XpBadge } from "../components/ui";
import CodeEditor from "../components/CodeEditor";
import { useExecuteCode, useExercise, useSubmitExercise } from "../hooks/useExercise";
import { useLesson } from "../hooks/useLesson";

const fallbackLanguages = ["Python"];
const fallbackCode = `print("hello")`;

export default function ExerciseIDE() {
  const { lessonId, exerciseId } = useParams();
  const queryClient = useQueryClient();
  const lesson = useLesson(lessonId);
  const exercise = useExercise(exerciseId);
  const executeCode = useExecuteCode();
  const submitExercise = useSubmitExercise();
  const [language, setLanguage] = useState("Python");
  const [output, setOutput] = useState<string | null>(null);
  const languages = parseStringArray(exercise.data?.allowedLanguages, fallbackLanguages);
  const starterCode = parseStarterCode(exercise.data?.starterCodeJson, language);
  const [code, setCode] = useState(starterCode);

  useEffect(() => {
    setCode(starterCode);
  }, [starterCode]);

  if (lesson.isLoading || exercise.isLoading) return <Card>Chargement de l'exercice...</Card>;
  if (lesson.isError || exercise.isError || !lesson.data || !exercise.data) return <Navigate to="/lessons" replace />;

  const runCode = async () => {
    setOutput(null);
    try {
      const result = await executeCode.mutateAsync({ language, code });
      setOutput([
        result.timedOut ? "Temps d'exécution dépassé." : `Code de sortie: ${result.exitCode}`,
        result.stdout && `\nSortie:\n${result.stdout.trim()}`,
        result.stderr && `\nErreurs:\n${result.stderr.trim()}`,
      ].filter(Boolean).join("\n"));
    } catch (err) {
      setOutput(err instanceof Error ? err.message : "Exécution impossible");
    }
  };

  const submit = async () => {
    setOutput(null);
    try {
      const result = await submitExercise.mutateAsync({ id: exercise.data.id, language, code });
      const lines = result.testResults.map((test, index) =>
        `${test.passed ? "✓" : "✗"} Test ${index + 1}: attendu "${test.expected}", obtenu "${test.actual}"`);
      setOutput([
        result.status === "PASSED" ? "Soumission validée." : "Soumission refusée.",
        ...lines,
        result.xpAwarded > 0 ? `+${result.xpAwarded} XP` : "Aucun XP ajouté",
        result.leveledUp ? `Nouveau niveau: ${result.newLevel}` : "",
      ].filter(Boolean).join("\n"));

      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["lessons"] }),
        queryClient.invalidateQueries({ queryKey: ["lesson", lessonId] }),
        queryClient.invalidateQueries({ queryKey: ["exercise", exerciseId] }),
        queryClient.invalidateQueries({ queryKey: ["userStats"] }),
        queryClient.invalidateQueries({ queryKey: ["achievements"] }),
        queryClient.invalidateQueries({ queryKey: ["questsDaily"] }),
      ]);
    } catch (err) {
      setOutput(err instanceof Error ? err.message : "Soumission impossible");
    }
  };

  return (
    <div className="grid h-[calc(100vh-5rem)] grid-cols-1 gap-6 lg:grid-cols-[0.85fr_1.15fr]">
      <div className="flex flex-col gap-5 overflow-y-auto pr-2">
        <Link to={`/lessons/${lesson.data.id}`}>
          <Button variant="ghost" size="sm">
            <ArrowLeft className="size-3.5" /> Retour
          </Button>
        </Link>

        <div>
          <div className="flex items-center gap-3">
            <h1 className="font-display text-3xl font-bold">{exercise.data.title}</h1>
            <XpBadge xp={exercise.data.xpReward} />
          </div>
          <p className="mt-3 text-sm leading-relaxed text-(--color-text-soft)">
            {exercise.data.statementMd}
          </p>
        </div>

        <div className="rounded-xl border border-(--color-border) p-4">
          <p className="mb-2 text-xs font-semibold uppercase tracking-wide text-(--color-text-mute)">
            Exemple de code
          </p>
          <pre className="overflow-x-auto font-mono text-xs leading-5 text-(--color-text-soft)">
            {exercise.data.examplesJson}
          </pre>
        </div>

        {output && (
          <div className="rounded-xl border border-(--color-border-hover) bg-(--color-surface) p-4">
            <p className="mb-2 flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-(--color-green)">
              <Terminal className="size-3.5" /> Résultat
            </p>
            <pre className="whitespace-pre-wrap font-mono text-xs leading-5 text-(--color-text)">
              {output}
            </pre>
          </div>
        )}
      </div>

      <div className="flex flex-col rounded-2xl border border-(--color-border) bg-(--color-surface) p-3">
        <div className="flex items-center justify-between border-b border-(--color-border) px-2 pb-3">
          <select
            value={language}
            onChange={(e) => setLanguage(e.target.value)}
            className="rounded-lg bg-transparent text-sm text-(--color-text-soft) outline-none"
          >
            {languages.map((l) => (
              <option key={l} value={l} className="bg-(--color-surface)">
                {l}
              </option>
            ))}
          </select>
          <Button variant="outline" size="sm" onClick={runCode} disabled={executeCode.isPending}>
            <Play className="size-3.5" /> {executeCode.isPending ? "Exécution..." : "Exécuter"}
          </Button>
          <Button variant="solid" size="sm" onClick={submit} disabled={submitExercise.isPending}>
            <CheckCircle className="size-3.5" /> {submitExercise.isPending ? "Correction..." : "Soumettre"}
          </Button>
        </div>
        <div className="mt-3 flex-1 overflow-hidden">
          <CodeEditor
            key={`${exercise.data.id}-${language}`}
            initialCode={starterCode}
            onChange={setCode}
          />
        </div>
      </div>
    </div>
  );
}

function parseStringArray(value: string | undefined, fallback: string[]) {
  if (!value) return fallback;
  try {
    const parsed = JSON.parse(value) as unknown;
    return Array.isArray(parsed) && parsed.every((item) => typeof item === "string") ? parsed : fallback;
  } catch {
    return fallback;
  }
}

function parseStarterCode(value: string | undefined, language: string) {
  if (!value) return fallbackCode;
  try {
    const parsed = JSON.parse(value) as Record<string, string>;
    return parsed[language] ?? Object.values(parsed)[0] ?? fallbackCode;
  } catch {
    return fallbackCode;
  }
}
