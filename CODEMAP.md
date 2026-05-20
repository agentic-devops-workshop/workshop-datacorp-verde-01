# CODEMAP.md — SIFAP 2.0

## Modules

```
prototype/backend/src/main/java/br/gov/sifap/
├── shared/                    # Shared Kernel (cross-cutting)
│   ├── domain/
│   │   ├── CPF.java           # Value Object — CPF with module-11 validation
│   │   ├── Money.java         # Value Object — truncated monetary values
│   │   └── AuditEvent.java    # Record — published by all contexts
│   └── infrastructure/
│       └── security/
│           └── SecurityConfig.java
├── beneficiary/               # BC: Beneficiary Management
│   ├── domain/
│   │   ├── Beneficiary.java           # JPA Entity — hub entity
│   │   ├── BeneficiaryStatus.java     # Enum (A/S/C/I/D)
│   │   └── BeneficiaryRepository.java # Interface (domain port)
│   ├── application/
│   │   └── BeneficiaryService.java    # Use cases
│   └── infrastructure/
│       ├── JpaBeneficiaryRepository.java # Spring Data impl
│       └── BeneficiaryController.java    # REST /api/v1/beneficiaries
├── payment/                   # BC: Payment Processing
│   ├── domain/
│   │   ├── Payment.java              # JPA Entity — lifecycle G→P/D/E
│   │   ├── PaymentStatus.java        # Enum with transition rules
│   │   ├── PaymentType.java          # Enum (N/D)
│   │   ├── BenefitCalculator.java    # Pure calculation logic
│   │   └── DiscountEngine.java       # Discount rules + 30% cap
│   ├── application/
│   │   └── PaymentService.java       # Generation + transition
│   └── infrastructure/
│       ├── JpaPaymentRepository.java
│       └── PaymentController.java     # REST /api/v1/payments
├── audit/                     # BC: Audit Trail
│   ├── domain/
│   │   └── AuditEventEntity.java     # JPA Entity (append-only)
│   ├── application/
│   │   └── AuditService.java         # Record + query
│   └── infrastructure/
│       └── JpaAuditEventRepository.java
└── admin/                     # BC: Administration
    ├── domain/
    │   └── SocialProgram.java         # JPA Entity
    └── infrastructure/
        └── JpaSocialProgramRepository.java
```

## Data Flow

```
[Client] → REST Controller → Service → Repository → PostgreSQL
                                ↓
                         AuditService.record()
                                ↓
                         audit.audit_event table
```

## External Integrations

| System | Protocol | Purpose |
|--------|----------|---------|
| PostgreSQL 16 | JDBC | Primary datastore |
| (Future) Bank Return Files | File/SFTP | Payment reconciliation |

## Database Schemas

| Schema | Tables | Owner BC |
|--------|--------|----------|
| `beneficiary` | beneficiary, beneficiary_dependent | Beneficiary |
| `payment` | payment, payment_batch_run | Payment |
| `audit` | audit_event | Audit |
| `admin` | social_program, reference_parameter | Admin |
