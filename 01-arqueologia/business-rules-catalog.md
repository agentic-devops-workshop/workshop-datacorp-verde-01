<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Catálogo de Regras de Negócio — SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **business-rules-catalog**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Registre aqui todas as regras de negócio extraídas do código Natural/Adabas.
> Cada regra precisa ter rastreabilidade até o código-fonte.
>
> **REGRA DURA:** linhas com `Programa Fonte` vazio são **inválidas** e não contam para o gate do Estágio 2. Use o formato `01-arqueologia/legado-sifap/natural-programs/ARQUIVO.NSN#L<inicio>-L<fim>` sempre que possível. Mínimo aceito: nome do arquivo .NSN.

## Como pensar em "regra de negócio"

O que conta:

- Um `IF` que decide algo no domínio (ex.: _"se a UF é do Nordeste e o programa é Seca, valor base × 1.2"_)
- Uma constante numérica sem explicação (ex.: `0.075` num cálculo de imposto)
- Uma transição de status com regra (ex.: _"só de A para S, nunca de I para A"_)
- Um tratamento especial para um caso (ex.: _"se o CPF começa com 999, é teste"_)

O que NÃO conta: paginação de relatório, formatação de saída, manipulação de cursor Adabas, abertura de arquivo. Ignore esses detalhes de implementação.

## Níveis de Risco

| Nível       | Descrição                                                     |
| ----------- | ------------------------------------------------------------- |
| **CRÍTICO** | Regra financeira ou de segurança — erro causa prejuízo direto |
| **ALTO**    | Regra de negócio central — afeta fluxo principal              |
| **MÉDIO**   | Regra de validação ou formatação — afeta qualidade dos dados  |
| **BAIXO**   | Regra de apresentação ou conveniência — impacto limitado      |

## Regras Encontradas

| ID | Regra de Negócio | Programa Fonte | Campos DDM | Nível de Risco | Notas |
|----|-----------------|----------------|-----------|----------------|-------|
| BR-001 | Beneficiário deve ter status 'A' (ativo) para ser calculado. Qualquer outro status bloqueia o cálculo. | `CALCBENF.NSN#L159-L161` | `BENEFICIARIO.STATUS` | ALTO | Confirmada — RN-003/doc 2012. Status válidos: A,S,C,I,D |
| BR-002 | Programa social deve existir e ter STATUS-PROG = 'A' para gerar pagamento | `CALCBENF.NSN#L165-L172` / `BATCHPGT.NSN#L226-L229` | `PROGRAMA-SOCIAL.STATUS-PROG` | ALTO | Confirmada — RN-003/doc 2012 |
| BR-003 | Fator regional: tabela fixa de 27 UFs com multiplicadores de 1.00 a 1.40. Regiões 1-5=Norte, 6-10=Nordeste, 11-15=Sudeste, 16-20=Sul, 21-25=C.Oeste. Fora do range → fator 1.0 | `CALCBENF.NSN#L86-L112` / `CALCBENF.NSN#L170-L175` | `BENEFICIARIO.COD-REGIAO` | CRÍTICO | Inferred. Hardcoded. Nordeste tem maiores fatores (1.32-1.40) |
| BR-004 | Fator familiar progressivo: 0 dep=1.00; 1-2 dep=1.0+0.05×n; 3-4 dep=1.10+0.03×(n-2); 5+ dep=1.16+0.02×(n-4) | `CALCBENF.NSN#L178-L190` | `BENEFICIARIO.NUM-DEPENDENTES` | CRÍTICO | Inferred. Regra não documentada em 2012. Limite de dependentes é 5+ (não 3 como doc RN-004 afirma) |
| BR-005 | Fator renda: 5 faixas decrescentes — ≤300=1.00, ≤600=0.85, ≤1000=0.70, ≤1500=0.55, >1500=0.40 | `CALCBENF.NSN#L118-L128` / `CALCBENF.NSN#L298-L305` (DET-FAIXA-RENDA) | `BENEFICIARIO.RENDA-FAMILIAR` | CRÍTICO | Confirmada parcial — RN-018/doc 2012 menciona faixas mas sem valores |
| BR-006 | Fator idade: ≥65 anos=1.15; ≥60 anos=1.10; <18 anos=1.05; demais=1.00 | `CALCBENF.NSN#L205-L215` | `BENEFICIARIO.DT-NASCIMENTO` | CRÍTICO | Inferred. Não documentada em 2012. Calculada como ANO-atual - ANO-nascimento (ignora mês/dia) |
| BR-007 | Fórmula principal: VLR = BASE × FATOR_REG × FATOR_FAM × FATOR_RND × FATOR_IDADE × (1+FATOR_REAJ) | `CALCBENF.NSN#L225-L230` | `PROGRAMA-SOCIAL.VLR-BASE`, `PROGRAMA-SOCIAL.FATOR-REAJUSTE` | CRÍTICO | Confirmada parcial — doc 2012 RN-013 mostra fórmula mais simples. O "Fator K" que doc menciona como mistério é provavelmente o FATOR-REAJUSTE |
| BR-008 | Truncamento: todos os valores monetários são truncados para 2 casas decimais (×100, inteiro, ÷100) — nunca arredondados matematicamente | `CALCBENF.NSN#L227-L229` / `CALCDSCT.NSN#L175-L177` | Todos campos N9.2 | CRÍTICO | Confirmada — RN-014/doc 2012 |
| BR-009 | 13° salário: em dezembro (mês=12), VLR_13 = BASE × FATOR_REG × FATOR_IDADE (fórmula diferente do benefício normal — não usa fator familiar nem renda) | `CALCBENF.NSN#L237-L248` | `PAGAMENTO.TIPO-PGTO` = 'D' | CRÍTICO | Inferred. Doc 2012 flagou como "pendente de levantamento" (seção 6). Agora documentada |
| BR-010 | Abono natalino: 15% do benefício mensal, exclusivo para programas tipo 'A' (assistencial), pago apenas em dezembro | `CALCBENF.NSN#L251-L256` | `PROGRAMA-SOCIAL.TIPO`, `PAGAMENTO.VLR-ABONO` | CRÍTICO | Inferred. Doc 2012 flagou como pendente. Agora documentada |
| BR-011 | Desconto simplificado: 3% de contribuição social quando bruto > R$500. Isento se ≤ R$500 | `CALCBENF.NSN#L305-L310` (CALC-DESCONTOS) | `PAGAMENTO.VLR-BRUTO`, `PAGAMENTO.VLR-DESCONTO` | ALTO | Inferred. Versão simplificada dentro do CALCBENF (o CALCDSCT tem lógica completa) |
| BR-012 | Valor líquido nunca pode ser negativo — se cálculo resultar < 0, assume valor 0 | `CALCBENF.NSN#L270-L272` / `BATCHPGT.NSN#L320-L322` | `PAGAMENTO.VLR-LIQUIDO` | ALTO | Inferred. Proteção silenciosa — sem mensagem de erro |
| BR-013 | Teto de descontos: total não pode exceder 30% do bruto. Exceto tipo 'J' (judicial) que não tem teto | `CALCDSCT.NSN#L102-L104` / `CALCDSCT.NSN#L166-L170` | `PAGAMENTO.VLR-BRUTO`, `BENEFICIARIO.TIPO-DSCT` | CRÍTICO | Confirmada — RN-021/doc 2012. Doc mencionava exceção judicial como "não confirmada" — agora confirmada no código |
| BR-014 | Contribuição social progressiva: ≤500=3%, ≤1000=5%, ≤2000=7%, >2000=9% | `CALCDSCT.NSN#L56-L60` / `CALCDSCT.NSN#L192-L200` (CALC-CONTRIB-SOCIAL) | `PAGAMENTO.VLR-BRUTO` | CRÍTICO | Inferred. Alíquotas hardcoded. Não descritas no doc 2012 |
| BR-015 | Desconto judicial: aplicado como valor fixo (VLR-DSCT) OU percentual (PCT-DSCT/100 × bruto). Ignora teto de 30% | `CALCDSCT.NSN#L129-L134` | `BENEFICIARIO.DESCONTOS(PE).TIPO-DSCT='J'` | CRÍTICO | Confirmada — exceção judicial é real. Doc 2012 tinha dúvida |
| BR-016 | Pensão alimentícia: valor fixo OU percentual. Sujeita ao teto 30% | `CALCDSCT.NSN#L136-L141` | `BENEFICIARIO.DESCONTOS(PE).TIPO-DSCT='P'` | CRÍTICO | Inferred |
| BR-017 | Imposto retido: sempre calculado como percentual do bruto | `CALCDSCT.NSN#L143-L145` | `BENEFICIARIO.DESCONTOS(PE).TIPO-DSCT='I'` | ALTO | Inferred |
| BR-018 | Desconto sindical: fixo em 1% do bruto (hardcoded) | `CALCDSCT.NSN#L147-L148` | `BENEFICIARIO.DESCONTOS(PE).TIPO-DSCT='S'` | MÉDIO | Inferred. Não parametrizado — valor embutido no código |
| BR-019 | Vigência de descontos: cada desconto tem DT-INICIO e DT-FIM. Desconto só é aplicado se data atual está dentro do intervalo. DT-FIM=0 significa sem prazo final | `CALCDSCT.NSN#L114-L119` | `BENEFICIARIO.DT-INICIO-DSCT`, `BENEFICIARIO.DT-FIM-DSCT` | ALTO | Inferred |
| BR-020 | Duplicação de pagamento: beneficiário não pode ter dois pagamentos na mesma competência. Sistema ignora silenciosamente se já existe | `BATCHPGT.NSN#L200-L208` | `PAGAMENTO.CPF-BENEF`, `PAGAMENTO.COMPETENCIA` | ALTO | Inferred. Sem mensagem de erro — simplesmente pula |

