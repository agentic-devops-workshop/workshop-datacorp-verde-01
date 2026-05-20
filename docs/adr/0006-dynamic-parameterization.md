# ADR-006 — Parametrização Dinâmica via Tabelas de Referência

## Status

✅ **Accepted** · 20/05/2026 · Par 2 (EA + SA) · Revisão: Par 4 (DBA)

## REQs vinculados

REQ-PARAM-001 (fatores regionais), REQ-PARAM-002 (faixas de desconto), REQ-CALC-003 (fator regional), REQ-CALC-005 (fator renda), REQ-DSC-003 (contribuição social)

## ADRs vinculados

ADR-002 (Persistência — tabelas no schema admin), ADR-001 (Monolito Modular — módulo admin)

## Contexto

O legado SIFAP tem **dados de referência hardcoded no código-fonte**:

- 27 fatores regionais (CALCBENF.NSN#L86-L112) — array inline
- 5 faixas de renda (CALCBENF.NSN#L118-L128) — IF/ELSE encadeado
- 4 alíquotas de contribuição social (CALCDSCT.NSN#L56-L60) — IF/ELSE
- 12 índices IPCA mensais × 3 anos (CALCCORR.NSN#L54-L95) — tabela fixa
- 8 prefixos especiais de CPF (VALDOCS.NSN#L49-L56) — array inline

Qualquer alteração nesses valores exigia **recompilação e deploy** do programa Natural. Na modernização, esses valores devem ser configuráveis sem deploy.

## Opções Consideradas

### Opção A — application.yml / environment variables

- **Prós:** Simples; familiar; sem tabelas adicionais; Spring Boot reload possível.
- **Contras:** Alteração exige restart (ou Spring Cloud Config); sem histórico de quem alterou; sem UI de gestão; difícil para 27+ valores estruturados.
- **Custo/Risco:** Custo baixo; risco médio (sem auditoria de alterações).

### Opção B — Tabelas de referência no banco + cache em memória (escolhida)

- **Prós:** Alteração via API admin sem deploy/restart; histórico completo (quem, quando, valor anterior/novo); cache em memória para performance; UI de gestão possível; constraint de banco garante integridade.
- **Contras:** Mais tabelas no banco; cache invalidation necessário; complexidade de cache coherency.
- **Custo/Risco:** Custo médio; risco baixo; approach mais flexível.

### Opção C — Serviço de configuração externo (Spring Cloud Config / Consul)

- **Prós:** Centralizado; múltiplos ambientes; refresh sem restart.
- **Contras:** Infraestrutura adicional (mais um serviço para operar); over-engineering para valores que mudam 1-2x por ano; não garante auditoria nativa.
- **Custo/Risco:** Custo alto; risco médio (mais uma dependência operacional).

## Decisão

Adotar **Opção B** — Tabelas de referência no banco PostgreSQL + cache Caffeine in-memory.

### Schema (módulo admin)

```sql
-- Tabela genérica de parâmetros de referência
CREATE TABLE reference_parameter (
    id          BIGSERIAL PRIMARY KEY,
    category    VARCHAR(50) NOT NULL,   -- 'REGIONAL_FACTOR', 'INCOME_BRACKET', 'TAX_RATE'
    code        VARCHAR(20) NOT NULL,   -- 'UF_07', 'BRACKET_1', 'RATE_1'
    value       NUMERIC(10,6) NOT NULL, -- valor numérico
    description VARCHAR(200),
    valid_from  DATE NOT NULL DEFAULT CURRENT_DATE,
    valid_to    DATE,                   -- null = sem expiração
    created_by  VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE(category, code, valid_from)
);

-- Histórico de alterações (append-only)
CREATE TABLE reference_parameter_history (
    id              BIGSERIAL PRIMARY KEY,
    parameter_id    BIGINT NOT NULL REFERENCES reference_parameter(id),
    previous_value  NUMERIC(10,6),
    new_value       NUMERIC(10,6) NOT NULL,
    changed_by      VARCHAR(100) NOT NULL,
    changed_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    reason          VARCHAR(500)
);
```

### Dados Iniciais (migração dos hardcoded)

```sql
-- Fatores regionais (seed dos 27 valores do legado)
INSERT INTO reference_parameter (category, code, value, description, valid_from, created_by) VALUES
('REGIONAL_FACTOR', 'UF_01', 1.25, 'Norte - Amazonas', '1997-01-01', 'MIGRATION'),
('REGIONAL_FACTOR', 'UF_02', 1.28, 'Norte - Pará', '1997-01-01', 'MIGRATION'),
-- ... 27 UFs
('INCOME_BRACKET', 'BRACKET_1', 1.00, 'Renda <= 300', '1997-01-01', 'MIGRATION'),
('INCOME_BRACKET', 'BRACKET_2', 0.85, 'Renda <= 600', '1997-01-01', 'MIGRATION'),
-- ... 5 faixas
('TAX_RATE', 'RATE_1', 0.03, 'Contribuição <= 500', '1997-01-01', 'MIGRATION'),
('TAX_RATE', 'RATE_2', 0.05, 'Contribuição <= 1000', '1997-01-01', 'MIGRATION');
-- ... 4 alíquotas
```

### Cache Strategy

```java
@Service
public class ReferenceParameterService {
    
    private final ReferenceParameterRepository repository;
    private final Cache<String, List<ReferenceParameter>> cache;

    public ReferenceParameterService(ReferenceParameterRepository repository) {
        this.repository = repository;
        this.cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(5))  // refresh a cada 5 min
            .maximumSize(100)
            .build();
    }

    public List<ReferenceParameter> getByCategory(String category) {
        return cache.get(category, 
            k -> repository.findByCategoryAndValidDate(k, LocalDate.now()));
    }

    @CacheEvict(allEntries = true)
    public void updateParameter(Long id, BigDecimal newValue, String changedBy, String reason) {
        // update + history em transação
    }
}
```

### API Admin

```
PUT  /api/v1/admin/parameters/{id}         — atualizar valor
GET  /api/v1/admin/parameters?category=X   — listar por categoria
GET  /api/v1/admin/parameters/{id}/history  — histórico de alterações
POST /api/v1/admin/parameters/reload-cache  — forçar refresh do cache
```

Acesso restrito a `ROLE_ADMIN` (ADR-003).

## Consequências

### Positivas
- Alteração de fatores/faixas sem deploy — operação do dia a dia simplificada.
- Histórico completo de alterações com auditoria (quem, quando, por quê).
- Cache garante que cálculos em batch não sofrem latência de leitura.
- Seed migra valores exatos do legado — zero divergência no dia 1.
- `valid_from` / `valid_to` permite versionamento temporal (valor vigente por período).

### Negativas
- Mais tabelas no banco (2 tabelas + seed de ~40 registros).
- Cache de 5 min significa que alteração leva até 5 min para propagar (ou forçar reload).
- Se cache falhar, fallback para banco direto (latência aceitável para frequência de uso).

### Riscos
- Se alguém alterar fator regional incorretamente, cálculos erram até correção.
  Mitigação: validação de range no PUT (fator regional entre 0.5 e 2.0).
- Se cache ficar stale por bug, valores antigos são usados.
  Mitigação: healthcheck compara cache vs banco a cada 15 min.

## Critérios de Envelhecimento

Revisitar se:
- 🚨 Mais de 500 parâmetros de referência (considerar schema dedicado ou service)
- 🚨 Alterações mais frequentes que 1x/dia (considerar event-driven cache invalidation)
- 🚨 Necessidade de versionamento complexo (A/B testing de fatores)

## Referências

- `01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L86-L112` — fatores hardcoded
- `01-arqueologia/business-rules-catalog.md` — BR-003 (27 UFs), BR-005 (5 faixas), BR-014 (4 alíquotas)
- `02-spec-moderna/SPECIFICATION.md` — REQ-PARAM-001, REQ-PARAM-002
- Caffeine Cache: https://github.com/ben-manes/caffeine