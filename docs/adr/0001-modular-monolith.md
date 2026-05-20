# ADR-001 — Adotar Monolito Modular (Package-by-Feature)

## Status

✅ **Accepted** · 20/05/2026 · Par 2 (EA + SA) · Revisão: Par 1 (PO)

## REQs vinculados

REQ-CALC-001 a REQ-CALC-010, REQ-DSC-001 a REQ-DSC-005, REQ-PAY-001 a REQ-PAY-005, REQ-ELIG-001 a REQ-ELIG-004, REQ-AUD-001 a REQ-AUD-003, REQ-BEN-001

## Contexto

Estamos modernizando o SIFAP (29 anos, Natural/Adabas) para Java 21 + Spring Boot 3.3 + PostgreSQL 16. O discovery-report identificou 4 domínios naturais com fronteiras claras (Beneficiary, Payment, Audit, Admin/SocialProgram) e forte acoplamento via DDM BENEFICIARIO (hub central — 11 programas acessam).

Restrições:

- **Time de 5 pessoas** — sem capacidade operacional para infra distribuída.
- **Janela de workshop** — 1 dia para protótipo funcional + deploy.
- **Auditoria transversal** — quase toda mutação gera registro de auditoria. Comunicação cross-service adicionaria latência sem benefício.
- **Lógica duplicada no legado** (CALCBENF ↔ BATCHPGT) — a modernização deve unificar, não multiplicar serviços.
- **Batch crítico** — geração de 4.2M+ pagamentos não se beneficia de distribuição, se beneficia de in-process.

## Opções Consideradas

### Opção A — Microsserviços desde o dia 1

- **Prós:** Escalabilidade horizontal por domínio; deploy independente; isolamento de falha.
- **Contras:** Não cabe em 8h (service mesh, contratos versionados, observabilidade distribuída); time sem skill operacional para 4+ serviços; latência inter-serviço quebraria SLA p95 < 200ms para ciclo batch.
- **Custo/Risco:** Custo altíssimo em tempo e operação; risco de não entregar.

### Opção B — Monolito Modular com Package-by-Feature (escolhida)

- **Prós:** 1 deployable (1 pipeline, 1 Docker image); latência inter-módulo zero; fronteiras de domínio visíveis via ArchUnit; refatoração para microsserviço possível módulo a módulo no futuro (Strangler Fig).
- **Contras:** Bug em um módulo pode derrubar todo o processo; deploy é all-or-nothing; escalabilidade horizontal "tudo ou nada".
- **Custo/Risco:** Custo baixo; risco baixo; reversível se necessário.

### Opção C — Monolito Tradicional (package-by-layer)

- **Prós:** Familiar; menor ceremony.
- **Contras:** Perde clareza de domínio — os 4 bounded contexts se misturam em `controllers/`, `services/`, `repositories/` (exatamente o anti-padrão do legado); refatoração futura extremamente custosa.
- **Custo/Risco:** Custo baixo; risco alto de dívida técnica irreversível.

## Decisão

Adotar **Monolito Modular** (Opção B) com a seguinte estrutura:

```
src/main/java/br/gov/sifap/
├── beneficiary/        ← bounded context
│   ├── domain/         (entidades, value objects, interfaces)
│   ├── application/    (services, use cases)
│   └── infrastructure/ (controllers, JPA repos, mappers)
├── payment/            ← bounded context
├── discount/           ← sub-módulo de payment
├── eligibility/        ← bounded context
├── audit/              ← bounded context
└── shared/             ← kernel (tipos cross-cutting: Money, CPF, AuditEvent)
```

Regras de fronteira:
1. Nenhum módulo importa `infrastructure/` de outro módulo.
2. Comunicação inter-módulo via interfaces em `domain/` ou domain events.
3. ArchUnit no CI valida fronteiras a cada PR.
4. Banco com schemas separados por módulo — cada módulo só faz JOIN no próprio schema.

## Consequências

### Positivas
- 1 deployable = simplicidade operacional máxima para time de 5.
- Latência inter-módulo zero (in-process) — crítico para batch de 4.2M registros.
- Bounded contexts visíveis e protegidos por ArchUnit — não vira espaguete.
- Migração futura para microsserviço é "extrair módulo" — não reescrever.

### Negativas
- Bug no módulo `payment` pode derrubar `beneficiary` (mesmo JVM).
- Deploy atualiza todos os módulos juntos.
- Escalabilidade horizontal é all-or-nothing.

### Riscos
- Se ArchUnit tests forem ignorados/relaxados, fronteiras degradam silenciosamente.
- Se volume de auditoria crescer 10x, `audit` módulo pode precisar extração.

## Critérios de Envelhecimento

Revisitar este ADR se:
- 🚨 Build time > 10 min (monolito grande demais)
- 🚨 Time cresce para > 15 pessoas (contenção no mesmo repo)
- 🚨 Módulo `audit` precisa escalar 5x mais que `payment`
- 🚨 Deploy de feature em módulo X causa rollback de módulo Y

## Referências

- `01-arqueologia/discovery-report.md` — 4 domínios identificados
- `01-arqueologia/dependency-map.md` — BENEFICIARIO como hub
- `02-spec-moderna/scope-decisions.md` — decisões de escopo
- *Building Evolutionary Architectures* (Ford, Parsons, Kua — 2017) cap. 4