# Fintech Cloud Backend

A Spring Boot backend project that models core banking workflows including customer onboarding, account management, deposits, inter-account transfers, business-account fee collection, and bank revenue tracking.

This repository is a good portfolio project for demonstrating backend design with layered architecture, transactional money movement, Redis caching, and PostgreSQL persistence.

## Highlights

- Supports both `personal` and `business` account types.
- Applies percentage-based transfer fees when the sender is a business account.
- Routes collected fees into a dedicated bank revenue account.
- Protects API endpoints with JWT-based authentication and role-based authorization.
- Persists customers, accounts, bank revenue accounts, and transaction records with Spring Data JPA.
- Uses Redis caching for account detail lookups and evicts cache on balance-changing transactions.
- Uses `@Transactional` and optimistic locking (`@Version`) to improve consistency during balance updates.
- Returns consistent JSON error responses through global exception handling and request validation.
- Includes automated integration tests for security, validation, and business transfer flows.
- Includes Docker and Docker Compose setup for local infrastructure and app startup.
- Includes a GitHub Actions workflow for CI test execution.
- Exposes interactive API documentation with Swagger UI.

## Business Rules

The main business scenario implemented in this project is fee-aware bank transfer processing:

1. A customer can own multiple accounts.
2. Each account has an `accountType` of `personal` or `business`.
3. Deposits increase the target account balance.
4. Transfers move funds from one customer account to another.
5. If the sender is a business account, the system can calculate a merchant fee using `amount * percentage`.
6. When a bank revenue account is provided, that fee is credited to the bank revenue account.
7. The receiving account gets `amount - merchantFee` for business-originated transfers.
8. Every deposit or transfer creates a transaction record.

## Tech Stack

- Java 17
- Spring Boot 3.3.2
- Spring Web
- Spring Data JPA
- Spring Security
- Bean Validation
- Spring Cache
- PostgreSQL
- H2 (test profile)
- Redis
- SpringDoc OpenAPI / Swagger UI
- Gradle
- Lombok
- Docker / Docker Compose
- GitHub Actions

## Architecture

The codebase follows a typical layered Spring Boot structure:

```text
src/main/java/com/fintech/banktransaction
├── config        # OpenAPI and Redis cache configuration
├── controller    # REST endpoints
├── dto           # Response / transport objects
├── errors        # Domain-specific exceptions
├── model         # JPA entities
├── repository    # Data access layer
└── service       # Business logic and transactions
```

Core domain objects:

- `Customer`: customer profile.
- `Account`: customer-owned account with balance and type.
- `BankRevenueAccount`: destination account for merchant fee collection.
- `TransactionRecord`: immutable record of deposits and transfers.

