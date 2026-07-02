import { GraduationCap, Star, Trophy, Zap, type LucideIcon } from "lucide-react";

const icons: Record<string, LucideIcon> = {
  GraduationCap,
  Star,
  Trophy,
  Zap,
};

export default function AchievementIcon({
  name,
  className = "size-4 text-(--color-green)",
}: {
  name: string;
  className?: string;
}) {
  if (isImageIcon(name)) {
    return <img src={name} alt="" className="size-full object-cover" />;
  }

  const Icon = icons[name] ?? Trophy;
  return <Icon className={className} />;
}

function isImageIcon(name: string) {
  return name.startsWith("/") || name.startsWith("http://") || name.startsWith("https://") || name.startsWith("data:");
}
