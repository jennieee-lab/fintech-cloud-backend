# Fintech Cloud Backend

A Spring Boot backend project that models core banking workflows including customer onboarding, account management, deposits, inter-account transfers, business-account fee collection, and bank revenue tracking.

This repository is a good portfolio project for demonstrating backend design with layered architecture, transactional money movement, Redis caching, and PostgreSQL persistence.

## Highlights

- Supports both `personal` and `business` account types.
- Applies percentage-based transfer fees when the sender is a business account.
- Routes collected fees into a dedicated bank revenue account.
- Persists customers, accounts, bank revenue accounts, and transaction records with Spring Data JPA.
- Uses Redis caching for account detail lookups and evicts cache on balance-changing transactions.
- Uses `@Transactional` and optimistic locking (`@Version`) to improve consistency during balance updates.
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
- Spring Cache
- PostgreSQL
- Redis
- SpringDoc OpenAPI / Swagger UI
- Gradle
- Lombok

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
| `POST` | `/api/customer` | Create a customer |
| `POST` | `/api/customer/{customerId}/account` | Create an account for a customer |
| `GET` | `/api/customer/{customerId}/account/{accountId}` | Get account details and related transaction records |
| `POST` | `/api/bankrevenueaccount` | Create a bank revenue account |
| `POST` | `/api/customer/{fromCustomerId}/account/{accountId}/transaction_record/deposit` | Deposit funds into an account |
| `POST` | `/api/customer/{fromCustomerId}/account/{accountId}/transaction_record/transfer` | Transfer funds between accounts |

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
- IntelliJ IDEA or a local Gradle installation

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
```

You can also copy `src/main/resources/application-local.properties.example` to `src/main/resources/application-local.properties` for local-only overrides, then run with `SPRING_PROFILES_ACTIVE=local`. That file is ignored by Git and should not be committed.

### 3. Start PostgreSQL and Redis

Make sure both services are running before starting the application.

### 4. Start the application

You can run the app in either of these ways:

- Run `BankTransactionApplication` directly from your IDE.
- Or use Gradle after restoring the missing wrapper JAR or installing a compatible local Gradle version.

Repository note:

- `gradle/wrapper/gradle-wrapper.jar` is currently missing, so `./gradlew` or `gradlew.bat` will fail until the wrapper is restored.

### 5. Open API docs

Once the application is running, open:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Caching and Consistency Notes

- Account detail queries are cached in Redis with a 10-minute TTL.
- Transfer and deposit operations evict the affected account cache entries.
- Account and transaction entities use optimistic locking through `@Version` fields.
- Transaction processing is wrapped in a single database transaction.

## Current Scope

This project focuses on core transfer workflow modeling and backend layering. It does not yet include:

- Authentication or authorization
- Automated tests
- Docker-based local environment setup
- CI/CD pipeline configuration
- Global exception handling beyond the custom balance and amount checks

## Suggested Next Improvements

If you plan to keep this repository public and mention it on your resume, the highest-value next steps would be:

1. Restore the Gradle wrapper so the project can be started with one command.
2. Remove or externalize local database credentials completely.
3. Add a small automated test suite for account creation and transfer scenarios.
4. Add input validation for request bodies.
5. Add authentication and role-based access control if you want to position it as a more production-oriented backend.

## Project Positioning

A concise way to describe this project on a resume or GitHub profile:

> Built a Spring Boot banking backend that supports customer and account management, fee-aware business transfers, PostgreSQL persistence, Redis-backed account caching, and Swagger-documented REST APIs.
