# Marketplace-SE347


## Backend

The Spring Boot backend is fully contained in [`backend/`](./backend). It uses Java 17 and Maven.

```text
backend/src/main/java/com/marketplace/
├── common/       # Cross-cutting concerns such as exception handling
├── config/       # Spring configuration
└── auth/         # Feature modules
    ├── controller/  # HTTP endpoints
    ├── service/     # Use cases and business rules
    ├── repository/  # Data access
    └── model/       # Domain objects and request/response models
```

Each feature follows the four-layer structure:

1. **Controller** receives and validates HTTP requests.
2. **Service** contains business logic.
3. **Repository** owns persistence/data access.
4. **Model** contains the feature's domain and API data types.

### Run

From `backend/`:

```bash
mvn spring-boot:run
```

The sample login endpoint is `POST /api/auth/login`:

```json
{
  "email": "demo@marketplace.local",
  "password": "12345678"
}
```

### MySQL setup

Import the database schema before starting the backend:

```bash
Get-Content -Path "backend\src\database\schema_web_mua_ban_do_cu.sql" | mysql --user="<MYSQL_USERNAME>" --password
```

Replace `<MYSQL_USERNAME>` with your MySQL username. The `-p` option prompts for `<MYSQL_PASSWORD>` securely.

To insert or reset the complete database demo data:

```bash
Get-Content -Path "backend\src\database\seed_database.sql" | mysql --user="<MYSQL_USERNAME>" --password
```

The `users` table must contain a BCrypt-encoded `password_hash`(password is 12345678); the backend no longer uses a demo in-memory account. Spring JPA uses `validate` mode, so it does not create or alter the MySQL schema.

### Authentication endpoints

- `POST /api/auth/register` - creates a USER account with a BCrypt password hash.
- `POST /api/auth/login` - verifies credentials and returns access and refresh JWTs.
- `POST /api/auth/refresh` - exchanges a valid refresh JWT for a new token pair.
- `POST /api/auth/logout` - requires the access token in `Authorization: Bearer <accessToken>` and `{ "refreshToken": "<refreshToken>" }` in the body; revokes both tokens.
- `GET /api/auth/me` - requires `Authorization: Bearer <accessToken>` and returns the current user.

### Store MySQL settings locally

Copy the local configuration template:

```bash
cd backend
copy application-local.yml.example application-local.yml
```

Edit `application-local.yml` and replace the placeholders:

```yaml
spring:
  datasource:
    url: jdbc:mysql://<MYSQL_HOST>:<MYSQL_PORT>/<MYSQL_DATABASE>?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: <MYSQL_USERNAME>
    password: <MYSQL_PASSWORD>
```

This file is automatically imported by Spring Boot and is ignored by Git.

After that, start the backend normally:

```bash
cd backend
mvn spring-boot:run
```

Environment variables can still override `application-local.yml` and are recommended for servers or shared CI environments.
