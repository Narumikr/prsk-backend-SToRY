# GitHub Copilot Instructions

This document provides comprehensive guidelines for AI-assisted code development and review in this Spring Boot project.

## Project Overview

This is a Spring Boot 3.5.6 REST API for music/artist management (Project Sekai music-list). The project uses Java 21, PostgreSQL, and follows a layered architecture pattern with Domain-Driven Design principles.

- **API Base Path**: `/btw-api/v1`
- **Port**: 8080
- **API Documentation**: https://narumikr.github.io/prsk-backend-SToRY/

## Architecture Principles

### Layered Structure
```
Controller → Service → Repository → Database
```

Each feature module contains:
- **Entity**: JPA entity with Lombok annotations, extends `BaseEntity`
- **Controller**: REST endpoints, handles HTTP requests/responses
- **Service**: Business logic with `@Transactional` annotations
- **Repository**: Spring Data JPA interface for data access
- **DTOs**: Request/Response data transfer objects

### Key Patterns
- **Soft deletes**: All entities use `isDeleted` flag for logical deletion
- **Audit fields**: Auto-managed via JPA auditing (`createdAt`, `updatedAt`, `createdBy`, `updatedBy`)
- **Pagination**: Spring Data `Page<T>` with `MetaInfo` in responses (default: page 1, limit 20, max 100)
- **Validation**: Jakarta Bean Validation annotations on request DTOs

## Configuration Files

- `application.properties` - Base configuration with `spring.profiles.active=dev`
- `application-dev.properties` - PostgreSQL connection for development
- `api/openapi.yaml` - OpenAPI specification
- `src/test/TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md` - Testing specification

## Code Review Guidelines

When reviewing code changes (both as an AI assistant and for human reviewers), **always** apply the following mandatory criteria:

### Review Language & Communication
- **レビューは日本語でコメントすること** (Write all review comments in Japanese)
- **レビュー指摘は具体的なコードを示すこと** (Provide specific code examples with every suggestion)

### Code Quality Checks
- **スペルミスや構文ミス**: Check for spelling errors, typos, and syntax mistakes that humans commonly overlook
- **命名規則**: Verify naming conventions follow Java/Spring Boot standards:
  - Classes: PascalCase (e.g., `ArtistService`)
  - Methods/variables: camelCase (e.g., `createArtist`)
  - Constants: UPPER_SNAKE_CASE (e.g., `MAX_PAGE_SIZE`)
  - Packages: lowercase (e.g., `com.example.untitled.artist`)

### Architecture & Design Principles
- **ドメイン駆動設計 (DDD)**: Verify the code follows Domain-Driven Design principles:
  - Business logic belongs in Service layer, not Controller or Repository
  - Entities represent domain concepts clearly
  - Repositories only handle data access, no business rules
  
- **DTOとEntityの役割分担**: Ensure proper separation of concerns:
  - **Entity**: JPA entities for database mapping, extend `BaseEntity`
  - **Request DTO**: Input validation, used in Controller `@RequestBody`
  - **Response DTO**: Output formatting, returned from Controller
  - **Never expose entities directly in REST endpoints**
  - Use static factory methods like `Response.from(Entity)` for conversion

### REST API Standards
- **REST原則への準拠**: Verify API design follows REST principles:
  - Proper HTTP methods: GET (read), POST (create), PUT (update), DELETE (delete)
  - Correct status codes: 200 OK, 201 Created, 204 No Content, 400 Bad Request, 404 Not Found, 409 Conflict
  - Resource-based URLs: `/artists`, `/artists/{id}` (not `/getArtist`, `/createArtist`)
  - Consistent pagination: `page`, `limit` parameters with `MetaInfo` in responses
  - Idempotency for PUT and DELETE operations

### Exception Handling
- **例外処理の統一**: Ensure consistent exception handling:
  - Use existing custom exceptions: `EntityNotFoundException`, `DuplicationResourceException`, `UnauthorizedException`
  - Never return generic `Exception` or `RuntimeException` from Service layer
  - Let `GlobalExceptionHandler` handle HTTP response mapping
  - Include meaningful error messages and field-level details

### Performance Optimization
- **パフォーマンス最適化**: Review for performance improvements:
  - **N+1 queries**: Use `@EntityGraph` or JOIN FETCH for related entities
  - **Pagination**: Always use `Page<T>` for list endpoints, never return entire collections
  - **Indexing**: Ensure frequently queried fields have database indexes (especially unique fields)
  - **Lazy loading**: Verify `spring.jpa.open-in-view=false` is respected
  - **Transaction boundaries**: Check `@Transactional` is used appropriately (read-only when possible)
  - **Caching opportunities**: Consider if frequently accessed, rarely changed data should be cached

### Test Coverage Review
- **テストカバレッジ**: Verify tests follow `TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md`:
  - Both Controller and Service tests exist for new features
  - All success and error paths covered
  - Edge cases tested (null, empty, boundary values)
  - Mock usage is appropriate (don't mock value objects)

### Review Process
1. Read the code change thoroughly
2. Apply all criteria above systematically
3. Write comments in Japanese with specific code examples
4. Suggest concrete improvements, not just point out problems
5. Acknowledge good practices when present

## Important Notes

### Testing Requirements
- All new features must include both Controller and Service tests
- Follow the test naming convention: `{method}{Success|Error}[_with{Condition}][_{Detail}]`
- Maintain 100% code coverage for all business logic
- See `src/test/TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md` for detailed specifications

### Development Workflow
1. Create feature branch from `main`
2. Implement feature following architecture principles
3. Write comprehensive tests (Controller + Service)
4. Run tests locally: `./gradlew test`
5. Request AI code review (apply guidelines above)
6. Address review feedback
7. Create Pull Request using the template

### Build Commands
```bash
# Run all tests
./gradlew test

# Run specific test class
./gradlew test --tests "com.example.untitled.artist.ArtistControllerTest"

# Build project
./gradlew build

# Run development server
./gradlew bootRun
```

### Common Pitfalls to Avoid
- ❌ Exposing Entity objects directly in REST endpoints
- ❌ Putting business logic in Controllers or Repositories
- ❌ Using generic exceptions instead of custom exceptions
- ❌ Forgetting to implement soft delete logic
- ❌ Not using pagination for list endpoints
- ❌ Skipping test coverage for edge cases
- ❌ Ignoring N+1 query problems

### Resources
- [Project API Documentation](https://narumikr.github.io/prsk-backend-SToRY/)
- [Testing Specification](../src/test/TEST_DESIGN_SPECIFICATION_FOR_UNITTEST.md)
- [Claude AI Guidelines](../CLAUDE.md)
