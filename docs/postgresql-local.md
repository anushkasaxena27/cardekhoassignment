# PostgreSQL setup (CarDekho)

## Default layout

- **Database:** `carapp`
- **Application user:** `user2` / `password1` (override with `DATABASE_USER` / `DATABASE_PASSWORD`)
- **Schema:** `cardekho` — all Flyway migrations and JPA tables live here (not in `public`)

This avoids **PostgreSQL 15+** `permission denied for schema public` for roles that cannot `CREATE` in `public`.

`application.yml` sets:

- JDBC `currentSchema=cardekho`
- `spring.flyway.schemas` / `default-schema: cardekho`
- `spring.jpa.properties.hibernate.default_schema: cardekho`

## Create the `cardekho` schema (superuser or DB owner)

Run once on database `carapp` (e.g. as `postgres`):

```sql
CREATE SCHEMA IF NOT EXISTS cardekho AUTHORIZATION user2;
```

The app user must **own** the schema (or have `USAGE` + `CREATE` on it) so Flyway can create `flyway_schema_history` and migration tables.

## Symptom: long Spring error chain ending in Flyway

If you still see **`permission denied for schema public`**, Flyway is not using `cardekho` — check `DATABASE_URL` does not strip `currentSchema`, and that `spring.flyway.default-schema` is set.

## Docker Compose in this repo

`docker-compose.yml` creates user **`cardekho`**, not `user2`. To use Docker Postgres with this app’s defaults, either:

- Create `user2` and schema `cardekho` inside the container DB, or  
- Set `DATABASE_USER=cardekho` and `DATABASE_PASSWORD=cardekho` when running Spring against that container.

## Legacy: grant `public` instead

If you insist on tables in `public`, grant `CREATE` on schema `public` to your app user (see older PostgreSQL 15 migration guides). The project standard is the dedicated **`cardekho`** schema.
