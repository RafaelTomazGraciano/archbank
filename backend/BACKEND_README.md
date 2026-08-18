# ArchBank - Backend

## Swagger

Interactive API documentation is automatically generated via [springdoc-openapi](https://springdoc.org/) and is available locally once the application is running:

- **Swagger UI (interactive):** http://localhost:8080/swagger-ui.html
- **OpenAPI specification (JSON):** http://localhost:8080/v3/api-docs

The Swagger UI lets you browse all endpoints, their parameters, request/response examples, and test calls directly from the browser.

For protected endpoints, click **Authorize** at the top of the page and provide the JWT token obtained from `/api/auth/login` in the format `Bearer <token>`.

## Endpoints

### Header

```
Bearer Token: TOKEN
```

### POST `/api/auth/signup` - Register a user his account

Header is not needed

**Body:**
```json
{
  "name": "Test",
  "cpf": "12345678910",
  "email": "test@email.com",
  "phone": "+5511999999999",
  "password": "Password@123",
  "transactionPin": "1234"
}
```

### POST `/api/auth/login` - Login

Header is not needed

**Body:**
```json
{
  "email": "rafael@email.com",
  "password": "Password@123"
}
```


## Environment Variables

This project reads its configuration from a `.env` file in the backend project root (loaded automatically via `spring-dotenv`). 
Copy the example and adjust as needed:

```bash
cp .env.example .env
```

| Variable      | Description                              |
| ------------- | ------------------------------------------ |
| `DB_URL`      | JDBC URL of the PostgreSQL database        |
| `DB_USERNAME` | PostgreSQL username                        |
| `DB_PASSWORD` | PostgreSQL password                        |
| `JWT_SECRET`  | Secret used to sign JWTs                   |

## Running PostgreSQL Locally

The command below spins up a local Postgres instance matching the **default** values in `.env.example`. If you change 
your DB credentials in `.env`, update this command (or your own Postgres setup) to match:

```bash
docker run --name archbank-db -e POSTGRES_DB=archbank -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

You're not required to use this exact command, any Postgres instance works, as long as its credentials match what's in your `.env`.