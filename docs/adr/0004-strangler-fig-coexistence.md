# ADR-004 — Coexistência Legado/Moderno via Strangler Fig

## Status

✅ **Accepted** · 20/05/2026 · Par 2 (EA + SA) · Revisão: Par 3 (TL)

## REQs vinculados

REQ-CONC-001 a REQ-CONC-004 (conciliação bancária), REQ-DSC-003 (unificação descontos), REQ-PAY-001 (geração batch)

## ADRs vinculados

ADR-001 (Monolito Modular), ADR-002 (Persistência)

## Contexto

O SIFAP legado continuará em operação durante a janela de modernização. Decisões específicas:

- **Conciliação bancária** (BATCHCON): ainda recebe arquivos CNAB 240 do BB. O módulo moderno precisa processar o mesmo formato durante a transição, depois migrar para API bancária.
- **Descontos**: legado usa 3% fixo (BATCHPGT), módulo online usa 4 faixas progressivas (CALCDSCT). Ambos os modos precisam coexistir até unificação completa.
- **Batch de pagamentos**: o legado processa 4.2M registros. Não é viável migrar tudo de uma vez — precisa de migração incremental por programa social.

O padrão Strangler Fig permite que a nova funcionalidade "envolva" a antiga, substituindo-a gradualmente sem big bang.

## Opções Consideradas

### Opção A — Big Bang (desligar legado de uma vez)

- **Prós:** Simplicidade conceitual; sem duplicação de lógica; data de corte clara.
- **Contras:** Risco catastrófico — 4.2M beneficiários sem pagamento se algo falhar; sem rollback possível; exige migração de dados completa antes do go-live.
- **Custo/Risco:** Custo baixo; risco altíssimo (inaceitável para sistema de pagamento social).

### Opção B — Strangler Fig com facade de roteamento (escolhida)

- **Prós:** Migração incremental (programa por programa); rollback simples (voltar rota para legado); ambos os modos coexistem; validação comparativa possível (shadow mode).
- **Contras:** Complexidade de manter duas implementações temporariamente; roteamento adiciona um nível de indireção; precisa de feature flags para controlar migração.
- **Custo/Risco:** Custo médio; risco baixo; totalmente reversível.

### Opção C — Parallel Run permanente

- **Prós:** Máxima segurança — executa ambos e compara.
- **Contras:** Dobra o custo computacional; exige reconciliação contínua; complexidade de manter sincronia.
- **Custo/Risco:** Custo alto; risco baixo; insustentável a longo prazo.

## Decisão

Adotar **Opção B** — Strangler Fig com facade de roteamento e feature flags.

### Estratégia de Migração por Módulo

```
┌──────────────────────────────────────────────────┐
│              Facade de Roteamento                  │
│  (Feature Flags controlam qual impl. atende)      │
├──────────────┬──────────────┬────────────────────┤
│  Beneficiary │   Payment    │  Reconciliation    │
│   MODERNO    │  MODERNO +   │     MODERNO        │
│   (100%)     │  LEGADO (%)  │  (CNAB → API grad) │
└──────────────┴──────────────┴────────────────────┘
```

### Fases de Migração

| Fase | Módulo | Estratégia | Critério de saída |
|------|--------|------------|-------------------|
| 1 | Beneficiary (cadastro) | Moderno desde dia 1 | Testes passam, dados migrados |
| 2 | Payment (cálculo) | Shadow mode → cutover por programa | Comparação 100% match por 30 dias |
| 3 | Discount (unificação) | Feature flag: `discount.mode=progressive` | Validação com negócio |
| 4 | Reconciliation (CNAB) | CNAB parser moderno → futura API BB | Parser funcional + testes |

### Feature Flags

```yaml
# application.yml
feature:
  payment:
    calculation-engine: modern  # modern | legacy | shadow
  discount:
    mode: progressive           # progressive | flat | both
  reconciliation:
    source: cnab240             # cnab240 | api (futuro)
```

### Shadow Mode (validação)

Para módulos críticos (Payment), período de shadow mode:
1. Ambos os engines calculam em paralelo.
2. Resultados comparados automaticamente.
3. Divergências logadas como alertas.
4. Cutover apenas quando divergência = 0% por 30 dias.

## Consequências

### Positivas
- Migração reversível a qualquer momento (voltar flag para legado).
- Risco de impacto em beneficiários minimizado.
- Validação comparativa detecta bugs antes do cutover.
- Cada programa social pode migrar independentemente.

### Negativas
- Custo de manter duas implementações durante transição.
- Feature flags adicionam complexidade ao código.
- Shadow mode dobra processamento durante validação.

### Riscos
- Se feature flags não forem removidas após cutover, código morto se acumula.
- Se shadow mode detectar divergências, investigação pode atrasar cutover.
- Manter legado "vivo" pode criar pressão para não completar migração.

## Critérios de Envelhecimento

Revisitar se:
- 🚨 Janela de coexistência excede 18 meses (fadiga de manutenção dupla)
- 🚨 Feature flags excedem 10 (complexidade de configuração)
- 🚨 Shadow mode detecta divergências sistêmicas sem resolução

## Referências

- `01-arqueologia/discovery-report.md` §5 — recomendações de migração
- `02-spec-moderna/scope-decisions.md` — riscos de escopo (unificação de descontos)
- Martin Fowler, *StranglerFigApplication* (2004)
- Sam Newman, *Monolith to Microservices* (2019) — cap. 3, Strangler Fig Pattern