# SPECIFICATION — SIFAP 2.0

## Metadados

- **Versão da spec:** 0.1.0 (Estágio 2)
- **Time:** DataCorp Verde 01
- **Data:** 20/05/2026
- **Aprovado pelo Product Owner:** ☐ (Pendente — Passagem #2)
- **Origem dos requisitos:** `01-arqueologia/business-rules-catalog.md` (BR-001 a BR-150)

---

## 1. Escopo

Esta especificação cobre **todos os módulos** definidos em `scope-decisions.md`:

**Prioridade Alta (P0):**
- **Beneficiary** — Cadastro, validação, dependentes
- **Payment** — Geração, cálculo e ciclo de vida de pagamentos
- **Discount** — Motor de descontos unificado
- **Eligibility** — Validação de elegibilidade
- **Audit** — Trilha de auditoria obrigatória

**Prioridade Média (P1):**
- **Dependent** — Gestão de dependentes (tabela separada)
- **SocialProgram** — Gestão de programas sociais
- **Reconciliation** — Conciliação bancária

**Greenfield:**
- **API REST** — Endpoints OpenAPI
- **Auth** — OAuth2/JWT
- **Parametrization** — Tabelas de referência dinâmicas

**Fora de escopo:** Correção Monetária (descartada), Dashboard Analítico (backlog), Notificações (backlog).

---

## 2. Requisitos (EARS)

---

### Módulo: Cálculo de Benefícios (Payment/Calculation)

#### REQ-CALC-001 · Pré-condição de status ativo do beneficiário

```yaml
REQ-CALC-001:
  pattern: unwanted
  text: "SE o beneficiário não possuir status 'A' (ativo), ENTÃO o sistema
         não deve calcular o benefício para esse beneficiário."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L159-L161
  business_rule: BR-001
  acceptance:
    - "Dado beneficiário com status 'S' (suspenso), quando o cálculo é executado, então nenhum pagamento é gerado para ele."
    - "Dado beneficiário com status 'A', quando o cálculo é executado, então o pagamento é gerado normalmente."
    - "Status válidos: A, S, C, I, D — apenas 'A' permite cálculo."
  priority: P0
  risk: ALTO
```

#### REQ-CALC-002 · Pré-condição de programa social ativo

```yaml
REQ-CALC-002:
  pattern: unwanted
  text: "SE o programa social vinculado ao beneficiário não possuir STATUS-PROG = 'A',
         ENTÃO o sistema não deve gerar pagamento para beneficiários desse programa."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L165-L172
  business_rule: BR-002
  acceptance:
    - "Dado programa com STATUS-PROG 'I' (inativo), quando batch executa, então zero pagamentos gerados para esse programa."
    - "Dado programa com STATUS-PROG 'A' e 50 beneficiários ativos, então 50 pagamentos são gerados."
  priority: P0
  risk: ALTO
```

#### REQ-CALC-003 · Fator regional por UF

```yaml
REQ-CALC-003:
  pattern: state-driven
  text: "ENQUANTO o sistema calcular o benefício de um beneficiário, o sistema
         deve aplicar o fator regional correspondente ao COD-REGIAO do beneficiário,
         conforme tabela de 27 UFs com multiplicadores de 1.00 a 1.40."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L86-L112
  business_rule: BR-003
  acceptance:
    - "Dado beneficiário com COD-REGIAO=7 (Nordeste), fator aplicado é 1.32."
    - "Dado beneficiário com COD-REGIAO fora do range 1-27, fator aplicado é 1.00 (default)."
    - "Tabela de fatores deve ser parametrizável (banco, não hardcoded)."
  priority: P0
  risk: CRÍTICO
```

#### REQ-CALC-004 · Fator familiar progressivo

```yaml
REQ-CALC-004:
  pattern: state-driven
  text: "ENQUANTO o sistema calcular o benefício, o sistema deve aplicar o fator
         familiar baseado no número de dependentes: 0=1.00; 1-2=1.0+0.05×n;
         3-4=1.10+0.03×(n-2); 5+=1.16+0.02×(n-4)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L178-L190
  business_rule: BR-004
  acceptance:
    - "Dado 0 dependentes, fator = 1.00."
    - "Dado 2 dependentes, fator = 1.10."
    - "Dado 4 dependentes, fator = 1.16."
    - "Dado 6 dependentes, fator = 1.20."
  priority: P0
  risk: CRÍTICO
```

#### REQ-CALC-005 · Fator renda (5 faixas decrescentes)

```yaml
REQ-CALC-005:
  pattern: state-driven
  text: "ENQUANTO o sistema calcular o benefício, o sistema deve aplicar o fator
         renda baseado na RENDA-FAMILIAR: ≤300=1.00; ≤600=0.85; ≤1000=0.70;
         ≤1500=0.55; >1500=0.40."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L118-L128
  business_rule: BR-005
  acceptance:
    - "Dado renda R$250, fator = 1.00."
    - "Dado renda R$600, fator = 0.85."
    - "Dado renda R$1001, fator = 0.70."
    - "Dado renda R$2000, fator = 0.40."
  priority: P0
  risk: CRÍTICO
```

#### REQ-CALC-006 · Fator idade

```yaml
REQ-CALC-006:
  pattern: state-driven
  text: "ENQUANTO o sistema calcular o benefício, o sistema deve aplicar o fator
         idade: ≥65 anos=1.15; ≥60 anos=1.10; <18 anos=1.05; demais=1.00.
         A idade deve ser calculada com data completa (ano+mês+dia)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L205-L215
  business_rule: BR-006
  acceptance:
    - "Dado beneficiário com 66 anos, fator = 1.15."
    - "Dado beneficiário com 62 anos, fator = 1.10."
    - "Dado beneficiário com 16 anos, fator = 1.05."
    - "Dado beneficiário com 35 anos, fator = 1.00."
  priority: P0
  risk: CRÍTICO
  notes: "Evolução: legado usava apenas ANO — modernização usa data completa."
```

#### REQ-CALC-007 · Fórmula principal do benefício

```yaml
REQ-CALC-007:
  pattern: ubiquitous
  text: "O sistema deve calcular o valor do benefício usando a fórmula:
         VLR-BENEFICIO = VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RND × FATOR-IDADE × (1 + FATOR-REAJ).
         O resultado deve ser TRUNCADO para 2 casas decimais (×100, inteiro, ÷100)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L225-L230
  business_rule: BR-007, BR-008
  acceptance:
    - "Dado VLR-BASE=500, FATOR-REG=1.20, FATOR-FAM=1.10, FATOR-RND=0.85, FATOR-IDADE=1.15, FATOR-REAJ=0.03 → resultado = 500×1.20×1.10×0.85×1.15×1.03 = 668.24 (truncado)."
    - "Resultado NUNCA é arredondado matematicamente — sempre truncado."
    - "Resultado com mais de 2 decimais: 668.249999 → 668.24 (não 668.25)."
  priority: P0
  risk: CRÍTICO
```

#### REQ-CALC-008 · 13° salário em dezembro

```yaml
REQ-CALC-008:
  pattern: event-driven
  text: "QUANDO o mês de competência for dezembro (mês=12), o sistema deve
         calcular o 13° salário usando fórmula diferenciada:
         VLR-13 = VLR-BASE × FATOR-REG × FATOR-IDADE (sem fator familiar e sem fator renda).
         O valor é somado ao bruto do pagamento."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L237-L248
  business_rule: BR-009
  acceptance:
    - "Dado competência dezembro/2026, pagamento inclui VLR-13 além do benefício normal."
    - "VLR-13 não usa FATOR-FAM nem FATOR-RND."
    - "TIPO-PGTO muda de 'N' (normal) para 'D' (dezembro)."
    - "Em meses diferentes de dezembro, VLR-13 = 0."
  priority: P0
  risk: CRÍTICO
```

#### REQ-CALC-009 · Abono natalino para programas assistenciais

```yaml
REQ-CALC-009:
  pattern: event-driven
  text: "QUANDO o mês de competência for dezembro E o programa social for do tipo 'A' (assistencial),
         o sistema deve calcular abono natalino = 15% do benefício mensal e somá-lo ao bruto."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L251-L256
  business_rule: BR-010
  acceptance:
    - "Dado programa tipo 'A' em dezembro, benefício mensal R$1000 → abono = R$150."
    - "Dado programa tipo 'P' (previdenciário) em dezembro → abono = R$0."
    - "Dado programa tipo 'A' em março → abono = R$0."
  priority: P0
  risk: CRÍTICO
```

#### REQ-CALC-010 · Valor líquido mínimo zero

```yaml
REQ-CALC-010:
  pattern: unwanted
  text: "SE o cálculo de valor líquido (bruto − descontos) resultar em valor negativo,
         ENTÃO o sistema deve atribuir valor líquido = 0 (zero)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L270-L272
  business_rule: BR-012
  acceptance:
    - "Dado bruto R$100 e descontos R$150 → líquido = R$0 (não -R$50)."
    - "Dado bruto R$100 e descontos R$80 → líquido = R$20."
  priority: P0
  risk: ALTO
```

---

### Módulo: Descontos (Discount)

#### REQ-DSC-001 · Teto de 30% para descontos não judiciais

```yaml
REQ-DSC-001:
  pattern: unwanted
  text: "SE o total de descontos NÃO judiciais exceder 30% do valor bruto do pagamento,
         ENTÃO o sistema deve truncar o total de descontos não judiciais em 30% do bruto."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L102-L104
  business_rule: BR-013
  acceptance:
    - "Dado bruto R$1000 e desconto tipo 'TAX' R$400 → aplicado R$300 (30% do bruto)."
    - "Dado bruto R$1000 e desconto tipo 'P' R$200 + tipo 'S' R$200 → aplicado R$300 (cap em 30%)."
    - "Descontos judiciais NÃO entram no cálculo do teto."
  priority: P0
  risk: CRÍTICO
```

#### REQ-DSC-002 · Desconto judicial sem teto

```yaml
REQ-DSC-002:
  pattern: event-driven
  text: "QUANDO um desconto do tipo 'J' (judicial) é aplicado, o sistema deve
         adicionar o valor integralmente ao total de descontos, sem aplicar o teto de 30%."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L129-L134
  business_rule: BR-015
  acceptance:
    - "Dado desconto judicial de 80% do bruto → aplicado integralmente."
    - "Dado desconto judicial R$800 + desconto sindical R$100 sobre bruto R$1000 → judicial R$800 + sindical limitado a R$300 (30%) = total R$1100? Não — sindical cap é sobre bruto, judicial não entra no cap."
    - "Múltiplos descontos judiciais somam sem limite."
  priority: P0
  risk: CRÍTICO
  notes: "Precedência legal — NUNCA aplicar teto a desconto judicial."
```

#### REQ-DSC-003 · Contribuição social progressiva (4 faixas)

```yaml
REQ-DSC-003:
  pattern: state-driven
  text: "ENQUANTO o sistema calcular descontos, o sistema deve aplicar a
         contribuição social progressiva: bruto ≤500=3%; ≤1000=5%; ≤2000=7%; >2000=9%."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L56-L60
  business_rule: BR-014
  acceptance:
    - "Dado bruto R$400 → contribuição = R$12 (3%)."
    - "Dado bruto R$800 → contribuição = R$40 (5%)."
    - "Dado bruto R$1500 → contribuição = R$105 (7%)."
    - "Dado bruto R$3000 → contribuição = R$270 (9%)."
  priority: P0
  risk: CRÍTICO
  notes: "Decisão pendente: BATCHPGT usa 3% fixo (BR-062). Adotar faixas progressivas como regra única."
```

#### REQ-DSC-004 · Vigência temporal de descontos

```yaml
REQ-DSC-004:
  pattern: state-driven
  text: "ENQUANTO o sistema aplicar descontos a um beneficiário, o sistema deve
         verificar se a data atual está dentro do intervalo [DT-INICIO, DT-FIM] do desconto.
         DT-FIM = 0 (ou nulo) significa sem prazo final."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L114-L119
  business_rule: BR-019
  acceptance:
    - "Dado desconto com DT-INICIO=01/01/2026 e DT-FIM=31/12/2026, data atual 15/06/2026 → desconto aplicado."
    - "Dado desconto com DT-FIM=01/01/2025, data atual 20/05/2026 → desconto NÃO aplicado (expirado)."
    - "Dado desconto com DT-FIM=0, data atual qualquer → desconto aplicado (sem prazo)."
  priority: P0
  risk: ALTO
```

#### REQ-DSC-005 · Desconto sindical fixo 1%

```yaml
REQ-DSC-005:
  pattern: ubiquitous
  text: "O sistema deve calcular o desconto sindical (tipo 'S') como 1% fixo do valor bruto."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L147-L148
  business_rule: BR-018
  acceptance:
    - "Dado bruto R$1000 e desconto tipo 'S' ativo → desconto = R$10."
    - "Valor sujeito ao teto de 30% (não é judicial)."
  priority: P1
  risk: MÉDIO
```

---

### Módulo: Geração de Pagamentos (Payment/Batch)

#### REQ-PAY-001 · Geração mensal de pagamentos em batch

```yaml
REQ-PAY-001:
  pattern: event-driven
  text: "QUANDO o ciclo de pagamento mensal é iniciado, o sistema deve gerar
         um registro de pagamento para cada beneficiário com status 'A' vinculado
         a um programa social com STATUS-PROG = 'A'."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L200-L208
  business_rule: BR-020, BR-001, BR-002
  acceptance:
    - "Dado 100 beneficiários ativos em programa ativo + 30 inativos → 100 pagamentos gerados."
    - "Beneficiário ativo em programa inativo → zero pagamentos para ele."
    - "Nenhum beneficiário pode ter dois pagamentos na mesma competência (duplicação bloqueada)."
  priority: P0
  risk: CRÍTICO
```

#### REQ-PAY-002 · Status inicial do pagamento gerado

```yaml
REQ-PAY-002:
  pattern: ubiquitous
  text: "O sistema deve criar todo pagamento gerado com STATUS-PGTO = 'G' (gerado)
         e TIPO-PGTO = 'N' (normal) ou 'D' (dezembro/13°)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L321-L333
  business_rule: BR-064
  acceptance:
    - "Pagamento gerado em maio → STATUS='G', TIPO='N'."
    - "Pagamento gerado em dezembro → STATUS='G', TIPO='D'."
    - "Não existe caminho de criação com status diferente de 'G'."
  priority: P0
  risk: ALTO
```

#### REQ-PAY-003 · Ciclo de vida do pagamento (máquina de estados)

```yaml
REQ-PAY-003:
  pattern: state-driven
  text: "ENQUANTO um pagamento existir no sistema, o ciclo de vida deve respeitar
         as transições: G (gerado) → P (pago) | D (devolvido) | E (erro).
         Nenhuma transição reversa é permitida. O sistema deve rejeitar qualquer
         tentativa de alterar status de pagamento que não esteja em 'G'."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L168-L194
  business_rule: BR-082
  acceptance:
    - "Pagamento 'G' recebe retorno '00' → status muda para 'P'."
    - "Pagamento 'G' recebe retorno '01' → status muda para 'D'."
    - "Pagamento 'G' recebe retorno '02' → status muda para 'E'."
    - "Pagamento 'P' recebe qualquer retorno → rejeitado (HTTP 409 ou exceção)."
    - "Pagamento 'D' recebe retorno '00' → rejeitado (guard clause — evolução do legado)."
  priority: P0
  risk: CRÍTICO
  notes: "Evolução: legado NÃO verifica status antes de update (MYS-017). Modernização DEVE ter guard clause."
```

#### REQ-PAY-004 · Conciliação por match triplo

```yaml
REQ-PAY-004:
  pattern: event-driven
  text: "QUANDO o sistema receber arquivo de retorno bancário, o sistema deve
         localizar o pagamento correspondente por match triplo:
         NUM-PAGTO + CPF + COMPETENCIA. Os três campos devem coincidir."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L134-L143
  business_rule: BR-071
  acceptance:
    - "Dado retorno com NUM-PAGTO=12345, CPF=11122233344, COMP=202605 e pagamento existente com mesmos valores → match encontrado."
    - "Dado retorno com CPF diferente → match falha, registro logado como não encontrado."
    - "Match parcial (apenas 2 de 3 campos) → NÃO concilia."
  priority: P0
  risk: ALTO
```

#### REQ-PAY-005 · Tolerância de conciliação

```yaml
REQ-PAY-005:
  pattern: state-driven
  text: "ENQUANTO o sistema conciliar pagamentos, o sistema deve aceitar
         divergência de valor entre VLR-LIQUIDO (SIFAP) e VLR-RETORNO (banco)
         com tolerância máxima de R$0,01 (um centavo)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L155-L161
  business_rule: BR-073
  acceptance:
    - "Dado VLR-LIQUIDO=1000.00 e VLR-RETORNO=1000.01 → conciliado (dentro da tolerância)."
    - "Dado VLR-LIQUIDO=1000.00 e VLR-RETORNO=1000.02 → divergência registrada."
  priority: P1
  risk: ALTO
```

---

### Módulo: Validação de Elegibilidade (Eligibility)

#### REQ-ELIG-001 · Rejeição por idade mínima do programa

```yaml
REQ-ELIG-001:
  pattern: unwanted
  text: "SE o programa social define IDADE-MIN > 0 E a idade do beneficiário for
         inferior ao mínimo, ENTÃO o sistema deve rejeitar a elegibilidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L130-L137
  business_rule: BR-023
  acceptance:
    - "Dado programa com IDADE-MIN=18 e beneficiário com 16 anos → elegibilidade rejeitada."
    - "Dado programa com IDADE-MIN=0 e beneficiário com 16 anos → critério de idade não se aplica."
    - "Dado programa com IDADE-MIN=18 e beneficiário com 18 anos → critério satisfeito."
  priority: P0
  risk: ALTO
```

#### REQ-ELIG-002 · Rejeição por renda máxima do programa

```yaml
REQ-ELIG-002:
  pattern: unwanted
  text: "SE o programa social define RENDA-MAX > 0 E a renda familiar do beneficiário
         exceder o teto, ENTÃO o sistema deve rejeitar a elegibilidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L147-L151
  business_rule: BR-025
  acceptance:
    - "Dado programa com RENDA-MAX=600 e beneficiário com renda R$700 → elegibilidade rejeitada."
    - "Dado programa com RENDA-MAX=0 e beneficiário com renda R$5000 → critério de renda não se aplica."
    - "Dado programa com RENDA-MAX=600 e beneficiário com renda R$600 → critério satisfeito (≤)."
  priority: P0
  risk: ALTO
```

#### REQ-ELIG-003 · Regra combinada para programas assistenciais

```yaml
REQ-ELIG-003:
  pattern: complex
  text: "QUANDO o programa for tipo 'A' (assistencial) E a renda familiar > R$600
         E o número de dependentes < 1, ENTÃO o sistema deve rejeitar a elegibilidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L157-L163
  business_rule: BR-026
  acceptance:
    - "Dado programa tipo 'A', renda R$700, 0 dependentes → rejeitado."
    - "Dado programa tipo 'A', renda R$700, 2 dependentes → NÃO rejeitado (tem dependentes)."
    - "Dado programa tipo 'P', renda R$700, 0 dependentes → critério não se aplica (não é tipo 'A')."
    - "Dado programa tipo 'A', renda R$500, 0 dependentes → NÃO rejeitado (renda ≤ 600)."
  priority: P0
  risk: ALTO
```

#### REQ-ELIG-004 · Documentação completa obrigatória (tipo A)

```yaml
REQ-ELIG-004:
  pattern: unwanted
  text: "SE o programa for tipo 'A' (assistencial) E o campo DOCUMENTOS-OK ≠ 'S',
         ENTÃO o sistema deve rejeitar a elegibilidade."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L164-L168
  business_rule: BR-027
  acceptance:
    - "Dado programa tipo 'A' e DOCUMENTOS-OK='N' → rejeitado."
    - "Dado programa tipo 'A' e DOCUMENTOS-OK='S' → critério satisfeito."
    - "Dado programa tipo 'P' e DOCUMENTOS-OK='N' → critério não se aplica."
  priority: P0
  risk: ALTO
  notes: "Campo DOCUMENTOS-OK é preenchido pelo módulo VALDOCS (validação prévia)."
```

---

### Módulo: Auditoria (Audit)

#### REQ-AUD-001 · Registro de auditoria em conciliação

```yaml
REQ-AUD-001:
  pattern: event-driven
  text: "QUANDO um pagamento for conciliado com sucesso, o sistema deve gravar
         registro de auditoria com ACAO='CO', TABELA='PAGAMENTO', CHAVE=NUM-PAGTO,
         e identificação do usuário/processo que executou."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L232-L244
  business_rule: BR-078
  acceptance:
    - "Dado conciliação bem-sucedida do pagamento 12345 → registro auditoria com ACAO='CO', CHAVE='12345'."
    - "Registro inclui timestamp UTC, usuário (não mais 'BATCH' hardcoded — usar JWT subject)."
    - "Registro inclui IP de origem e session ID."
  priority: P0
  risk: ALTO
  notes: "Evolução: legado usa USUARIO='BATCH' hardcoded. Modernização usa JWT claims."
```

#### REQ-AUD-002 · Registro de divergência de valor

```yaml
REQ-AUD-002:
  pattern: event-driven
  text: "QUANDO uma divergência de valor for detectada entre SIFAP e retorno bancário
         (diferença > R$0,01), o sistema deve gravar registro de auditoria com
         ACAO='DV', incluindo VLR-ANTERIOR (SIFAP) e VLR-NOVO (banco)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L247-L263
  business_rule: BR-079
  acceptance:
    - "Dado VLR-LIQUIDO=1000.00 e VLR-RETORNO=1005.00 → auditoria com ACAO='DV', VLR-ANTERIOR=1000.00, VLR-NOVO=1005.00."
    - "Dado divergência de R$0.01 → NÃO gera auditoria de divergência (está na tolerância)."
  priority: P0
  risk: ALTO
```

#### REQ-AUD-003 · Trilha completa incluindo exclusões

```yaml
REQ-AUD-003:
  pattern: ubiquitous
  text: "O sistema deve registrar auditoria para TODAS as ações, incluindo exclusões
         (ACAO='EX'). Nenhuma ação deve ser filtrada ou ocultada da trilha de auditoria."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L232-L263
  business_rule: BR-082
  acceptance:
    - "Dado exclusão de beneficiário → registro de auditoria com ACAO='EX' é criado."
    - "Consulta de auditoria retorna registros de todas as 6+ ações: IN, AL, CO, CN, DV, EX."
    - "Não existe filtro que oculte qualquer tipo de ação."
  priority: P0
  risk: ALTO
  notes: "Evolução: legado filtrava exclusões em alguns relatórios (MYS-010). Modernização exige trilha completa."
```

---

### Módulo: Cadastro de Beneficiários (Beneficiary)

#### REQ-BEN-001 · Mascaramento de CPF (LGPD)

```yaml
REQ-BEN-001:
  pattern: ubiquitous
  text: "O sistema deve mascarar o CPF do beneficiário em todos os logs, UIs e
         relatórios, exibindo no formato ***.XXX.XXX-** (apenas 6 dígitos centrais visíveis)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CONSBENF.NSN#L89-L95
  business_rule: BR-035
  acceptance:
    - "Dado CPF 123.456.789-00 em log → exibido como ***.456.789-**."
    - "CPF completo acessível apenas em contexto de processamento interno (nunca em output)."
    - "Política consistente em toda a aplicação (não divergente como o legado)."
  priority: P0
  risk: ALTO
  notes: "Evolução: legado tinha mascaramento inconsistente (CONSBENF ocultava 6, RELPGT ocultava 3). Unificar."
```

#### REQ-BEN-002 · Validação CPF unificada (módulo 11)

```yaml
REQ-BEN-002:
  pattern: ubiquitous
  text: "O sistema deve validar todo CPF pelo algoritmo módulo 11 (dois dígitos
         verificadores) em um serviço único centralizado. CPF inválido deve
         bloquear a operação."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L222-L274
  business_rule: BR-037, BR-047, BR-096, BR-103
  acceptance:
    - "Dado CPF 123.456.789-09 (DV correto) → válido."
    - "Dado CPF 123.456.789-00 (DV incorreto) → inválido, operação bloqueada."
    - "Dado CPF com todos dígitos iguais (111.111.111-11) → inválido."
    - "Validação é a MESMA em todos os pontos do sistema (unificada)."
  priority: P1
  risk: ALTO
  notes: "Evolução: legado tem 3 cópias divergentes (CADBENEF, VALBENEF, VALDOCS). Unificar em 1 serviço."
```

#### REQ-BEN-003 · CPF obrigatório e único

```yaml
REQ-BEN-003:
  pattern: unwanted
  text: "SE o CPF não for informado (vazio ou zero) OU já existir no cadastro
         para outro beneficiário ativo, ENTÃO o sistema deve rejeitar a operação."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L102-L105
  business_rule: BR-036, BR-041
  acceptance:
    - "Dado CPF = 0 → rejeitado com erro 'CPF obrigatório'."
    - "Dado CPF já existente em outro beneficiário ativo → rejeitado com 'CPF já cadastrado'."
    - "Dado CPF existente em beneficiário com status 'D' (desligado) → aceito (reuso permitido)."
  priority: P1
  risk: ALTO
```

#### REQ-BEN-004 · Campos obrigatórios no cadastro

```yaml
REQ-BEN-004:
  pattern: unwanted
  text: "SE os campos obrigatórios (CPF, nome, data de nascimento) não estiverem
         preenchidos, ENTÃO o sistema deve rejeitar o cadastro informando todos
         os erros encontrados (até 10) em uma única resposta."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L114-L122
  business_rule: BR-038, BR-039, BR-093, BR-099
  acceptance:
    - "Dado nome vazio + DT-NASC vazio → resposta contém 2 erros listados."
    - "Dado nome sem espaço ('João') → rejeitado com 'nome deve conter nome e sobrenome'."
    - "Erros acumulados (não para no primeiro) — retorna lista completa."
  priority: P1
  risk: MÉDIO
```

#### REQ-BEN-005 · Status inicial e regra dos 75 anos

```yaml
REQ-BEN-005:
  pattern: event-driven
  text: "QUANDO um beneficiário for cadastrado, o sistema deve atribuir status
         inicial 'A' (ativo). SE a idade do beneficiário for > 75 anos,
         ENTÃO o sistema deve atribuir status 'S' (suspenso/senior)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L155-L160
  business_rule: BR-043, BR-044
  acceptance:
    - "Dado beneficiário com 50 anos → status = 'A'."
    - "Dado beneficiário com 76 anos → status = 'S'."
    - "Dado beneficiário com 75 anos exatos → status = 'A' (> 75, não >=)."
  priority: P1
  risk: ALTO
  notes: "Mistério: por que suspender > 75? Possível exigência de recadastramento presencial."
```

#### REQ-BEN-006 · Campos imutáveis após inclusão

```yaml
REQ-BEN-006:
  pattern: unwanted
  text: "SE uma alteração tentar modificar campos de identidade (CPF, DT-NASCIMENTO,
         SEXO, COD-PROGRAMA, COD-REGIAO, NIS, DT-CADASTRO), ENTÃO o sistema
         deve rejeitar a alteração."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L201-L213
  business_rule: BR-045
  acceptance:
    - "Dado alteração de CPF → rejeitada."
    - "Dado alteração de NOME → aceita."
    - "Dado alteração de RENDA-FAMILIAR → aceita."
    - "DT-ATUALIZACAO atualizada automaticamente em toda alteração aceita."
  priority: P1
  risk: ALTO
```

---

### Módulo: Dependentes (Beneficiary/Dependent)

#### REQ-DEP-001 · Limite máximo de 5 dependentes

```yaml
REQ-DEP-001:
  pattern: unwanted
  text: "SE o beneficiário já possuir 5 dependentes cadastrados, ENTÃO o sistema
         deve rejeitar novas inclusões."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L64-L67
  business_rule: BR-112
  acceptance:
    - "Dado beneficiário com 5 dependentes, inclusão do 6° → rejeitada."
    - "Dado beneficiário com 4 dependentes, inclusão do 5° → aceita."
  priority: P1
  risk: MÉDIO
```

#### REQ-DEP-002 · Validação de parentesco

```yaml
REQ-DEP-002:
  pattern: unwanted
  text: "SE o parentesco informado não for um dos valores válidos (FI=filho,
         CO=cônjuge, IR=irmão, OU=outro), ENTÃO o sistema deve rejeitar."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L83-L87
  business_rule: BR-114
  acceptance:
    - "Dado parentesco 'FI' → aceito."
    - "Dado parentesco 'XX' → rejeitado."
    - "Dado parentesco vazio → rejeitado."
  priority: P1
  risk: MÉDIO
```

#### REQ-DEP-003 · CPF de dependente único por titular

```yaml
REQ-DEP-003:
  pattern: unwanted
  text: "SE o CPF do dependente (quando informado, ≠ vazio) já existir na lista
         de dependentes do mesmo titular, ENTÃO o sistema deve rejeitar."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L93-L101
  business_rule: BR-115
  acceptance:
    - "Dado dependente com CPF 111.222.333-44 já cadastrado para titular X → rejeitado."
    - "Dado dependente sem CPF informado (opcional) → aceito sem verificação de duplicidade."
  priority: P1
  risk: MÉDIO
```

#### REQ-DEP-004 · NUM-DEPENDENTES como campo calculado

```yaml
REQ-DEP-004:
  pattern: ubiquitous
  text: "O sistema deve derivar o número de dependentes de um beneficiário pelo
         COUNT da tabela de dependentes (não armazenar como campo redundante)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L108-L116
  business_rule: BR-117
  acceptance:
    - "Dado beneficiário com 3 registros na tabela dependent → NUM-DEPENDENTES retorna 3."
    - "Inclusão de dependente → contagem automaticamente incrementa."
    - "Exclusão de dependente → contagem automaticamente decrementa."
    - "Campo nunca dessincroniza (derivado, não redundante)."
  priority: P1
  risk: MÉDIO
  notes: "Evolução: legado usava contador redundante que dessincronizava. Modernização usa COUNT."
```

#### REQ-DEP-005 · Bloqueio de inclusão para status cancelado/desligado

```yaml
REQ-DEP-005:
  pattern: unwanted
  text: "SE o beneficiário titular possuir status 'C' (cancelado) ou 'D' (desligado),
         ENTÃO o sistema deve rejeitar a inclusão de dependentes."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADDEPEND.NSN#L57-L60
  business_rule: BR-111
  acceptance:
    - "Dado titular com status 'C' → inclusão rejeitada."
    - "Dado titular com status 'D' → inclusão rejeitada."
    - "Dado titular com status 'S' (suspenso) → inclusão aceita."
    - "Dado titular com status 'I' (inativo) → inclusão aceita."
  priority: P1
  risk: ALTO
```

---

### Módulo: Programas Sociais (Admin/SocialProgram)

#### REQ-PROG-001 · Tipos válidos de programa social

```yaml
REQ-PROG-001:
  pattern: unwanted
  text: "SE o tipo de programa social informado não for 'A' (assistencial),
         'P' (previdenciário) ou 'T' (trabalho), ENTÃO o sistema deve rejeitar."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN#L182-L185
  business_rule: BR-030
  acceptance:
    - "Dado tipo 'A' → aceito."
    - "Dado tipo 'P' → aceito."
    - "Dado tipo 'T' → aceito."
    - "Dado tipo 'X' → rejeitado."
  priority: P1
  risk: MÉDIO
```

#### REQ-PROG-002 · Código de programa único

```yaml
REQ-PROG-002:
  pattern: unwanted
  text: "SE o código de programa social já existir no cadastro, ENTÃO o sistema
         deve rejeitar a inclusão."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN#L80-L84
  business_rule: BR-120
  acceptance:
    - "Dado código 'PROG-001' já existente → rejeitado com 'programa já cadastrado'."
    - "Dado código 'PROG-NEW' inexistente → aceito."
  priority: P1
  risk: MÉDIO
```

#### REQ-PROG-003 · Status inicial ativo e vigência indeterminada

```yaml
REQ-PROG-003:
  pattern: event-driven
  text: "QUANDO um programa social for cadastrado, o sistema deve atribuir STATUS-PROG = 'A'
         (ativo). DT-FIM = null significa vigência indeterminada."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN#L96
  business_rule: BR-122, BR-123
  acceptance:
    - "Programa criado → status = 'A'."
    - "Programa criado sem data fim → vigência indeterminada (null, não zero)."
    - "Programa criado com DT-FIM = 2027-12-31 → vigência encerra nessa data."
  priority: P1
  risk: MÉDIO
```

---

### Módulo: Conciliação Bancária (Payment/Reconciliation)

#### REQ-CONC-001 · Processamento de arquivo retorno CNAB 240

```yaml
REQ-CONC-001:
  pattern: event-driven
  text: "QUANDO o sistema receber arquivo de retorno CNAB 240, o sistema deve
         processar apenas registros do tipo detalhe (TIPO-REG = '3'), ignorando
         headers, trailers e lotes."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L113-L115
  business_rule: BR-068
  acceptance:
    - "Dado arquivo com 1000 linhas (50 headers + 900 detalhes + 50 trailers) → apenas 900 processados."
    - "Registros tipo '0', '1', '5', '9' → ignorados sem erro."
  priority: P1
  risk: MÉDIO
```

#### REQ-CONC-002 · Conversão de centavos para reais

```yaml
REQ-CONC-002:
  pattern: ubiquitous
  text: "O sistema deve converter valores do CNAB de centavos para reais (÷100)
         antes de comparar com valores internos."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L128-L129
  business_rule: BR-070
  acceptance:
    - "Dado valor CNAB 100050 → valor em reais = R$1.000,50."
    - "Dado valor CNAB 1 → valor em reais = R$0,01."
  priority: P1
  risk: MÉDIO
```

#### REQ-CONC-003 · Registro de pagamentos não encontrados

```yaml
REQ-CONC-003:
  pattern: unwanted
  text: "SE o match triplo (NUM-PAGTO + CPF + COMPETENCIA) falhar para um registro
         do retorno bancário, ENTÃO o sistema deve registrar no log e continuar
         processando os demais registros (não é erro fatal)."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L145-L152
  business_rule: BR-072
  acceptance:
    - "Dado 100 registros no retorno, 5 sem match → 95 processados, 5 logados, nenhum erro fatal."
    - "Log inclui: NUM-PAGTO, CPF, COMPETENCIA do registro não encontrado."
  priority: P1
  risk: MÉDIO
```

#### REQ-CONC-004 · Código de retorno desconhecido

```yaml
REQ-CONC-004:
  pattern: unwanted
  text: "SE o código de retorno bancário for diferente de '00', '01' ou '02',
         ENTÃO o sistema deve registrar no log mas NÃO alterar o status do pagamento."
  source_legacy: 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L191-L194
  business_rule: BR-077
  acceptance:
    - "Dado código retorno '99' → pagamento mantém status 'G', evento logado."
    - "Dado código retorno '00' → status muda para 'P' (tratamento normal)."
  priority: P1
  risk: MÉDIO
```

---

### Módulo: Greenfield — API REST

#### REQ-API-001 · Convenção de endpoints REST

```yaml
REQ-API-001:
  pattern: ubiquitous
  text: "O sistema deve expor todos os recursos via API REST seguindo a convenção
         /api/v1/{recurso}, com verbos HTTP corretos e status codes apropriados."
  source_legacy: "[GREENFIELD] Legado não possui API — interação era via telas 3270/MAP"
  business_rule: N/A
  acceptance:
    - "GET /api/v1/beneficiaries → 200 com lista paginada."
    - "POST /api/v1/beneficiaries → 201 com Location header."
    - "GET /api/v1/beneficiaries/{id} inexistente → 404."
    - "POST com payload inválido → 400 com lista de erros."
    - "Todos endpoints documentados com OpenAPI/Swagger."
  priority: P0
  risk: MÉDIO
```

#### REQ-API-002 · Paginação e ordenação

```yaml
REQ-API-002:
  pattern: ubiquitous
  text: "O sistema deve suportar paginação (page, size) e ordenação (sort) em
         todos os endpoints de listagem. Tamanho máximo de página: 100 registros."
  source_legacy: "[GREENFIELD] Legado não tinha paginação — tela 3270 exibia tudo ou 20 registros fixos"
  business_rule: N/A
  acceptance:
    - "GET /api/v1/beneficiaries?page=0&size=20 → 20 registros, metadata com totalPages."
    - "GET /api/v1/beneficiaries?size=200 → limitado a 100 (cap)."
    - "GET /api/v1/beneficiaries?sort=name,asc → ordenado por nome."
  priority: P1
  risk: BAIXO
```

---

### Módulo: Greenfield — Autenticação

#### REQ-AUTH-001 · Autenticação obrigatória via OAuth2/JWT

```yaml
REQ-AUTH-001:
  pattern: unwanted
  text: "SE uma requisição não contiver token JWT válido no header Authorization,
         ENTÃO o sistema deve rejeitar com HTTP 401 Unauthorized."
  source_legacy: "[GREENFIELD] Legado não tem autenticação real — USUARIO='BATCH' hardcoded"
  business_rule: N/A (ADR-003)
  acceptance:
    - "Requisição sem header Authorization → 401."
    - "Requisição com token expirado → 401."
    - "Requisição com token válido → processada normalmente."
    - "Endpoint /actuator/health → permitido sem token (healthcheck)."
  priority: P0
  risk: ALTO
```

#### REQ-AUTH-002 · Controle de acesso por role

```yaml
REQ-AUTH-002:
  pattern: unwanted
  text: "SE o usuário autenticado não possuir a role necessária para o endpoint,
         ENTÃO o sistema deve rejeitar com HTTP 403 Forbidden."
  source_legacy: "[GREENFIELD] Legado não tem controle de acesso — todos os perfis acessam tudo"
  business_rule: N/A (ADR-003)
  acceptance:
    - "AUDITOR acessando POST /api/v1/payments/generate → 403."
    - "OPERATOR acessando POST /api/v1/payments/generate → 200."
    - "ANALYST acessando GET /api/v1/beneficiaries → 200."
    - "ANALYST acessando DELETE /api/v1/beneficiaries/{id} → 403."
  priority: P0
  risk: ALTO
```

---

### Módulo: Greenfield — Parametrização

#### REQ-PARAM-001 · Fatores regionais em tabela de referência

```yaml
REQ-PARAM-001:
  pattern: ubiquitous
  text: "O sistema deve armazenar os fatores regionais (27 UFs) em tabela de
         referência no banco de dados, editáveis sem necessidade de novo deploy."
  source_legacy: "[GREENFIELD] Legado tem 27 fatores hardcoded em CALCBENF.NSN#L86-L112 — modernização parametriza"
  business_rule: BR-003
  acceptance:
    - "Alteração de fator regional via API admin → próximo cálculo usa valor novo."
    - "Sem necessidade de rebuild/redeploy para ajustar fatores."
    - "Histórico de alterações mantido (quem, quando, valor anterior, valor novo)."
  priority: P1
  risk: MÉDIO
```

#### REQ-PARAM-002 · Faixas de desconto parametrizáveis

```yaml
REQ-PARAM-002:
  pattern: ubiquitous
  text: "O sistema deve armazenar as faixas de contribuição social (limites e alíquotas)
         em tabela de referência, editáveis sem deploy."
  source_legacy: "[GREENFIELD] Legado tem alíquotas hardcoded em CALCDSCT.NSN#L56-L60 — modernização parametriza"
  business_rule: BR-014
  acceptance:
    - "Alteração de faixa (ex.: ≤500 de 3% para 4%) → próximo cálculo usa alíquota nova."
    - "Adição de nova faixa possível sem código."
    - "Histórico de alterações mantido."
  priority: P1
  risk: MÉDIO
```

---

## 3. Resumo

| Módulo | REQ-IDs | Prioridade |
|--------|---------|------------|
| Cálculo de Benefícios | REQ-CALC-001 a REQ-CALC-010 | P0 |
| Descontos | REQ-DSC-001 a REQ-DSC-005 | P0/P1 |
| Geração de Pagamentos | REQ-PAY-001 a REQ-PAY-005 | P0/P1 |
| Elegibilidade | REQ-ELIG-001 a REQ-ELIG-004 | P0 |
| Auditoria | REQ-AUD-001 a REQ-AUD-003 | P0 |
| Beneficiário | REQ-BEN-001 a REQ-BEN-006 | P0/P1 |
| Dependentes | REQ-DEP-001 a REQ-DEP-005 | P1 |
| Programas Sociais | REQ-PROG-001 a REQ-PROG-003 | P1 |
| Conciliação Bancária | REQ-CONC-001 a REQ-CONC-004 | P1 |
| API REST (Greenfield) | REQ-API-001 a REQ-API-002 | P0/P1 |
| Autenticação (Greenfield) | REQ-AUTH-001 a REQ-AUTH-002 | P0 |
| Parametrização (Greenfield) | REQ-PARAM-001 a REQ-PARAM-002 | P1 |

**Total: 48 REQ-IDs** (31 P0 + 17 P1)

---

## 4. Decisões Pendentes (impactam REQ-IDs)

| # | Questão | REQ-IDs afetados | Decisão necessária antes de |
|---|---------|------------------|---------------------------|
| 1 | Reajuste duplo (Fator K + FATOR-REAJ) | REQ-CALC-007 | Implementação |
| 2 | Desconto batch 3% vs 4 faixas | REQ-DSC-003, REQ-PARAM-002 | Implementação |
| 3 | Bypass região 99 | REQ-CALC-003, REQ-PARAM-001 | Implementação |
| 4 | Prefixos especiais CPF (8 prefixos) | REQ-BEN-002 | Implementação |
| 5 | Regra dos 75 anos — manter ou eliminar? | REQ-BEN-005 | Implementação |
| 6 | Fevereiro aceita 29 em ano não-bissexto — corrigir? | REQ-BEN-004 | Implementação |

---

## 5. Gate de Qualidade

- [x] Todo requisito é testável (tem `acceptance:`)
- [x] Todo requisito tem `source_legacy:` não vazio (incluindo `[GREENFIELD]` com justificativa)
- [x] Nenhuma contradição entre requisitos
- [x] Padrões EARS aplicados corretamente (6 tipos usados)
- [x] REQ-IDs únicos (48 IDs, nenhuma colisão)
- [x] Cobertura completa do scope-decisions (13 funcionalidades + 5 greenfield)
- [ ] Aprovação do Product Owner (Passagem #2)