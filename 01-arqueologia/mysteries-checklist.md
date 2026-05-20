<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Checklist de Mistérios do SIFAP

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **mysteries-checklist**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Há **10 regras de negócio escondidas** e **3 easter eggs** plantados no código legado. Quanto mais seu time encontrar, melhor a nota na rubrica (dimensão A1).

## Por que isso existe

Em sistemas legados de verdade, regras de negócio críticas frequentemente ficam escondidas em código sem comentário, em constantes mágicas, em casos especiais sem justificativa. A facilitadora plantou 10 dessas armadilhas no SIFAP justamente para treinar o olhar do time. Quem aprende a achar mistérios no workshop, acha em produção.

## Como funciona

- Cada mistério vale 1–3 pontos dependendo da dificuldade
- Total possível: **32 pontos**
- Os mistérios estão distribuídos nos 15 programas .NSN e nos 4 DDMs
- Nenhum mistério está documentado em `legacy-docs/` (os docs estão desatualizados de propósito!)

## Regras de Negócio Escondidas (10)

Marque [x] quando encontrar:

- [x] **MYS-001** (★★): Um programa modifica silenciosamente o status do beneficiário baseado em um critério demográfico. Onde? Por quê?
  > **ENCONTRADO** — CADBENEF.NSN:L159-L160. Idade > 75 → status 'S' (suspenso). Header diz "AJUSTE STATUS IDOSO (2011)". Doc 2012 não menciona. (BR-044)
- [x] **MYS-002** (★): Um limite numérico está hardcoded no código mas contradiz a capacidade definida no DDM. Qual é o limite? Em qual programa?
  > **ENCONTRADO** — CADDEPEND.NSN:L64-L67. Limite de 5 dependentes hardcoded, mas PE group do DDM suporta até 99 ocorrências. (BR-112)
- [x] **MYS-003** (★★★): Uma variável misteriosa é usada em cálculos mas nunca foi documentada — ninguém sabe de onde veio a constante. Qual variável?
  > **ENCONTRADO** — CADPROG.NSN:L87-L88. Constante 0.347215 no "Fator K". Fórmula: 1.00 + (FATOR-REAJ × 0.347215). Nenhuma documentação explica a origem. (BR-121)
- [x] **MYS-004** (★★★): Em um mês específico do ano, o cálculo de benefício muda completamente. Qual mês? O que muda?
  > **ENCONTRADO** — CALCBENF.NSN / BATCHPGT.NSN em dezembro (mês 12). Adiciona 13° salário + abono de natal 15% para tipo 'A'. (BR-009, BR-010, BR-060, BR-061)
- [x] **MYS-005** (★★★): O sistema usa uma técnica de arredondamento que causa perda sistemática de centavos. Qual técnica? Onde?
  > **ENCONTRADO** — CALCBENF.NSN/BATCHPGT.NSN usam TRUNCATE (×100, inteiro, ÷100) enquanto BATCHREL.NSN usa ROUND (+0.005). Truncamento causa perda sistemática. (BR-087, BR-134)
- [x] **MYS-006** (★★): Um tipo de desconto ignora uma regra de limite que se aplica a todos os outros. Qual tipo? Por quê?
  > **ENCONTRADO** — CALCDSCT.NSN: desconto judicial (tipo 'J') ignora o teto de 30% que limita todos os outros 5 tipos. (BR-015)
- [x] **MYS-007** (★): Certos CPFs são aceitos sem validação real. Quais? Isso é um bug ou feature?
  > **ENCONTRADO** — VALDOCS.NSN:L49-L56, L167-L181. CPFs com prefixo 000/001/002/010/011/099/100/999 contornam toda validação. VALBENEF.NSN aceita "000..." com todos dígitos iguais. (BR-095, BR-106, BR-107)
- [x] **MYS-008** (★): Beneficiários de uma região específica pulam TODAS as verificações de elegibilidade. Qual região?
  > **ENCONTRADO** — VALELEG.NSN:L101-L104. Região 99 = bypass total. Doc 2012 diz "é um bypass do Roberto". (BR-021)
