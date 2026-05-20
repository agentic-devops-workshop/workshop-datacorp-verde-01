# Inventário Legado — [Nome da Equipe]

> Primeira passada de mapeamento top-down. Nenhum programa foi aberto.
> Data: 2026-05-20
> Gerado por: `@archaeologist` via `/archaeology-kickoff`
> Fonte: `01-arqueologia/legado-sifap/` (branch `visao`)

## Estrutura de Pastas

```
legado-sifap/
├── COMO-LER-NATURAL.md
├── README.md
├── adabas-ddms/        (4 DDMs + README)
├── legacy-docs/        (3 docs em .docx + .md + README)
└── natural-programs/   (15 fontes .NSN + README)
```

Total de diretórios: **4** (raiz + 3 subpastas).

## Contagem de Arquivos por Tipo

| Extensão | Contagem | Finalidade provável |
|----------|---------:|---------------------|
| `.NSN`   | 15       | Fontes Natural (Software AG) |
| `.md`    | 8        | READMEs + transcrições de docs |
| `.ddm`   | 4        | Definições de view Adabas |
| `.docx`  | 3        | Documentos legados originais |
| **Total**| **30**   | |

Verificável por: `find 01-arqueologia/legado-sifap -type f | sed 's/.*\.//' | sort | uniq -c`

## Padrões de Convenção de Nomes

`natural-programs/`:

| Prefixo | Contagem | Membros |
|---------|---------:|---------|
| `BATCH` | 3 | BATCHCON, BATCHPGT, BATCHREL |
| `CAD`   | 3 | CADBENEF, CADDEPEND, CADPROG |
| `CALC`  | 3 | CALCBENF, CALCCORR, CALCDSCT |
| `VAL`   | 3 | VALBENEF, VALDOCS, VALELEG |
| `REL`   | 2 | RELAUDIT, RELPGT |
| `CONS`  | 1 | CONSBENF (singleton) |

`adabas-ddms/`: nomes de entidade em CAPS, sem prefixo (AUDITORIA, BENEFICIARIO, PAGAMENTO, PROGRAMA-SOCIAL).

`legacy-docs/`: padrão `ASSUNTO-ANO` em pares `.docx`/`.md`.

## Itens Incomuns (Top 3)

1. **Extensão `.NSN` (maiúsculas)** em `natural-programs/` — desvio da convenção `.nat` esperada pelo prompt-base. Pode ser export específico do Natural Studio/SYSMAIN. _Verificar com a equipe de plataforma antes de aplicar qualquer ferramenta de parse._
2. **Singleton `CONS*`** — `natural-programs/CONSBENF.NSN` é o único `CONS*`. Investigar se faltam arquivos no export ou se essa é mesmo a única consulta online sobrevivente.
3. **Duplicação `.docx` + `.md` em `legacy-docs/`** — 3 pares com mesmo nome-base (ARQUITETURA-ORIGINAL-1997, MANUAL-TECNICO-SIFAP-2008, REGRAS-NEGOCIO-2012). Confirmar qual é a fonte-da-verdade antes de citar trechos.

## Ordem de Leitura Proposta

1. `legacy-docs/*.md` (3 docs — contexto histórico 1997 → 2008 → 2012)
2. `adabas-ddms/*.ddm` (4 DDMs — modelo de dados ancora nomes nos programas)
3. `natural-programs/BATCH*.NSN` (3 entry points batch — orquestração)
4. `natural-programs/CAD*.NSN` (3 cadastros — operam sobre os DDMs)
5. `natural-programs/VAL*.NSN` (3 validações — chamadas por CAD/BATCH)
6. `natural-programs/CALC*.NSN` (3 cálculos — provável núcleo de regras)
7. `natural-programs/CONSBENF.NSN` + `REL*.NSN` (3 pontas de saída)

READMEs internos devem ser lidos junto da categoria correspondente.

## Definição de Pronto

- [x] Estrutura de pastas documentada
- [x] Contagens por extensão verificáveis via `find`
- [x] 6 padrões de prefixo identificados em `natural-programs/`
- [x] 3 itens incomuns sinalizados com path + motivo
- [x] Ordem de leitura justificada por padrão de prefixo + camada arquitetural
