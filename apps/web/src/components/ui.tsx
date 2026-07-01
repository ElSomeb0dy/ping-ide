import type { ReactNode } from "react";

export function XpBadge({ xp }: { xp: number }) {
  return (
    <span className="inline-flex items-center rounded-full border border-(--color-orange-dim) bg-(--color-orange-soft) px-2.5 py-1 text-xs font-semibold text-(--color-orange)">
      +{xp} xp
    </span>
  );
}

export function Pill({
  children,
  tone = "neutral",
}: {
  children: ReactNode;
  tone?: "neutral" | "green" | "orange" | "muted";
}) {
  const tones: Record<string, string> = {
    neutral: "border-(--color-border-hover) text-(--color-text-soft)",
    green: "border-(--color-green) text-(--color-green) bg-(--color-green-soft)",
    orange: "border-(--color-orange-dim) text-(--color-orange) bg-(--color-orange-soft)",
    muted: "border-(--color-border) text-(--color-text-mute)",
  };
  return (
    <span
      className={`inline-flex items-center rounded-full border px-3 py-1 text-xs font-medium ${tones[tone]}`}
    >
      {children}
    </span>
  );
}

export function ProgressBar({
  value,
  max,
  showLabel = true,
}: {
  value: number;
  max: number;
  showLabel?: boolean;
}) {
  const pct = Math.round((value / max) * 100);
  return (
    <div className="flex items-center gap-3">
      <div className="h-2 flex-1 overflow-hidden rounded-full bg-(--color-surface-elevated)">
        <div
          className="h-full rounded-full bg-(--color-orange) transition-[width]"
          style={{ width: `${pct}%` }}
        />
      </div>
      {showLabel && (
        <span className="shrink-0 text-xs font-medium text-(--color-text-soft)">{pct}%</span>
      )}
    </div>
  );
}

export function Button({
  children,
  variant = "outline",
  size = "md",
  ...props
}: {
  children: ReactNode;
  variant?: "outline" | "solid" | "ghost";
  size?: "sm" | "md";
} & React.ButtonHTMLAttributes<HTMLButtonElement>) {
  const base =
    "inline-flex items-center justify-center gap-1.5 rounded-full font-medium transition-colors disabled:cursor-not-allowed disabled:opacity-40";
  const sizes = {
    sm: "px-3.5 py-1.5 text-xs",
    md: "px-5 py-2 text-sm",
  };
  const variants = {
    outline:
      "border border-(--color-green) text-(--color-green) hover:bg-(--color-green-soft)",
    solid: "bg-(--color-green) text-(--color-bg) hover:bg-(--color-green-dim)",
    ghost:
      "border border-(--color-border-hover) text-(--color-text-soft) hover:bg-(--color-surface)",
  };
  return (
    <button className={`${base} ${sizes[size]} ${variants[variant]}`} {...props}>
      {children}
    </button>
  );
}

export function Card({
  children,
  highlight = false,
  className = "",
}: {
  children: ReactNode;
  highlight?: boolean;
  className?: string;
}) {
  return (
    <div
      className={`rounded-2xl border bg-(--color-surface) p-6 ${
        highlight ? "border-(--color-green)" : "border-(--color-border)"
      } ${className}`}
    >
      {children}
    </div>
  );
}
