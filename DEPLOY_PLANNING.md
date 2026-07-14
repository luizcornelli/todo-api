# Deploy Planning

## Branch

`main` is the project's primary branch. Every deploy should be based on it.

## Prerequisites

- Docker `28.3.1` and Docker Compose `v2.36.2`
- JDK `21` and Maven (only needed to build the `.jar`; you can use the `./mvnw` wrapper)

## Versions used

| Component        | Version                                |
|-------------------|-----------------------------------------|
| Java              | `21` (`eclipse-temurin:21-jdk` image)   |
| PostgreSQL        | `16` (`postgres:16` image)              |
| Docker            | `28.3.1`                                |
| Docker Compose    | `v2.36.2`                               |

## Steps

1. Build the application jar (the `Dockerfile` copies a pre-built jar, it does not build the project):
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. Build the images and start the containers (Postgres + API):
   ```bash
   docker compose up -d --build
   ```

   This starts two containers:
   - `todo-api-db` (Postgres 16, port `5432`, data persisted in the `todo-api-db-data` volume)
   - `todo-api` (the API, port `8080` by default) — only starts once Postgres reports `healthy`

3. Verify it's up:
   ```bash
   curl http://localhost:8080/swagger-ui.html
   ```

## Environment variables

| Variable      | Default (dev)                                           | Note                                        |
|---------------|-----------------------------------------------------------|-----------------------------------------------|
| `JWT_SECRET`  | `dev-only-secret-key-change-me-please-32bytes-min`         | **Replace it in production.** Minimum 32 bytes. |
| `API_PORT`    | `8080`                                                     | Host port the API is exposed on.              |

Override example:
```bash
JWT_SECRET=a-strong-production-secret-with-32-bytes-or-more API_PORT=8080 docker compose up -d --build
```

## Stopping the containers

```bash
docker compose down       # keeps Postgres data (volume)
docker compose down -v    # also removes the volume (deletes the data)
```
