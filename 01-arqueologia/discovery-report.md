<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Relatório de Descoberta — Estágio 1: Arqueologia Digital

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **discovery-report**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Este documento consolida todas as descobertas do Estágio 1.
> Preencha cada seção com as conclusões do time. **Este é o input principal do Estágio 2** — sem ele, a especificação vira chute.

**Time**: DataCorp Verde 01
**Data**: 20/05/2026
**Edição**: 1.0
**Participantes**: Equipe completa (5 pares, 10 personas)

---

## 1. Sumário Executivo

O SIFAP é um sistema de gestão de pagamentos de programas sociais desenvolvido em Natural/Adabas desde 1997. Processa mensalmente benefícios para populações vulneráveis, incluindo cálculos com fatores regionais, familiares e etários, descontos progressivos, 13° salário, abono natalino e correção monetária retroativa. O código contém 29 anos de evolução incremental por pelo menos 8 desenvolvedores, com duplicação massiva de lógica entre programas (CALCBENF e BATCHPGT compartilham a mesma fórmula copiada), validações inconsistentes entre módulos, e pelo menos 23 mistérios não documentados. A criticidade é ALTA: erros impactam diretamente o sustento de beneficiários.

---

## 2. Visão Geral do Sistema

### 2.1 Propósito do SIFAP

Sistema de pagamento de benefícios sociais que gerencia o ciclo completo: cadastro de beneficiários e programas sociais → validação de elegibilidade → cálculo de benefícios → geração em lote de pagamentos mensais → conciliação bancária (CNAB 240) → correção monetária retroativa → relatórios gerenciais e de auditoria.

### 2.2 Arquitetura Legada

- **15 programas Natural** (.NSN) organizados em 6 famílias: BATCH (3), CAD (3), CALC (3), VAL (3), REL (2), CONS (1)
- **4 arquivos Adabas** (DDMs): BENEFICIARIO (hub, 11 programas acessam), PAGAMENTO (9 programas), PROGRAMA-SOCIAL (4 programas), AUDITORIA (2 programas)
- **Zero CALLNAT, Zero INCLUDE**: todos os programas são standalone — acoplamento é apenas via DDMs compartilhados
- **Interação**: telas 3270 (MAP) para online; WORK FILEs para batch; PRINT para relatórios (66 lin/pag)
- **Integrações**: Banco do Brasil (CNAB 240) para pagamento; Banco Real (código morto desde 2007)

### 2.3 Usuários e Perfis

Identificados no código (sem controle de acesso explícito):
- **Operador de cadastro** — usa CADBENEF, CADDEPEND, CADPROG, VALDOCS
- **Analista de benefícios** — usa CONSBENF, VALELEG, CALCBENF, CALCDSCT
- **Operador batch** — executa BATCHPGT (1° dia útil), BATCHCON (após retorno bancário), BATCHREL (mensal)
- **Auditor** — usa RELAUDIT, RELPGT
- **Administrador** — CALCCORR (correção retroativa, input manual de CPF/período)
- **Nota**: USUARIO='BATCH' hardcoded na auditoria — sem autenticação real

---

## 3. Principais Descobertas

### 3.1 Regras de Negócio Críticas

1. **Fórmula de benefício** (BR-003 a BR-008, BR-059): VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RND × FATOR-IDADE × (1+FATOR-REAJ). Truncado 2 decimais. Coração financeiro do sistema.
2. **Dezembro especial** (BR-009, BR-010, BR-060, BR-061): 13° salário + abono 15% para tipo 'A'. Muda completamente o valor pago em dezembro.
3. **Exceção judicial** (BR-015): Desconto tipo 'J' ignora teto de 30%. Única exceção ao cap. Risco legal se removida.
4. **Fator K** (BR-121): Constante 0.347215 aplicada ao VLR-BASE na inclusão do programa. Combinado com reajuste mensal = reajuste duplo. Sem documentação da origem.
5. **Ciclo de vida pagamento** (BR-064, BR-074-BR-076, BR-082): G→P/D/E sem transição reversa. Batch não verifica status atual antes de update (MYS-017).

### 3.2 Dependências Complexas

