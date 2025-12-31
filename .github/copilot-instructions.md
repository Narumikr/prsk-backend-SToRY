# GitHub Copilot Instructions

This file provides comprehensive guidance for GitHub Copilot coding agents working on this repository. It covers the codebase structure, build processes, testing standards, and architectural patterns.

## 1. Repository Overview

**Project Description**: Spring Boot 3.5.6 REST API for music/artist management (Project Sekai music-list backend)

**Language Composition**:
- Java: 98.1%
- Shell scripts: 1.9%

**Technology Stack**:
- **Language**: Java 21
- **Framework**: Spring Boot 3.5.6
- **Database**: PostgreSQL (dev/E2E), H2 (unit tests)
- **Build Tool**: Gradle (always use `./gradlew`, never system Gradle)
- **API Base Path**: `/btw-api/v1`
- **Port**: 8080

**API Documentation**: https://narumikr.github.io/prsk-backend-SToRY/

## 2. Build & Test Commands

### Core Commands

**Always use `./gradlew`** (never system Gradle)

```bash
# Build project (skip tests)
./gradlew build -x test

# Run development server (requires PostgreSQL on localhost:5432)
./gradlew bootRun

# Clean build artifacts
./gradlew clean
```

### Test Commands

```bash
# Run unit tests only (uses H2 in-memory database)
./gradlew testUnit

# Run E2E tests only (uses Docker PostgreSQL on port 5433)
./gradlew testE2e

# Run all tests (both unit and E2E)
./gradlew test
```

### Test Execution Details

**Unit Tests** (`./gradlew testUnit`):
- Excludes tests tagged with `@Tag("e2e")`
- Uses H2 in-memory database
- Report location: `build/reports/tests/testUnit/index.html`
- Automatically opens report in browser (except in CI)

**E2E Tests** (`./gradlew testE2e`):
- Auto-starts PostgreSQL container via `dockerComposeUp` task
- Waits up to 30 seconds for database readiness
- Uses PostgreSQL on localhost:5433 (container port mapping: 5433:5432)
- Report location: `build/reports/tests/testE2e/index.html`
- Automatically opens report in browser (except in CI)

**Docker Management**:
```bash
# Stop E2E Docker containers manually if needed
./gradlew dockerComposeDown

# Or use docker compose directly
docker compose -f docker-compose.e2e.yml down
```

### Common Issues & Solutions

| Issue | Solution |
|-------|----------|
| E2E tests fail | Check Docker is running and port 5433 is free |
| Permission denied | Run `chmod +x gradlew` |
| Dev server won't start | Verify PostgreSQL is running on localhost:5432 with credentials postgres/postgres |
| Port already in use | Stop existing services on port 8080 (dev) or 5433 (E2E) |

## 3. Project Architecture

### Directory Structure

```
Narumikr/prsk-backend-SToRY/
├── .github/
│   ├── workflows/              # CI/CD workflows
│   │   ├── unit-test.yml       # Unit test automation
│   │   ├── e2e-test.yml        # E2E test automation
│   │   └── api-docs-deploy.yml # API docs deployment
│   └── PULL_REQUEST_TEMPLATE.md
├── api/                        # OpenAPI specifications
├── src/main/java/com/example/untitled/
│   ├── UntitledApplication.java  # Main application entry point
│   ├── artist/                   # Artist feature module
│   ├── user/                     # User feature module
│   ├── common/                   # Shared components
│   │   ├── entity/
│   │   │   └── BaseEntity.java  # Abstract base with audit fields
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java  # Centralized exception handling
│   │   ├── dto/                 # Shared DTOs (ErrorResponse, MetaInfo)
│   │   └── util/
│   │       └── EntityHelper.java  # Helper for partial updates
│   └── config/
│       └── JpaConfig.java       # JPA auditing configuration
├── src/main/resources/
│   ├── application.properties        # Base configuration
│   └── application-dev.properties    # Development profile config
├── src/test/
│   ├── java/com/example/untitled/
│   ├── resources/
│   │   ├── application.properties      # Test base config
│   │   └── application-e2e.properties  # E2E test config
│   └── TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md  # Authoritative test spec
├── build.gradle                # Build configuration
├── docker-compose.e2e.yml      # E2E PostgreSQL container
└── gradlew / gradlew.bat       # Gradle wrapper scripts
```

