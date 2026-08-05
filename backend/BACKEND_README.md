# ArchBank - Backend

Command to run PostgreSQL on Docker:

```bash
docker run --name archbank-db -e POSTGRES_DB=archbank -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

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