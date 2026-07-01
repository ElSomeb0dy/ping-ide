# Ping — IDE gamifiée

Frontend React + TypeScript + Tailwind CSS v4 pour la plateforme d'apprentissage du code gamifiée (projet EPITA — Conception d'une IDE gamifiée).

## Stack

- React 19 + Vite + TypeScript
- Tailwind CSS v4
- React Router
- lucide-react (icônes)
- Polices : Space Grotesk (titres/UI) et JetBrains Mono (éditeur de code)

## Pages

- `/` — Dashboard (niveau, XP, série, quêtes du jour, succès récents)
- `/lessons` — Liste des leçons avec recherche et filtres
- `/lessons/:lessonId` — Détail d'une leçon et grille d'exercices
- `/lessons/:lessonId/exercises/:exerciseId` — IDE (éditeur de code + exécution simulée)
- `/achievements` — Succès débloqués / en cours / verrouillés
- `/profile` — Statistiques du profil
- `/settings` — Réglages de l'environnement IDE

## Lancer le projet

```bash
npm install
npm run dev
```

## Palette de couleurs

| Token | Hex | Usage |
|---|---|---|
| `--color-bg` | `#0a0b09` | Fond général |
| `--color-surface` | `#12150f` | Cartes |
| `--color-surface-elevated` | `#1b201a` | Éditeur de code, anneaux |
| `--color-border` | `#262e21` | Bordures discrètes |
| `--color-border-hover` | `#38432f` | Bordures actives/hover |
| `--color-green` | `#8fd15c` | Couleur de marque, navigation active, boutons principaux |
| `--color-orange` | `#e3a878` | XP, progression, série |
| `--color-text` | `#f3f4ef` | Texte principal |
| `--color-text-soft` | `#98a08c` | Texte secondaire |
| `--color-text-mute` | `#5b6253` | Texte tertiaire |

Toutes les données affichées sont des données factices (`src/data/mockData.ts`) à connecter à votre backend (Quarkus/Go) lors de l'intégration.
