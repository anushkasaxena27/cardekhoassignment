# Run backend + frontend with npm

From this directory (same folder as `pom.xml` and `mvnw.cmd`):

```bash
npm install
npm run install:web
npm start
```

- **Backend:** Spring Boot on **port 8081** (`SERVER_PORT=8081`).
- **Frontend:** After the API responds at `http://127.0.0.1:8081/api/cars`, Angular dev server starts in `cardekho-web/` (proxy to 8081).

Requirements: JDK, Node.js 20+, PostgreSQL **`carapp`** / **`user2`** and schema **`cardekho`** — see **`docs/postgresql-local.md`**. Example: `docker compose up -d postgres` only matches Spring defaults if you add `user2` + `cardekho` in that DB or set **`DATABASE_USER` / `DATABASE_PASSWORD`** to the compose user (`cardekho` / `cardekho`).

**Ctrl+C** stops both servers.

---

## Ship the whole stack with Docker (recipient runs one command)

See **`DEPLOY-DEMO.md`** (same folder as `docker-compose.demo.yml`) for **Postgres + API + UI** at **http://localhost:8080**.
