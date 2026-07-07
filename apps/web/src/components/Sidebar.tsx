import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  LayoutDashboard,
  GraduationCap,
  Trophy,
  CircleUser,
  Settings,
  LogOut,
  Zap,
  Shield,
  Menu,
  X,
} from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { useUser } from "../hooks/useUser";

const navItems = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard, end: true },
  { to: "/lessons", label: "Leçons", icon: GraduationCap, end: false },
  { to: "/achievements", label: "Succès", icon: Trophy, end: false },
  { to: "/profile", label: "Profil", icon: CircleUser, end: false },
  { to: "/settings", label: "Réglages", icon: Settings, end: false },
];

export default function Sidebar() {
  const auth = useAuth();
  const navigate = useNavigate();
  const userQuery = useUser();
  const [open, setOpen] = useState(false);
  const displayName = userQuery.data?.displayName ?? "Ping";
  const items = auth.isAdmin
    ? [...navItems, { to: "/admin", label: "Admin", icon: Shield, end: false }]
    : navItems;

  const logout = () => {
    auth.logout();
    navigate("/login", { replace: true });
  };

  return (
    <>
      <button
        onClick={() => setOpen(true)}
        aria-label="Ouvrir le menu"
        className="fixed top-4 left-4 z-30 flex size-10 items-center justify-center rounded-xl border border-(--color-border) bg-(--color-bg) text-(--color-text) lg:hidden"
      >
        <Menu className="size-5" strokeWidth={2} />
      </button>

      {open && (
        <div
          onClick={() => setOpen(false)}
          className="fixed inset-0 z-40 bg-black/50 lg:hidden"
        />
      )}

      <aside
        className={`fixed inset-y-0 left-0 z-50 flex h-screen w-64 shrink-0 flex-col border-r border-(--color-border) bg-(--color-bg) px-5 py-7 transition-transform duration-200 lg:static lg:translate-x-0 ${
          open ? "translate-x-0" : "-translate-x-full"
        }`}
      >
        <div className="flex items-center justify-between px-1">
          <div className="flex items-center gap-2">
            <Zap className="size-6 text-(--color-green)" strokeWidth={2.5} />
            <span className="font-display text-2xl font-bold tracking-tight text-(--color-green)">
              Ping
            </span>
          </div>
          <button
            onClick={() => setOpen(false)}
            aria-label="Fermer le menu"
            className="flex size-8 items-center justify-center rounded-lg text-(--color-text-mute) hover:text-(--color-text) lg:hidden"
          >
            <X className="size-5" strokeWidth={2} />
          </button>
        </div>

        <div className="mt-7 flex items-center gap-3 px-1">
          <div className="flex size-10 items-center justify-center rounded-full border border-(--color-border-hover) bg-(--color-surface) text-sm font-semibold text-(--color-text-soft)">
            {displayName.slice(0, 1).toUpperCase()}
          </div>
          <div className="min-w-0">
            <p className="truncate text-sm font-semibold text-(--color-text)">{displayName}</p>
            <p className="text-xs text-(--color-text-mute)">
              {auth.isAdmin ? "Admin" : "Apprenant"}
            </p>
          </div>
        </div>

        <nav className="mt-8 flex flex-col gap-1">
          {items.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              onClick={() => setOpen(false)}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-xl border px-3.5 py-2.5 text-[15px] transition-colors ${
                  isActive
                    ? "border-(--color-green) bg-(--color-green-soft) text-(--color-green) font-medium"
                    : "border-transparent text-(--color-text-soft) hover:bg-(--color-surface) hover:text-(--color-text)"
                }`
              }
            >
              <Icon className="size-[18px]" strokeWidth={2} />
              {label}
            </NavLink>
          ))}
        </nav>

        <div className="mt-auto">
          <button
            onClick={logout}
            className="flex w-full items-center gap-3 rounded-xl px-3.5 py-2.5 text-[15px] text-(--color-text-mute) transition-colors hover:bg-(--color-surface) hover:text-(--color-text-soft)"
          >
            <LogOut className="size-[18px]" strokeWidth={2} />
            Déconnexion
          </button>
        </div>
      </aside>
    </>
  );
}
