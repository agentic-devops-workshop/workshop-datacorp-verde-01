<!--
Sync Impact Report:
- Version change: N/A → 1.0.0 (initial creation)
- Added sections: Core Principles (7), Technology Stack, Development Workflow, Governance
- Templates requiring updates: ✅ plan-template.md (Constitution Check section aligns)
                               ✅ spec-template.md (user stories + acceptance align)
                               ✅ tasks-template.md (phase structure aligns)
- Follow-up TODOs: None
-->

# SIFAP 2.0 Constitution

## Core Principles

### I. Legacy Traceability (NON-NEGOTIABLE)

Every requirement, test, and implementation artifact MUST trace back to the
legacy source code or be explicitly marked as `[GREENFIELD]` with justification.

- Every EARS requirement carries a `source_legacy:` field pointing to
  `01-arqueologia/legado-sifap/natural-programs/*.NSN` or `*.ddm`
- Tests reference REQ-IDs via inline comments
- PRs without `source_legacy:` traceability are rejected by CI
  (`legacy-traceability` job)
- The business rules catalog (`01-arqueologia/business-rules-catalog.md`,
  BR-001 to BR-150) is the authoritative source of truth for domain logic

### II. Test-First for Business Rules (NON-NEGOTIABLE)

All business logic derived from the legacy MUST have tests written before or
concurrently with implementation. No code ships without passing tests.

- Unit tests are mandatory for all calculation, discount, eligibility, and
  state-machine logic (REQ-CALC-*, REQ-DSC-*, REQ-ELIG-*, REQ-PAY-003)
- Integration tests (Testcontainers) required for persistence boundaries
  and batch processing flows
- Acceptance criteria from SPECIFICATION.md translate directly to test cases
- Test naming convention: `should_<expected>_when_<condition>`

### III. Domain Integrity

The modernized system MUST preserve all validated legacy business rules exactly,
including edge cases, unless an explicit decision is documented in an ADR.

- Benefit calculation uses TRUNCATION (×100, integer, ÷100) — never rounding
- Payment state machine enforces guard clauses (G→P/D/E, no reverse)
- Judicial discounts NEVER have the 30% cap applied (legal precedence)
- CPF masking is consistent across all outputs (`***.XXX.XXX-**`) per LGPD
- 6 pending decisions (mysteries) require ADR or PO approval before deviating

### IV. Modular Monolith Architecture

The system follows a package-by-feature Modular Monolith pattern with clear
bounded context boundaries enforced at compile time.

- 4 bounded contexts: Beneficiary, Payment, Audit, Admin
- Inter-module communication via public API interfaces only (no direct
  repository access across modules)
- ArchUnit tests enforce module boundaries
- Strangler Fig pattern for coexistence during migration (ADR-004)
- Each module owns its database tables (logical separation within PostgreSQL)

### V. Security by Default (OWASP Top 10)

Security controls are built-in, not bolted-on.

- Authentication: OAuth2/JWT via Spring Security + Azure AD (ADR-003)
- Authorization: 5 roles (OPERATOR, ANALYST, AUDITOR, ADMIN, BATCH)
- No hardcoded secrets — Azure Key Vault for all credentials
- SQL via JPA/JPQL only — no string concatenation
- Input validation at every system boundary (`@Valid` + Bean Validation)
- CPF never appears in logs, error messages, or API responses in full
- CORS explicitly configured — no wildcard `*` in production

### VI. Spec-Driven Development

All features follow the Spec-Kit workflow: specify → clarify → plan → tasks →
implement. No code without a spec.

- Every feature lives in `specs/<NNN>-<feature>/` with `spec.md`, `plan.md`,
  `tasks.md`
- EARS notation (6 patterns) is the standard for requirements
- Branch naming: `spec/<NNN>-<feature>`
- Merge order: `spec/*` → `develop` → `stage` → `main`
- ADRs document architectural decisions with rejected alternatives

### VII. Simplicity and No Over-Engineering

Start simple. Add complexity only when proven necessary by a failing test or
a documented requirement.

- No frameworks or libraries without justification in an ADR
- No abstractions for one-time operations
- No premature optimization — measure first
- Prefer standard library solutions over third-party when equivalent
- YAGNI: if the SPECIFICATION.md doesn't require it, don't build it

## Technology Stack

| Layer | Technology | Version | Justification |
|-------|-----------|---------|---------------|
| Backend | Java + Spring Boot | 21 + 3.3 | ADR-001 |
| Persistence | JPA/Hibernate + PostgreSQL | 6.4 + 16 | ADR-002 |
| Frontend | Next.js + TypeScript | 15 + 5 (strict) | Greenfield |
| Styling | Tailwind CSS + shadcn/ui | Latest | Greenfield |
| Auth | Spring Security + OAuth2/JWT | 6.x | ADR-003 |
| Testing (BE) | JUnit 5 + Testcontainers | 5.10 + Latest | Standard |
| Testing (FE) | Vitest + Testing Library | Latest | Standard |
| Containers | Docker + Docker Compose | Latest | Dev parity |
| IaC | Terraform (Azure) | ~> 3.x | Workshop req |
| CI/CD | GitHub Actions | N/A | Workshop req |

**Constraints:**
- Java 21 features required: records (DTOs), sealed interfaces, pattern
  matching, virtual threads
- TypeScript `strict: true` — no exceptions
- Named exports only in frontend (no default exports)
- `@Transactional` only on service layer, never repositories

## Development Workflow

### Branch Strategy

```
spec/<NNN>-<feature> → develop → stage → main
```

### Quality Gates (per PR)

1. All tests pass (unit + integration)
2. `source_legacy:` present on all new EARS
3. No secrets in code, commits, or PR descriptions
4. At least one peer review
5. `terraform fmt` + `terraform validate` pass (IaC changes)
6. ArchUnit module boundary tests pass

### Commit Convention

```
<type>(<scope>): <description>

Types: feat, fix, refactor, test, docs, chore, ci
Scopes: beneficiary, payment, audit, admin, infra, spec
```

### ADR Process

- Every new dependency requires an ADR
- Every architectural deviation from legacy requires an ADR
- ADR format: Context → Decision → Alternatives Rejected → Consequences
- Stored in `docs/adr/`

## Governance

This constitution is the supreme authority for the SIFAP 2.0 project.
All code, specs, and architectural decisions MUST comply.

- **Amendments** require: documented rationale + team consensus + version bump
- **Versioning**: SemVer (MAJOR: principle removal/redefinition, MINOR: new
  principle or section, PATCH: clarification/wording)
- **Compliance review**: every PR is checked against these principles
- **Conflict resolution**: Constitution > ADRs > SPECIFICATION.md > Code
- **Runtime guidance**: see `.github/copilot-instructions.md` for Copilot-
  specific rules and `02-spec-moderna/GUIDE.md` for workflow

**Version**: 1.0.0 | **Ratified**: 2026-05-20 | **Last Amended**: 2026-05-20
