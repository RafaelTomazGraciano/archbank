# ArchBank - Backend

Comando para criar o postgresql no Docker:

```bash
docker run --name archbank-db -e POSTGRES_DB=archbank -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```