## Regras por Categoria

### Cálculos Financeiros

- **BR-003** Fator regional (tabela 27 UFs)
- **BR-004** Fator familiar (progressivo por dependentes)
- **BR-005** Fator renda (5 faixas decrescentes)
- **BR-006** Fator idade (3 faixas etárias + default)
- **BR-007** Fórmula principal do benefício
- **BR-008** Truncamento (nunca arredondamento)
- **BR-009** 13° salário — dezembro — fórmula diferenciada
- **BR-010** Abono natalino 15% — programas assistenciais
- **BR-011** Desconto simplificado 3% (versão CALCBENF)
- **BR-012** Valor líquido mínimo = 0
- **BR-013** Teto 30% descontos (exceto judicial)
- **BR-014** Contribuição social progressiva (4 faixas)
- **BR-015** Desconto judicial sem teto
- **BR-016** Pensão alimentícia
- **BR-017** Imposto retido (percentual)
- **BR-018** Desconto sindical 1% fixo

### Validações de Status

- **BR-001** Beneficiário status = 'A' obrigatório para cálculo
- **BR-002** Programa status = 'A' obrigatório para geração

### Regras de Negócio Temporais

- **BR-009** 13° salário exclusivo de dezembro
- **BR-010** Abono natalino exclusivo de dezembro
- **BR-019** Vigência temporal de descontos (DT-INICIO / DT-FIM)
- **BR-020** Unicidade de pagamento por competência

### Regras com Mistério

<!-- MYSTERY: BATCHPGT.NSN:L14 declara "CHAMA CALCBENF E CALCDSCT" mas o código duplica a lógica. A duplicação é intencional (performance batch) ou erro de manutenção? -->
<!-- MYSTERY: Fator regional hardcoded em duas cópias (CALCBENF e BATCHPGT) — se divergirem, qual prevalece? -->
<!-- MYSTERY: CALCBENF usa DET-FAIXA-RENDA (sub-rotina) enquanto BATCHPGT usa DET-FAIXA-RENDA-BATCH — são idênticas, mas porque duplicar? -->
<!-- MYSTERY: O "Fator K" mencionado no doc 2012 é o FATOR-REAJUSTE ou é o cálculo 1.00 + (FATOR_REAJ × 0.347215) em CADPROG.NSN:L89? -->

---

## Regras de VALELEG.NSN

