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

`src/main/resources/application.properties` is gitignored (contains DB credentials and JWT secret) and does not exist in a fresh checkout — it must be created before running locally. It needs at least a PostgreSQL datasource and `api.security.token.secret` (read by `JwtService`). When running via `docker-compose`, equivalent values are supplied through environment variables (`PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER`, `PGPASSWORD`, `JWT_SECRET`), plus `ADMIN_NOME`/`ADMIN_EMAIL`/`ADMIN_SENHA` (seeded admin user, see below), `CORS_ALLOWED_ORIGINS` (see CORS below), and optionally `api.barbearia.horario-abertura`/`api.barbearia.horario-fechamento` (business hours, default `09:00`/`19:00`). `docker-compose.yml` reads secrets from a gitignored `.env` (see `.env.example`) and refuses to start without `POSTGRES_PASSWORD`/`JWT_SECRET` set.

Every property added since the initial setup has a sensible default via `@Value("${prop:default}")`, so the app boots without any of them being set explicitly — only the datasource and JWT secret are truly required to have real values (defaults exist but point at throwaway local credentials).

On startup, `AdminSeeder` creates a `Role.ADMIN` user (`ADMIN_EMAIL`/`ADMIN_SENHA`, defaulting to `admin@barbearia.com`/`admin123`) if one with that email doesn't already exist yet — this is the only way to obtain an admin account, since registration (`POST /api/usuarios`) always hardcodes `Role.CLIENTE`.

## Architecture

Standard layered Spring MVC structure under `src/main/java/com/danielnery/barbearia/api/`:

- `Controller` — REST endpoints, one per resource (`Usuario`, `Barbeiro`, `Servico`, `Agendamento`, `Auth`).
- `Service` — business logic and validation; controllers delegate directly to services (no separate use-case layer).
- `Repository` — Spring Data JPA interfaces.
- `Model` — JPA entities (`Usuario`, `Barbeiro`, `Servico`, `Agendamento`) and `Model/enums` (`Role`, `Especialidade`, `StatusAgendamento`).
- `DTO/Request` and `DTO/response` — request/response DTOs (note the inconsistent casing: `Request` is capitalized, `response` is lowercase — match the existing directory when adding new DTOs).
- `Exception` — one exception class per error case, all handled centrally in `GlobalExceptionHandler` via `@RestControllerAdvice`/`@ExceptionHandler`, returning `{"mensagem": "..."}` with an appropriate HTTP status. Add new domain errors the same way: a dedicated exception class + a handler method here.
- `Security` — `SecurityConfig` (filter chain, CORS, BCrypt, `AuthenticationManager` bean) and `JwtAuthenticationFilter` (reads the `Bearer` token, validates it via `JwtService`, and populates `SecurityContextHolder`).
- `Config` — `OpenApiConfig` for Swagger, `AdminSeeder` for the bootstrap admin user.

### Auth flow

`POST /auth/login` (`AuthController`) authenticates via Spring's `AuthenticationManager` against `CustomUsuarioDetailsService` (loads `Usuario` by email), then `JwtService` issues a JWT (auth0 `java-jwt`, HMAC256, issuer `barbearia-api`, includes a `roles` claim, 2h expiry). Every subsequent request is intercepted by `JwtAuthenticationFilter`, which validates the token and manually sets the `Authentication` in the `SecurityContext`. `GET /auth/me` returns the logged-in user's `id`/`nome`/`email`/`role` (resolved via `Authentication#getName()` against `UsuarioRepository`, never the password) — the frontend's only way to learn who is logged in.

