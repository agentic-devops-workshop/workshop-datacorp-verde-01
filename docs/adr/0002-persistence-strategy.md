# ADR-002 — Estratégia de Persistência: JPA/Hibernate + PostgreSQL com Mapeamento Adabas

## Status

✅ **Accepted** · 20/05/2026 · Par 2 (EA + SA) · Revisão: Par 4 (DBA)

## REQs vinculados

REQ-CALC-003 a REQ-CALC-007 (fatores/fórmula), REQ-DSC-004 (vigência), REQ-PAY-001 (geração batch), REQ-PAY-003 (ciclo de vida), REQ-BEN-001 (beneficiário)

## ADRs vinculados

ADR-001 (Monolito Modular — banco com schemas separados por módulo)

## Contexto

O legado SIFAP usa Adabas (banco hierárquico, não-relacional) com estruturas específicas:
- **MU fields** (multiple-value): campos multi-valorados dentro de um registro (ex.: telefones do beneficiário).
- **PE groups** (periodic groups): grupos repetitivos com múltiplos campos (ex.: DESCONTOS com TIPO-DSCT, VLR-DSCT, PCT-DSCT, DT-INICIO-DSCT, DT-FIM-DSCT — até N ocorrências por beneficiário).
- **Super-descriptors**: índices compostos virtuais (ex.: CPF + COMPETENCIA para busca de pagamentos).
- **NUM-DEPENDENTES redundante**: contador desnormalizado usado no cálculo (BR-004) mas inconsistente com o real count do PE group.

A modernização precisa mapear essas estruturas para PostgreSQL 16 relacional com JPA/Hibernate, preservando a semântica de negócio sem importar dívida técnica.

## Opções Consideradas

### Opção A — JSONB para tudo (schema-less)

- **Prós:** Flexível; suporta estruturas hierárquicas naturalmente; menos migrations.
- **Contras:** Perde validação de schema no banco; consultas JSONB são mais lentas para JOINs; JPA mapping não-trivial; difícil garantir integridade referencial.
- **Custo/Risco:** Custo médio; risco alto de bugs silenciosos por schema mismatch.

### Opção B — Relacional normalizado + @ElementCollection para MU + @OneToMany para PE (escolhida)

- **Prós:** Integridade referencial forte; queries SQL standard; JPA nativo; auditabilidade por schema; PE groups viram tabelas filhas (ex.: `beneficiary_discount`) com FK; MU fields viram `@ElementCollection`; super-descriptors viram `@Index` compostos.
- **Contras:** Mais tabelas (uma por PE group); migrations Flyway necessárias para cada mudança; NUM-DEPENDENTES vira `@Formula` ou campo calculado.
- **Custo/Risco:** Custo médio; risco baixo; approach mais testado.

### Opção C — Event Sourcing

- **Prós:** Histórico completo de mudanças; replay possível; auditoria natural.
- **Contras:** Complexidade enorme para time de 5 em 1 dia; read models necessários; tooling CQRS completo; over-engineering para o volume atual.
- **Custo/Risco:** Custo altíssimo; risco de não entregar.

## Decisão

Adotar **Opção B** — relacional normalizado com JPA/Hibernate sobre PostgreSQL 16.

### Mapeamento Adabas → JPA

| Estrutura Adabas | Mapeamento JPA | Exemplo SIFAP |
|-----------------|----------------|---------------|
| Campo simples | `@Column` | `STATUS`, `CPF`, `VLR-BASE` |
| MU field (multi-value) | `@ElementCollection` ou coluna JSONB | Telefones do beneficiário |
| PE group (periodic group) | `@OneToMany` + entidade filha | `DESCONTOS` → tabela `beneficiary_discount` |
| Super-descriptor | `@Index` composto na entidade | CPF+COMPETENCIA → `@Index({"cpf","competencia"})` |
| Contador redundante (NUM-DEPENDENTES) | `@Formula("(SELECT COUNT(*) FROM dependent d WHERE d.beneficiary_id = id)")` | Campo calculado, não armazenado |

### Schema por módulo

```sql
-- Schema: beneficiary
CREATE TABLE beneficiary (id BIGSERIAL PK, cpf VARCHAR(11) UNIQUE, status CHAR(1), ...);
CREATE TABLE beneficiary_dependent (id BIGSERIAL PK, beneficiary_id BIGINT FK, ...);
CREATE TABLE beneficiary_discount (id BIGSERIAL PK, beneficiary_id BIGINT FK, tipo CHAR(1), ...);

-- Schema: payment
CREATE TABLE payment (id BIGSERIAL PK, num_pagto BIGINT UNIQUE, cpf VARCHAR(11), competencia VARCHAR(6), status CHAR(1), ...);

-- Schema: audit
CREATE TABLE audit_event (id BIGSERIAL PK, seq_audit BIGINT, acao CHAR(2), tabela VARCHAR(30), ...);

-- Schema: admin
CREATE TABLE social_program (id BIGSERIAL PK, codigo VARCHAR(10), tipo CHAR(1), status CHAR(1), ...);
```

### Migrations

- Flyway para versionamento (`V1__init_beneficiary.sql`, `V2__init_payment.sql`, etc.)
- Cada módulo possui suas migrations no diretório próprio
- Rollback via `U` (undo) migrations para desenvolvimento

## Consequências

### Positivas
- Integridade referencial garante que PE groups não dessincronizam (diferente do legado).
- NUM-DEPENDENTES calculado = fim da inconsistência BR-004.
- Super-descriptors como `@Index` compostos mantém performance de busca do legado.
- Schema separation por módulo alinha com ADR-001 (fronteiras de monolito modular).

### Negativas
- Mais tabelas que o Adabas original (1 file Adabas → N tabelas PostgreSQL).
- JPA lazy loading precisa de cuidado em batch (N+1 queries se não usar `@EntityGraph`).
- Flyway migrations exigem disciplina de versionamento.

### Riscos
- Batch de 4.2M registros pode ter gargalo se JPA flush não for otimizado (usar `@Modifying` bulk ou StatelessSession).
- Se mapeamento `@Formula` for lento, pode precisar virar materialized view ou trigger.

## Critérios de Envelhecimento

Revisitar se:
- 🚨 Batch processing > 30 min (JPA não escala — considerar StatelessSession ou native query)
- 🚨 Schema changes bloqueiam deploys frequentes (considerar schema migration tool mais flexível)
- 🚨 Volume de auditoria > 100M registros/ano (considerar particionamento por mês)

## Referências

- `01-arqueologia/legado-sifap/adabas-ddms/` — estrutura dos 4 DDMs
- `01-arqueologia/business-rules-catalog.md` — BR-004 (fator familiar/dependentes), BR-019 (vigência descontos)
- `01-arqueologia/discovery-report.md` §3.2 — dependências e PE groups
- *Java Persistence with Hibernate* (Bauer, King — 2015) — cap. 7, Collections mapping