<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Glossário do SIFAP Legado

![ESTÁGIO 01 Arqueologia](https://img.shields.io/badge/ESTÁGIO-01%20Arqueologia-F25022?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge)

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

Sistemas legados têm vocabulário próprio que ninguém documenta em lugar nenhum — só está no nome das variáveis. Se o time do Estágio 2 não souber o que `DSCT`, `BENF`, `PE` ou `CTC` significam, vai inventar nomes diferentes para as mesmas coisas e o sistema novo vira uma colcha de retalhos.

## Como preencher

- **Termo**: a abreviação ou sigla exatamente como aparece no código
- **Expansão**: o significado completo do termo
- **Programa**: em qual arquivo `.NSN` ou `.ddm` o termo foi encontrado
- **Contexto**: breve explicação de como/onde o termo é usado

## Dica de extração

Prompt útil no Copilot Chat (cole o conteúdo de 2–3 arquivos `.NSN` no chat antes):

> _"Liste todas as abreviações e siglas usadas neste código Natural. Para cada uma, sugira a expansão e marque com 'CONFIRMADO' ou 'HIPÓTESE'."_

## Termos encontrados

| #  | Termo | Expansão | Programa | Contexto |
|----|-------|----------|----------|----------|
| 1  | `SIFAP` | Sistema de Fiscalização e Administração de Pagamentos | `legado-sifap/README.md` | Sigla do sistema. Gestão de pagamentos de benefícios sociais federais; em produção desde 1998, v4.1.2 (2018). |
| 2  | `DDM` | Data Definition Module | `legado-sifap/COMO-LER-NATURAL.md`, `adabas-ddms/*.ddm` | Definição de view do Adabas — equivale ao "schema" de uma tabela. SIFAP tem 4. |
| 3  | `DE` | Descriptor | `BENEFICIARIO.ddm#L92` | Campo indexado para busca rápida no Adabas. Vira `INDEX` no PostgreSQL. |
| 4  | `MU` | Multiple Value Field | `BENEFICIARIO.ddm#L94`, `PROGRAMA-SOCIAL.ddm#L68` | Campo com múltiplos valores na mesma linha. No Estágio 3 vira tabela filha. |
| 5  | `PE` | Periodic Group | `BENEFICIARIO.ddm#L61`, `PAGAMENTO.ddm#L38` | Grupo periódico (subregistros repetidos). `GRP-DEPENDENTE` (max 10), `GRP-DESCONTO` (max 8). Vira tabela filha. |
| 6  | `FNR` | File Number (Adabas) | `BENEFICIARIO.ddm#L15` | Identificador do arquivo Adabas. BENEFICIARIO=150, PROGRAMA-SOCIAL=151, PAGAMENTO=152, AUDITORIA=153. |
| 7  | `ISN` | Internal Sequence Number | `BENEFICIARIO.ddm#L20` | ID interno único do Adabas. `NUM-INSCRICAO` é "ISN alternativo / matrícula". |
| 8  | `CALLNAT` | Call Natural (subprogram) | `legado-sifap/COMO-LER-NATURAL.md#L120` | Chamada de subprograma Natural. Usado para mapear grafo de dependências. |
| 9  | `BENF` / `BENEF` | Beneficiário | `CADBENEF.NSN`, `CALCBENF.NSN`, `VALBENEF.NSN`, `CONSBENF.NSN` | Pessoa cadastrada para receber benefício social. Base principal: ~4,2M registros. |
| 10 | `DEPEND` | Dependente | `CADDEPEND.NSN`, `BENEFICIARIO.ddm#L61` | Vinculado ao beneficiário titular. Max 10 por titular (PE group). Parentesco: FI/CJ/NT/TU. |
| 11 | `PROG` | Programa social | `CADPROG.NSN`, `PROGRAMA-SOCIAL.ddm` | Programa de transferência de renda (PBF, BPC, PETI etc.). ~45 ativos. |
| 12 | `PGT` / `PGTO` | Pagamento | `BATCHPGT.NSN`, `RELPGT.NSN`, `PAGAMENTO.ddm` | Transação financeira mensal. ~180M registros, ~3,8M/mês. |
| 13 | `DSCT` | Desconto | `CALCDSCT.NSN`, `PAGAMENTO.ddm#L38` | Dedução aplicada sobre valor bruto. Teto de 30% exceto judiciais. |
| 14 | `CORR` | Correção / Reajuste | `CALCCORR.NSN` | Cálculo de correções e reajustes anuais. (HIPÓTESE — só nome do arquivo lido) |
| 15 | `ELEG` | Elegibilidade | `VALELEG.NSN` | Validação de requisitos cadastro × programa: idade, renda, status, documentação, NIS. |
| 16 | `DOCS` | Documentos / Documentação | `VALDOCS.NSN`, `VALELEG.NSN#L178` | Checklist de documentação comprobatória por tipo de programa. `DOCS-OK = 'S'` é gate. |
| 17 | `AUDIT` | Auditoria | `RELAUDIT.NSN`, `AUDITORIA.ddm` | Trilha imutável de ações. Obrigatoriedade legal IN-TCU 63/2010, retenção 10 anos. |
| 18 | `CONS` | Consulta | `CONSBENF.NSN` | Programa de consulta online. Único `CONS*` sobrevivente. |
| 19 | `CAD` | Cadastro | `CADBENEF.NSN`, `CADDEPEND.NSN`, `CADPROG.NSN` | Prefixo de programas online de manutenção (inclusão/alteração). |
| 20 | `CALC` | Cálculo | `CALCBENF.NSN`, `CALCCORR.NSN`, `CALCDSCT.NSN` | Prefixo do núcleo financeiro. Onde estão fórmulas e constantes. |
| 21 | `VAL` | Validação | `VALBENEF.NSN`, `VALDOCS.NSN`, `VALELEG.NSN` | Prefixo de programas que retornam elegível/não-elegível com motivos. |
| 22 | `REL` | Relatório | `RELPGT.NSN`, `RELAUDIT.NSN` | Geração de relatórios gerenciais e de auditoria. |
| 23 | `BATCH` | Processamento em lote | `BATCHPGT.NSN`, `BATCHCON.NSN`, `BATCHREL.NSN` | Jobs noturnos (janela 22h–06h). Acionados pelo scheduler JES2. |
| 24 | `VLR` | Valor (monetário) | `PAGAMENTO.ddm#L32`, `CALCDSCT.NSN#L36` | Prefixo de campos financeiros: `VLR-BRUTO`, `VLR-LIQUIDO`, `VLR-DESCONTO`. Tipo `N9.2`. |
| 25 | `SIT` | Situação / Status | `BENEFICIARIO.ddm#L52`, `PAGAMENTO.ddm#L48` | Prefixo de códigos de estado: `SIT-BENEFICIARIO`, `SIT-PAGAMENTO`, `SIT-PROGRAMA`. |
| 26 | `SIT-BENEFICIARIO` (A/S/C/I/D) | Status beneficiário: Ativo / Suspenso / Cancelado / Inativo / Desligado | `BENEFICIARIO.ddm#L52`, `VALELEG.NSN#L116-L134` | Apenas `A` é elegível para pagamento. Demais geram motivo de rejeição. |
| 27 | `SIT-PAGAMENTO` (P/G/E/C/D/X/R) | Status pagamento: Pendente / Gerado / Emitido / Confirmado / Devolvido / Cancelado / Reprocessado | `PAGAMENTO.ddm#L48-L50` | Máquina de estados do ciclo financeiro. |
| 28 | `TIPO-PROGRAMA` (A/T/P) | Assistencial / Trabalho / Previdenciário | `PROGRAMA-SOCIAL.ddm#L24`, `VALELEG.NSN#L168-L201` | Define regras de elegibilidade: A→renda baixa+docs, T→idade 16-65, P→idade ≥60. |
| 29 | `TIPO-DESCONTO` (DDM) | Códigos 2 letras: IR/JD/CS/PA/EM/TX/OU/EX | `PAGAMENTO.ddm#L39` | IR=IRRF, JD=Judicial, CS=Consignado, PA=Pensão Alimentícia, EM=Empréstimo, TX=Taxa, OU=Outros, EX=Extraordinário. |
| 30 | `TIPO-DSCT` (NSN) | Códigos 1 letra: C/I/J/S/P/A | `CALCDSCT.NSN#L26-L27` | C=Contrib, I=Imposto, J=Judicial, S=Sindical, P=Pensão, A=Admin. ⚠️ DIVERGE do DDM (2 letras) — possível mistério. |
| 31 | `COD-REGIAO 99` | Código de região especial (Internacional/Diplomático) | `VALELEG.NSN#L107-L110`, `BENEFICIARIO.ddm#L44` | Bypass de elegibilidade — beneficiário é declarado elegível sem verificar idade/renda. |
| 32 | `FATOR-K` | Fator de correção especial | `PROGRAMA-SOCIAL.ddm#L39-L43` | ⚠️ NÃO DOCUMENTADO. Inserido 2008 por Adilson "atende solicitação SENARC". Usado em cálculos. Mistério candidato. |
| 33 | `NIS` | Número de Identificação Social | `CADBENEF.NSN#L48`, `VALELEG.NSN#L228-L232` | Identificador no Cadastro Único. Obrigatório para programas com `COD-ELEG` iniciando em `R`. |
| 34 | `CPF` | Cadastro de Pessoas Físicas | `CADBENEF.NSN#L33`, `BENEFICIARIO.ddm#L21` | Chave de busca primária. Validado por algoritmo Módulo 11 (CADBENEF L222-L269). |
| 35 | `SIAFI` | Sistema Integrado de Administração Financeira (STN) | `legado-sifap/README.md#L36`, `PAGAMENTO.ddm#L66-L72` | Integração para conciliação financeira. Campos `NUM-OB-SIAFI`, `NUM-NE-SIAFI`, `COD-UG`. |
| 36 | `OB` | Ordem Bancária (SIAFI) | `PAGAMENTO.ddm#L68` | Documento financeiro emitido no SIAFI para autorizar pagamento. |
| 37 | `CNAB` | Centro Nac. Automação Bancária (formato 240) | `PAGAMENTO.ddm#L79`, `legado-sifap/README.md#L124` | Padrão de arquivo para remessa/retorno bancário (Banco do Brasil, CAIXA). |
| 38 | `TCU` | Tribunal de Contas da União | `AUDITORIA.ddm#L11` | Determina retenção de 10 anos do log de auditoria (IN-TCU 63/2010). |
| 39 | `SENARC` | Secretaria Nacional de Renda de Cidadania | `legado-sifap/README.md#L44` | Órgão que aprova alterações em `FATOR-K` e parâmetros de programa. |
| 40 | `COMPETENCIA` | Mês de referência do pagamento (AAAAMM) | `BATCHPGT.NSN#L110`, `PAGAMENTO.ddm#L27` | Granularidade do ciclo financeiro. Usada na chave única CPF+COMPETENCIA. |

> Adicione mais linhas conforme necessário. Não se limite a 40!

## Exemplo de linha bem preenchida

| #   | Termo  | Expansão | Programa                        | Contexto                                                                                                         |
| --- | ------ | -------- | ------------------------------- | ---------------------------------------------------------------------------------------------------------------- |
| 1   | `DSCT` | Desconto | `CALCDSCT.NSN`, `PAGAMENTO.ddm` | Tipo de dedução aplicada sobre valor bruto do pagamento. Tipos: 'J' (judicial), 'I' (imposto), 'T' (trabalhista) |

## Observações

- **Convenções de prefixo de programa detectadas** (6):
  - `BATCH*` (3) — processamento em lote noturno
  - `CAD*` (3) — manutenção cadastral online
  - `CALC*` (3) — núcleo de cálculo financeiro
  - `VAL*` (3) — validação de regras de negócio
  - `REL*` (2) — geração de relatórios
  - `CONS*` (1) — consulta online (singleton — possível arquivo perdido?)
- **Convenções de prefixo de campo (DDM)** detectadas: `VLR-` (valores monetários), `DT-` (data AAAAMMDD), `HR-` (hora HHMMSS), `SIT-` (status 1 letra), `COD-` (códigos), `IND-` (indicador S/N), `NUM-` (numérico identificador), `GRP-` (grupo).
- **Convenção de variável Natural:** `#` no início = variável local (ex: `#VLR-MAX-DSCT`); sem `#` = campo do DDM (ex: `VLR-BRUTO`).
- **Termos ambíguos / precisam validação:**
  - `FATOR-K` em `PROGRAMA-SOCIAL.ddm#L39` — **sem documentação**, candidato forte a `mysteries-found.md`.
  - **Divergência `TIPO-DESCONTO`**: o DDM PAGAMENTO usa códigos de 2 letras (IR/JD/CS…), mas `CALCDSCT.NSN` usa códigos de 1 letra (C/I/J/S/P/A). Pode ser conversão implícita ou bug. Candidato forte a mistério.
  - **`COD-REGIAO = 99`** em `VALELEG.NSN#L107` faz bypass total da validação — easter egg / regra histórica?
  - **`SIT-BENEFICIARIO`**: DDM lista A/S/C/I/D, mas `CADBENEF.NSN#L167-L169` força `S` para idade > 75. Regra implícita não documentada.

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
