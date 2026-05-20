# Inventário Legado — Equipe DataCorp Verde 01

> Primeira passada — 20/05/2026. A ser revisado conforme a equipe lê arquivos individuais.

## Estrutura de Pastas

```
01-arqueologia/legado-sifap/
├── COMO-LER-NATURAL.md
├── README.md
├── adabas-ddms/            ← schemas de dados Adabas (4 DDMs)
├── legacy-docs/            ← documentação legada (3 .docx + 3 .md)
└── natural-programs/       ← programas-fonte Natural (15 .NSN)
```

Total de diretórios de conteúdo: 3

## Contagem de Arquivos por Tipo

| Extensão | Contagem | Finalidade provável |
|----------|----------|---------------------|
| `.NSN` | 15 | Programa-fonte Natural (código de negócio) |
| `.ddm` | 4 | Data Definition Module — schema do arquivo Adabas |
| `.md` | 6 | Documentação / READMEs |
| `.docx` | 3 | Documentação legada original (Microsoft Word) |

## Padrões de Convenção de Nomes

### Programas Natural (`.NSN`)

| Prefixo | Arquivos | Contagem | Hipótese |
|---------|----------|----------|----------|
| `BATCH` | BATCHCON, BATCHPGT, BATCHREL | 3 | Processamento batch — consolidação, pagamento, relatório |
| `CAD` | CADBENEF, CADDEPEND, CADPROG | 3 | Cadastro — registro/manutenção de entidades |
| `CALC` | CALCBENF, CALCCORR, CALCDSCT | 3 | Cálculo — lógica computacional de negócio |
| `VAL` | VALBENEF, VALDOCS, VALELEG | 3 | Validação — verificação de regras antes de persistir |
| `REL` | RELAUDIT, RELPGT | 2 | Relatório — geração de saídas/reportes |
| `CONS` | CONSBENF | 1 | Consulta — leitura/exibição de dados (único; investigar) |

### DDMs Adabas (`.ddm`)

| Arquivo | Hipótese de domínio |
|---------|---------------------|
| `BENEFICIARIO.ddm` | Entidade beneficiário — pessoa que recebe benefícios |
| `PAGAMENTO.ddm` | Entidade pagamento — registros financeiros |
| `PROGRAMA-SOCIAL.ddm` | Entidade programa social — tabela master de programas |
| `AUDITORIA.ddm` | Entidade auditoria — log de operações |

## Itens Incomuns (Top 3)

1. **`CONSBENF.NSN`** — único programa com prefixo `CONS`. Pode ser entry-point online isolado ou subprograma utilitário.
   - *Investigar:* verificar se outros programas fazem CALLNAT para ele.

2. **`PROGRAMA-SOCIAL.ddm`** — único DDM com nome composto (hífen). Todos os outros são palavras únicas.
   - *Investigar:* pode ser a entidade master/central do sistema; ler primeiro.

3. **`legacy-docs/*.docx`** — arquivos Word não são pesquisáveis diretamente, mas existem versões `.md` paralelas.
   - *Investigar:* confirmar que os `.md` são conversões fiéis para usar como fonte de cross-reference.

## Ordem de Leitura Proposta

| Ordem | Arquivo(s) | Justificativa |
|-------|-----------|---------------|
| 1 | DDMs: BENEFICIARIO, PAGAMENTO, PROGRAMA-SOCIAL, AUDITORIA | Dados antes de código — entender entidades do domínio |
| 2 | BATCHCON, BATCHPGT, BATCHREL | Entry-points batch — fluxo principal de processamento |
| 3 | CALCBENF, CALCCORR, CALCDSCT | Lógica computacional — regras de negócio mais densas |
| 4 | VALBENEF, VALDOCS, VALELEG | Validações — fronteiras de aceitação |
| 5 | CADBENEF, CADDEPEND, CADPROG | Cadastros — CRUD de entidades |
| 6 | RELAUDIT, RELPGT, CONSBENF | Relatórios e consultas — saídas do sistema |

> ⚠️ Esta é uma hipótese baseada em nomes. A ordem real de leitura mudará ao rastrear dependências CALLNAT.

## Próximos Passos

- [ ] Ler os 4 DDMs para montar vocabulário de dados
- [ ] Rodar `/map-dependencies` para descobrir o call graph real
- [ ] Rodar `/extract-business-rules` nos programas CALC* e VAL* (mais densos em lógica)
- [ ] Confirmar fidelidade dos `.md` em `legacy-docs/` contra os `.docx` originais
