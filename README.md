# NovaBank Digital Services

Interactive console banking application developed as part of the NTT Backend Java Training Programme - Module 1: Java Fundamentals and Object-Oriented Programming.

## Description

NovaBank Digital Services is a console-based bank management system that allows bank employees to perform basic operations:

- Register and search clients.
- Create and query bank accounts.
- Execute financial operations: deposits, withdrawals and transfers.
- Query balances and transaction history.

Data is stored in memory using standard Java structures. All data is lost when the application closes, this is intentional for Module 1. Real persistence will be added in Module 2.

## Technologies

| Technology | Version |
|------------|--------|
| Java       | 17     |
| Maven      | 3.x    |
| JUnit 5    | 5.10.0 |
| Mockito    | 5.4.0  |
| Git        | 2.52.0 |

## Requirements

- **Java 17** or higher
- **Maven 3.6** or higher

Verify your environment:

```bash
java -version
mvn -version
```

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

## Project structure

```
src/
├── main/
│   ├── java/com/novabank/
│   │   ├── Main.java              # Application entry point, wires all menus
│   │   ├── model/                 # Domain entities
│   │   │   ├── Client.java
│   │   │   ├── Account.java
│   │   │   ├── Transaction.java
│   │   │   └── TransactionType.java
│   │   ├── repository/            # In-memory data stores (HashMap-based)
│   │   │   ├── ClientRepository.java
│   │   │   ├── AccountRepository.java
│   │   │   └── TransactionRepository.java
│   │   ├── service/               # Business logic and validations
│   │   │   ├── ClientService.java
│   │   │   ├── AccountService.java
│   │   │   └── TransactionService.java
│   │   └── console/               # Console menus (user interaction)
│   │       ├── ClientMenu.java
│   │       ├── AccountMenu.java
│   │       ├── TransactionMenu.java
│   │       └── InquiryMenu.java
│   └── resources/
│       └── schema.sql             # SQL schema prepared for Module 2
└── test/
    └── java/com/novabank/
        └── service/               # Unit tests for all services
            ├── ClientServiceTest.java
            ├── AccountServiceTest.java
            └── TransactionServiceTest.java
```

| Package      | Responsibility                                                      |
|--------------|---------------------------------------------------------------------|
| `model`      | Domain entities with attributes and domain behaviour (credit/debit) |
| `repository` | In-memory persistence using `HashMap`, simulates a database layer   |
| `service`    | Business logic: validations, ID generation, orchestration           |
| `console`    | User interaction: reads input, calls services, prints results       |

## GitHub repository

[https://github.com/MVictoriaHuesca/NovaBank-Digital-Services](https://github.com/MVictoriaHuesca/NovaBank-Digital-Services)