- **BENEFICIARIO é o hub central** — 11 de 15 programas acessam. Qualquer mudança no schema impacta quase tudo.
- **CALCBENF ↔ BATCHPGT**: lógica duplicada (7 regras idênticas). Manutenção requer sincronização manual.
- **VALELEG depende de VALDOCS**: campo DOCUMENTOS-OK preenchido por VALDOCS é pré-requisito para elegibilidade tipo 'A'.
- **Sistemas downstream dependem da ordenação por CPF** do BATCHPGT (comentário explícito no código).
- **NUM-DEPENDENTES é redundante** mas CALCBENF/BATCHPGT dependem dele para o fator familiar — se PE group e contador dessincronizarem, cálculos erram.

### 3.3 Dívida Técnica Identificada

- [x] **Duplicação massiva**: fórmula de cálculo copiada entre CALCBENF e BATCHPGT (7 regras idênticas)
- [x] **Algoritmo CPF em 3 cópias**: CADBENEF, VALBENEF, VALDOCS — cada uma com variantes ligeiramente diferentes
- [x] **Tabelas hardcoded**: 27 fatores regionais, 5 faixas de renda, IPCA mensal, 8 prefixos especiais, 27 UFs — tudo embutido no código
- [x] **Desconto divergente**: CALCDSCT (4 faixas progressivas, 6 tipos) nunca é chamado pelo BATCHPGT que usa 3% fixo
- [x] **Mascaramento CPF inconsistente**: CONSBENF oculta 6 dígitos, RELPGT oculta apenas 3
- [x] **Sequências sem lock**: NUM-PAGTO e SEQ-AUDIT incrementados sem transação atômica
- [x] **Programa CALCCORR congelado**: tabela IPCA parada em 2012, última carga 2014

### 3.4 Gaps de Documentação

O documento `REGRAS-NEGOCIO-2012.md` cobre apenas ~25% das regras reais. Gaps principais:
- Fator K (constante 0.347215) — completamente ausente
- Exceção judicial ao teto 30% — não mencionada
- Regra dos 75 anos (status 'S' automático) — não mencionada
- Bypass região 99 — citada como "bypass do Roberto" sem explicação
- Reajuste duplo (Fator K + cálculo mensal) — não mencionado
- Tipo pagamento 'T' (terceiro) — referenciado em RELPGT mas nunca gerado
- 13° salário e abono — marcados como "PENDENTE" no doc mas implementados desde 2009

---

## 4. Mistérios e Riscos

### 4.1 Mistérios Não Resolvidos

| ID | Descrição | Risco para Migração |
| --- | --------- | ------------------- |
| MYS-003 | Constante 0.347215 (Fator K) sem origem documentada | CRÍTICO — valor base errado se perdido |
| MYS-005 | Truncamento vs arredondamento inconsistente | ALTO — divergências em auditoria comparativa |
| MYS-008 | Bypass região 99 — sem justificativa formal | ALTO — remover pode bloquear caso legítimo; manter é risco de segurança |
| MYS-010 | Exclusões ocultadas da auditoria | ALTO — compliance moderno exige trilha completa |
| MYS-014 | Desconto batch (3%) ≠ online (4 faixas progressivas) | CRÍTICO — beneficiários recebem valores diferentes |
| MYS-017 | Conciliação sem guard clause de status | ALTO — risco em processamento concorrente |
| MYS-011 | Tabela IPCA congelada (2012) | MÉDIO — programa possivelmente abandonado |

### 4.2 Riscos para o Estágio 2

1. **Reajuste duplo não intencional**: Fator K aplica FATOR-REAJ ao VLR-BASE e depois CALCBENF aplica novamente. Se a modernização unificar, valores mudam. Precisa validar com negócio se é intencional.
2. **Unificação de descontos**: Batch usa 3% fixo, CALCDSCT usa 4 faixas. Qual é a verdade? Qual manter na modernização?
3. **Bypass de segurança**: 8 prefixos de CPF + região 99 contornam validações. Remover pode quebrar fluxos legítimos; manter expõe o sistema.
4. **Ordenação como contrato**: Sistemas downstream dependem de output ordenado por CPF. Modernização precisa manter ou migrar consumidores.
5. **Campos sem validação**: DT-NASC-DEP, CPF-DEP (no módulo 11), SEXO-DEP em CADDEPEND; tipo programa em CADPROG — dados sujos no legado.

---

## 5. Recomendações

### 5.1 O que migrar primeiro

