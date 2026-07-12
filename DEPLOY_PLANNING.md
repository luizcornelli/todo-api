# Deploy Planning

## Branch

`main` é a branch principal do projeto. Todo deploy deve partir dela.

## Pré-requisitos

- Docker `28.3.1` e Docker Compose `v2.36.2`
- JDK `21` e Maven (só para gerar o `.jar`; pode usar o wrapper `./mvnw`)

## Versões utilizadas

| Componente       | Versão                          |
|------------------|----------------------------------|
| Java             | `21` (imagem `eclipse-temurin:21-jdk`) |
| PostgreSQL       | `16` (imagem `postgres:16`)      |
| Docker           | `28.3.1`                         |
| Docker Compose   | `v2.36.2`                        |

## Passo a passo

1. Gerar o jar da aplicação (o `Dockerfile` copia um jar já compilado, não builda o projeto):
   ```bash
   ./mvnw clean package -DskipTests
   ```

2. Buildar as imagens e subir os containers (Postgres + API):
   ```bash
   docker compose up -d --build
   ```

   Isso sobe dois containers:
   - `todo-api-db` (Postgres 16, porta `5432`, dados persistidos no volume `todo-api-db-data`)
   - `todo-api` (a API, porta `8080` por padrão) — só inicia depois que o Postgres reportar `healthy`

3. Verificar que subiu:
   ```bash
   curl http://localhost:8080/swagger-ui.html
   ```

## Variáveis de ambiente

| Variável      | Padrão (dev)                                          | Observação                                  |
|---------------|--------------------------------------------------------|----------------------------------------------|
| `JWT_SECRET`  | `dev-only-secret-key-change-me-please-32bytes-min`      | **Trocar em produção.** Mínimo 32 bytes.     |
| `API_PORT`    | `8080`                                                  | Porta exposta no host para a API.            |

Exemplo de sobrescrita:
```bash
JWT_SECRET=um-segredo-forte-de-producao-com-32-bytes-ou-mais API_PORT=8080 docker compose up -d --build
```

## Parar os containers

```bash
docker compose down       # mantém os dados do Postgres (volume)
docker compose down -v    # remove também o volume (apaga os dados)
```