## API Overview

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/api/auth/login` | Authenticate and obtain a JWT access token |
| `POST` | `/api/customer` | Create a customer |
| `POST` | `/api/customer/{customerId}/account` | Create an account for a customer |
| `GET` | `/api/customer/{customerId}/account/{accountId}` | Get account details and related transaction records |
| `POST` | `/api/bankrevenueaccount` | Create a bank revenue account |
| `POST` | `/api/customer/{fromCustomerId}/account/{accountId}/transaction_record/deposit` | Deposit funds into an account |
| `POST` | `/api/customer/{fromCustomerId}/account/{accountId}/transaction_record/transfer` | Transfer funds between accounts |

## Authentication and Authorization

The API uses JWT bearer tokens.

- `ADMIN` role can create customers, accounts, bank revenue accounts, deposits, and transfers.
- `USER` role can read account details.
- `/actuator/health`, Swagger UI, and OpenAPI docs are publicly accessible for local development and container health checks.

Default local credentials:

- Admin: `admin` / `change-me-admin`
- User: `auditor` / `change-me-user`

Default login flow:

1. `POST /api/auth/login` with a username and password
2. Extract `accessToken` from the response
3. Call protected endpoints with `Authorization: Bearer <token>`

Example login request:

```json
{
  "username": "admin",
  "password": "change-me-admin"
}
```

Override credentials and the signing secret with environment variables before public deployment:

```powershell
$env:APP_SECURITY_ADMIN_USERNAME="admin"
$env:APP_SECURITY_ADMIN_PASSWORD="strong-admin-password"
$env:APP_SECURITY_USER_USERNAME="auditor"
$env:APP_SECURITY_USER_PASSWORD="strong-user-password"
$env:APP_SECURITY_JWT_SECRET="replace-with-a-long-random-secret"
```

## Example Requests

Create a customer:

```json
{
  "firstName": "Jennie",
  "lastName": "Kim"
}
```

Create an account:

```json
{
  "accountName": "Jennie Business",
  "accountType": "business"
}
```

Create a bank revenue account:

```json
{
  "bankAccountName": "Platform Revenue"
}
```

Deposit into an account:

```json
{
  "amount": 80000
}
```

Transfer from a business account:

```json
{
  "toCustomerId": 2,
  "toAccountId": 10,
  "amount": 5000,
  "bankAccountId": 1,
  "percentage": 0.05
}
```

In the example above, if the sender is a business account, the merchant fee is `250`, the receiver gets `4750`, and the bank revenue account receives `250`.

## Running the Project

### Prerequisites

- Java 17
- PostgreSQL
- Redis
- Docker Desktop (optional, for containerized startup)
- IntelliJ IDEA or Gradle Wrapper

### 1. Create the database

Create a PostgreSQL database named `banktransaction`.

### 2. Configure application settings

The project currently includes local development defaults in `src/main/resources/application.properties`.

Recommended approach for local runs is to override them with environment variables instead of committing real credentials:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/banktransaction"
$env:SPRING_DATASOURCE_USERNAME="postgres"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
$env:SPRING_DATA_REDIS_HOST="localhost"
$env:SPRING_DATA_REDIS_PORT="6379"
$env:APP_SECURITY_JWT_SECRET="replace-with-a-long-random-secret"
```

You can also copy `src/main/resources/application-local.properties.example` to `src/main/resources/application-local.properties` for local-only overrides, then run with `SPRING_PROFILES_ACTIVE=local`. That file is ignored by Git and should not be committed.

### 3. Start PostgreSQL and Redis

Make sure both services are running before starting the application.

### 4. Start the application

You can run the app in either of these ways:

- Run `BankTransactionApplication` directly from your IDE.
- Or run the Gradle wrapper:

```powershell
.\gradlew.bat bootRun
```

If your local machine has permissions issues with the default user-level Gradle cache, run:

```powershell
$env:GRADLE_USER_HOME = Join-Path (Get-Location) ".gradle-user-home"
.\gradlew.bat bootRun
```

### 5. Run with Docker Compose

To start PostgreSQL, Redis, and the application together:

```powershell
docker compose up --build
```

The app will be available at `http://localhost:8080`.

### 6. Open API docs

Once the application is running, open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Testing

Run the automated test suite with:

```powershell
.\gradlew.bat test
```

The test profile uses:

- H2 in-memory database
- Simple in-memory cache instead of Redis
- Spring Boot integration tests with MockMvc and Spring Security test support

## Caching and Consistency Notes

- Account detail queries are cached in Redis with a 10-minute TTL.
- Transfer and deposit operations evict the affected account cache entries.
- Account and transaction entities use optimistic locking through `@Version` fields.
- Transaction processing is wrapped in a single database transaction.

## Delivery Tooling

- `Dockerfile` builds the application into a container image.
- `docker-compose.yml` starts the app with PostgreSQL and Redis.
- `.github/workflows/ci.yml` runs the test suite on pushes and pull requests.

## Current Scope

This project now includes authentication, automated tests, Docker-based local setup, CI, and global exception handling. Higher-value future improvements would be:

1. Replace the current in-memory demo users with database-backed user management and refresh tokens.
2. Add account ownership rules tied to authenticated identities instead of role-only protection.
3. Expand tests to cover concurrent balance updates, cache behavior, and failure rollback scenarios.
4. Add environment-specific profiles for staging and production deployments.
5. Add deployment automation after CI, such as image publishing or infrastructure rollout.

## Project Positioning

> Built a Spring Boot banking backend that supports customer and account management, fee-aware business transfers, PostgreSQL persistence, Redis-backed account caching, and Swagger-documented REST APIs.
