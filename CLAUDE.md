# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring Boot 3.5.6 REST API for music/artist management (Project Sekai music-list). Uses Java 21, PostgreSQL, and follows a layered architecture pattern.

- **API Base Path**: `/btw-api/v1`
- **Port**: 8080
- **API Docs**: https://narumikr.github.io/prsk-backend-SToRY/

## Build Commands

```bash
# Run all tests (opens report in browser on Windows/Mac/Linux)
./gradlew test

# Run specific test class
./gradlew test --tests "com.example.untitled.artist.ArtistControllerTest"

# Run specific test method
./gradlew test --tests "com.example.untitled.artist.ArtistServiceTest.createArtistSuccess"

# Build project
./gradlew build

# Run development server
./gradlew bootRun
```

## Architecture

### Layered Structure
```
Controller → Service → Repository → Database
```

Each feature module (`artist/`, `user/`) contains:
- `Entity.java` - JPA entity with Lombok annotations
- `Controller.java` - REST endpoints
- `Service.java` - Business logic with `@Transactional`
- `Repository.java` - Spring Data JPA interface
- `dto/` - Request/Response DTOs

### Common Module (`common/`)
- `entity/BaseEntity.java` - Abstract base with audit fields (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`, `isDeleted`)
- `exception/GlobalExceptionHandler.java` - Centralized exception handling
- `dto/` - Shared DTOs (ErrorResponse, MetaInfo for pagination)

### Key Patterns
- **Soft deletes**: All entities use `isDeleted` flag (logical deletion)
- **Audit fields**: Auto-managed via JPA auditing (`@EnableJpaAuditing`)
- **Pagination**: Spring Data `Page<T>` with metadata in responses (default: page 1, limit 20, max 100)
- **Validation**: Jakarta Bean Validation annotations on request DTOs

## Testing Conventions

See `src/test/TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md` for detailed testing specification.

### Controller Tests (`@WebMvcTest`)
- Use `MockMvc` for HTTP testing
- Mock services with `@MockitoBean`
- Test all HTTP status codes and validation errors

### Service Tests (`@ExtendWith(MockitoExtension.class)`)
- Mock repositories with `@Mock` and `@InjectMocks`
- Test business logic and exception scenarios
- Verify repository method calls

### Test Naming Convention
```
{method}{Success|Error}[_with{Condition}][_{Detail}]
```
Examples: `createArtistSuccess`, `createArtistError_withConflict_AlreadyExist`

### Test Database
H2 in-memory database for tests (configured via `testRuntimeOnly`).

## API Endpoints Pattern

All resources follow the same CRUD pattern:
- `GET /{resources}` - List with pagination (`page`, `limit`, `sort` params)
- `POST /{resources}` - Create (returns 201)
- `PUT /{resources}/{id}` - Update
- `DELETE /{resources}/{id}` - Soft delete (returns 204)

## Configuration

- `application.properties` - Base config with `spring.profiles.active=dev`
- `application-dev.properties` - PostgreSQL connection for development
- OpenAPI spec in `api/openapi.yaml`
