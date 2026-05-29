# CarDekho — Conversational Car Recommendation API

Spring Boot **3.3.x**, **Java 17**, **PostgreSQL**, **Flyway**, **JWT** auth, **rule-based NLP**, **recommendation scoring**, **OpenAPI/Swagger**, and a **programmatic catalog seed** (180+ variants, 900+ reviews, multiple images per variant).

## Prerequisites

- JDK 17+
- Maven 3.9+
- Docker (for PostgreSQL and integration tests via Testcontainers)

## Quick start (local)

1. Start PostgreSQL:

   ```bash
   docker compose up -d postgres
   ```

2. Run the application (Flyway applies schema; seed runs on first empty DB):

   ```bash
   mvn spring-boot:run
   ```

3. Open Swagger UI: `http://localhost:8081/swagger-ui.html`

Default datasource (override with env vars `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`):

- URL: `jdbc:postgresql://localhost:5432/carapp`
- User / password: `cardekho` / `cardekho`

Disable seeding: `SEED_ENABLED=false` or `app.seed.enabled=false`.

## Caching

Spring **simple** in-memory cache is enabled (trending analytics). **Redis** is not wired in this revision; you can add `spring-boot-starter-data-redis`, set `spring.cache.type=redis`, and provide a `RedisConnectionFactory` for distributed cache.

## Key API paths

| Method | Path | Auth |
|--------|------|------|
| POST | `/api/auth/register` | Public |
| POST | `/api/auth/login` | Public |
| POST | `/api/auth/refresh` | Public |
| GET | `/api/cars` | Public |
| GET | `/api/cars/{id}` | Public |
| POST | `/api/cars/filter` | Public |
| POST | `/api/cars/search` | Public |
| POST | `/api/cars/compare` | Public |
| POST | `/api/recommendations/chat` | Public |
| GET | `/api/analytics/trending` | Public |
| GET | `/api/analytics/most-searched` | Public |
| GET | `/api/analytics/top-rated` | Public |
| GET/POST/DELETE | `/api/shortlist` … | JWT |

## Sample requests

**Register**

```http
POST /api/auth/register
Content-Type: application/json

{"email":"buyer@example.com","password":"changeMe123","fullName":"Buyer"}
```

**Login**

```http
POST /api/auth/login
Content-Type: application/json

{"email":"buyer@example.com","password":"changeMe123"}
```

**Conversational recommendations**

```http
POST /api/recommendations/chat
Content-Type: application/json

{"query":"I need a safe SUV under 20 lakhs with good mileage"}
```

Response includes `extractedIntent`, `appliedFilters`, and `recommendations` with `matchScore` and human-readable `reasons`.

**Shortlist (JWT)**

```http
Authorization: Bearer <access_token>
POST /api/shortlist/42
```

## Tests

```bash
mvn test
```

Integration tests require Docker (Testcontainers PostgreSQL).

## Advanced / not included

- **Elasticsearch** semantic search — extension point: add documents on seed and query from a dedicated `SearchService`.
- **Redis** — optional; see Caching above.

## License

Demo / educational use.
