# Ping IDE

Gamified coding-exercise platform (EPITA project: "Conception d'une IDE gamifiée"). Users solve Python exercises in a browser IDE, submit code that runs in a sandboxed Docker container against test cases, and earn XP/levels/streaks/achievements/quests.

Monorepo layout:
- `apps/web`: React 19 + TypeScript + Vite + Tailwind CSS v4 frontend
- `apps/api`: Quarkus (Java 21) backend

## Prerequisites

- Docker (with the Docker daemon running)
- Node.js + npm

## Install

Install the frontend dependencies once:

```bash
npm install --prefix apps/web
```

## Launch

From the repo root, in two terminals:

```bash
# 1. Build and start the backend (API + Postgres)
docker-compose up --build
```

```bash
# 2. Start the frontend dev server
npm --prefix apps/web run dev
```

- Frontend: http://localhost:5173
- Backend API: http://localhost:8080

On first startup the backend seeds demo lessons/exercises/achievements/quests and creates a bootstrap admin account:
- login: `admin.admin`
- password: `admin`

You can also register a new account from the app's Register page (username must look like `first.last`).
