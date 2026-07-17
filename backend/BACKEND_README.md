# ArchBank - Backend

Comando para criar o postgresql no Docker:

```bash
docker run --name archbank-db -e POSTGRES_DB=archbank -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

## Endpoints

### Header

```
Bearer Token: TOKEN
```

### POST `/api/auth/register` - Register a user his account

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