# Repository Guidelines

## Project Structure & Module Organization
This service is a Spring Boot GraphQL backend. Source code lives in `src/main/java/pluto`, organised by domain modules under `pluto/upik`; shared infrastructure sits in `pluto/upik/shared` for configs, AOP, and utilities. API schemas and static assets are under `src/main/resources/graphql` and `src/main/resources/static`, while service wiring lives in `application.properties` and `logback-spring.xml`. Tests reside in `src/test/java/pluto`, mirroring the main package layout; add fixtures alongside test classes when needed.

## Build, Test, and Development Commands
- `./gradlew clean build` – compile, run unit/integration tests, and produce the bootable jar.
- `./gradlew bootRun` – start the local server using the active Spring profile.
- `./gradlew test` – run the JUnit test suite without rebuilding the jar.
- `docker-compose up -d` – start dependent services defined in `docker-compose.yml` (e.g., MariaDB, Elasticsearch).

## Coding Style & Naming Conventions
Target Java 21 and Spring Boot idioms. Use four-space indentation, UpperCamelCase for classes, lowerCamelCase for methods and variables, and uppercase snake case for constants. Controllers, services, and repositories should land in domain-specific packages under `pluto/upik/domain`. Favor Lombok annotations (`@Builder`, `@Slf4j`) already present, and keep configuration in `pluto/upik/shared/config`. When touching GraphQL, keep schema filenames kebab-case and align resolver method names with the query or mutation they serve.

## Testing Guidelines
Tests use JUnit 5 with Spring Boot test slices and Spring GraphQL utilities. Name test classes with the `*Test` suffix and mirror the package structure of the code under test. Run `./gradlew test` before pushing; add focused integration tests for security and cache layers touching `AsyncConfig` and `CacheConfig`. Aim to cover new service methods and GraphQL resolvers with happy-path and guard-rail cases.

## Commit & Pull Request Guidelines
Follow the existing convention `type(#issue) :: short summary` (e.g., `feat(#21) :: enable vote trigger`), referencing the relevant tracker ID. Keep commits scoped and runnable. Pull requests should include a concise description, testing notes (`./gradlew test` output), and screenshots or cURL samples for GraphQL changes. Link issues and flag any new environment variables or schema migrations.

## Environment & Security Tips
Sensitive credentials load via `.env.properties` and service JSON keys under `src/main/resources`; never commit overrides. Rotate Google/Azure keys when sharing access and verify policies in `logback-spring.xml` before enabling debug logs in production.