### Layered Architecture

**Flow**: `Controller → Service → Repository → Database`

Each feature module (e.g., `artist/`, `user/`) contains:
- **Entity.java**: JPA entity with Lombok annotations
- **Controller.java**: REST endpoints, validation handling
- **Service.java**: Business logic with `@Transactional` annotations
- **Repository.java**: Spring Data JPA interface
- **dto/**: Request and Response DTOs

### Key Components

**BaseEntity** (`common/entity/BaseEntity.java`):
- Provides audit fields: `createdAt`, `updatedAt`, `createdBy`, `updatedBy`
- Soft delete support: `isDeleted` boolean flag
- All entities inherit from BaseEntity

**GlobalExceptionHandler** (`common/exception/GlobalExceptionHandler.java`):
- Maps exceptions to HTTP responses
- Handles validation errors, not found errors, conflicts
- Returns consistent error response format

**EntityHelper** (`common/util/EntityHelper.java`):
- `updateIfNotNull()` method for partial updates
- Prevents overwriting existing values with null

### Architectural Patterns

1. **Soft Deletes**: All deletions set `isDeleted=true` (never hard delete)
2. **Audit Fields**: Auto-managed via JPA auditing (`@EnableJpaAuditing`)
3. **Pagination**: Spring Data `Page<T>` with metadata (default: page 1, limit 20, max 100)
4. **Validation**: Jakarta Bean Validation annotations on DTOs (`@NotNull`, `@Size`, etc.)
5. **Lombok**: All entities/DTOs use Lombok (`@Data`, `@Builder`, etc.)
6. **Transactional Service Methods**: Methods modifying data use `@Transactional`

## 4. Testing Standards (CRITICAL)

**Reference Document**: `src/test/TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md` is the authoritative source for testing standards. Always consult it when writing tests.

### Test Naming Convention

```
{methodName}{Success|Error}[_with{Condition}][_{Detail}]
```

Examples:
- `createArtistSuccess`
- `createArtistError_withBadRequest_RequiredFieldNull`
- `updateArtistSuccess_PartialUpdate`

### Controller Tests

**Annotations**: `@WebMvcTest({Resource}Controller.class)`

**Key Practices**:
- Use `MockMvc` for HTTP testing
- Mock services with `@MockitoBean`
- Test all HTTP status codes (200, 201, 400, 404, 409, etc.)
- Verify request validation
- Check JSON response structure with `jsonPath`

**Required Test Cases** (per endpoint):
- **POST**: success, required null, length over, unique constraint
- **GET (list)**: default params, pagination, invalid page/limit
- **PUT**: all fields, partial update, not found, length over, unique constraint, invalid ID
- **DELETE**: success, not found

### Service Tests

**Annotations**: `@ExtendWith(MockitoExtension.class)`

**Key Practices**:
- Mock repositories with `@Mock`
- Inject service with `@InjectMocks`
- Test business logic and exception scenarios
- Verify repository method calls with `verify()`

**Required Test Cases** (per method):
- **create**: success, duplication error
- **getAll**: ASC sort, DESC sort, empty list
- **update**: all fields, partial, same value (unique field), all null, not found, duplication
- **delete**: success, not found

### Coverage Goals

- **Line Coverage**: 100%
- **Branch Coverage**: 100%
- **Method Coverage**: 100%

### Reference Implementations

- `ArtistControllerTest.java`: 16 test cases
- `ArtistServiceTest.java`: 13 test cases

Use these as templates for new feature tests.

## 5. CI/CD & Validation

### GitHub Actions Workflows

**unit-test.yml**:
- Triggers on PRs to `main` branch
- Uses JDK 21 (temurin distribution)
- Runs `./gradlew testUnit`
- Publishes test report using `dorny/test-reporter@v2`

**e2e-test.yml**:
- Triggers on PRs to `main` branch
- Uses JDK 21 (temurin distribution)
- Runs `./gradlew testE2e`
- Always cleans up Docker containers: `docker compose -f docker-compose.e2e.yml down`

**api-docs-deploy.yml**:
- Deploys API documentation to GitHub Pages

### PR Checklist (from template)

Before submitting a PR, verify:
- [ ] Branch name describes the work (e.g., `20250831_add_readme`)
- [ ] New APIs have unit tests added
- [ ] New APIs have E2E tests added
- [ ] Unit tests pass locally (`./gradlew testUnit`)
- [ ] E2E tests pass locally (`./gradlew testE2e`)
- [ ] API documentation updated if endpoints changed
- [ ] Dev environment deployment tested
- [ ] AI review completed and issues addressed

## 6. Configuration Files

### build.gradle

- Java 21 with `JavaLanguageVersion.of(21)`
- Spring Boot 3.5.6
- Custom test tasks: `testUnit` (excludes `@Tag("e2e")`), `testE2e` (includes only `@Tag("e2e")`)
- Docker tasks: `dockerComposeUp`, `dockerComposeDown`
- Auto-opens test reports in browser (disabled in CI)

### application.properties (Base)

```properties
spring.application.name=untitled
server.servlet.context-path=/btw-api/v1
server.port=8080
spring.profiles.active=dev
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.open-in-view=false
```

### application-dev.properties (Development)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### application-e2e.properties (E2E Tests)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/e2e_testdb
spring.datasource.username=e2e_user
spring.datasource.password=e2e_password
spring.jpa.hibernate.ddl-auto=create-drop
```

### docker-compose.e2e.yml

- **Image**: postgres:16-alpine
- **Container Name**: prsk-e2e-db
- **Port Mapping**: 5433:5432 (avoids conflict with dev database)
- **Credentials**: e2e_user/e2e_password
- **Database**: e2e_testdb
- **Health Check**: Enabled with 5s interval

## 7. Important Notes

### Trust These Instructions

These instructions are comprehensive and verified. Only search the codebase if:
- You need specific implementation details not covered here
- You're looking for reference examples
- The instructions appear incomplete for your specific task

### Development Guidelines

1. **Soft Deletes**: ALWAYS use soft deletes (`isDeleted=true`). Never hard delete records.
2. **Validation**: Use Jakarta Bean Validation annotations on all DTOs (`@NotNull`, `@Size`, `@Min`, `@Max`)
3. **Lombok**: Use Lombok annotations to reduce boilerplate (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`)
4. **Transactions**: Service methods that modify data MUST have `@Transactional` annotation
5. **Partial Updates**: Use `EntityHelper.updateIfNotNull()` for optional field updates
6. **Pagination**: Default page=1, limit=20, max limit=100
7. **Naming**: Follow existing patterns (e.g., `{Resource}Controller`, `{Resource}Service`)

### API Endpoint Patterns

All resources follow consistent CRUD patterns:
- `GET /{resources}` - List with pagination (`page`, `limit`, `sort` query params)
- `POST /{resources}` - Create (returns 201 Created)
- `PUT /{resources}/{id}` - Update (returns 200 OK)
- `DELETE /{resources}/{id}` - Soft delete (returns 204 No Content)

### Testing Guidelines

1. Reference `TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md` for complete testing requirements
2. Use `ArtistControllerTest.java` and `ArtistServiceTest.java` as templates
3. Write tests BEFORE implementing features (TDD approach recommended)
4. Aim for 100% coverage (line, branch, method)
5. Test both success and failure paths
6. Include edge cases and boundary conditions

### Code Quality

- Follow existing code patterns and structure
- Write clear, descriptive test names
- Add JavaDoc comments for public methods (match existing style)
- Keep methods focused and single-purpose
- Use meaningful variable names

---

**Last Updated**: 2025-12-31
**Version**: 1.0.0
