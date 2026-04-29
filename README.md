# NovaBank Digital Services

Interactive console banking application developed as part of the NTT Backend Java Training Programme - Module 2: Layered Architecture and PostgreSQL Persistence.

## Description

NovaBank Digital Services is a console-based bank management system that allows bank employees to perform basic operations:

- Register and search clients.
- Create and query bank accounts.
- Execute financial operations: deposits, withdrawals and transfers.
- Query balances and transaction history.

Data is now persisted in a PostgreSQL database. The system uses a layered architecture that separates presentation, business logic, domain and persistence concerns.

## Architecture

The system is organised in four layers. Dependencies flow strictly downward — no layer accesses one above it.

```
┌──────────────────────────────────────────┐
│           PRESENTATION LAYER             │
│  Console menus · Input reading           │
│  Result and error display                │
└─────────────────────┬────────────────────┘
                      │ calls
┌─────────────────────▼────────────────────┐
│             SERVICE LAYER                │
│  Business logic · Validations            │
│  Coordination of complex operations      │
└─────────────────────┬────────────────────┘
                      │ uses
┌─────────────────────▼────────────────────┐
│              DOMAIN LAYER                │
│  Entities: Client, Account, Transaction  │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│           PERSISTENCE LAYER              │
│  Repositories · SQL queries · JDBC       │
│  ResultSet mapping to Java objects       │
└──────────────────────────────────────────┘
                      │
                 PostgreSQL
```

| Package          | Responsibility                                                           |
|------------------|--------------------------------------------------------------------------|
| `model`          | Domain entities with attributes and domain behaviour (credit/debit)      |
| `model` (patterns) | `ClientBuilder`, `TransactionFactory` - object construction helpers      |
| `repository`     | Repository interfaces (contract used by services)                        |
| `repository/jdbc`| JDBC implementations: SQL queries and ResultSet mapping                  |
| `repository/inmemory` | In-memory implementations kept for reference from Module 1               |
| `service`        | Business logic: validations, ID generation, orchestration                |
| `controller`     | Coordinates between view and service layers                              |
| `view`           | Console presentation: displays menus and reads input                     |
| `config`         | `DatabaseConnectionManager` (Singleton) · `ConnectionProvider` interface |

## Technologies

| Technology  | Version |
|-------------|---------|
| Java        | 17      |
| Maven       | 3.x     |
| PostgreSQL  | 15+     |
| JUnit 5     | 5.10.0  |
| Mockito     | 5.4.0   |
| Git         | 2.52.0  |

## Requirements

- **Java 17** or higher
- **Maven 3.6** or higher
- **PostgreSQL 15** or higher, installed and running

Verify your environment:

```bash
java -version
mvn -version
psql --version
```

## Database setup

**1. Create the database:**

```sql
CREATE DATABASE novabank;
```

**2. Run the schema:**

```bash
psql -U postgres -d novabank -f src/main/resources/schema.sql
```

This creates three tables: `clients`, `accounts` and `transactions`.

**3. Configure connection credentials:**

Create the file `src/main/resources/database.properties` with your PostgreSQL credentials:

```properties
db.url=jdbc:postgresql://localhost:5432/novabank
db.user=your_user
db.password=your_password
```

This file is listed in `.gitignore` and must never be committed to the repository.

## How to compile

```bash
mvn clean compile
```

## How to run

```bash
mvn exec:java
```

## How to run tests

```bash
mvn test
```

> **Note:** Tests in `repository/` (`ClientRepositoryJdbcTest`, `AccountRepositoryJdbcTest`, `TransactionRepositoryJdbcTest`) require an active PostgreSQL connection with the `novabank` database configured. Tests in `service/` run with Mockito and do not need a database.

## Design patterns applied

| Pattern     | Where applied                    | Problem it solves                                                                                 |
|-------------|----------------------------------|---------------------------------------------------------------------------------------------------|
| **Singleton** | `DatabaseConnectionManager`    | Centralises connection configuration in a single instance; avoids scattered credentials.          |
| **Factory** | `TransactionFactory`             | Centralises construction of the four transaction types; services call a named method instead of `new Transaction(...)` with a raw enum. |
| **Builder** | `ClientBuilder`                  | Makes multi-field object construction readable and explicit, especially in tests.                  |

## Project structure

```
src/
├── main/
│   ├── java/com/novabank/
│   │   ├── Main.java
│   │   ├── config/
│   │   │   ├── DatabaseConnectionManager.java
│   │   │   └── ConnectionProvider.java
│   │   ├── model/
│   │   │   ├── Client.java
│   │   │   ├── Account.java
│   │   │   ├── Transaction.java
│   │   │   ├── TransactionType.java
│   │   │   ├── ClientBuilder.java
│   │   │   └── TransactionFactory.java
│   │   ├── repository/
│   │   │   ├── ClientRepository.java
│   │   │   ├── AccountRepository.java
│   │   │   ├── TransactionRepository.java
│   │   │   ├── jdbc/
│   │   │   │   ├── ClientRepositoryJdbc.java
│   │   │   │   ├── AccountRepositoryJdbc.java
│   │   │   │   └── TransactionRepositoryJdbc.java
│   │   │   └── inmemory/
│   │   │       ├── InMemoryClientRepository.java
│   │   │       ├── InMemoryAccountRepository.java
│   │   │       └── InMemoryTransactionRepository.java
│   │   ├── service/
│   │   │   ├── ClientService.java
│   │   │   ├── AccountService.java
│   │   │   └── TransactionService.java
│   │   ├── controller/
│   │   │   ├── ClientController.java
│   │   │   ├── AccountController.java
│   │   │   ├── TransactionController.java
│   │   │   └── InquiryController.java
│   │   └── view/
│   │       ├── ClientView.java
│   │       ├── AccountView.java
│   │       ├── TransactionView.java
│   │       └── InquiryView.java
│   └── resources/
│       └── schema.sql
|       └── database.properties.example
│
└── test/
    └── java/com/novabank/
        ├── service/
        │   ├── ClientServiceTest.java
        │   ├── AccountServiceTest.java
        │   └── TransactionServiceTest.java
        └── repository/
            ├── ClientRepositoryJdbcTest.java
            ├── AccountRepositoryJdbcTest.java
            └── TransactionRepositoryJdbcTest.java
```

## GitHub repository

[https://github.com/MVictoriaHuesca/NovaBank-Digital-Services](https://github.com/MVictoriaHuesca/NovaBank-Digital-Services)