- [x] **MYS-009** (★★): O processamento batch segue uma ordem que não é a mais lógica, mas que virou dependência de outros sistemas. Qual ordem?
  > **ENCONTRADO** — BATCHPGT.NSN:L180-L183. Processa em ordem de CPF (READ BY CPF). Comentário: "SISTEMAS DOWNSTREAM DEPENDEM DESTA ORDENACAO". Otimização de 2000. (BR-049)
- [x] **MYS-010** (★★★): Um tipo de evento de auditoria é sistematicamente ocultado dos relatórios. Qual tipo? Isso é intencional ou bug?
  > **ENCONTRADO** — RELAUDIT.NSN:L101-L104. Ações 'EX' (exclusão) são SEMPRE filtradas, independente dos filtros. Contradiz propósito de trilha de auditoria. (BR-138)

## Easter Eggs (3)

- [x] **EGG-001** (★): Um bloco de código comentado referencia uma política econômica dos anos 90 que nunca foi removida. Qual política?
  > **ENCONTRADO** — CALCCORR.NSN:L97-L108. Correção do Plano Verão (1989-1991), fator 2.75, transição Cruzado→Cruzeiro. Responsável: João Batista, 2003. (BR-092)
- [x] **EGG-002** (★): Um programa tem uma função de validação especial que aceita certos documentos sem verificação. Parece um backdoor de teste. Onde?
  > **ENCONTRADO** — VALDOCS.NSN:L167-L181 (CHECK-DOC-ESPECIAL). Sobrescreve TODOS os erros se CPF tem prefixo especial. Zera QTD-ERROS e força resultado='V'. (BR-106)
- [x] **EGG-003** (★): Código morto referencia uma integração com uma empresa que não existe mais. Qual empresa?
  > **ENCONTRADO** — BATCHCON.NSN:L203-L218. Integração Banco Real (código 356), adquirido pelo Santander em 2007. Layout diferente do BB, sub-rotina CONCILIA-REAL nunca chamada. (BR-081)

## Inconsistências entre Documentação e Código (bônus)

- [x] **INC-001**: Um limite documentado diverge do que o código permite
  > Doc 2012 RN-004 diz "máximo dependentes" sem número; DDM suporta 99 ocorrências PE; CADDEPEND limita a 5. (BR-112)
- [x] **INC-002**: O documento de arquitetura original não menciona uma estrutura de dados que foi adicionada depois
  > COD-ELEGIBILIDADE (5 posições, flags posicionais) adicionado em 2012 por Fernanda Costa. Não aparece no doc de 2012. (BR-125)
- [x] **INC-003**: Regras críticas de cálculo não aparecem em nenhum documento
  > Fator K (constante 0.347215) não documentada. Desconto judicial sem teto (30% cap exception) não documentada. Reajuste duplo (VLR-BASE × FatorK + cálculo × (1+FATOR-REAJ)) não documentada. (BR-121, BR-015)
- [x] **INC-004**: Dois programas usam métodos de arredondamento diferentes para o mesmo tipo de valor
  > CALCBENF/BATCHPGT truncam (×100, int, ÷100). BATCHREL arredonda (+0.005 antes de truncar). Totais do relatório podem divergir dos cálculos. (BR-134)

## Pontuação

| Faixa        | Classificação                           |
| ------------ | --------------------------------------- |
| 26–32 pontos | Excelente — arqueologia completa!       |
| 18–25 pontos | Sólido — bom trabalho de investigação   |
| 10–17 pontos | Satisfatório — encontrou o básico       |
| 0–9 pontos   | Precisa melhorar — explore mais a fundo |

## Dicas

- Use **Copilot Chat** para perguntar sobre cada programa: _"Tem alguma lógica escondida neste código? Existe alguma condição que parece um workaround ou caso especial não documentado?"_
- Compare o que a **documentação diz** com o que o **código faz** — as inconsistências são intencionais
- Os DDMs também contêm pistas em seus comentários
- Se travar, levante a mão — o facilitador pode dar uma dica calibrada após 90 minutos

---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="GUIDE.md"><strong>GUIDE do Estágio 1</strong></a><br/>
<sub>Passo a passo do estágio.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="mysteries-found.md"><strong>mysteries-found.md</strong></a><br/>
<sub>Onde você registra os mistérios.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

