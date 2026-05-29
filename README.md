# CarDekho AI — conversational car recommendations

## Start the full app (Docker) — **read this first**

1. **Install and open [Docker Desktop](https://www.docker.com/products/docker-desktop/)**  
   Installing is not enough: start the app from the Start menu and wait until it says **Docker is running** (whale icon idle).

2. In a terminal, **cd to this folder** (the one that contains `pom.xml` and `docker-compose.demo.yml`).

3. Run:

   ```bash
   docker compose -f docker-compose.demo.yml up --build
   ```

4. Open **http://localhost:8080** in the browser when logs settle (first run builds images and can take several minutes).

**Stuck on `npipe` / “cannot find the file specified”?** The Docker engine is not running — open Docker Desktop, wait until it is ready, then run `docker version` and confirm you see **Server** as well as **Client**.

More detail (reset DB, change port, timing): **[DEPLOY-DEMO.md](./DEPLOY-DEMO.md)**.

---

## What we built and why

A **small car marketplace + AI assistant**: users describe what they want in plain language; the backend **parses intent**, **filters inventory**, **scores variants**, and returns **explainable** picks with photos and prices. The goal was a **credible demo** of “CarDekho-style” discovery (browse, compare, shortlist) wired to a **real** Spring API and PostgreSQL catalog—not a slide deck.

### Deliberately not in scope (cut or stubbed)

- **No hosted LLM** — intent is **rule-based NLP** (regex/heuristics), not GPT calls (cost, keys, latency).
- **No Redis / Elasticsearch** — analytics use a **simple in-memory cache**; search is SQL/JPA, not a search cluster.
- **No production hardening** — demo JWT secret, no rate limits, no pen-test story.
- **No mobile app** — responsive web only.

---

## Tech stack (and why)

| Layer | Choice | Why |
|--------|--------|-----|
| API | **Spring Boot 3**, **Java 17** | Standard stack for REST, security, validation, OpenAPI. |
| Data | **PostgreSQL**, **Flyway**, **JPA** | Relational model for cars, users, reviews; migrations are reproducible. |
| Auth | **JWT** (access + refresh) | Stateless API; shortlist and profile stay protected. |
| UI | **Angular** (standalone, **Material**, signals-friendly services) | Structured SPA, accessible components, easy to theme “marketplace” UI. |
| Dev / share | **Docker Compose** (Postgres + API + **nginx** SPA) | One command for someone else; same-origin `/api` avoids CORS pain. |

---

## AI tools vs. hand work

**Where AI coding assistants helped most**

- Faster **scaffolding** (Angular routes, Material layouts, proxy, Docker compose files).
- **Boilerplate** (DTOs, repetitive service wiring, initial README structure).
- **Debugging hints** after errors (e.g. read-only transaction + `INSERT`, `Double` vs `BigDecimal` from JPQL `avg()`).

**What we kept manual / judgment-heavy**

- **Data model and API contract** (what the recommendation response must contain).
- **Transaction boundaries** (read-only vs writes, `REQUIRES_NEW` for logging).
- **PostgreSQL 15 `public` schema permissions** — moved app tables to a dedicated **`cardekho`** schema and aligned Flyway/JPA.
- **CORS and same-origin** story (proxy vs nginx in Docker).

**Where tools got in the way**

- Wrong **defaults** (ports, DB users) that looked fine in snippets but broke a real machine.
- Over-long docs; easy to **ship contradictions** unless trimmed (this README is intentionally short).

---

## If we had four more hours

1. **Publish images** (GHCR/Docker Hub) so `docker compose pull && up` is fast for everyone—no local Maven/npm inside Docker on first run.  
2. **Spring Boot Actuator** + documented probes for Kubernetes or Compose `healthcheck` without `wget` in the image.  
3. **One smoke E2E** (Playwright or Cypress) against `http://localhost:8080` for chat + browse.  
4. **Rate limiting** on `/api/recommendations/chat` and stricter JWT rotation notes for anything beyond demo.

---

## Local development (without full Docker stack)

- **Database only:** `docker compose up -d postgres` (see [docker-compose.yml](./docker-compose.yml)).  
- **Backend:** `mvn spring-boot:run` — API on **8081** by default; Swagger: **http://localhost:8081/swagger-ui.html**.  
- **Frontend + backend together:** from repo root, **[README-NPM.md](./README-NPM.md)** (`npm start` after `npm install` + `npm run install:web`).

DB defaults and schema notes: **[docs/postgresql-local.md](./docs/postgresql-local.md)**.

---

## API overview

Public highlights: `POST /api/recommendations/chat`, `GET/POST /api/cars`, `GET /api/analytics/trending`. Shortlist and other user routes need JWT. Full list: **Swagger** when the API is running.

---

## License

Demo / educational use.
