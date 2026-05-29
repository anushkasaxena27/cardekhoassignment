# Run the full app in Docker (share with a teammate)

**Paste for a colleague:**

> Install Docker Desktop, clone or unzip this repo, `cd` into the project folder, then run:  
> `docker compose -f docker-compose.demo.yml up --build`  
> Open **http://localhost:8080** when the logs stop spamming (first run can take several minutes to build; later starts are much faster).  
> Details: see sections below.

One URL for the UI (**nginx** on port **8080**) and the **API** behind the same host (`/api/…`), so there are no CORS issues.

## What they need

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or Docker Engine + Compose v2)  
- **~4 GB free RAM** (Postgres + Java + seed data)  
- **Git** (or send them a zip of the repo)

## One command

From the **repository root** (`CarDekho` folder that contains `pom.xml` and `cardekho-web/`):

```bash
docker compose -f docker-compose.demo.yml up --build
```

When the logs quiet down and health checks pass, open:

**http://localhost:8080**

Stop with `Ctrl+C` or:

```bash
docker compose -f docker-compose.demo.yml down
```

## How long does it take?

| Situation | Typical time |
|-----------|----------------|
| **First run** on a machine (downloads base images + Maven + npm builds inside Docker) | **~5–15 min** depending on CPU/network |
| **Second run** (images already built, volume already has DB) | **often ~1–2 min** to Postgres + Flyway + seed + API healthy |
| **Clean DB** (delete volume — see below) | add **~1–3 min** for migrations + seed again |

So: **under 2 minutes is realistic after the first successful build**, not always on the very first `up --build` everywhere.

## Clean slate (reset database)

```bash
docker compose -f docker-compose.demo.yml down -v
docker compose -f docker-compose.demo.yml up --build
```

## If port 8080 is busy

Edit `docker-compose.demo.yml` → under `web` → `ports` → e.g. `"9080:80"` and open **http://localhost:9080**.

## Optional: ship without Git

1. Zip the project (exclude `node_modules`, `target`, `.git` if huge).  
2. Recipient unzips and runs the same `docker compose` command.

## Optional: publish images (fast for everyone)

Build and push to GHCR/Docker Hub, then replace `build:` blocks with `image: your-registry/cardekho-api:tag` so recipients only run `docker compose pull && docker compose up` (**~1–2 min** with good network). Steps depend on your registry; ask if you want a sample `docker-compose.publish.yml`.

## Notes

- Demo compose uses DB user **`cardekho`** / **`cardekho`** and schema **`cardekho`** (see `docker/postgres-init/`). This overrides local `application.yml` defaults via Spring env vars.
- API is **not** exposed on the host; only **web:8080** is. API is reachable inside the compose network as `http://api:8081`.
