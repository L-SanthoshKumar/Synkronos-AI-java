# Frontend - Synkronos AI (React + Vite)

This folder contains the React frontend for the Synkronos AI job portal.

## Requirements
- Node.js 18+
- npm (or yarn)

## Quick start (local)
1. Install dependencies:

```powershell
cd frontend
npm install
```

2. Start dev server (Vite):

```powershell
npm run dev
```

The frontend dev server runs at `http://localhost:5173` by default.

3. Build for production:

```powershell
npm run build
```

4. Preview production build locally:

```powershell
npm run preview
```

## Configuration
- API base URL used by the frontend is configured in `src/services/api.js` or via environment variables when building.

## Docker
A `Dockerfile` is included for building a production image. The repo root `docker-compose.yml` can run the frontend together with backend and ML engine.

## Troubleshooting
- If the dev server fails to start, ensure your Node.js version is 18+ and that port 5173 is free.
- Delete `node_modules` and reinstall if dependency issues occur:

```powershell
rm -r node_modules
npm install
```

---
Created/updated by automation script.