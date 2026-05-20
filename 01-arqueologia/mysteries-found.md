## Mistérios Catalogados

Total: 6 | Critical (blocks-stage-2): 2 | High: 2 | Medium: 1 | Low: 1
Por classificação: needs-facilitator: 1 | needs-investigation: 1 | parked: 2 | blocks-stage-2: 2

| ID | Descrição | Onde Encontrado | Severidade | Classificação | Confiança |
| --- | --- | --- | --- | --- | --- |
| MYS-001 | Filtro silencioso descarta eventos de auditoria com ACAO='EX' (exclusão lógica) sem registro no resumo. Pode mascarar rastro de exclusão de beneficiários. | RELAUDIT.NSN#L92-L96 (BR-048) | **Critical** | blocks-stage-2 | ALTA |
| MYS-002 | Máscara de CPF em CONSBENF expõe os 3 primeiros dígitos para CPFs com menos de 11 dígitos. Comentário no código diz "INCONSISTENCIA CONHECIDA — NAO CORRIGIR SEM APROVACAO DA AUDITORIA". | CONSBENF.NSN#L155-L180 (BR-045) | **Critical** | blocks-stage-2 | ALTA |
| MYS-003 | Máscaras de CPF divergentes entre CONSBENF (`***.***.NNN-NN`) e RELPGT (`***.NNN.NNN-NN`). Mesmo dado sensível com proteções diferentes em cada programa. | CONSBENF.NSN#L155-L180 ↔ RELPGT.NSN#L107-L112 (BR-045 vs BR-057) | High | blocks-stage-2 | ALTA |
| MYS-004 | RELPGT decodifica `TIPO-PGTO='T'` como TERCEIRO, mas grep mostra que nenhum programa do legado grava esse valor (CALCBENF usa apenas N/D). Possível código morto ou TIPO produzido por sistema externo não incluído neste pacote. | RELPGT.NSN#L121 (BR-055) | High | needs-investigation | MÉDIA |
| MYS-005 | Hard-cap de 12 itens no histórico de pagamentos da consulta online, sem documentação. | CONSBENF.NSN#L138-L150 (BR-043) | Medium | needs-facilitator | MÉDIA |
| MYS-006 | Constante 19970101 como início padrão do relatório de auditoria. ARQUITETURA-ORIGINAL-1997.md confirma 1997 como ano de implantação — mistério resolvido como evidência inferida, manter parked. | RELAUDIT.NSN#L74-L80 (BR-046) | Low | parked | ALTA |

## Detalhamento dos Mistérios

### MYS-001: Filtro silencioso de eventos de exclusão na auditoria

- **Arquivo**: `01-arqueologia/legado-sifap/natural-programs/RELAUDIT.NSN#L92-L96`
- **Trecho de código**:

  ```natural
  IF AUDITORIA-V.ACAO = 'EX'
    ADD 1 TO #QTD-FILTRADOS
    ESCAPE TOP
  END-IF