| Prioridade | Funcionalidade | Justificativa |
| ---------- | -------------- | ------------- |
| 1 | Cadastro de beneficiários (CADBENEF + VALBENEF + VALDOCS) | Entidade central; 11 programas dependem. Base para todo o resto. |
| 2 | Cálculo de benefícios (CALCBENF + CALCDSCT) | Core financeiro; unificar lógica duplicada elimina principal dívida técnica. |
| 3 | Geração de pagamentos (BATCHPGT) | Processo crítico mensal; eliminar duplicação com CALCBENF. |
| 4 | Conciliação bancária (BATCHCON) | Integração bancária modernizada (API PIX/SPB vs CNAB flat file). |
| 5 | Elegibilidade (VALELEG) | Regras complexas que beneficiam de parametrização moderna. |

### 5.2 O que descartar

- **Integração Banco Real** (BATCHCON código morto): banco não existe desde 2007
- **Correção Plano Verão** (CALCCORR código comentado): política dos anos 90, irrelevante
- **Tipo pagamento 'T'** (RELPGT): nunca gerado pelo batch, possivelmente planejado e abandonado
- **MAP screens 3270** (CONSBENF): substituídas por UI web moderna
- **Paginação 66 linhas** (todos relatórios): substituída por PDF/export

### 5.3 O que evoluir

- **Mascaramento CPF**: unificar política (LGPD compliance), ocultar mais dígitos, consistente em toda aplicação
- **Auditoria**: incluir exclusões (remover filtro 'EX'), adicionar IP/sessão, JWT claims
- **Descontos**: unificar em engine única com faixas parametrizáveis (eliminar divergência batch/online)
- **Tabela IPCA**: carga dinâmica via API do IBGE em vez de hardcoded
- **Fatores regionais**: tabela de referência no banco em vez de array hardcoded em cada programa
- **Validação idade**: usar data completa (ano+mês+dia) em vez de apenas ano
- **Guard clauses**: status machine formal para pagamentos (G→P/D/E com transições permitidas)

---

## 6. Métricas do Estágio

| Métrica                       | Valor        |
| ----------------------------- | ------------ |
| Programas analisados          | **15** / 15  |
| DDMs mapeados                 | **4** / 4    |
| Regras de negócio encontradas | **150**      |
| Regras escondidas encontradas | **10** / 10  |
| Easter eggs encontrados       | **3** / 3    |
| Termos no glossário           | **45**       |
| Mistérios catalogados         | **23**       |
| Tempo total gasto             | ~3 horas     |

---

## 7. Notas para o Próximo Estágio

**Para o @architect e as personas do Estágio 2:**

1. **Toda EARS DEVE ter `source_legacy:`** — este relatório e o `business-rules-catalog.md` fornecem as 150 regras com linhas exatas dos programas .NSN.
2. **Não assuma que o doc 2012 está correto** — ele tem 75% de gaps. Use o catálogo de regras (BR-001 a BR-150) como fonte de verdade.
3. **Decisão obrigatória sobre reajuste duplo** — antes de especificar CALCBENF moderno, valide com negócio se Fator K + (1+FATOR-REAJ) mensal é intencional ou bug acumulado.
4. **Decisão obrigatória sobre descontos** — CALCDSCT (4 faixas, 6 tipos) vs BATCHPGT (3% fixo). Qual é a regra correta? São contextos diferentes?
5. **Região 99 e prefixos especiais** — documentar formalmente como exceções controladas ou eliminar. Não manter como está.
6. **Dependentes**: migrar de PE group (desnormalizado) para tabela separada. NUM-DEPENDENTES vira campo calculado.
7. **Ordenação CPF**: verificar se sistemas downstream ainda existem antes de manter como requisito.

---

## Definição de Pronto deste relatório

- [x] Todas as seções acima preenchidas (sem placeholders).
- [x] Pelo menos 5 regras críticas listadas em §3.1, cada uma referenciando uma `BR-XXX` do catálogo.
- [x] Decisões de migrar/descartar/evoluir em §5 cobrem as 8+ funcionalidades principais.
- [x] Métricas de §6 conferem com os outros artefatos (glossary.md, business-rules-catalog.md, mysteries-found.md).


---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="mysteries-found.md"><strong>mysteries-found.md</strong></a><br/>
<sub>Lista de mistérios.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="../02-spec-moderna/GUIDE.md"><strong>Estágio 2 — Spec</strong></a><br/>
<sub>Próximo estágio: spec moderna.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>

