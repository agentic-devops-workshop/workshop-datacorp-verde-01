---
title: "Glossário SIFAP — Termos do Legado"
description: "Vocabulário ubíquo extraído dos programas Natural e DDMs Adabas para alimentar EARS, ADRs e código moderno."
author: "Par 5 · Operações (Tech Writer) — consolidado pelo @archaeologist"
date: "2026-05-20"
version: "0.1.0"
status: "draft"
stage: "01-arqueologia"
sources_analyzed:
  - 01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN
  - 01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN
tags: ["glossary", "ubiquitous-language", "stage-1", "sifap"]
---

<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD034 MD040 -->

# Glossário SIFAP — Estágio 1 (Arqueologia)

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![ARTEFATO Passagem 1](https://img.shields.io/badge/ARTEFATO-Passagem%201-1A1A1A?style=for-the-badge) ![META ≥ 30 termos](https://img.shields.io/badge/META-≥%2030%20termos-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **glossary**

> **Para quem é isto?** Para todo o time, mas especialmente Par 1 (Visão) e Par 2 (Arquitetura) no Estágio 2 — cada termo aqui vira sujeito ou objeto de EARS.
>
> **Fonte:** termos extraídos linha a linha dos programas Natural lidos até agora. Toda entrada carrega `legacy source` quando veio de código. Termos sem fonte legada são marcados `[GREENFIELD]`.

> ⚠️ **Status:** rascunho parcial — alimentado por **2 dos 15 programas** (BATCHCON, BATCHPGT). Os outros 13 programas e 4 DDMs adicionarão termos. Não fechar a Passagem #1 sem ≥ 30 termos consolidados.

---

## Como ler uma entrada

```text
**Termo** (`SIGLA-LEGADO` se houver)
: Definição em 1–2 frases na voz do negócio.
  - **Tipo:** entidade · atributo · evento · regra · código de domínio
  - **legacy source:** `caminho/ARQUIVO.NSN#L<ini>-L<fim>` ou `[GREENFIELD]`
  - **Notas:** ambiguidades, sinônimos, decisões pendentes
```

---

## A

**Abono Natalino**
: Bônus de 15% sobre o valor base do benefício, pago em dezembro **apenas** para beneficiários cujo `PROGRAMA-SOCIAL.TIPO = 'A'` (assistencial).
- **Tipo:** regra de cálculo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L296-L301`
- **Notas:** Regra não documentada nos manuais; descoberta apenas no código. Candidata a `MYS-PGT-06`.

**Ação (de Auditoria)** (`AUDITORIA.ACAO`)
: Código de 2 letras que identifica o tipo de evento auditado (`CO` = conciliado, `DV` = divergência, …).
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L218, L233`
- **Notas:** Catálogo completo de códigos ainda não mapeado — depende de leitura de `RELAUDIT.NSN`.

**Adabas**
: SGBD não-relacional da Software AG usado pelo SIFAP desde 1997. Armazena dados em "arquivos" numerados (150, 155, 160, 170) com campos `MU` (multi-valor) e `PE` (grupo periódico).
- **Tipo:** plataforma
- **legacy source:** `[GREENFIELD]` (termo de infraestrutura — alvo de migração para PostgreSQL 16)

**Arquivo 150 / 155 / 160 / 170**
: Numeração interna dos arquivos Adabas — respectivamente `BENEFICIARIO`, `PROGRAMA-SOCIAL`, `PAGAMENTO`, `AUDITORIA`.
- **Tipo:** identificador físico
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L11` (header), `BATCHCON.NSN#L11`

**Auditoria** (`AUDITORIA`)
: Registro append-only de eventos relevantes (conciliação, divergência, alteração de pagamento). Cada evento carrega usuário, data/hora, tabela referenciada, chave, valor anterior e novo.
- **Tipo:** entidade
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L25-L35`
- **Notas:** Sequência (`SEQ-AUDIT`) é gerada por `READ ... DESCENDING + ESCAPE BOTTOM` — sem identity nativa.

## B

**Banco do Brasil (BB)**
: Único banco efetivamente integrado ao SIFAP em produção. `COD-BANCO = 1` hardcoded no fonte.
- **Tipo:** parceiro externo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L156`
- **Notas:** Campo `COD-BANCO` é `N3` (suporta 999) mas só `1` é gravado.

**Banco Real (descontinuado)**
: Integração CNAB com layout próprio (CPF em pos. 30-43, valor em 100-112). Banco adquirido pelo Santander em 2007; código permanece comentado no fonte.
- **Tipo:** parceiro externo (histórico)
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L199-L213`
- **Notas:** Pode haver dados históricos pré-2007 ainda nesse layout. Tratar em ADR de migração.

**Batch Mensal**
: Processamento crítico executado no **1º dia útil do mês** que gera pagamentos para todos os beneficiários ativos.
- **Tipo:** evento de processo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L9` (header)

**Beneficiário** (`BENEFICIARIO`)
: Pessoa física cadastrada para receber pagamento de um programa social. Chave: CPF.
- **Tipo:** entidade central
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L14-L23`

## C

**CNAB 240**
: Layout bancário FEBRABAN de 240 caracteres por linha usado pelo Banco do Brasil para retorno de pagamentos. Registro tipo `3` é detalhe.
- **Tipo:** formato de integração
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L37-L46`

**Código de Retorno** (`COD-RETORNO`)
: Código de 2 caracteres devolvido pelo banco indicando o resultado do pagamento: `00` pago, `01` devolvido, `02` erro. Demais códigos são logados mas **não atualizam status** (bug histórico).
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L152-L180`

**Competência**
: Mês/ano de referência do pagamento no formato `AAAAMM` (ex.: `202605`). Calculado a partir de `*DATN` na execução do batch.
- **Tipo:** atributo temporal
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L104-L106`

**Conciliação Bancária**
: Processo que cruza pagamentos gerados (`STATUS = 'G'`) com o retorno CNAB, atualizando para `P/D/E` conforme o código bancário e gerando registros de auditoria.
- **Tipo:** processo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L60-L195`

**CPF**
: Identificador do beneficiário (11 dígitos). Chave de leitura ordenada em `BATCHPGT` — ordenação que outros sistemas downstream dependem.
- **Tipo:** atributo / chave natural
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L175-L178`

## D

**Dependentes** (`NUM-DEPENDENTES`)
: Quantidade de dependentes do beneficiário; entra no cálculo do **Fator Familiar**.
- **Tipo:** atributo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L19, L246-L256`

**Devolvido (D)**
: Status de pagamento atribuído quando o banco retorna código `01` — fundos não creditados.
- **Tipo:** estado
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L162-L168`

**Divergência**
: Diferença entre valor SIFAP e valor pago pelo banco maior que **R$ 0,01** (tolerância). Gera evento `DV` em `AUDITORIA`.
- **Tipo:** evento
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L136-L150`

## E

**Erro (E)**
: Status de pagamento atribuído quando o banco retorna código `02`.
- **Tipo:** estado
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L169-L175`

## F

**Faixa de Renda**
: 5 faixas de renda familiar com fatores multiplicativos decrescentes (0,40 a 1,00). Quanto maior a renda, menor o fator.
- **Tipo:** tabela de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L153-L161`

**Fator Familiar**
: Multiplicador derivado do número de dependentes (escalonado: +5%/dep até 2, +3%/dep até 4, +2%/dep acima).
- **Tipo:** regra de cálculo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L246-L256`

**Fator Idade**
: Multiplicador por faixa etária (1,15 para ≥65; 1,10 para ≥60; 1,05 para <18; 1,00 caso contrário).
- **Tipo:** regra de cálculo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L265-L275`

**Fator Reajuste** (`FATOR-REAJUSTE`)
: Multiplicador anual cadastrado no `PROGRAMA-SOCIAL`. Aplicado ao final do cálculo: `VLR × (1 + FATOR_REAJ)`.
- **Tipo:** atributo de programa
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L38, L281`

**Fator Regional**
: Multiplicador de 27 posições (1,00 a 1,40) indexado por `COD-REGIAO`. Tabela **duplicada** entre BATCHPGT e CALCBENF.
- **Tipo:** tabela de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L120-L148`

## G

**Gerado (G)**
: Status inicial do pagamento ao ser criado por `BATCHPGT`. Aguarda conciliação para virar `P/D/E`.
- **Tipo:** estado
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L334`

## I

**Idempotência Mensal**
: Garantia de que `BATCHPGT` não gera segundo pagamento se já existir pagamento com mesma `COMPETENCIA` para o CPF.
- **Tipo:** regra de negócio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L196-L204`

## L

**Layout BB**
: Posicionamento dos campos no CNAB 240 do Banco do Brasil (CPF em 44-54, valor em 120-134, data em 140-147, cód. retorno em 231-232).
- **Tipo:** mapeamento de integração
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L101-L108`

## N

**NIS**
: Número de Identificação Social do beneficiário (11 dígitos). Atributo presente mas não usado nas regras de cálculo lidas até agora.
- **Tipo:** atributo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L23`
- **Notas:** Confirmar uso em `CADBENEF.NSN` (Par 1).

## P

**Pagamento** (`PAGAMENTO`)
: Registro mensal de valor devido a um beneficiário. Atributos-chave: `NUM-PAGTO`, `CPF-BENEF`, `COMPETENCIA`, `VLR-BRUTO`, `VLR-DESCONTO`, `VLR-LIQUIDO`, `STATUS-PGTO`, `TIPO-PGTO`.
- **Tipo:** entidade central
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L25-L35`

**Pago (P)**
: Status final positivo — banco confirmou crédito com código `00`. Grava `DT-PAGAMENTO` e `COD-BANCO`.
- **Tipo:** estado
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L153-L161`

**Programa Social** (`PROGRAMA-SOCIAL`)
: Catálogo de programas pagáveis. Tem `TIPO` (`A` = assistencial), `VLR-BASE`, `FATOR-REAJUSTE`, `RENDA-MAX` e `STATUS-PROG`.
- **Tipo:** entidade
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L37-L43`

## R

**Registro Detalhe (CNAB tipo 3)**
: Linha do arquivo CNAB que carrega dados de pagamento individual. Outros tipos (cabeçalho, trailer) são ignorados.
- **Tipo:** registro de integração
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L97-L100`

**Renda Familiar** (`RENDA-FAMILIAR`)
: Renda mensal declarada da família. Entra na função **Faixa de Renda** para definir o fator multiplicativo.
- **Tipo:** atributo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L18, L238`

## S

**SEQ-AUDIT**
: Número sequencial monotônico de eventos de auditoria. Obtido por `READ ... DESCENDING + ESCAPE BOTTOM` no início do batch e incrementado em memória.
- **Tipo:** atributo / chave
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L82-L85`
- **Notas:** Sem garantia de unicidade sob concorrência — risco em ambiente moderno multi-instância.

**Status do Beneficiário** (`STATUS`)
: Letra única indicando se beneficiário é ativo (`A`). Apenas ativos entram no batch.
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L193`

**Status do Pagamento** (`STATUS-PGTO`)
: Máquina de estados: `G` (gerado) → `P` (pago) | `D` (devolvido) | `E` (erro). Sem documentação de transições inválidas no legado.
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L334`, `BATCHCON.NSN#L153-L175`

**Status do Programa** (`STATUS-PROG`)
: Letra única indicando se programa está ativo (`A`). Programas inativos não geram pagamento.
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L225`

## T

**Tipo de Pagamento** (`TIPO-PGTO`)
: `N` = normal · `D` = dezembro (com 13º). Atribuído por `BATCHPGT`.
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L289, L292`

**Tipo de Programa** (`PROGRAMA-SOCIAL.TIPO`)
: `A` = assistencial (recebe abono natalino) · demais tipos não documentados nos programas lidos.
- **Tipo:** código de domínio
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L296`

**Tolerância de Centavo**
: Diferença até **R$ 0,01** entre valor SIFAP e valor banco é aceita sem gerar divergência. Origem regulatória **não documentada** (`MYS-CON-03`).
- **Tipo:** regra de cálculo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L139`

**Truncamento (vs. arredondamento)**
: `BATCHPGT` trunca valores a 2 casas decimais via `(#VLR * 100) / 100` em vez de arredondar. `BATCHREL` arredonda → relatórios divergem da folha.
- **Tipo:** convenção numérica
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L283-L286`

## V

**Valor Base** (`VLR-BASE`)
: Valor de partida do benefício cadastrado em `PROGRAMA-SOCIAL`. Antes da aplicação de fatores.
- **Tipo:** atributo
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L40`

**Valor Bruto** (`VLR-BRUTO`)
: Valor calculado após fatores e antes do desconto. Em dezembro inclui 13º e abono.
- **Tipo:** atributo monetário
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L288, L300`

**Valor Desconto** (`VLR-DESCONTO`)
: Desconto simplificado de 3% sobre o bruto quando bruto > R$ 500 (lógica completa está em `CALCDSCT.NSN`, ainda não lido).
- **Tipo:** atributo monetário
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L306-L312`

**Valor Líquido** (`VLR-LIQUIDO`)
: Bruto menos desconto, nunca negativo (`IF #VLR-LIQ < 0 MOVE 0`).
- **Tipo:** atributo monetário
- **legacy source:** `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L315-L321`

---

## Termos pendentes (a preencher quando outros programas forem lidos)

| Termo candidato | Programa que deve fornecer | Par responsável |
| --- | --- | --- |
| Dependente (cadastro completo) | `CADDEPEND.NSN` | Par 1 |
| Validação CPF (mod-11) | `VALDOCS.NSN`, `VALBENEF.NSN` | Par 4 |
| Elegibilidade | `VALELEG.NSN` | Par 4 |
| Correção Monetária | `CALCCORR.NSN` | Par 3 |
| Desconto Judicial / IR / Sindical | `CALCDSCT.NSN` | Par 3 |
| Relatório de Pagamentos | `RELPGT.NSN`, `BATCHREL.NSN` | Par 2 / Par 5 |
| Consulta Beneficiário | `CONSBENF.NSN` | Par 5 |
| Auditoria de Acesso | `RELAUDIT.NSN` | Par 5 |
| Campos `MU` / `PE` (Adabas) | DDMs | Par 4 |

---

## Notas de processo

- **Cobertura atual:** ~40 termos a partir de 2 programas. Meta da Passagem #1 (≥30) **atingida**, mas o glossário **não** é considerado pronto até os 15 `.NSN` + 4 `.ddm` serem lidos.
- **Voz:** padronização final fica com **Par 5 · Tech Writer** antes do H1.
- **Conflitos pendentes** (resolver com Par 1 · PO):
  - Truncamento (BATCHPGT) vs. arredondamento (BATCHREL) — qual é a verdade financeira?
  - Desconto inline 3% (BATCHPGT) vs. lógica completa (CALCDSCT) — qual prevalece?
  - Códigos de retorno bancário só mapeados para `00/01/02` — comportamento para os demais é bug ou regra?

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="LEGACY-EXPLORATION-CHECKLIST.md"><strong>Checklist de Exploração</strong></a><br/>
<sub>HARD GATE antes do Estágio 2.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="business-rules-catalog.md"><strong>Catálogo de Regras</strong></a><br/>
<sub>BRs com Programa Fonte.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>