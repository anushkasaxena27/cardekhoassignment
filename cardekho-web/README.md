# CarDekho Web (Angular)

Production-style SPA for the conversational car recommendation platform. It talks to the Spring Boot API using **relative** paths (`/api/...`) so `ng serve` can proxy to the backend without CORS friction.

## Prerequisites

- Node.js 20+ (LTS recommended)
- npm
- Spring Boot backend running (this repo defaults to **port 8081** — see `src/main/resources/application.yml`)

## Install & run

```bash
cd cardekho-web
npm install
npm start
```

Open `http://localhost:4200`.

## One command: backend + frontend (`npm start` at repo root)

From the **repository root** (the folder that contains `pom.xml`, `mvnw.cmd`, and `cardekho-web/`), with PostgreSQL running and DB configured:

```bash
npm install
npm run install:web
npm start
```

This runs Spring Boot on **port 8081** and, in parallel, **waits** until `GET http://127.0.0.1:8081/api/cars` returns **200**, then runs `ng serve` for this app (with the dev proxy). **Ctrl+C** stops both processes (via `concurrently -k`).

The dev server uses `src/proxy.conf.json` to forward `/api` → `http://localhost:8081`. To point at another host/port, edit that file (or set `apiBaseUrl` in `src/environments/environment.prod.ts` for production builds behind a reverse proxy).

## Build

```bash
npm run build
```

Output: `dist/cardekho-web/browser`.

## Tests

```bash
npm test
```

## Docker (static nginx)

For **Postgres + Spring Boot + this UI** in one command (best way to share with someone), use the repo root **`docker-compose.demo.yml`** — see **`../DEPLOY-DEMO.md`**.

The standalone `Dockerfile` in this folder builds only the SPA; its default `nginx.conf` proxies `/api` to `host.docker.internal:8081` (Docker Desktop on Windows/macOS). On Linux Docker, add `--add-host=host.docker.internal:host-gateway` to `docker run`, or change `nginx.conf` to your backend service hostname.

From `cardekho-web/`:

```bash
docker build -t cardekho-web .
docker run --rm -p 8080:80 cardekho-web
```

In production, put nginx or an API gateway in front of Spring Boot and align `/api` routing with your deployment.

## Features (high level)

- Standalone components, Angular Material, signals-friendly services
- JWT auth with `HttpClient` interceptor; login/register routes
- Chat recommendations (`POST /api/recommendations/chat`), browse/filter cars, detail tabs, compare, shortlist (guest localStorage + authenticated API when logged in)
- Theme toggle (dark/light) persisted in `localStorage`

## Backend CORS

Spring should allow your dev origin (e.g. `http://localhost:4200`, `http://localhost:8080` for the Docker demo). The Angular proxy is still recommended during local development so the browser always calls same-origin `/api`.
