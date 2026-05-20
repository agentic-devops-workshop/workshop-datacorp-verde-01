<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Mistérios Encontrados — SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **mysteries-found**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Registre aqui toda lógica, comportamento ou código que o time não conseguiu explicar.
> "Mistérios" são trechos de código sem documentação, com lógica não-óbvia ou que parecem workarounds.
>
> **Cota mínima para passar pelo portão do Estágio 2:** 5 mistérios documentados.

## O que conta como "mistério"?

- Código que faz algo inesperado sem comentário explicando por quê
- Valores hardcoded sem explicação (números mágicos)
- Lógica condicional que parece um workaround ou gambiarra
- Campos no DDM que não são usados por nenhum programa
- Programas que existem mas não são chamados por ninguém
- Comportamento diferente entre o que a documentação diz e o que o código faz
- Easter eggs deixados pelos desenvolvedores originais

## Níveis de Confiança

| Nível     | Significado                                         |
| --------- | --------------------------------------------------- |
| **ALTA**  | Temos certeza de que há algo estranho aqui          |
| **MÉDIA** | Parece suspeito, mas pode ter explicação            |
| **BAIXA** | Pode ser intencional, mas não conseguimos confirmar |

## Mistérios Catalogados

| ID      | Descrição | Onde Encontrado | Impacto Potencial | Confiança |
| ------- | --------- | --------------- | ----------------- | --------- |
| MYS-001 | Status 'S' automático para idade > 75 anos | CADBENEF.NSN:L159-L160 | Alto — beneficiários perdem acesso sem aviso | ALTA |
| MYS-002 | Limite 5 dependentes vs PE group que suporta 99 | CADDEPEND.NSN:L64-L67 | Baixo — restrição de negócio OK, DDM é genérico | MÉDIA |
| MYS-003 | Constante 0.347215 no "Fator K" sem documentação | CADPROG.NSN:L87-L88 | Crítico — se perder na migração, valores base ficam errados | ALTA |
| MYS-004 | Dezembro: 13° + abono 15% mudam toda a fórmula | CALCBENF.NSN:L145-L175, BATCHPGT.NSN:L292-L302 | Alto — replicado em 2 programas; se divergirem, pagamentos errados | ALTA |
| MYS-005 | Truncamento (×100, int, ÷100) causa perda de centavos | CALCBENF.NSN:L132-L134, BATCHPGT.NSN:L283-L285 | Médio — perda sistemática; relatório arredonda diferente | ALTA |
| MYS-006 | Desconto judicial ignora teto de 30% | CALCDSCT.NSN:L145-L148 | Alto — se modernização aplicar 30% a judicial, viola lei | ALTA |
| MYS-007 | CPFs com 8 prefixos especiais contornam validação | VALDOCS.NSN:L49-L56, L167-L181 | Segurança — possível backdoor de teste em produção | ALTA |
| MYS-008 | Região 99 = bypass total de elegibilidade | VALELEG.NSN:L101-L104 | Segurança — "bypass do Roberto" sem justificativa formal | ALTA |
| MYS-009 | Processamento por ordem de CPF, dependência downstream | BATCHPGT.NSN:L180-L183 | Médio — modernização precisa preservar ou migrar dependentes | MÉDIA |
| MYS-010 | Ações 'EX' ocultadas do relatório de auditoria | RELAUDIT.NSN:L101-L104 | Compliance — trilha incompleta pode violar regulamentação | ALTA |
| MYS-011 | Tabela IPCA congelada em 2012 (última carga 2014) | CALCCORR.NSN:L54-L95 | Alto — correções pós-2012 retornam fator 1.0 (zero correção) | ALTA |
| MYS-012 | Deflação ignorada (VLR-DIFF ≤ 0 → skip) | CALCCORR.NSN:L158-L165 | Médio — proteção ao beneficiário ou limitação legal? | MÉDIA |
| MYS-013 | Cálculo idade ignora mês/dia (só ANO-ATUAL − ANO-NASC) | VALELEG.NSN:L72-L73, CALCBENF.NSN | Baixo — erro de até 11 meses; impacta fator idade em bordas | MÉDIA |
| MYS-014 | BATCHPGT usa desconto fixo 3% > R$500, CALCDSCT tem 4 faixas progressivas | BATCHPGT.NSN:L306-L310 | Crítico — beneficiários batch vs online recebem descontos diferentes | ALTA |
| MYS-015 | NUM-PAGTO sequencial sem lock ou transação | BATCHPGT.NSN:L174-L178 | Baixo — provavelmente seguro em janela batch exclusiva | BAIXA |
| MYS-016 | Tolerância de 1 centavo na conciliação sem documentação | BATCHCON.NSN:L155-L161 | Baixo — padrão bancário implícito | BAIXA |
| MYS-017 | Status payment não verificado antes de update na conciliação | BATCHCON.NSN:L168-L194 | Alto — re-execução pode sobrescrever 'D' para 'P' | ALTA |
| MYS-018 | Fevereiro aceita dia 29 em qualquer ano (sem check bissexto) | VALBENEF.NSN:L100 | Baixo — dados inválidos aceitos mas sem impacto em cálculos | BAIXA |
| MYS-019 | CPF "000..." aceito como exceção "teste governo" | VALBENEF.NSN:L189-L197 | Segurança — possível backdoor | MÉDIA |
| MYS-020 | Título eleitor e CTPS coletados mas nunca validados | VALDOCS.NSN:L60-L63 | Baixo — funcionalidade nunca implementada | BAIXA |
| MYS-021 | Tipo programa não validado na inclusão (CADPROG) | CADPROG.NSN:L63 | Médio — tipos inválidos causam rejeição tardia em VALELEG | MÉDIA |
| MYS-022 | Bug mascaramento CPF documentado mas não corrigido ("NAO CORRIGIR SEM AUDITORIA") | CONSBENF.NSN:L173-L188 | Baixo — exposição parcial de CPF em caso edge | MÉDIA |
| MYS-023 | Mascaramento CPF inconsistente entre programas (CONSBENF vs RELPGT) | CONSBENF.NSN / RELPGT.NSN | Compliance/LGPD — políticas de mascaramento divergentes | MÉDIA |

