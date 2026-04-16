# NovaBank Digital Services

Aplicación bancaria interactiva de consola desarrollada como parte del Plan Formativo Backend Java de NTT - Módulo 1: Fundamentos de Java y Programación Orientada a Objetos.

## Descripción

NovaBank Digital Services es un sistema de gestión bancaria en consola que permite a los empleados del banco realizar operaciones básicas:

- Registro y búsqueda de clientes.
- Creación y consulta de cuentas bancarias.
- Ejecución de operaciones financieras: depósitos, retiradas y transferencias.
- Consulta de saldos e historial de movimientos.

Los datos se almacenan en memoria usando estructuras estándar de Java. Al cerrar la aplicación todos los datos se pierden, esto es intencionado en el Módulo 1. La persistencia real se incorporará en el Módulo 2.

## Tecnologías

| Tecnología | Versión |
|------------|---------|
| Java       | 17      |
| Maven      | 3.x     |
| JUnit 5    | 5.10.0  |
| Mockito    | 5.4.0   |
| Git        | 2.52.0  |

## Requisitos del sistema

- **Java 17** o superior
- **Maven 3.6** o superior

Verificar el entorno:

```bash
java -version
mvn -version
```

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

## Estructura del proyecto

```
src/
├── main/
│   ├── java/com/novabank/
│   │   ├── Main.java              # Punto de entrada, inicializa todos los menús
│   │   ├── model/                 # Entidades del dominio
│   │   │   ├── Client.java
│   │   │   ├── Account.java
│   │   │   ├── Transaction.java
│   │   │   └── TransactionType.java
│   │   ├── repository/            # Almacenamiento en memoria (basado en HashMap)
│   │   │   ├── ClientRepository.java
│   │   │   ├── AccountRepository.java
│   │   │   └── TransactionRepository.java
│   │   ├── service/               # Lógica de negocio y validaciones
│   │   │   ├── ClientService.java
│   │   │   ├── AccountService.java
│   │   │   └── TransactionService.java
│   │   └── console/               # Menús de consola (interacción con el usuario)
│   │       ├── ClientMenu.java
│   │       ├── AccountMenu.java
│   │       ├── TransactionMenu.java
│   │       └── InquiryMenu.java
│   └── resources/
│       └── schema.sql             # Script SQL preparado para el Módulo 2
└── test/
    └── java/com/novabank/
        └── service/               # Tests unitarios de los servicios
            ├── ClientServiceTest.java
            ├── AccountServiceTest.java
            └── TransactionServiceTest.java
```

| Paquete      | Responsabilidad                                                              |
|--------------|------------------------------------------------------------------------------|
| `model`      | Entidades del dominio con atributos y comportamiento (métodos credit/debit)  |
| `repository` | Persistencia en memoria con `HashMap`, simula la capa de base de datos       |
| `service`    | Lógica de negocio: validaciones, generación de IDs, orquestación             |
| `console`    | Interacción con el usuario: lectura de entradas, llamadas a servicios y salida por consola |

## Repositorio en GitHub

[https://github.com/MVictoriaHuesca/NovaBank-Digital-Services](https://github.com/MVictoriaHuesca/NovaBank-Digital-Services)
