# AGENTS.md — SIFAP 2.0

## Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 21 + Spring Boot 3.3 + JPA/Hibernate + PostgreSQL 16 |
| Frontend | Next.js 15 (App Router) + TypeScript 5 (strict) + Tailwind CSS |
| Database | PostgreSQL 16 + Flyway migrations |
| Containers | Docker + Docker Compose |
| CI/CD | GitHub Actions |
| Testing | JUnit 5 + Testcontainers (backend); Vitest (frontend) |

## Build Commands

```bash
# Backend
cd prototype/backend && ./mvnw package -DskipTests

# Frontend
cd prototype/frontend && npm run build

# Full stack (Docker)
docker compose up -d
```

## Test Commands

```bash
# Backend (requires Docker for Testcontainers)
cd prototype/backend && ./mvnw test

# Frontend
cd prototype/frontend && npm test
```

## Lint Commands

```bash
# Frontend
cd prototype/frontend && npm run lint && npm run typecheck
```

## Code Conventions

- Java: records for DTOs, sealed interfaces for unions, constructor injection, no null returns from public methods
- TypeScript: strict mode, named exports only, server components by default
- REST: `/api/v1/{resource}`, proper HTTP verbs, OpenAPI annotations
- Commits: `feat|fix|test|refactor(module): description — Implements REQ-XXX`
- Branches: `impl/<feature-name>` from `develop`

## Architecture: Modular Monolith

4 bounded contexts as top-level packages:
- `beneficiary` — Cadastro, validação, dependentes
- `payment` — Cálculo, geração batch, descontos, conciliação
- `audit` — Trilha de auditoria (append-only)
- `admin` — Programas sociais, parametrização

Each context has 3 layers: `domain/` → `application/` → `infrastructure/`

## Security Rules

- CPF always masked in logs (LGPD)
- No hardcoded secrets
- SQL only via JPA (no string concatenation)
- Bean Validation on all controller inputs
- JWT/OAuth2 authentication
