# NovaBank Digital Services

Aplicación bancaria de consola desarrollada como parte del Plan Formativo Backend Java de NTT - Módulo 2: Arquitectura por Capas y Persistencia con PostgreSQL.

## Descripción

NovaBank Digital Services es un sistema de gestión bancaria por consola que permite a los empleados del banco realizar operaciones básicas:

- Registrar y buscar clientes.
- Crear y consultar cuentas bancarias.
- Ejecutar operaciones financieras: depósitos, retiradas y transferencias.
- Consultar saldos e historial de movimientos.

Los datos se persisten en una base de datos PostgreSQL. El sistema utiliza una arquitectura por capas que separa presentación, lógica de negocio, dominio y persistencia.

## Arquitectura

El sistema se organiza en cuatro capas. Las dependencias fluyen estrictamente hacia abajo — ninguna capa accede a una capa superior.

```
┌──────────────────────────────────────────┐
│           CAPA DE PRESENTACIÓN           │
│  Menús de consola · Lectura de entrada   │
│  Visualización de resultados y errores   │
└─────────────────────┬────────────────────┘
                      │ llama a
┌─────────────────────▼────────────────────┐
│           CAPA DE SERVICIOS              │
│  Lógica de negocio · Validaciones        │
│  Coordinación de operaciones complejas   │
└─────────────────────┬────────────────────┘
                      │ usa
┌─────────────────────▼────────────────────┐
│             CAPA DE DOMINIO              │
│  Entidades: Client, Account, Transaction │
└──────────────────────────────────────────┘

┌──────────────────────────────────────────┐
│           CAPA DE PERSISTENCIA           │
│  Repositorios · Consultas SQL · JDBC     │
│  Mapeo de ResultSet a objetos Java       │
└──────────────────────────────────────────┘
                      │
                 PostgreSQL
```

| Paquete               | Responsabilidad                                                            |
|-----------------------|----------------------------------------------------------------------------|
| `model`               | Entidades del dominio con atributos y comportamiento (crédito/débito)      |
| `model` (patrones)    | `ClientBuilder`, `TransactionFactory` - helpers de construcción de objetos |
| `repository`          | Interfaces de repositorio (contrato utilizado por los servicios)           |
| `repository/jdbc`     | Implementaciones JDBC: consultas SQL y mapeo de ResultSet                  |
| `repository/inmemory` | Implementaciones en memoria conservadas como referencia del Módulo 1       |
| `service`             | Lógica de negocio: validaciones, generación de IDs, orquestación           |
| `controller`          | Coordina entre la capa de vista y la de servicios                          |
| `view`                | Presentación por consola: muestra menús y lee la entrada del usuario       |
| `config`              | `DatabaseConnectionManager` (Singleton) · interfaz `ConnectionProvider`    |

## Tecnologías

| Tecnología  | Versión |
|-------------|---------|
| Java        | 17      |
| Maven       | 3.x     |
| PostgreSQL  | 15+     |
| JUnit 5     | 5.10.0  |
| Mockito     | 5.4.0   |
| Git         | 2.52.0  |

## Requisitos

- **Java 17** o superior
- **Maven 3.6** o superior
- **PostgreSQL 15** o superior, instalado y en ejecución

Verificar el entorno:

```bash
java -version
mvn -version
psql --version
```

## Configuración de la base de datos

**1. Crear la base de datos:**

```sql
CREATE DATABASE novabank;
```

**2. Ejecutar el esquema:**

```bash
psql -U postgres -d novabank -f src/main/resources/schema.sql
```

Esto crea tres tablas: `clients`, `accounts` y `transactions`.

**3. Configurar las credenciales de conexión:**

Crear el archivo `src/main/resources/database.properties` con las credenciales de PostgreSQL:

```properties
db.url=jdbc:postgresql://localhost:5432/novabank
db.user=tu_usuario
db.password=tu_contraseña
```

Este archivo está incluido en `.gitignore` y nunca debe commitearse al repositorio.

## Cómo compilar

```bash
mvn clean compile
```

## Cómo ejecutar

```bash
mvn exec:java
```

## Cómo ejecutar los tests

```bash
mvn test
```

> **Nota:** Los tests de `repository/` (`ClientRepositoryJdbcTest`, `AccountRepositoryJdbcTest`, `TransactionRepositoryJdbcTest`) requieren una conexión activa a PostgreSQL con la base de datos `novabank` configurada. Los tests de `service/` usan Mockito y no necesitan base de datos.

## Patrones de diseño aplicados

| Patrón        | Dónde se aplica               | Problema que resuelve                                                                                              |
|---------------|-------------------------------|--------------------------------------------------------------------------------------------------------------------|
| **Singleton** | `DatabaseConnectionManager`   | Centraliza la configuración de conexión en una única instancia; evita credenciales dispersas por el código.        |
| **Factory**   | `TransactionFactory`          | Centraliza la construcción de los cuatro tipos de transacción; los servicios llaman a un método con nombre propio en lugar de usar `new Transaction(...)` con un enum crudo. |
| **Builder**   | `ClientBuilder`               | Hace que la construcción de objetos con múltiples campos sea legible y explícita, especialmente en los tests.      |

## Estructura del proyecto

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
│       └── database.properties.example
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

## Repositorio GitHub

[https://github.com/MVictoriaHuesca/NovaBank-Digital-Services](https://github.com/MVictoriaHuesca/NovaBank-Digital-Services)