## Detalhamento dos Mistérios

### MYS-001: Status Idoso Automático

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/CADBENEF.NSN#L159-L160`
- **Trecho de código**:

```natural
  IF #IDADE > 75
    MOVE 'S' TO #STATUS
  END-IF
```

- **O que esperávamos**: Status inicial sempre 'A' (ativo) conforme documentação
- **O que o código faz**: Sobrescreve para 'S' (suspenso) se idade > 75 — silenciosamente
- **Hipótese do time**: Medida de proteção social (recadastramento presencial obrigatório) ou exigência do Tribunal de Contas para beneficiários idosos. Alteração de 2011 (header).
- **Risco se ignorarmos**: Beneficiários idosos ficariam ativos sem a proteção/controle que justificou a regra

---

### MYS-003: Constante 0.347215 (Fator K)

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN#L87-L88`
- **Trecho de código**:

```natural
COMPUTE #FATOR-K = 1.00 + (#FATOR-REAJ * 0.347215)
COMPUTE #VLR-CALC = #VLR-BASE * #FATOR-K
```

- **O que esperávamos**: Constantes documentadas com origem (lei, decreto, cálculo atuarial)
- **O que o código faz**: Aplica fator misterioso que ajusta o valor base antes de gravar. FATOR-REAJUSTE é então aplicado NOVAMENTE no cálculo mensal → reajuste duplo
- **Hipótese do time**: 0.347215 pode ser derivada de: taxa atuarial padrão INSS, fórmula de expectativa de vida, ou política econômica específica da época (1997-2003)
- **Risco se ignorarmos**: Valores base calculados incorretamente na modernização; acúmulo financeiro significativo ao longo de milhares de beneficiários

---

### MYS-005: Truncamento vs Arredondamento

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN#L132-L134` e `BATCHREL.NSN#L123-L126`
- **Trecho de código**:

```natural
* CALCBENF/BATCHPGT — TRUNCA
COMPUTE #VLR-TEMP = #VLR-BENF * 100
COMPUTE #VLR-BENF = #VLR-TEMP / 100

* BATCHREL — ARREDONDA
COMPUTE #VLR-ARR = PAGAMENTO-V.VLR-BRUTO + 0.005
COMPUTE #VLR-TEMP = #VLR-ARR * 100
COMPUTE #VLR-ARR = #VLR-TEMP / 100
```