`SecurityConfig` permits `/auth/login`, Swagger paths, and `POST /api/usuarios` (registration) without auth — note the matcher is scoped to `HttpMethod.POST`, since a bare path matcher would also expose `GET /api/usuarios` (list all users). Everything else requires a valid Bearer token; an unauthenticated request now gets a `401` with the standard `{"mensagem": "..."}` body via a custom `authenticationEntryPoint` (Spring Security's default, with no `httpBasic`/`formLogin` configured, is an empty-body `403`, not `401`). Passwords are BCrypt-hashed.

#### Role-based authorization

`@EnableMethodSecurity` is on, and admin-only endpoints carry `@PreAuthorize("hasRole('ADMIN')")` — `Usuario.getAuthorities()` returns `ROLE_ADMIN`/`ROLE_CLIENTE`, so `hasRole(...)` (not `hasAuthority`) is correct. Restricted to `ADMIN`: `Barbeiro`/`Servico` `cadastrar`/`ativar`/`desativar`/`listarTodos`, `Usuario` `listarTodos`/`buscarPorId`, `Agendamento` `listarTodos` and `GET /api/agendamentos/barbeiro/{barbeiroId}`. Open to any authenticated user: `/api/barbeiros/ativos`, `/api/servicos/ativos`, `/{id}` lookups, and all of the client-facing `Agendamento` endpoints (booking, `/meus`, `/disponibilidade`, cancelling). `@PreAuthorize` denials are resolved by `GlobalExceptionHandler`'s `AccessDeniedException` handler (this works because the exception is thrown during controller invocation, inside `DispatcherServlet`'s handler exception resolution — not from the URL-based `authorizeHttpRequests` filter chain, which a `@RestControllerAdvice` can't reach).

#### CORS

Configured centrally in `SecurityConfig` via a `CorsConfigurationSource` bean plus `.cors(Customizer.withDefaults())` — controllers do **not** use `@CrossOrigin`. Allowed origins come from `api.cors.allowed-origins` (comma-separated), defaulting to `http://localhost:5173,http://localhost:3000`; override via `CORS_ALLOWED_ORIGINS` in `.env`/`docker-compose.yml`.

### Agendamento (appointment) rules

`AgendamentoService.cadastrar` enforces several invariants before saving — keep these in sync when touching appointment logic: barbeiro must exist and be `ativo`, servico must exist and be `ativo`, appointment time must fall on a 30-minute boundary (`:00` or `:30`), and the barbeiro must not already have an appointment at that exact `dataHoraVisita` (checked via `AgendamentoRepository.existsByBarbeiroAndDataHoraVisita`, backed by a unique DB constraint on `(barbeiro_id, data_hora_visita)` to close a race between the check and the save). `valorServicoNoMomento` snapshots the service's price at booking time rather than referencing the live `Servico` price.

`cadastrar` takes the `cliente` as an already-resolved `Usuario` parameter, not an id in the request body — `AgendamentoController` resolves it via `@AuthenticationPrincipal Usuario`, so a request can never book on behalf of someone else by editing the JSON. If a future ADMIN-books-for-client flow is needed, it should be a separate, explicit endpoint.

Additional read endpoints beyond `GET /api/agendamentos` (ADMIN-only, all appointments): `GET /api/agendamentos/meus` (any authenticated user, their own, newest first), `GET /api/agendamentos/barbeiro/{barbeiroId}?data=YYYY-MM-DD` (ADMIN-only, one barbeiro's agenda for a day), and `GET /api/agendamentos/disponibilidade?barbeiroId={uuid}&data=YYYY-MM-DD` (any authenticated user, free 30-minute slots as `"HH:mm"` strings within `api.barbearia.horario-abertura`/`horario-fechamento`, excluding slots with a non-`CANCELADO` appointment and, for today, times already passed; an inactive barbeiro returns an empty list rather than an error).

Cancelling (`PATCH /api/agendamentos/{id}/cancelar`) is restricted to the agendamento's owner or an `ADMIN`, and rejects cancelling an already-`CANCELADO` agendamento or one whose `dataHoraVisita` has passed — all three cases throw `OperacaoNaoPermitidaException` (403).

### Active/inactive pattern

`Barbeiro` and `Servico` both use an `ativo` boolean flag rather than deletion, with dedicated activate/deactivate endpoints and list-active-only endpoints. Follow this pattern for similar entities instead of hard deletes.

## Testing

Only the default Spring Boot context-load test (`BarbeariaApiApplicationTests`) exists currently; there is no test suite for services/controllers yet.
