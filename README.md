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
  "password": "password"
}
```

The current repository is only an in-memory starter implementation.
