import { useEffect, useState } from "react";
import { Link, useParams, Navigate } from "react-router-dom";
import { ArrowLeft, Check, CheckCircle, Play, Sparkles, Terminal, Trophy, X } from "lucide-react";
import { useQueryClient } from "@tanstack/react-query";
import { Button, Card, XpBadge } from "../components/ui";
import CodeEditor from "../components/CodeEditor";
import { useExecuteCode, useExercise, useSubmitExercise } from "../hooks/useExercise";
import { useLesson } from "../hooks/useLesson";
import type { ExecuteResponse, SubmitExerciseResponse } from "../api/exercises";

const fallbackLanguages = ["Python"];
const fallbackCode = `print("hello")`;

type ResultPanel =
  | { type: "execute"; result: ExecuteResponse }
  | { type: "submit"; result: SubmitExerciseResponse }
  | { type: "error"; message: string };

export default function ExerciseIDE() {
  const { lessonId, exerciseId } = useParams();
  const queryClient = useQueryClient();
  const lesson = useLesson(lessonId);
  const exercise = useExercise(exerciseId);
  const executeCode = useExecuteCode();
  const submitExercise = useSubmitExercise();
  const [language, setLanguage] = useState("Python");
  const [output, setOutput] = useState<ResultPanel | null>(null);
  const languages = parseStringArray(exercise.data?.allowedLanguages, fallbackLanguages);
  const starterCode = parseStarterCode(exercise.data?.starterCodeJson, language);
  const examples = parseExamples(exercise.data?.examplesJson);
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
      setOutput({ type: "execute", result });
    } catch (err) {
      setOutput({ type: "error", message: err instanceof Error ? err.message : "Exécution impossible" });
    }
  };

  const submit = async () => {
    setOutput(null);
    try {
      const result = await submitExercise.mutateAsync({ id: exercise.data.id, language, code });
      setOutput({ type: "submit", result });

      await Promise.all([
        queryClient.invalidateQueries({ queryKey: ["lessons"] }),
        queryClient.invalidateQueries({ queryKey: ["lesson", lessonId] }),
        queryClient.invalidateQueries({ queryKey: ["exercise", exerciseId] }),
        queryClient.invalidateQueries({ queryKey: ["userStats"] }),
        queryClient.invalidateQueries({ queryKey: ["achievements"] }),
        queryClient.invalidateQueries({ queryKey: ["questsDaily"] }),
      ]);
    } catch (err) {
      setOutput({ type: "error", message: err instanceof Error ? err.message : "Soumission impossible" });
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
          <div className="flex flex-col gap-3">
            {examples.map((example, index) => (
              <div key={`${example.input}-${index}`} className="grid gap-2 sm:grid-cols-2">
                <div>
                  <p className="text-[11px] font-semibold uppercase tracking-wide text-(--color-text-mute)">
                    Entrée
                  </p>
                  <pre className="mt-1 overflow-x-auto whitespace-pre-wrap rounded-lg bg-(--color-surface-elevated) px-3 py-2 font-mono text-xs leading-5 text-(--color-text-soft)">
                    {example.input || "-"}
                  </pre>
                </div>
                <div>
                  <p className="text-[11px] font-semibold uppercase tracking-wide text-(--color-text-mute)">
                    Sortie
                  </p>
                  <pre className="mt-1 overflow-x-auto whitespace-pre-wrap rounded-lg bg-(--color-surface-elevated) px-3 py-2 font-mono text-xs leading-5 text-(--color-text-soft)">
                    {example.output || "-"}
                  </pre>
                </div>
              </div>
            ))}
          </div>
        </div>

        {output && <ResultPanelView output={output} />}
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

function ResultPanelView({ output }: { output: ResultPanel }) {
  if (output.type === "error") {
    return (
      <div className="rounded-xl border border-(--color-orange-dim) bg-(--color-orange-soft) p-4">
        <p className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-(--color-orange)">
          <X className="size-3.5" /> Erreur
        </p>
        <p className="mt-3 text-sm text-(--color-text)">{output.message}</p>
      </div>
    );
  }

  if (output.type === "execute") {
    const result = output.result;
    const isOk = !result.timedOut && result.exitCode === 0;
    return (
      <div className="rounded-xl border border-(--color-border-hover) bg-(--color-surface) p-4">
        <div className="flex items-center justify-between gap-3">
          <p className="flex items-center gap-2 text-xs font-semibold uppercase tracking-wide text-(--color-green)">
            <Terminal className="size-3.5" /> Exécution
          </p>
          <span
            className={`rounded-full border px-2.5 py-1 text-xs ${
              isOk ? "border-(--color-green) text-(--color-green)" : "border-(--color-orange-dim) text-(--color-orange)"
            }`}
          >
            {result.timedOut ? "Timeout" : `Code ${result.exitCode}`}
          </span>
        </div>

        {result.stdout && <OutputBlock label="Sortie" value={result.stdout.trim()} />}
        {result.stderr && <OutputBlock label="Erreurs" value={result.stderr.trim()} tone="orange" />}
        {!result.stdout && !result.stderr && (
          <p className="mt-4 text-sm text-(--color-text-soft)">Aucune sortie produite.</p>
        )}
      </div>
    );
  }

  const result = output.result;
  const passed = result.status === "PASSED";
  const completedQuestUpdates = result.questUpdates.filter((quest) => quest.status === "COMPLETED" || quest.status === "CLAIMED");

  return (
    <div
      className={`rounded-xl border p-4 ${
        passed ? "border-(--color-green) bg-(--color-green-soft)" : "border-(--color-orange-dim) bg-(--color-orange-soft)"
      }`}
    >
      <div className="flex flex-wrap items-center justify-between gap-3">
        <p
          className={`flex items-center gap-2 text-xs font-semibold uppercase tracking-wide ${
            passed ? "text-(--color-green)" : "text-(--color-orange)"
          }`}
        >
          {passed ? <Check className="size-3.5" /> : <X className="size-3.5" />}
          {passed ? "Soumission validée" : "Soumission refusée"}
        </p>
        <div className="flex flex-wrap items-center gap-2">
          <span className="rounded-full border border-(--color-border-hover) px-2.5 py-1 text-xs text-(--color-text-soft)">
            {result.testResults.filter((test) => test.passed).length}/{result.testResults.length} tests
          </span>
          <XpBadge xp={result.xpAwarded} />
          {result.leveledUp && (
            <span className="rounded-full border border-(--color-green) px-2.5 py-1 text-xs text-(--color-green)">
              Niveau {result.newLevel}
            </span>
          )}
        </div>
      </div>

      <div className="mt-4 flex flex-col gap-2">
        {result.testResults.map((test, index) => (
          <div
            key={`${test.input}-${index}`}
            className="rounded-lg border border-(--color-border) bg-(--color-surface) p-3"
          >
            <div className="flex items-center justify-between gap-2">
              <p className="text-sm font-semibold">Test {index + 1}</p>
              <span
                className={`inline-flex items-center gap-1 rounded-full px-2 py-1 text-xs ${
                  test.passed ? "bg-(--color-green-soft) text-(--color-green)" : "bg-(--color-orange-soft) text-(--color-orange)"
                }`}
              >
                {test.passed ? <Check className="size-3" /> : <X className="size-3" />}
                {test.passed ? "Réussi" : "Échoué"}
              </span>
            </div>
            <div className="mt-3 grid gap-2 sm:grid-cols-2">
              <OutputBlock label="Attendu" value={test.expected || "-"} compact />
              <OutputBlock label="Obtenu" value={test.actual || "-"} compact tone={test.passed ? "neutral" : "orange"} />
            </div>
          </div>
        ))}
      </div>

      {(result.newAchievements.length > 0 || completedQuestUpdates.length > 0) && (
        <div className="mt-4 flex flex-col gap-2">
          {result.newAchievements.map((achievement) => (
            <p key={achievement.id} className="inline-flex items-center gap-2 text-sm text-(--color-green)">
              <Trophy className="size-4" /> Succès débloqué: {achievement.title}
            </p>
          ))}
          {completedQuestUpdates.map((quest) => (
            <p key={quest.id} className="inline-flex items-center gap-2 text-sm text-(--color-green)">
              <Sparkles className="size-4" /> Quête prête: {quest.title}
            </p>
          ))}
        </div>
      )}
    </div>
  );
}

function OutputBlock({
  label,
  value,
  compact = false,
  tone = "neutral",
}: {
  label: string;
  value: string;
  compact?: boolean;
  tone?: "neutral" | "orange";
}) {
  return (
    <div className={compact ? "" : "mt-4"}>
      <p className="text-[11px] font-semibold uppercase tracking-wide text-(--color-text-mute)">
        {label}
      </p>
      <pre
        className={`mt-1 overflow-x-auto whitespace-pre-wrap rounded-lg px-3 py-2 font-mono text-xs leading-5 ${
          tone === "orange"
            ? "bg-(--color-orange-soft) text-(--color-orange)"
            : "bg-(--color-surface-elevated) text-(--color-text-soft)"
        }`}
      >
        {value}
      </pre>
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

function parseExamples(value: string | undefined) {
  if (!value) {
    return [{ input: "", output: "" }];
  }

  try {
    const parsed = JSON.parse(value) as Array<{ input?: string; output?: string }>;
    if (!Array.isArray(parsed) || parsed.length === 0) {
      return [{ input: value, output: "" }];
    }
    return parsed.map((example) => ({
      input: example.input ?? "",
      output: example.output ?? "",
    }));
  } catch {
    return [{ input: value, output: "" }];
  }
}