> Programa de validação de elegibilidade — verifica se um beneficiário pode participar de um programa social.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-021 | Quando a região do beneficiário = 99 (internacional/diplomático), o sistema deverá aprovar elegibilidade automaticamente, ignorando todas as demais verificações | Optional | VALELEG.NSN:L101-L104 | Mistério | <!-- MYSTERY: bypass total por região 99. Doc 2012 menciona em nota que "é um bypass do Roberto" sem explicar. Possível mecanismo de teste ou privilégio diplomático --> |
| BR-022 | Se o beneficiário não estiver com status 'A' (ativo), o sistema deverá rejeitar elegibilidade. Status 'S'=suspenso, 'C'=cancelado, 'D'=desligado, 'I'=inativo — todos bloqueiam | Unwanted | VALELEG.NSN:L109-L125 | Confirmada | RN-003/doc 2012 (vínculo ativo obrigatório). Seção 4.2 lista "situação cadastral ativa" |
| BR-023 | Quando o programa define IDADE-MIN > 0, se a idade do beneficiário for inferior ao mínimo, o sistema deverá rejeitar elegibilidade | Unwanted | VALELEG.NSN:L130-L137 | Confirmada | Doc 2012 seção 4.2 menciona faixa etária como critério |
| BR-024 | Quando o programa define IDADE-MAX > 0, se a idade do beneficiário for superior ao máximo, o sistema deverá rejeitar elegibilidade | Unwanted | VALELEG.NSN:L138-L142 | Confirmada | Doc 2012 seção 4.2 |
| BR-025 | Quando o programa define RENDA-MAX > 0, se a renda familiar do beneficiário exceder o teto do programa, o sistema deverá rejeitar elegibilidade | Unwanted | VALELEG.NSN:L147-L151 | Confirmada | Doc 2012 seção 4.2 + RN-018 (faixas por renda) |
| BR-026 | Para programas tipo 'A' (assistencial): se renda > R$600 E número de dependentes < 1, o sistema deverá rejeitar elegibilidade | Optional | VALELEG.NSN:L157-L163 | Inferred | Regra combinada: renda alta SEM dependentes = inelegível. Com dependentes, renda > 600 é tolerada |
| BR-027 | Para programas tipo 'A' (assistencial): se documentação não estiver completa (DOCUMENTOS-OK ≠ 'S'), o sistema deverá rejeitar elegibilidade | Unwanted | VALELEG.NSN:L164-L168 | Inferred | Campo DOCUMENTOS-OK vem do programa VALDOCS (validação prévia) |
| BR-028 | Para programas tipo 'P' (previdenciário): se idade < 60 anos, o sistema deverá rejeitar elegibilidade | Unwanted | VALELEG.NSN:L170-L174 | Inferred | Idade mínima 60 hardcoded para previdenciário |
| BR-029 | Para programas tipo 'T' (trabalho): se idade < 16 OU idade > 65, o sistema deverá rejeitar elegibilidade | Unwanted | VALELEG.NSN:L176-L180 | Inferred | Faixa 16-65 hardcoded para programas de trabalho |
| BR-030 | Se o tipo de programa não for 'A', 'P' nem 'T', o sistema deverá rejeitar elegibilidade com motivo "tipo programa desconhecido" | Unwanted | VALELEG.NSN:L182-L185 | Inferred | Proteção contra dados inválidos no cadastro de programas |
| BR-031 | Quando o código de elegibilidade específica começa com 'R' (1° caractere), o sistema deverá exigir NIS cadastrado (≠ 0). Se NIS = 0, rejeitar | Optional | VALELEG.NSN:L228-L233 | Inferred | Código de elegibilidade é campo alfanumérico de 5 posições no DDM PROGRAMA-SOCIAL |
| BR-032 | Quando o código de elegibilidade específica tem 'D' no 2° caractere, o sistema deverá exigir pelo menos 1 dependente cadastrado | Optional | VALELEG.NSN:L235-L240 | Inferred | Sistema de flags posicionais no COD-ELEGIBILIDADE |
| BR-033 | O programa social deve estar ativo (STATUS-PROG = 'A') para que qualquer validação de elegibilidade seja realizada | Unwanted | VALELEG.NSN:L95-L97 | Confirmada | Consistente com BR-002 |
| BR-034 | A idade do beneficiário é calculada como ANO-ATUAL menos ANO-NASCIMENTO (ignora mês e dia — cálculo aproximado) | Ubiquitous | VALELEG.NSN:L72-L73 | Inferred | <!-- MYSTERY: cálculo de idade ignora mês/dia. Um beneficiário nascido em dezembro, consultado em janeiro, terá idade "errada" por 11 meses. É intencional ou bug? Mesmo padrão no CALCBENF --> |

---

## Regras de CADBENEF.NSN

> Programa de cadastro de beneficiários — inclusão e alteração de dados cadastrais.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-035 | O sistema deverá aceitar apenas operações 'I' (inclusão) ou 'A' (alteração). Qualquer outro valor é rejeitado | Unwanted | CADBENEF.NSN:L97-L100 | Inferred | Sem operação 'E' (exclusão) neste programa |
| BR-036 | CPF é campo obrigatório — o sistema deverá rejeitar cadastro quando CPF = 0 | Unwanted | CADBENEF.NSN:L102-L105 | Confirmada | RN-001/doc 2012 |
| BR-037 | O sistema deverá validar CPF pelo algoritmo módulo 11 (dois dígitos verificadores). CPF inválido bloqueia a operação | Unwanted | CADBENEF.NSN:L108-L112, L222-L274 | Confirmada | RN-001/doc 2012 "validação por dígito verificador" |
| BR-038 | Nome é campo obrigatório — o sistema deverá rejeitar cadastro quando nome está em branco | Unwanted | CADBENEF.NSN:L114-L117 | Inferred | |
| BR-039 | Data de nascimento é campo obrigatório — o sistema deverá rejeitar cadastro quando DT-NASC = 0 | Unwanted | CADBENEF.NSN:L119-L122 | Confirmada | RN-006/doc 2012 "campo obrigatório" |
| BR-040 | Sexo deve ser 'M' ou 'F' — o sistema deverá rejeitar valores diferentes | Unwanted | CADBENEF.NSN:L124-L127 | Inferred | Binário sem opção neutra/outro |
| BR-041 | Na inclusão ('I'), se já existe beneficiário com mesmo CPF no cadastro, o sistema deverá rejeitar com "BENEFICIARIO JA CADASTRADO" | Unwanted | CADBENEF.NSN:L143-L146 | Confirmada | RN-002/doc 2012 "CPF único para beneficiário ativo" |
| BR-042 | Na alteração ('A'), se o beneficiário não for encontrado pelo CPF, o sistema deverá rejeitar com "NAO ENCONTRADO PARA ALTERACAO" | Unwanted | CADBENEF.NSN:L148-L151 | Inferred | |
| BR-043 | Na inclusão, o status inicial do beneficiário é sempre 'A' (ativo) | Event-driven | CADBENEF.NSN:L155-L156 | Inferred | Todos entram como ativos |
| BR-044 | Se o beneficiário tem idade > 75 anos, o sistema deverá atribuir status 'S' (suspenso/senior), sobrescrevendo o status padrão 'A' | State-driven | CADBENEF.NSN:L159-L160 | Inferred | <!-- MYSTERY: header do programa diz "AJUSTE STATUS IDOSO" (alteração de 2011). Por que suspender automaticamente acima de 75? Doc 2012 não menciona esta regra. Possível medida de proteção social ou exigência de recadastramento presencial --> |
| BR-045 | Na alteração, o sistema NÃO permite alterar: CPF, DT-NASCIMENTO, SEXO, COD-PROGRAMA, COD-REGIAO, NIS, DT-CADASTRO. Apenas permite: NOME, ENDERECO, MUNICIPIO, UF, CEP, TELEFONE, RG, STATUS, RENDA-FAMILIAR, NUM-DEPENDENTES | State-driven | CADBENEF.NSN:L201-L213 | Inferred | Campos de identidade são imutáveis após inclusão. DT-ATUALIZACAO é atualizada automaticamente |
| BR-046 | O sistema deverá registrar a data atual como DT-CADASTRO na inclusão e DT-ATUALIZACAO na alteração | Ubiquitous | CADBENEF.NSN:L192-L193, L212 | Inferred | Rastreabilidade temporal automática |
| BR-047 | A validação CPF módulo 11: resto < 2 → dígito = 0; resto ≥ 2 → dígito = 11 - resto. Se DV calculado ≠ DV informado → CPF inválido | Ubiquitous | CADBENEF.NSN:L240-L270 | Confirmada | Algoritmo padrão Receita Federal |

---

## Regras de BATCHPGT.NSN