- **O que esperávamos**: Método consistente em todo o sistema
- **O que o código faz**: Cálculos truncam (perda sistemática de até R$0.009 por operação), relatório arredonda (soma pode não bater com individuas)
- **Hipótese do time**: Truncamento era prática comum em sistemas financeiros mainframe dos anos 90 (bankers' truncation). Relatório "corrige" para apresentação mas não altera dados.
- **Risco se ignorarmos**: Se modernização usar apenas ROUND ou apenas TRUNCATE, haverá divergências em auditorias comparativas legado × moderno

---

### MYS-006: Exceção Judicial ao Teto de 30%

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN#L145-L148`
- **Trecho de código**:

```natural
* VERIF TETO - EXCETO JUDICIAL
  IF #TIPO-DESC NE 'J'
    IF #TOTAL-DESC > (#VLR-BRUTO * 0.30)
      COMPUTE #TOTAL-DESC = #VLR-BRUTO * 0.30
    END-IF
  END-IF
```

- **O que esperávamos**: Teto aplica-se a todos os tipos ou exceção documentada
- **O que o código faz**: Judicial ignora completamente o limite — pode descontar 100% do bruto
- **Hipótese do time**: Determinação judicial tem precedência legal sobre limite administrativo. CPC prevê penhora integral em certos casos.
- **Risco se ignorarmos**: Aplicar teto 30% a judiciais na modernização pode violar decisões judiciais e gerar problemas legais

---

### MYS-007/EGG-002: Prefixos Especiais (Backdoor)

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/VALDOCS.NSN#L49-L56, L167-L181`
- **Trecho de código**:

```natural
MOVE '000' TO #PREF-ESP(1)
MOVE '001' TO #PREF-ESP(2)
MOVE '002' TO #PREF-ESP(3)
MOVE '010' TO #PREF-ESP(4)
MOVE '011' TO #PREF-ESP(5)
MOVE '099' TO #PREF-ESP(6)
MOVE '100' TO #PREF-ESP(7)
MOVE '999' TO #PREF-ESP(8)
...
      MOVE TRUE TO #DOC-ESP-OK
      MOVE TRUE TO #CPF-OK
      MOVE 'V' TO #RESULTADO
      MOVE 0 TO #QTD-ERROS
```

- **O que esperávamos**: Todos os CPFs passam pela mesma validação
- **O que o código faz**: 8 prefixos contornam TODA validação, inclusive RG. Resultado forçado para válido, erros zerados.
- **Hipótese do time**: Mecanismo de teste/homologação que permaneceu em produção. "AJUSTE CHECK ESPEC" de 2011 (Roberto Mendes) pode ter sido para consertar mas acabou ampliando.
- **Risco se ignorarmos**: Vetor de bypass de segurança. Na modernização, remover ou documentar formalmente como exceção controlada.

---

### MYS-010: Exclusões Ocultadas da Auditoria

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/RELAUDIT.NSN#L101-L104`
- **Trecho de código**:

```natural
  IF AUDITORIA-V.ACAO = 'EX'
    ADD 1 TO #QTD-FILTRADOS
    ESCAPE TOP
  END-IF
```

- **O que esperávamos**: Trilha de auditoria completa, especialmente para exclusões
- **O que o código faz**: Filtra 'EX' ANTES dos filtros do usuário — impossível ver exclusões
- **Hipótese do time**: Possibilidades: (1) exclusões são soft-deletes que poluíam; (2) decisão gerencial de ocultar; (3) compliance que proíbe exibir dados excluídos
- **Risco se ignorarmos**: Regulamentações modernas (LGPD, TCU) exigem trilha completa. Modernização deve incluir exclusões na auditoria.

---

### MYS-014: Divergência Desconto Batch vs Online

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN#L306-L310`
- **Trecho de código**:

```natural
* CALC DESCONTOS SIMPLIFICADO
  IF #VLR-BRUTO > 500.00
    COMPUTE #VLR-DESC = #VLR-BRUTO * 0.03
  END-IF
```

- **O que esperávamos**: Batch usa CALCDSCT (4 faixas progressivas, 6 tipos)
- **O que o código faz**: Usa desconto fixo de 3% acima de R$500. CALCDSCT nunca é chamado pelo batch.
- **Hipótese do time**: Simplificação para performance batch (anos 90) que nunca foi unificada com CALCDSCT
- **Risco se ignorarmos**: Beneficiários podem receber descontos diferentes dependendo se o cálculo é via batch ou online. Na modernização, unificar para uma única engine de desconto.

---

### MYS-017: Conciliação Sem Verificar Status Atual

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN#L168-L194`
- **Trecho de código**:

```natural
* Não verifica STATUS-PGTO atual antes de:
  MOVE 'P' TO PAGAMENTO-V.STATUS-PGTO
  UPDATE PAGAMENTO-V
```

- **O que esperávamos**: Verificar se pagamento está em status 'G' antes de transicionar
- **O que o código faz**: Sobrescreve qualquer status ('G', 'D', 'E') para o novo status do retorno bancário
- **Hipótese do time**: Seguro se batch roda em janela exclusiva e arquivo retorno é idempotente. Mas re-envio ao banco poderia alterar 'D' → 'P' sem controle.
- **Risco se ignorarmos**: Na modernização com processamento concorrente, deve-se adicionar guard clause (somente transicionar de 'G').

## Easter Eggs

> Dica: existem **3 easter eggs** escondidos no código legado. Registre aqui os que encontrar:

1. [x] Easter Egg 1: **Plano Verão (1989-1991)** — CALCCORR.NSN:L97-L108. Código comentado com fator 2.75 + 1.4289, transição Cruzado→Cruzeiro. Responsável: João Batista, 2003.
2. [x] Easter Egg 2: **Backdoor "Doc Especial"** — VALDOCS.NSN:L167-L181. Sub-rotina CHECK-DOC-ESPECIAL aceita 8 prefixos de CPF sem validação. Sobrescreve todos os erros.
3. [x] Easter Egg 3: **Banco Real (falecido 2007)** — BATCHCON.NSN:L203-L218. Integração com Banco Real (código 356) comentada. Comentário: "BANCO REAL FOI ADQUIRIDO PELO SANTANDER EM 2007 - MANTER CODIGO PARA REFERENCIA HISTORICA".

## Resumo

- Total de mistérios encontrados: **23**
- Confiança alta: **12**
- Confiança média: **7**
- Confiança baixa: **4**
- Easter eggs encontrados: **3** / 3

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="mysteries-checklist.md"><strong>mysteries-checklist.md</strong></a><br/>
<sub>Lista do que procurar.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="discovery-report.md"><strong>discovery-report.md</strong></a><br/>
<sub>Síntese final.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

