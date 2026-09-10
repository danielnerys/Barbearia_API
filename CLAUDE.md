# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview

Spring Boot 3 (Java 21) REST API for managing a barbershop: users, barbers, services, and appointments, with JWT authentication and Swagger/OpenAPI docs. Package root: `com.danielnery.barbearia.api`.

## Commands

Build and run (use `mvnw`/`mvnw.cmd`, no local Maven install required):

```bash
./mvnw clean package        # build
./mvnw spring-boot:run      # run locally
./mvnw test                 # run all tests
./mvnw test -Dtest=ClassName#methodName   # run a single test
```

Docker (app + PostgreSQL):

```bash
docker-compose up --build
```

There is no separate lint command configured in `pom.xml`.

### Required local config

`src/main/resources/application.properties` is gitignored (contains DB credentials and JWT secret) and does not exist in a fresh checkout — it must be created before running locally. It needs at least a PostgreSQL datasource and `api.security.token.secret` (read by `JwtService`). When running via `docker-compose`, equivalent values are supplied through environment variables (`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`, `JWT_SECRET`) instead.

## Architecture

Standard layered Spring MVC structure under `src/main/java/com/danielnery/barbearia/api/`:

- `Controller` — REST endpoints, one per resource (`Usuario`, `Barbeiro`, `Servico`, `Agendamento`, `Auth`).
- `Service` — business logic and validation; controllers delegate directly to services (no separate use-case layer).
- `Repository` — Spring Data JPA interfaces.
- `Model` — JPA entities (`Usuario`, `Barbeiro`, `Servico`, `Agendamento`) and `Model/enums` (`Role`, `Especialidade`, `StatusAgendamento`).
- `DTO/Request` and `DTO/response` — request/response DTOs (note the inconsistent casing: `Request` is capitalized, `response` is lowercase — match the existing directory when adding new DTOs).
- `Exception` — one exception class per error case, all handled centrally in `GlobalExceptionHandler` via `@RestControllerAdvice`/`@ExceptionHandler`, returning `{"mensagem": "..."}` with an appropriate HTTP status. Add new domain errors the same way: a dedicated exception class + a handler method here.
- `Security` — `SecurityConfig` (filter chain, BCrypt, `AuthenticationManager` bean) and `JwtAuthenticationFilter` (reads the `Bearer` token, validates it via `JwtService`, and populates `SecurityContextHolder`).
- `Config` — `OpenApiConfig` for Swagger.

### Auth flow

`POST /auth/login` (`AuthController`) authenticates via Spring's `AuthenticationManager` against `CustomUsuarioDetailsService` (loads `Usuario` by email), then `JwtService` issues a JWT (auth0 `java-jwt`, HMAC256, issuer `barbearia-api`, includes a `roles` claim, 2h expiry). Every subsequent request is intercepted by `JwtAuthenticationFilter`, which validates the token and manually sets the `Authentication` in the `SecurityContext`. `SecurityConfig` permits `/auth/login`, Swagger paths, and `POST /api/usuarios` (registration) without auth; everything else requires a valid Bearer token. Passwords are BCrypt-hashed.

### Agendamento (appointment) rules

`AgendamentoService.cadastrar` enforces several invariants before saving — keep these in sync when touching appointment logic: barbeiro must exist and be `ativo`, servico must exist and be `ativo`, appointment time must fall on a 30-minute boundary (`:00` or `:30`), and the barbeiro must not already have an appointment at that exact `dataHoraVisita` (checked via `AgendamentoRepository.existsByBarbeiroAndDataHoraVisita`). `valorServicoNoMomento` snapshots the service's price at booking time rather than referencing the live `Servico` price.

### Active/inactive pattern

`Barbeiro` and `Servico` both use an `ativo` boolean flag rather than deletion, with dedicated activate/deactivate endpoints and list-active-only endpoints. Follow this pattern for similar entities instead of hard deletes.

## Testing

Only the default Spring Boot context-load test (`BarbeariaApiApplicationTests`) exists currently; there is no test suite for services/controllers yet.