> Programa batch de geração mensal de pagamentos — executa no 1° dia útil, processa todos beneficiários ativos em ordem de CPF.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-048 | O batch gera pagamentos para a competência do mês corrente (AAAAMM). A competência é derivada da data de execução do sistema | Ubiquitous | BATCHPGT.NSN:L108-L110 | Inferred | |
| BR-049 | O sistema deverá processar beneficiários em ordem de CPF (READ BY CPF). Sistemas downstream dependem desta ordenação | Unwanted | BATCHPGT.NSN:L180-L183 | Confirmada | Comentário no código: "SISTEMAS DOWNSTREAM DEPENDEM DESTA ORDENACAO" (otimização de 2000) |
| BR-050 | Se o mesmo CPF aparecer mais de uma vez na leitura, o sistema deverá ignorar as ocorrências duplicadas (controle #CPF-ANT) | Unwanted | BATCHPGT.NSN:L188-L192 | Inferred | Proteção contra dados duplicados no Adabas |
| BR-051 | Apenas beneficiários com STATUS = 'A' (ativo) são processados. Todos outros status são ignorados | State-driven | BATCHPGT.NSN:L195-L198 | Confirmada | Consistente com BR-022 |
| BR-052 | Se já existe pagamento para o mesmo CPF na mesma competência, o sistema deverá ignorar (idempotência — não gera duplicata) | Unwanted | BATCHPGT.NSN:L201-L210 | Confirmada | Proteção contra re-execução do batch |
| BR-053 | Se o programa social vinculado ao beneficiário não for encontrado no cadastro, o sistema deverá registrar erro no log e pular o beneficiário | Unwanted | BATCHPGT.NSN:L216-L224 | Inferred | Log inclui CPF + COD-PROGRAMA |
| BR-054 | Se o programa social não está ativo (STATUS-PROG ≠ 'A'), o beneficiário é ignorado | Unwanted | BATCHPGT.NSN:L226-L228 | Confirmada | Consistente com BR-033 |
| BR-055 | Fator regional: tabela hardcoded de 27 valores (1 por UF). Regiões 1-5 (Norte) e 6-10 (Nordeste) têm fatores mais altos (1.10-1.40). Regiões 11-18 (Sul/Sudeste/CO) têm fatores menores (1.00-1.15). Região inválida (>25) usa fator 1.0 | State-driven | BATCHPGT.NSN:L123-L150, L242-L246 | Confirmada | **DUPLICADA** de CALCBENF — mesmos 27 valores. Regiões 26-27 são 1.0 (filler) |
| BR-056 | Fator familiar: 0 dep=1.00; 1-2 dep=1.00+(N×0.05); 3-4 dep=1.10+((N-2)×0.03); 5+ dep=1.16+((N-4)×0.02) | State-driven | BATCHPGT.NSN:L249-L261 | Confirmada | **DUPLICADA** de CALCBENF. Mesma lógica escalonada |
| BR-057 | Fator renda: 5 faixas. ≤300=1.00; ≤600=0.85; ≤1000=0.70; ≤1500=0.55; >1500=0.40 | State-driven | BATCHPGT.NSN:L152-L163, L367-L376 | Confirmada | **DUPLICADA** de CALCBENF. Mesmos limites e fatores |
| BR-058 | Fator idade: ≥65 = 1.15; 60-64 = 1.10; <18 = 1.05; 18-59 = 1.00 | State-driven | BATCHPGT.NSN:L266-L278 | Confirmada | **DUPLICADA** de CALCBENF |
| BR-059 | Fórmula de benefício: VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RND × FATOR-IDADE × (1+FATOR-REAJ). Resultado truncado em 2 decimais | Ubiquitous | BATCHPGT.NSN:L281-L286 | Confirmada | **DUPLICADA** de CALCBENF. Idêntica fórmula. Truncamento via ×100 / 100 |
| BR-060 | Em dezembro (mês 12): 13° salário = VLR-BASE × FATOR-REG × FATOR-IDADE (sem fator familiar/renda). É somado ao bruto | Optional | BATCHPGT.NSN:L292-L296 | Confirmada | **DUPLICADA** de CALCBENF. TIPO-PGTO muda de 'N' para 'D' |
| BR-061 | Em dezembro, para programas tipo 'A' (assistencial): abono de natal = 15% do benefício mensal. Somado ao bruto | Optional | BATCHPGT.NSN:L297-L302 | Confirmada | **DUPLICADA** de CALCBENF |
| BR-062 | Desconto simplificado no batch: se VLR-BRUTO > R$500, desconto = 3% do bruto. Caso contrário, desconto = 0 | State-driven | BATCHPGT.NSN:L306-L310 | Inferred | <!-- MYSTERY: CALCDSCT tem 4 faixas progressivas e 6 tipos de desconto, mas BATCHPGT usa desconto fixo de 3% acima de 500. Divergência intencional (simplificação batch) ou bug de sincronização? O CALCDSCT nunca é chamado pelo batch --> |
| BR-063 | Valor líquido = bruto − desconto. Se negativo, trunca para zero | Ubiquitous | BATCHPGT.NSN:L313-L317 | Inferred | Proteção — líquido nunca negativo |
| BR-064 | Pagamento gravado com STATUS-PGTO = 'G' (gerado). Tipo 'N' (normal) ou 'D' (dezembro/13°) | Event-driven | BATCHPGT.NSN:L321-L333 | Confirmada | Status 'G' é o initial state no ciclo de vida do pagamento |
| BR-065 | O NUM-PAGTO é sequencial global — obtido buscando o maior existente e incrementando | Ubiquitous | BATCHPGT.NSN:L174-L178, L320 | Inferred | <!-- MYSTERY: sem lock ou transação ao obter sequência. Re-execução concorrente poderia gerar NUM-PAGTO duplicado. Provavelmente seguro porque batch roda em janela exclusiva --> |
| BR-066 | Log de progresso a cada 1000 registros gerados (write CPF corrente) | Ubiquitous | BATCHPGT.NSN:L342-L344 | Inferred | Monitoramento operacional |
| BR-067 | Ao final, o batch emite resumo: totais processados, gerados, ignorados, erros, e valores acumulados (bruto/desc/líquido/abono) | Ubiquitous | BATCHPGT.NSN:L348-L362 | Inferred | Controle operacional — validação de batimento |

---

## Regras de BATCHCON.NSN

> Programa batch de conciliação bancária — processa arquivo retorno CNAB 240 (Banco do Brasil), atualiza status de pagamentos e gera auditoria.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-068 | O sistema deverá processar apenas registros do tipo detalhe (TIPO-REG = '3') do arquivo CNAB 240. Headers (0), trailers (9) e lotes (1,5) são ignorados | Unwanted | BATCHCON.NSN:L113-L115 | Confirmada | Padrão CNAB 240 FEBRABAN |
| BR-069 | O layout CNAB 240 BB: CPF em posição 44-54, valor em 120-134 (centavos), data pagamento em 140-147, código retorno em 231-232, número documento em 74-83 | Ubiquitous | BATCHCON.NSN:L117-L122 | Confirmada | Layout específico Banco do Brasil |
| BR-070 | O valor no CNAB é em centavos — o sistema deverá dividir por 100 para converter em reais antes de comparar | Ubiquitous | BATCHCON.NSN:L128-L129 | Inferred | Padrão bancário brasileiro |
| BR-071 | Para conciliar, o sistema busca pagamento por NUM-PAGTO + CPF + COMPETENCIA. Todos os 3 campos devem coincidir | Ubiquitous | BATCHCON.NSN:L134-L143 | Inferred | Match triplo para segurança |
| BR-072 | Se o pagamento não for encontrado no SIFAP (match triplo falha), o sistema deverá registrar no log e continuar processando os demais registros | Unwanted | BATCHCON.NSN:L145-L152 | Inferred | Não é erro fatal — log e skip |
| BR-073 | O sistema deverá comparar VLR-LIQUIDO (SIFAP) com VLR-RETORNO (banco). Tolerância de divergência: R$0,01 (um centavo). Diferença ≤ 0,01 = conciliado; > 0,01 = divergente | State-driven | BATCHCON.NSN:L155-L161 | Inferred | <!-- MYSTERY: tolerância de 1 centavo é por arredondamento bancário ou por design? Não documentada em lugar nenhum --> |
| BR-074 | Quando conciliado E código retorno = '00' (pagamento efetuado): STATUS-PGTO muda de 'G' → 'P' (pago). DT-PAGAMENTO e COD-BANCO são atualizados | Event-driven | BATCHCON.NSN:L168-L176 | Confirmada | Transição principal do ciclo de vida |
| BR-075 | Quando conciliado E código retorno = '01' (pagamento devolvido): STATUS-PGTO muda de 'G' → 'D' (devolvido) | Event-driven | BATCHCON.NSN:L177-L183 | Confirmada | Beneficiário não sacou / conta inválida |
| BR-076 | Quando conciliado E código retorno = '02' (erro no pagamento): STATUS-PGTO muda de 'G' → 'E' (erro) | Event-driven | BATCHCON.NSN:L184-L190 | Inferred | Erro bancário técnico |
| BR-077 | Código de retorno diferente de '00', '01', '02' é desconhecido — o sistema registra no log mas não altera o status do pagamento | Unwanted | BATCHCON.NSN:L191-L194 | Inferred | Proteção contra novos códigos não mapeados |
| BR-078 | Toda conciliação bem-sucedida gera registro de auditoria com ACAO='CO', TABELA='PAGAMENTO', CHAVE=NUM-PAGTO, USUARIO='BATCH' | Event-driven | BATCHCON.NSN:L232-L244 | Confirmada | Rastreabilidade obrigatória (inclusão de 2014) |
| BR-079 | Toda divergência de valor gera registro de auditoria com ACAO='DV', registrando VLR-ANTERIOR (SIFAP) e VLR-NOVO (banco) | Event-driven | BATCHCON.NSN:L247-L263 | Confirmada | Permite investigação posterior de divergências |
| BR-080 | O SEQ-AUDIT é sequencial global — obtido buscando o maior existente no início do programa | Ubiquitous | BATCHCON.NSN:L80-L84 | Inferred | Mesmo padrão (e mesma fragilidade) do NUM-PAGTO em BATCHPGT |
| BR-081 | O código Banco Real (356) está comentado e mantido apenas como referência histórica (banco adquirido pelo Santander em 2007). Layout era diferente do BB | Ubiquitous | BATCHCON.NSN:L203-L218 | Inferred | Código morto preservado. Sub-rotina CONCILIA-REAL nunca chamada |
| BR-082 | O ciclo de vida completo do pagamento é: G (gerado) → P (pago) / D (devolvido) / E (erro). Não há transição reversa definida no código | State-driven | BATCHCON.NSN:L168-L194 | Confirmada | <!-- MYSTERY: e se um pagamento 'D' (devolvido) for reprocessado? O batch não verifica status atual antes de atualizar. Um re-envio ao banco poderia alterar 'D'→'P' sem controle --> |

---

## Regras de CALCCORR.NSN

> Programa de cálculo de correção monetária retroativa — aplica índices IPCA acumulados sobre pagamentos passados não corrigidos.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-083 | O sistema deverá rejeitar operação quando competência inicial > competência final (período inválido) | Unwanted | CALCCORR.NSN:L119-L122 | Inferred | Validação de entrada |
| BR-084 | O sistema processa apenas pagamentos do beneficiário informado (CPF) dentro do intervalo de competências [COMP-INI, COMP-FIM] | Ubiquitous | CALCCORR.NSN:L130-L140 | Inferred | Filtro triplo: CPF + competência ≥ início + competência ≤ fim |
| BR-085 | Pagamentos já corrigidos (IND-CORRIGIDO = 'S') são ignorados — o sistema não aplica correção dupla | Unwanted | CALCCORR.NSN:L142-L144 | Confirmada | Idempotência da correção |
| BR-086 | O índice acumulado é calculado multiplicando (1 + IPCA-mensal) para cada mês da competência do pagamento até a data atual. Fórmula: IND-ACUM = ∏(1 + IPCA[ano,mês]) | Ubiquitous | CALCCORR.NSN:L148-L149, L183-L194 | Confirmada | Juros compostos — multiplicação de fatores mensais |
| BR-087 | Valor corrigido = VLR-BRUTO × IND-ACUM. Truncado em 2 casas decimais (×100, inteiro, ÷100) | Ubiquitous | CALCCORR.NSN:L152-L155 | Confirmada | Mesmo padrão de truncamento do CALCBENF/BATCHPGT |
| BR-088 | A correção é aplicada APENAS quando o valor corrigido é MAIOR que o original (VLR-DIFF > 0). Deflação (correção negativa) é ignorada | State-driven | CALCCORR.NSN:L158-L165 | Inferred | <!-- MYSTERY: por que ignorar deflação? O sistema nunca reduz um pagamento retroativamente — proteção ao beneficiário ou limitação legal? --> |
| BR-089 | Ao aplicar correção: grava VLR-CORRECAO (valor corrigido total), DT-CORRECAO (data de hoje), e marca IND-CORRIGIDO = 'S' | Event-driven | CALCCORR.NSN:L159-L163 | Inferred | Campos dedicados no DDM PAGAMENTO para rastreio |
| BR-090 | Tabela IPCA hardcoded com índices mensais para anos 2010, 2011 e 2012. Última carga: 2014 (comentário no código) | Ubiquitous | CALCCORR.NSN:L54-L95 | Inferred | <!-- MYSTERY: tabela parada em 2012 (última carga 2014). Para períodos pós-2012, o FOR não encontra o ano e retorna IND-ACUM=1.0 (sem correção). O programa ficou "congelado" sem atualização de índices? Ou há outro mecanismo de carga? --> |
| BR-091 | Se o ano da competência não existe na tabela IPCA (fora de 2010-2012), o fator daquele mês é 1.0 (neutro) — efetivamente sem correção | State-driven | CALCCORR.NSN:L183-L194 | Inferred | Falha silenciosa — sem mensagem de erro |
| BR-092 | Código comentado preserva lógica de correção do Plano Verão (1989-1991): fator fixo 2.75 + fator adicional 1.4289 para competências pré-jul/1989. Marcava IND-CORRIGIDO = 'V' (Verão) | Ubiquitous | CALCCORR.NSN:L97-L108 | Inferred | Código morto — referência histórica da transição Cruzado→Cruzeiro |

---

## Regras de VALBENEF.NSN

> Programa de validação de dados cadastrais do beneficiário — rotina chamada antes de gravações, acumula múltiplos erros antes de retornar resultado.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-093 | A validação acumula todos os erros encontrados (até 10) e retorna lista completa — não para no primeiro erro | Ubiquitous | VALBENEF.NSN:L32, L113-L170 | Inferred | Array #MSG-ERRO(10). Boa UX: mostra todos os problemas de uma vez |
| BR-094 | Resultado = 'V' (válido) se zero erros; = 'I' (inválido) se qualquer erro encontrado | Ubiquitous | VALBENEF.NSN:L108, L117 | Inferred | Binário — sem "warnings" |
| BR-095 | CPF com todos os dígitos iguais (111...1, 222...2 etc.) é inválido — EXCETO CPFs iniciando com '000' que são aceitos (teste governo) | Unwanted | VALBENEF.NSN:L189-L197 | Inferred | <!-- MYSTERY: CPF 00000000000 é aceito como válido! É para testes internos do governo? Essa exceção não existe no algoritmo oficial da Receita Federal. Possível backdoor de teste que ficou em produção --> |
| BR-096 | Validação CPF módulo 11: mesmo algoritmo do CADBENEF (pesos 10→2 para DV1, pesos 11→2 para DV2) | Ubiquitous | VALBENEF.NSN:L200-L226 | Confirmada | **DUPLICADA** de CADBENEF — idêntica, com exceção do check de dígitos iguais que só existe aqui |
| BR-097 | Data de nascimento válida: ano entre 1900 e ano-atual, mês entre 1 e 12, dia entre 1 e dias-do-mês | Unwanted | VALBENEF.NSN:L239-L259 | Confirmada | Fevereiro aceita até 29 (considera bissexto sempre) |
| BR-098 | Fevereiro sempre aceita até dia 29 — não verifica se o ano é de fato bissexto | State-driven | VALBENEF.NSN:L100 | Inferred | <!-- MYSTERY: 29/02 em ano não-bissexto passa na validação. Bug ou simplificação intencional? Ex: 29/02/2001 seria aceito --> |
| BR-099 | Nome válido: não pode ser vazio E deve conter pelo menos um espaço (exige nome + sobrenome) | Unwanted | VALBENEF.NSN:L263-L278 | Confirmada | Inclusão de 2010 (José Ferreira). Nome de uma palavra só é rejeitado |
| BR-100 | UF válida: deve constar em tabela hardcoded de 27 UFs brasileiras. Campo em branco é aceito (não obrigatório) | Optional | VALBENEF.NSN:L145-L157 | Confirmada | IF #UF NE ' ' — só valida se preenchido. Tabela cobre todos os 26 estados + DF |
| BR-101 | Status válido: deve ser um de 'A' (ativo), 'S' (suspenso), 'C' (cancelado), 'I' (inativo), 'D' (desligado). Qualquer outro valor é inválido | Unwanted | VALBENEF.NSN:L162-L167 | Confirmada | 5 estados possíveis — consistente com BR-022 (VALELEG) |

---

## Regras de VALDOCS.NSN

> Programa de validação de documentos do beneficiário — CPF, RG e verificação de prefixos especiais (governo/teste).
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-102 | CPF = 0 é inválido — o sistema deverá rejeitar antes de aplicar módulo 11 | Unwanted | VALDOCS.NSN:L102-L105 | Confirmada | Mesma regra que BR-036 (CADBENEF) |
| BR-103 | Validação CPF módulo 11: idêntica às implementações de CADBENEF e VALBENEF (sem check de dígitos iguais) | Ubiquitous | VALDOCS.NSN:L106-L142 | Confirmada | **DUPLICADA** — 3ª cópia do mesmo algoritmo na codebase |
| BR-104 | RG é obrigatório — campo em branco é inválido | Unwanted | VALDOCS.NSN:L148-L151 | Inferred | Inclusão de 2003 (Ana Lucia) |
| BR-105 | RG deve ter pelo menos 5 caracteres de conteúdo (posição do primeiro espaço > 5, ou 15 se sem espaços) | Unwanted | VALDOCS.NSN:L153-L161 | Inferred | Tamanho mínimo — rejeita "12" ou "AB" como RG |
| BR-106 | Quando os 3 primeiros dígitos do CPF correspondem a um dos 8 prefixos especiais (000, 001, 002, 010, 011, 099, 100, 999), o sistema deverá: marcar documento como especial, IGNORAR qualquer erro de CPF anterior, forçar resultado = 'V' (válido), e zerar erros acumulados | Optional | VALDOCS.NSN:L167-L181 | Inferred | <!-- MYSTERY: esta sub-rotina sobrescreve TODOS os erros — incluindo RG! Se o CPF tem prefixo especial, o RG inválido é perdoado. Intencional (beneficiários de "documento especial" não precisam de RG)? Ou bug na ordem de execução? Alteração de 2011 (Roberto Mendes — "AJUSTE CHECK ESPEC") --> |
| BR-107 | Os 8 prefixos especiais são: 000, 001, 002, 010, 011, 099, 100, 999. CPFs com esses prefixos contornam validação normal | Ubiquitous | VALDOCS.NSN:L49-L56 | Inferred | Relação com BR-095 (VALBENEF aceita "000..."). Lista mais ampla aqui — 8 prefixos vs 1 exceção em VALBENEF |
| BR-108 | A validação de documentos acumula erros em array (até 5 mensagens), mesmo padrão do VALBENEF (que acumula até 10) | Ubiquitous | VALDOCS.NSN:L26, L69-L92 | Inferred | Design pattern consistente entre programas de validação |
| BR-109 | Título de eleitor e CTPS são solicitados na tela de input mas NÃO são validados pelo programa — apenas coletados | Ubiquitous | VALDOCS.NSN:L60-L63 | Inferred | <!-- MYSTERY: campos pedidos mas nunca usados. Foram planejados para uma validação futura que nunca foi implementada? Ou são registrados em outro lugar não visível aqui? --> |

---

## Regras de CADDEPEND.NSN

> Programa de cadastro de dependentes — inclusão de dependentes vinculados ao beneficiário titular via PE group (periodic group Adabas).
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-110 | O beneficiário titular deve existir no cadastro (busca por CPF). Se não encontrado, operação é bloqueada | Unwanted | CADDEPEND.NSN:L48-L55 | Inferred | Integridade referencial manual |
| BR-111 | Beneficiários com status 'C' (cancelado) ou 'D' (desligado) não permitem inclusão de dependentes | Unwanted | CADDEPEND.NSN:L57-L60 | Inferred | Status 'S' (suspenso) e 'I' (inativo) permitem inclusão — apenas C e D bloqueiam |
| BR-112 | O limite máximo de dependentes por beneficiário é 5. Ao atingir, novas inclusões são bloqueadas | Unwanted | CADDEPEND.NSN:L64-L67 | Confirmada | RN-004/doc 2012 "máximo 5 dependentes" |
| BR-113 | Nome do dependente é obrigatório — campo em branco bloqueia a inclusão | Unwanted | CADDEPEND.NSN:L78-L81 | Inferred | |
| BR-114 | Parentesco deve ser um dos valores: 'FI' (filho), 'CO' (cônjuge), 'IR' (irmão), 'OU' (outro). Qualquer outro valor é rejeitado | Unwanted | CADDEPEND.NSN:L83-L87 | Confirmada | 4 tipos de vínculo. Doc 2012 menciona "grau de parentesco" |
| BR-115 | Se o CPF do dependente (quando informado, ≠ 0) já existe na lista de dependentes do mesmo titular, o sistema rejeita como "DEPENDENTE JA CADASTRADO" | Unwanted | CADDEPEND.NSN:L93-L101 | Inferred | Proteção contra duplicata. CPF-DEP = 0 (não informado) não é verificado |
| BR-116 | Dependentes são armazenados em PE group (periodic group) — estrutura array dentro do registro Adabas do beneficiário. Cada ocorrência tem: NOME-DEP, DT-NASC-DEP, PARENTESCO, CPF-DEP, DOC-DEP, SEXO-DEP | Ubiquitous | CADDEPEND.NSN:L20-L26, L107-L115 | Confirmada | Modelo desnormalizado — dependentes vivem dentro do registro do titular |
| BR-117 | O campo NUM-DEPENDENTES do titular é incrementado e atualizado a cada inclusão | Event-driven | CADDEPEND.NSN:L108-L116 | Inferred | Contador redundante (poderia ser derivado do PE count) mas usado em CALCBENF para fator familiar |
| BR-118 | O programa opera em loop interativo — após cada inclusão, pergunta se deseja incluir outro. 'S' continua, qualquer outro valor encerra | Optional | CADDEPEND.NSN:L120-L123 | Inferred | UX de terminal — one-by-one com confirmação |

---

## Regras de CADPROG.NSN

> Programa de cadastro de programas sociais — inclusão e consulta. Calcula "Fator K" no momento da inclusão.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-119 | O sistema aceita apenas operações 'I' (inclusão) ou 'C' (consulta). Não há alteração nem exclusão de programas sociais | Unwanted | CADPROG.NSN:L51-L54 | Inferred | Programas sociais são imutáveis após criação no código |
| BR-120 | Se o código de programa já existe no cadastro, a inclusão é rejeitada ("PROGRAMA JA CADASTRADO") | Unwanted | CADPROG.NSN:L80-L84 | Inferred | Unicidade por COD-PROGRAMA |
| BR-121 | O "Fator K" é calculado como: FATOR-K = 1.00 + (FATOR-REAJUSTE × 0.347215). O VLR-BASE gravado é o VLR-BASE informado × FATOR-K | Ubiquitous | CADPROG.NSN:L87-L88 | Inferred | <!-- MYSTERY: RESOLVIDO! O "Fator K" mencionado no doc 2012 é esta fórmula. A constante 0.347215 não tem explicação no código — possivelmente derivada de política econômica ou cálculo atuarial. Nota: o FATOR-REAJUSTE é gravado separadamente e usado NOVAMENTE no CALCBENF/BATCHPGT na fórmula (1+FATOR-REAJ). Isso significa que o reajuste é aplicado DUAS VEZES: uma no VLR-BASE (via Fator K) e outra no cálculo mensal --> |
| BR-122 | O status inicial de todo programa social é 'A' (ativo) — hardcoded na inclusão | Event-driven | CADPROG.NSN:L96 | Inferred | Consistente com BR-033, BR-054 |
| BR-123 | DT-FIM = 0 significa programa com vigência indeterminada (sem data de encerramento) | Optional | CADPROG.NSN:L68 | Inferred | Comentário no INPUT: "(0=INDETERMINADO)" |
| BR-124 | O tipo do programa deve ser 'A' (assistencial), 'P' (previdenciário) ou 'T' (trabalho) — coletado no input mas NÃO validado no código | Ubiquitous | CADPROG.NSN:L63 | Inferred | <!-- MYSTERY: diferente de VALELEG que valida tipo A/P/T, CADPROG aceita qualquer valor! Tipos inválidos inseridos aqui causariam rejeição apenas no momento da elegibilidade (BR-030) --> |
| BR-125 | O COD-ELEGIBILIDADE é campo alfanumérico de 5 posições — cada posição é um flag posicional usado por VALELEG (BR-031, BR-032) | Ubiquitous | CADPROG.NSN:L17, L65 | Confirmada | Inclusão de 2012 (Fernanda Costa — "NOVOS COD ELEG") |
| BR-126 | RENDA-MAX, IDADE-MIN e IDADE-MAX são parâmetros configuráveis por programa — usados na validação de elegibilidade (BR-023 a BR-025) | Ubiquitous | CADPROG.NSN:L23-L25, L67-L69 | Confirmada | Parametrização que permite regras diferentes por programa |

---

## Regras de CONSBENF.NSN

> Programa de consulta online de beneficiário — tela 3270, exibe dados cadastrais mascarados e histórico de últimos 12 pagamentos.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-127 | O sistema permite busca por CPF (tipo 'C') ou por NIS (tipo 'N'). Se tipo não informado, assume 'C' (CPF) como padrão | Optional | CONSBENF.NSN:L78-L96 | Inferred | Dois pontos de acesso ao beneficiário |
| BR-128 | O CPF é exibido mascarado no formato `***.***. XXX-XX` — os 6 primeiros dígitos são ocultados, apenas os 5 últimos são visíveis | Ubiquitous | CONSBENF.NSN:L106-L107, L173-L188 | Confirmada | Proteção LGPD/dados sensíveis. Inclusão de 2003 |
| BR-129 | Quando CPF < 10000000000 (menos de 11 dígitos armazenados — com zeros à esquerda), a máscara exibe os 3 PRIMEIROS dígitos em vez dos últimos | State-driven | CONSBENF.NSN:L177-L180 | Inferred | <!-- MYSTERY: comentário no código diz "INCONSISTENCIA CONHECIDA". Documenta o bug mas não corrige. Nota: "NAO CORRIGIR SEM APROVACAO DA AUDITORIA". Bug de 2003 preservado por 23 anos por medo de impacto em auditoria --> |
| BR-130 | O status é traduzido para descrição legível: A=ATIVO, S=SUSPENSO, C=CANCELADO, I=INATIVO, D=DESLIGADO. Status desconhecido exibe "DESCONHECIDO" | Ubiquitous | CONSBENF.NSN:L112-L124 | Confirmada | Consistente com BR-101 (5 status válidos) |
| BR-131 | O histórico de pagamentos exibe no máximo os 12 últimos registros (lidos em ordem por CPF-BENEF) | Optional | CONSBENF.NSN:L148-L162 | Confirmada | Limitação de tela 3270 (espaço de exibição) |
| BR-132 | O programa tenta usar MAP (tela formatada 3270) primeiro. Se falhar (*ERROR-NR ≠ 0), apresenta tela alternativa via INPUT | Optional | CONSBENF.NSN:L68-L74 | Inferred | Fallback para ambientes sem MAP instalado — robustez operacional |

---

## Regras de BATCHREL.NSN

> Programa batch de geração de relatório consolidado mensal — sumariza pagamentos por região e status, saída para impressora mainframe.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-133 | O mapeamento de COD-REGIAO (1-27) para macro-regiões é: 1-5=Norte, 6-10=Nordeste, 11-15=Sudeste, 16-20=Sul, 21+=Centro-Oeste | Ubiquitous | BATCHREL.NSN:L102-L119 | Inferred | Mapeamento diferente da tabela de fatores regionais (27 UFs) — aqui agrupa em 5 regiões |
| BR-134 | O arredondamento no relatório usa ROUND (soma +0.005 antes de truncar), diferente do CALCBENF/BATCHPGT que usam TRUNCATE | State-driven | BATCHREL.NSN:L123-L126 | Inferred | <!-- MYSTERY: divergência intencional? Relatório arredonda mas cálculo trunca. Totais do relatório podem diferir dos valores individuais somados --> |
| BR-135 | Os 5 status de pagamento mapeados no relatório: G=Gerado, P=Pago, C=Cancelado, D=Devolvido, E=Estornado | Ubiquitous | BATCHREL.NSN:L81-L86, L131-L142 | Confirmada | Status 'E' aqui é "Estornado" — no BATCHCON é mapeado como "Erro". Mesmo código, semântica diferente? |
| BR-136 | Paginação padrão impressora mainframe: 66 linhas por página com form feed entre páginas | Ubiquitous | BATCHREL.NSN:L67 | Inferred | Padrão de impressão corporativa |
| BR-137 | Relatório emite totais gerais (bruto/desc/líquido/qtd) e subtotais por região e por status | Ubiquitous | BATCHREL.NSN:L153-L171 | Inferred | Controle gerencial |

---

## Regras de RELAUDIT.NSN

> Programa de relatório de trilha de auditoria — filtra eventos por período, ação, usuário e tabela. Exclui ações 'EX'.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-138 | Ações com código 'EX' (exclusão) são SEMPRE filtradas do relatório de auditoria — nunca exibidas, independente dos filtros | Unwanted | RELAUDIT.NSN:L101-L104 | Inferred | <!-- MYSTERY: por que ocultar exclusões do relatório de auditoria? Isso contradiz o propósito de uma trilha de auditoria. Possível: exclusões lógicas eram "soft deletes" e poluíam o relatório, ou havia requisito legal de não mostrar operações reversas --> |
| BR-139 | Os filtros são opcionais (campo em branco = todos): ação, usuário, tabela. Podem ser combinados (AND lógico) | Optional | RELAUDIT.NSN:L106-L124 | Inferred | Flexibilidade de consulta |
| BR-140 | Data inicial = 0 assume 01/01/1997 (data de criação do sistema). Data final = 0 assume data de hoje | Optional | RELAUDIT.NSN:L85-L90 | Inferred | Defaults razoáveis |
| BR-141 | Os códigos de ação reconhecidos são: IN (inclusão), AL (alteração), CO (conciliação), CN (consulta), DV (divergência), EX (exclusão — filtrada) | Ubiquitous | RELAUDIT.NSN:L128-L146 | Confirmada | Vocabulário completo de auditoria do sistema |
| BR-142 | Saída pode ser Tela ('T', padrão) ou Impressora ('I'). Na impressora, inclui campo DESCRICAO; na tela, omite por espaço | Optional | RELAUDIT.NSN:L82, L156-L166 | Inferred | Limitação de largura do terminal 3270 (80 cols) vs impressora (132 cols) |
| BR-143 | Hora formatada de HHMMSS numérico para HH:MM:SS string para exibição | Ubiquitous | RELAUDIT.NSN:L150-L152 | Inferred | Conversão de formato de apresentação |

---

## Regras de RELPGT.NSN

> Programa de relatório analítico de pagamentos por período — listagem detalhada com subtotais por programa e total geral.
> Analisado em 20/05/2026.

| # | Declaração da Regra | Candidato EARS | Fonte | Classificação | Notas |
|---|---|---|---|---|---|
| BR-144 | O relatório permite filtro por código de programa (0=todos). Processa pagamentos dentro do intervalo de competências informado | Optional | RELPGT.NSN:L76-L82 | Inferred | |
| BR-145 | Subtotais são emitidos automaticamente a cada quebra de programa (AT BREAK por COD-PROGRAMA) | Event-driven | RELPGT.NSN:L85-L91 | Confirmada | Inclusão de 2010 (José Ferreira — "INC SUBTOTAL PROG") |
| BR-146 | CPF mascarado no relatório no formato `***.XXX.XXX-XX` — oculta apenas os 3 primeiros dígitos (variante diferente de CONSBENF!) | Ubiquitous | RELPGT.NSN:L104-L107 | Inferred | <!-- MYSTERY: CONSBENF oculta os 6 primeiros (***.***.XXX-XX) mas RELPGT oculta só os 3 primeiros (***.XXX.XXX-XX). Duas políticas de mascaramento diferentes no mesmo sistema! --> |
| BR-147 | Tipos de pagamento exibidos: N=NORMAL, D=DECIMO (13°), T=TERCEIRO. Tipo 'T' (terceiro) NÃO existe em outros programas | Ubiquitous | RELPGT.NSN:L110-L119 | Inferred | <!-- MYSTERY: BATCHPGT gera apenas 'N' e 'D'. De onde vem tipo 'T'? Possível funcionalidade planejada (terço de férias?) nunca implementada no batch --> |
| BR-148 | Status no relatório: G=GERADO, P=PAGO, C=CANCELAD, D=DEVOLVID, E=ESTORNAD (truncados por espaço de coluna) | Ubiquitous | RELPGT.NSN:L122-L136 | Confirmada | Mesmo vocabulário de BATCHREL |
| BR-149 | Paginação 66 linhas. Cabeçalho com período e data. Subtotal por programa (bruto+líquido+qtd). Total geral (bruto+desc+líquido+abono) | Ubiquitous | RELPGT.NSN:L45, L161-L176, L179-L186 | Inferred | |
| BR-150 | Busca nome do beneficiário (truncado em 30 chars) e UF para exibição. Se beneficiário não encontrado, exibe espaços em branco | Optional | RELPGT.NSN:L94-L100 | Inferred | JOIN manual entre PAGAMENTO e BENEFICIARIO |

## Resumo Estatístico

- Total de regras encontradas: **150**
- Regras críticas: **12** (BR-003 a BR-010, BR-013 a BR-015)
- Regras de elegibilidade: **14** (BR-021 a BR-034)
- Regras de cadastro: **13** (BR-035 a BR-047)
- Regras de pagamento batch: **20** (BR-048 a BR-067)
- Regras de conciliação: **15** (BR-068 a BR-082)
- Regras de correção monetária: **10** (BR-083 a BR-092)
- Regras de validação cadastral: **9** (BR-093 a BR-101)
- Regras de validação de documentos: **8** (BR-102 a BR-109)
- Regras de dependentes: **9** (BR-110 a BR-118)
- Regras de programas sociais: **8** (BR-119 a BR-126)
- Regras de consulta online: **6** (BR-127 a BR-132)
- Regras de relatórios: **18** (BR-133 a BR-150)
- Regras com duplicação entre programas: **12** (CPF módulo 11 em 3 programas)
- Regras confirmadas por documentação 2012: **58**
- Regras inferred (só código): **69**
- Regras que resolvem pendências do doc 2012: **5** (13°, abono, exceção judicial, bypass região 99, Fator K)
- Mistérios identificados: **23** (1 resolvido: Fator K)

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
<a href="dependency-map.md"><strong>dependency-map.md</strong></a><br/>
<sub>Mapa de quem chama quem.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

