<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Glossário do SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S1](https://img.shields.io/badge/PREENCHA-Durante%20S1-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 1](README.md) → **glossary**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 1 (Arqueologia).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento totalmente preenchido com os dados reais do legado SIFAP
> 2. Rastreabilidade para `01-arqueologia/legado-sifap/` (programas `.NSN` e DDMs)
> 3. Base de evidência usada nas EARS do Estágio 2 (`source_legacy:`)
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Preencha esta tabela com todos os termos, abreviações e siglas encontrados no código Natural/Adabas.
> **Meta: no mínimo 30 termos.**

## Por que isso importa

Sistemas legados têm vocabulário próprio que ninguém documenta em lugar nenhum — só está no nome das variáveis. Se o time do Estágio 2 não souber o que `DSCT`, `BENF`, `PE` ou `CTC` significam, vai escrever uma spec sobre o que ele _acha_ que isso significa. Glossário é o que evita esse desencontro.

## Como preencher

- **Termo**: a abreviação ou sigla exatamente como aparece no código
- **Expansão**: o significado completo do termo
- **Programa**: em qual arquivo `.NSN` ou `.ddm` o termo foi encontrado
- **Contexto**: breve explicação de como/onde o termo é usado

## Dica de extração

Prompt útil no Copilot Chat (cole o conteúdo de 2–3 arquivos `.NSN` no chat antes):

> _"Liste todas as abreviações e siglas usadas neste código Natural. Para cada uma, sugira a expansão e marque com 'CONFIRMADO' ou 'HIPÓTESE'."_

## Termos encontrados

| #   | Termo | Expansão | Programa | Contexto |
| --- | ----- | -------- | -------- | -------- |
| 1 | SIFAP | Sistema de Fiscalização e Administração de Pagamentos | Todos | Nome do sistema legado completo |
| 2 | BENF | Benefício / Beneficiário | CALCBENF, CADBENEF, CONSBENF, VALBENEF | Abreviação usada em nomes de programa e variáveis |
| 3 | PGT / PGTO | Pagamento | BATCHPGT, RELPGT | Registro financeiro de crédito ao beneficiário |
| 4 | DSCT | Desconto | CALCDSCT | Dedução sobre valor bruto. 6 tipos: C,I,J,S,P,A |
| 5 | CORR | Correção (monetária) | CALCCORR | Recálculo retroativo por índice IPCA |
| 6 | CON | Conciliação | BATCHCON | Processo de batimento SIFAP × retorno bancário |
| 7 | REL | Relatório | BATCHREL, RELAUDIT, RELPGT | Programas de emissão de relatórios |
| 8 | ELEG | Elegibilidade | VALELEG | Validação se beneficiário pode participar de programa |
| 9 | DEPEND | Dependente | CADDEPEND | Pessoa vinculada ao beneficiário titular (PE group) |
| 10 | PROG | Programa Social | CADPROG, PROGRAMA-SOCIAL.ddm | Entidade que define regras de benefício (tipo A/P/T) |
| 11 | PE | Periodic Group | CADDEPEND, BENEFICIARIO.ddm | Estrutura Adabas: array de registros repetitivos dentro de um registro |
| 12 | DDM | Data Definition Module | Todos os .ddm | Schema de arquivo Adabas — define campos, tipos e índices |
| 13 | FDT | Field Definition Table | DDMs | Tabela interna Adabas que descreve a estrutura do arquivo |
| 14 | ISN | Internal Sequence Number | DDMs | Identificador interno de registro no Adabas (análogo a rowid) |
| 15 | DE | Descriptor | DDMs | Campo indexado para busca rápida (WHERE clause) |
| 16 | MU | Multiple Value | DDMs | Campo que aceita múltiplos valores (array simples) |
| 17 | VLR | Valor | CALCBENF, BATCHPGT, CALCCORR | Prefixo para variáveis monetárias (VLR-BASE, VLR-BRUTO, VLR-LIQ) |
| 18 | DT | Data | Todos | Prefixo para campos de data no formato AAAAMMDD (N8) |
| 19 | CPF | Cadastro de Pessoa Física | Todos | Documento de identificação brasileiro (11 dígitos, mod-11) |
| 20 | NIS | Número de Identificação Social | CADBENEF, VALELEG, CONSBENF | Identificador PIS/PASEP usado em programas sociais |
| 21 | UF | Unidade Federativa | CADBENEF, VALBENEF | Sigla do estado brasileiro (2 chars, 27 válidos) |
| 22 | CNAB | Centro Nacional de Automação Bancária | BATCHCON | Padrão de arquivo de retorno bancário (layout 240 posições) |
| 23 | IPCA | Índice de Preços ao Consumidor Amplo | CALCCORR | Índice oficial de inflação (IBGE) usado para correção monetária |
| 24 | FATOR-K | Fator de Ajuste Inicial | CADPROG | Fórmula misteriosa: 1.00 + (FATOR-REAJ × 0.347215). Aplicado ao VLR-BASE na inclusão |
| 25 | COMPETENCIA | Mês/Ano de referência do pagamento | BATCHPGT, BATCHCON, relatórios | Formato AAAAMM (N6). Identifica a qual período o pagamento se refere |
| 26 | STATUS-PGTO | Status do Pagamento | PAGAMENTO.ddm | Ciclo: G(gerado) → P(pago) / D(devolvido) / E(erro) |
| 27 | STATUS (benef) | Situação cadastral do beneficiário | BENEFICIARIO.ddm | A(ativo), S(suspenso), C(cancelado), I(inativo), D(desligado) |
| 28 | TIPO-PGTO | Tipo de Pagamento | BATCHPGT, RELPGT | N(normal), D(dezembro/13°), T(terceiro — não gerado?) |
| 29 | TIPO (prog) | Tipo de Programa Social | CADPROG, VALELEG | A(assistencial), P(previdenciário), T(trabalho) |
| 30 | COD-REGIAO | Código de Região/UF | BENEFICIARIO.ddm | 1-27 (mapeado para UFs). Regiões 1-5=Norte, 6-10=NE, 11-15=SE, 16-20=Sul, 21+=CO |
| 31 | PARENTESCO | Grau de parentesco do dependente | CADDEPEND | FI(filho), CO(cônjuge), IR(irmão), OU(outro) |
| 32 | COD-ELEGIBILIDADE | Código de Elegibilidade Específica | PROGRAMA-SOCIAL.ddm, VALELEG | Campo A5 com flags posicionais: 1°char='R' exige NIS, 2°char='D' exige dependentes |
| 33 | RENDA-FAM / RENDA-FAMILIAR | Renda familiar mensal | BENEFICIARIO.ddm | Usado em faixas de desconto e elegibilidade. 5 faixas: ≤300, ≤600, ≤1000, ≤1500, >1500 |
| 34 | NUM-DEP / NUM-DEPENDENTES | Número de dependentes | BENEFICIARIO.ddm | Contador redundante (vs PE count). Usado no fator familiar do cálculo |
| 35 | FATOR-REAJUSTE | Fator de reajuste anual | PROGRAMA-SOCIAL.ddm | N3.4. Aplicado 2x: no Fator K (inclusão) e no cálculo mensal (1+FATOR-REAJ) |
| 36 | SEQ-AUDIT | Sequência de Auditoria | AUDITORIA.ddm | Chave sequencial global da trilha de auditoria |
| 37 | ACAO (audit) | Código de Ação na Auditoria | AUDITORIA.ddm | IN(inclusão), AL(alteração), CO(conciliação), CN(consulta), DV(divergência), EX(exclusão) |
| 38 | IND-CORRIGIDO | Indicador de Correção Aplicada | PAGAMENTO.ddm | S(sim, IPCA), V(Plano Verão — código morto), branco(não corrigido) |
| 39 | DV / DV1 / DV2 | Dígito Verificador | CADBENEF, VALBENEF, VALDOCS | Dígitos de controle do CPF (algoritmo módulo 11) |
| 40 | MAP | Tela Formatada 3270 | CONSBENF | Definição de layout de terminal para interação online |
| 41 | BATCH | Processamento em Lote | BATCHPGT, BATCHCON, BATCHREL | Prefixo de programas que rodam sem interação (scheduled) |
| 42 | CAD | Cadastro | CADBENEF, CADDEPEND, CADPROG | Prefixo de programas de manutenção de dados (CRUD) |
| 43 | CALC | Cálculo | CALCBENF, CALCDSCT, CALCCORR | Prefixo de programas de processamento numérico/financeiro |
| 44 | VAL | Validação | VALBENEF, VALDOCS, VALELEG | Prefixo de programas que verificam integridade de dados |
| 45 | CONS | Consulta | CONSBENF | Prefixo de programas de leitura/exibição (read-only) |

> Adicione mais linhas conforme necessário. Não se limite a 30!

## Exemplo de linha bem preenchida

| #   | Termo  | Expansão | Programa                        | Contexto                                                                                                         |
| --- | ------ | -------- | ------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| 1   | `DSCT` | Desconto | `CALCDSCT.NSN`, `PAGAMENTO.ddm` | Tipo de dedução aplicada sobre valor bruto do pagamento. Tipos: 'J' (judicial), 'I' (imposto), 'T' (trabalhista) |

## Observações

- Anote aqui qualquer padrão de nomenclatura que o time identificou:
  - **Prefixos de programa**: BATCH (lote), CAD (cadastro), CALC (cálculo), VAL (validação), CONS (consulta), REL (relatório)
  - **Prefixos de variável**: `#` = variável local, `VLR-` = valor monetário, `DT-` = data, `QTD-` = quantidade, `TOT-` = total acumulador, `IDX-` = índice de array
  - **Sufixos de VIEW**: `-V` indica uma view Adabas (ex: BENEFICIARIO-V, PAGAMENTO-V)
- Convenções de prefixo/sufixo encontradas:
  - Campos N8 para datas (AAAAMMDD), N6 para competências (AAAAMM), N11 para CPF
  - Status sempre A1 (1 char): A/S/C/I/D para beneficiário, G/P/D/E para pagamento, A para programa
  - Fatores sempre N3.4 (3 inteiros, 4 decimais): FATOR-REG, FATOR-FAM, FATOR-RND, FATOR-IDADE
- Termos ambíguos que precisam de validação com especialista:
  - **FATOR-K / 0.347215**: origem desconhecida (atuarial? econômica?)
  - **Região 99**: bypass ou caso legítimo?
  - **Status 'E'**: "Estornado" (BATCHREL/RELPGT) vs "Erro" (BATCHCON) — mesmo código, semântica diferente?
  - **Tipo 'T'**: "Trabalho" (programa) vs "Terceiro" (pagamento) — coincidência de letra?

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
<a href="business-rules-catalog.md"><strong>business-rules-catalog.md</strong></a><br/>
<sub>Catálogo de regras.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="README.md">Voltar ao Kit PT-BR</a></sub>

