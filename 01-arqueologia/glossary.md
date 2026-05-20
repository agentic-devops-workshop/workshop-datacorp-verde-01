# Glossário do SIFAP Legado

## Termos encontrados

| # | Termo | Expansão | Programa | Contexto |
| --- | --- | --- | --- | --- |
| 1 | SIFAP | Sistema de Fiscalização e Administração de Pagamentos | [01-arqueologia/legado-sifap/README.md](01-arqueologia/legado-sifap/README.md) | Sistema legado principal de benefícios sociais. |
| 2 | NSN | Fonte de programa Natural | [01-arqueologia/legado-sifap/natural-programs/README.md](01-arqueologia/legado-sifap/natural-programs/README.md) | Extensão dos programas de negócio legados. |
| 3 | DDM | Data Definition Module | [01-arqueologia/legado-sifap/adabas-ddms/README.md](01-arqueologia/legado-sifap/adabas-ddms/README.md) | Define estrutura de dados no Adabas. |
| 4 | Adabas | Banco de dados legado mainframe | [01-arqueologia/legado-sifap/README.md](01-arqueologia/legado-sifap/README.md) | Base de dados operacional do sistema. |
| 5 | MU | Multiple Value | [01-arqueologia/legado-sifap/adabas-ddms/PROGRAMA-SOCIAL.ddm](01-arqueologia/legado-sifap/adabas-ddms/PROGRAMA-SOCIAL.ddm) | Campo multivalorado no modelo Adabas. |
| 6 | PE | Periodic Group | [01-arqueologia/legado-sifap/adabas-ddms/BENEFICIARIO.ddm](01-arqueologia/legado-sifap/adabas-ddms/BENEFICIARIO.ddm) | Grupo repetitivo de campos (ocorrências). |
| 7 | FNR | File Number | [01-arqueologia/legado-sifap/README.md](01-arqueologia/legado-sifap/README.md) | Identificador de arquivo no Adabas. |
| 8 | CPF | Cadastro de Pessoa Física | [01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN](01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN) | Identificador principal do beneficiário. |
| 9 | NIS | Número de Identificação Social | [01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN](01-arqueologia/legado-sifap/natural-programs/VALELEG.NSN) | Usado em elegibilidade e validações. |
| 10 | UF | Unidade Federativa | [01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN](01-arqueologia/legado-sifap/natural-programs/VALBENEF.NSN) | Estado do beneficiário, validado por tabela. |
| 11 | BENEF | Beneficiário | [01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN](01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN) | Pessoa processada para recebimento do benefício. |
| 12 | BENF | Benefício | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Valor mensal calculado para pagamento. |
| 13 | DEP | Dependente | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Influencia fator familiar no cálculo. |
| 14 | PROG | Programa Social | [01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN](01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN) | Cadastro de regras e parâmetros de programa. |
| 15 | COD-PROGRAMA | Código do programa social | [01-arqueologia/legado-sifap/adabas-ddms/PROGRAMA-SOCIAL.ddm](01-arqueologia/legado-sifap/adabas-ddms/PROGRAMA-SOCIAL.ddm) | Chave para vincular beneficiário a regras. |
| 16 | VLR | Valor monetário | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Prefixo de campos financeiros. |
| 17 | VLR-BRUTO | Valor bruto | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Valor antes de descontos. |
| 18 | VLR-LIQUIDO | Valor líquido | [01-arqueologia/legado-sifap/adabas-ddms/PAGAMENTO.ddm](01-arqueologia/legado-sifap/adabas-ddms/PAGAMENTO.ddm) | Valor final após descontos. |
| 19 | DSCT | Desconto | [01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN](01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN) | Dedução aplicada ao pagamento. |
| 20 | VLR-DESCONTO | Valor de desconto | [01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN](01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN) | Soma de descontos do pagamento. |
| 21 | PCT | Percentual | [01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN](01-arqueologia/legado-sifap/natural-programs/CALCDSCT.NSN) | Alíquota percentual em cálculos. |
| 22 | FATOR-REG | Fator regional | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Multiplicador por região. |
| 23 | FATOR-FAM | Fator familiar | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Multiplicador conforme dependentes. |
| 24 | FATOR-RND | Fator de renda | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Multiplicador por faixa de renda. |
| 25 | FATOR-IDADE | Fator etário | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Multiplicador por faixa de idade. |
| 26 | FATOR-REAJ | Fator de reajuste | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Índice de reajuste aplicado ao valor. |
| 27 | FATOR-K | Fator de correção especial | [01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN](01-arqueologia/legado-sifap/natural-programs/CADPROG.NSN) | Ajuste adicional com documentação parcial. |
| 28 | COMPETENCIA | Referência AAAAMM | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Mês/ano do ciclo de cálculo e pagamento. |
| 29 | DT | Data | [01-arqueologia/legado-sifap/adabas-ddms/BENEFICIARIO.ddm](01-arqueologia/legado-sifap/adabas-ddms/BENEFICIARIO.ddm) | Prefixo de campos de data. |
| 30 | HR | Hora | [01-arqueologia/legado-sifap/adabas-ddms/AUDITORIA.ddm](01-arqueologia/legado-sifap/adabas-ddms/AUDITORIA.ddm) | Prefixo de campos de horário. |
| 31 | BATCH | Processamento em lote | [01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN](01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN) | Execução massiva de rotinas mensais. |
| 32 | BATCHPGT | Batch de geração de pagamentos | [01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN](01-arqueologia/legado-sifap/natural-programs/BATCHPGT.NSN) | Gera pagamentos para beneficiários ativos. |
| 33 | BATCHCON | Batch de conciliação | [01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN](01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN) | Concilia retorno bancário com pagamentos internos. |
| 34 | CNAB 240 | Layout bancário de arquivos | [01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN](01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN) | Formato de remessa e retorno bancário. |
| 35 | SIAFI | Sistema Integrado de Administração Financeira | [01-arqueologia/legado-sifap/README.md](01-arqueologia/legado-sifap/README.md) | Sistema externo para integração financeira. |
| 36 | AUDITORIA | Trilha de eventos | [01-arqueologia/legado-sifap/adabas-ddms/AUDITORIA.ddm](01-arqueologia/legado-sifap/adabas-ddms/AUDITORIA.ddm) | Registro de ações e ocorrências operacionais. |
| 37 | RELAUDIT | Relatório de auditoria | [01-arqueologia/legado-sifap/natural-programs/RELAUDIT.NSN](01-arqueologia/legado-sifap/natural-programs/RELAUDIT.NSN) | Emite trilha de auditoria por filtros. |
| 38 | STATUS-PGTO | Situação do pagamento | [01-arqueologia/legado-sifap/adabas-ddms/PAGAMENTO.ddm](01-arqueologia/legado-sifap/adabas-ddms/PAGAMENTO.ddm) | Estado do pagamento no ciclo operacional. |
| 39 | COD-RETORNO | Código de retorno bancário | [01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN](01-arqueologia/legado-sifap/natural-programs/BATCHCON.NSN) | Indica sucesso, devolução ou erro no retorno. |
| 40 | ABONO | Parcela adicional | [01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN](01-arqueologia/legado-sifap/natural-programs/CALCBENF.NSN) | Valor extra em condições específicas (ex.: dezembro). |

## Observações

- Convenções de prefixo identificadas: VLR, DT, HR, COD, IND, SIT e STATUS.
- Termos ambíguos para validação com especialista: FATOR-K e regras detalhadas de alguns códigos de retorno bancário.
- Há forte presença de abreviações históricas dos anos 90, exigindo rastreabilidade por programa/DDM durante a modernização.