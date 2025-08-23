# TrackIt API

A comprehensive expense tracking REST API built with Spring Boot, designed as a learning project to demonstrate modern Java development practices including Domain-Driven Design (DDD) and SOLID principles.

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Latest-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Supported-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

## 🎯 About

TrackIt is a personal expense tracking API that I developed to deepen my understanding of enterprise-level Java development. This project serves as a practical implementation of software engineering best practices and design patterns, making it an ideal showcase for my technical skills and learning journey.

### Learning Objectives

- **Domain-Driven Design (DDD)**: Implementing clean separation between domain logic and infrastructure concerns
- **SOLID Principles**: Creating maintainable and extensible code through proper abstraction and dependency management
- **DRY (Don't Repeat Yourself)**: Eliminating code duplication through reusable components and utilities
- **Test-Driven Development**: Comprehensive testing strategy with unit and integration tests

## 🏗️ Architecture

The application follows a **layered architecture** inspired by **Domain-Driven Design** principles:

```
┌─────────────────────┐
│   Controller Layer  │ ← REST endpoints, request/response handling
├─────────────────────┤
│   Service Layer     │ ← Business logic, use cases
├─────────────────────┤
│   Repository Layer  │ ← Data access abstraction
├─────────────────────┤
│   Infrastructure    │ ← External concerns (security, config)
└─────────────────────┘
```

### Key Architectural Patterns

#### Domain-Driven Design (DDD)
- **Domain Models**: Rich domain objects (`User`, `Expense`) with business logic
- **Repository Pattern**: Abstract data access with clean interfaces
- **Specification Pattern**: Dynamic query building for complex filtering
- **Value Objects**: Immutable objects for data transfer (`DTOs`)

### Package Structure
```
com.spedine.trackit/
├── controller/         # REST endpoints
├── service/           # Business logic
├── repository/        # Data access layer
├── model/            # Domain entities
├── dto/              # Data transfer objects
├── infra/            # Infrastructure concerns
│   ├── exception/    # Global exception handling
│   ├── security/     # JWT, authentication
│   └── util/         # Common utilities
├── config/           # Configuration classes
└── specification/    # Query specifications
```

## 🛠️ Tech Stack

### Backend
- **Java 17** - Modern LTS Java version
- **Spring Boot 3.5.4** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework
- **PostgreSQL** - Primary database
- **H2** - In-memory database for testing

### Additional Technologies
- **JWT** - Token-based authentication
- **Flyway** - Database migration tool
- **SpringDoc OpenAPI** - API documentation
- **Bean Validation** - Input validation
- **JUnit 5** - Testing framework
- **Docker** - Containerization

### Build Tools
- **Gradle** - Build automation
- **Docker Compose** - Development environment orchestration

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Docker and Docker Compose

### Quick Start with Docker

1. **Clone the repository**
   ```bash
   git clone https://github.com/arthurspedine/trackit-api.git
   cd trackit-api
   ```

2. **Start the application with Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Access the API**
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

### Local Development

1. **Start PostgreSQL database**
   ```bash
   docker-compose up postgres -d
   ```

2. **Set environment variables**
   ```bash
   export DB_URL="jdbc:postgresql://localhost:5432/trackit_db"
   export DB_USERNAME="trackit_user"
   export DB_PASSWORD="secret"
   export JWT_SECRET="your-secret-key-here"
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

### Environment Variables
| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | Database connection URL | Required |
| `DB_USERNAME` | Database username | Required |
| `DB_PASSWORD` | Database password | Required |
| `JWT_SECRET` | JWT signing secret | `your_secret_key` |

## 📖 API Documentation

### Interactive Documentation
Access the full API documentation through Swagger UI:
- **Local**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

### Quick API Overview

#### Authentication Endpoints
```
POST /auth/register   # User registration
POST /auth/login      # User authentication
```

#### Expense Management
```
GET    /api/expenses           # List user expenses
POST   /api/expenses           # Create new expense
GET    /api/expenses/{id}      # Get specific expense
PUT    /api/expenses/{id}      # Update expense
DELETE /api/expenses/{id}      # Delete expense
GET    /api/expenses/summary   # Get expense analytics
```

## 🧪 Testing

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests with coverage report
./gradlew test jacocoTestReport

# Run only unit tests
./gradlew test --tests "*Test"
```

### Test Reports
After running tests, view detailed reports at:
- **Test Results**: `build/reports/tests/test/index.html`
- **Coverage Report**: `build/reports/jacoco/test/html/index.html`

### Key Test Classes
- `ExpenseTest` - Domain model validation tests
- `ExpenseServiceTest` - Business logic tests
- `AuthenticationServiceTest` - Authentication logic tests
- `TrackItApplicationTests` - Application context loading tests

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
