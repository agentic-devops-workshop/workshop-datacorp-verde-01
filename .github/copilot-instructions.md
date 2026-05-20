# Instruções do GitHub Copilot — Workshop de Modernização de Legado

> Estas instruções dizem ao Copilot o que sua equipe está construindo, qual stack usar,
> quais convenções seguir e o que NÃO fazer. Elas se aplicam a todo o repositório
> da equipe.

## Ferramentas Aprovadas — Somente Estas

Este workshop roda com uma **toolchain fixa**. Usar qualquer outra coisa fragmenta a equipe e quebra as demos.

| Use estas | Por quê |
|-----------|-----|
| **VS Code** (ou VS Code Insiders) | Editor único para toda a equipe. O devcontainer + extensões assumem isso. |
| **GitHub Copilot** (modos Ask + Plan + Agent) | Assistente de IA principal. Copilot Workspace também é permitido para delegação Issue → PR. |
| **GitHub Copilot CLI** *(opcional)* | Para tarefas em fluxo de terminal. As mesmas regras de roteamento de modelo se aplicam. |
| **GitHub Spec-Kit** (`Specify CLI` + `/speckit.*`) | Toolkit oficial de Spec-Driven Development para especificação, planejamento, tarefas e implementação. |
| **GitHub** (Issues, PRs, Actions, Projects) | Fonte da verdade para trabalho, código e CI. |
| **Docker / Docker Compose** | Paridade do ambiente local. |
| **Terraform** | IaC (Azure provider). |

**Não use** outros assistentes de IA, IDEs ou frameworks de SDD durante o workshop. Especificamente:

- ❌ Sem Cursor, Windsurf, Antigravity, Codex, Cline, Continue, Aider, Codeium, Tabnine ou outros concorrentes do Copilot
- ❌ Sem UIs web de chat para geração de código (seus prompts e saídas devem permanecer rastreáveis pelo histórico do Copilot Chat)
- ❌ Sem IntelliJ, Eclipse, Sublime ou plugins do Neovim como editor principal
- ❌ Sem frameworks alternativos de SDD (Kiro, pipelines YAML customizados etc.). O GitHub Spec-Kit oficial é permitido e obrigatório.

Por que isso é rígido: o workshop mede **engenharia agentic com a stack oficial Microsoft + GitHub**. Misturar ferramentas quebra os passagens da equipe, fragmenta a rubrica e torna impossível validar o rastreamento de spec → code → test.

## Contexto do Projeto

Este repositório pertence a uma equipe do workshop que está modernizando o legado **SIFAP**
(Sistema de Fiscalização e Administração de Pagamentos) de Natural/Adabas para
uma stack moderna. O workspace de referência fica em
[`workshop-legacy-modernization-datacorp`](https://github.com/paulasilvatech/workshop-legacy-modernization-datacorp).

### Status Atual: Estágio 1 completo → Estágio 2 (Especificação)

O Estágio 1 (Arqueologia Digital) foi concluído com 150 regras de negócio extraídas, 23 mistérios catalogados e 45 termos no glossário. Todos os 15 programas Natural e 4 DDMs foram analisados.

## Domínio SIFAP — Conhecimento Essencial

> Estas são regras de domínio que impactam TODA geração de código. Detalhes em [`01-arqueologia/business-rules-catalog.md`](../01-arqueologia/business-rules-catalog.md).

### Entidades Centrais

| Entidade | DDM | Relacionamento |
|----------|-----|----------------|
| Beneficiário | BENEFICIARIO (hub — 11 progs acessam) | 1:N Pagamentos, 1:N Dependentes (PE group→tabela), N:1 Programa Social |
| Pagamento | PAGAMENTO | Ciclo: G(gerado) → P(pago) / D(devolvido) / E(erro). Sem transição reversa. |
| Programa Social | PROGRAMA-SOCIAL | Tipos: A(assistencial), P(previdenciário), T(trabalho). Status A(ativo) ou I(inativo). |
| Auditoria | AUDITORIA | Ações: IN/AL/CO/CN/DV/EX. Trilha obrigatória. |

### Fórmula de Benefício (Core — impacta toda geração financeira)

```
VLR-BENEFICIO = VLR-BASE × FATOR-REG × FATOR-FAM × FATOR-RND × FATOR-IDADE × (1 + FATOR-REAJ)
```

- Resultado TRUNCADO em 2 decimais (×100, int, ÷100) — **não arredondar**
- Dezembro: adiciona 13° + abono 15% (tipo 'A')
- Desconto judicial (tipo 'J') ignora teto de 30%

### Regras Invioláveis na Modernização

- CPF sempre mascarado em logs e UIs (LGPD) — formato `***.***. XXX-XX`
- `source_legacy:` obrigatório em toda EARS — sem exceção
- Desconto judicial NÃO pode ter teto aplicado (precedência legal)
- Ordenação por CPF no batch pode ter dependentes downstream — validar antes de remover
- NUM-DEPENDENTES é redundante mas usado no cálculo — na modernização, derivar do COUNT da tabela

### Decisões Pendentes (requerem validação com negócio)

1. **Reajuste duplo**: Fator K (constante 0.347215) + (1+FATOR-REAJ) mensal — intencional ou bug?
2. **Desconto batch vs online**: 3% fixo (BATCHPGT) vs 4 faixas progressivas (CALCDSCT) — unificar para qual?
3. **Bypass região 99**: manter como exceção controlada ou eliminar?
4. **Prefixos especiais CPF**: 8 prefixos contornam validação — teste ou caso legítimo?

### Artefatos de Referência (Stage 1 — fontes de verdade)

- [`01-arqueologia/business-rules-catalog.md`](../01-arqueologia/business-rules-catalog.md) — 150 regras com linhas exatas (BR-001 a BR-150)
- [`01-arqueologia/discovery-report.md`](../01-arqueologia/discovery-report.md) — síntese executiva + recomendações de migração
- [`01-arqueologia/glossary.md`](../01-arqueologia/glossary.md) — 45 termos de domínio (use para naming em Java/TS)
- [`01-arqueologia/mysteries-found.md`](../01-arqueologia/mysteries-found.md) — 23 mistérios com hipóteses e risco
- [`01-arqueologia/dependency-map.md`](../01-arqueologia/dependency-map.md) — call graph e acoplamento entre programas

## Duas Camadas de Agentes — Ambas Obrigatórias

Este kit inclui **duas camadas complementares de agentes**. Elas cobrem eixos ortogonais (role × stage). Use ambas.

### `05-personas/NN-*/` — instalado uma vez, roda o dia todo

- Um kit por persona (Product Owner, Requirements Engineer, …, Tech Writer).
- Cada kit contém: 1 `agent.md` + 2–4 `prompts/*.prompt.md` + 1–2 `skills/*/SKILL.md` + `instructions/*.instructions.md` opcional + `mcp.json`.
- Cada pessoa da equipe copia **ambos** os seus kits de persona para a pasta `.github/` do repositório da equipe via `cp -r 05-personas/XX-*/.github/* .github/`.
- Depois disso, o Copilot fica configurado para o papel dessa pessoa durante todo o dia. Slash commands (`/spec`, `/ears-convert`, …) ficam disponíveis.

### `06-agentes-de-estagio/` — selecionado novamente a cada estágio

- Um agente por estágio: `@archaeologist` (Estágio 1) · `@architect` (Estágio 2) · `@builder` (Estágio 3) · `@evolution` (Estágio 4).
- Compartilhado por toda a equipe. Define uma "persona protagonista" + "personas secundárias" + "personas observadoras" para aquele estágio.
- Ativado no Copilot Chat: abra o painel de chat, clique no seletor de agente, escolha o agente do estágio. Cole o prompt de abertura do README daquele kit.
- O agente orienta a **coordenação da equipe** durante o estágio: quais artefatos produzir, como percorrer o código legado, quando escalar etc.

### Como elas se combinam

1. Persona-kit carregado → slash commands (`/ears-convert`, `/spec`) funcionam.
2. Agente de estágio selecionado (`@architect` para Estágio 2) → guia coordenação.
3. Ambas as camadas trabalham juntas. Nenhuma é opcional.

## Stack-Alvo

- **Backend:** Java 21 + Spring Boot 3.3 + JPA/Hibernate + PostgreSQL 16
- **Frontend:** Next.js 15 (App Router) + TypeScript 5 (strict) + Tailwind CSS + shadcn/ui
- **Containers:** Docker + Docker Compose
- **IaC:** Terraform (Azure provider ~> 3.x)
- **CI/CD:** GitHub Actions
- **Testing:** JUnit 5 + Testcontainers (backend); Vitest + Testing Library (frontend)

## Regras de Geração de Código

### Java
- Use recursos do Java 21: records para DTOs, sealed interfaces para uniões discriminadas, pattern matching, virtual threads
- Use `Optional` corretamente — nunca retorne `null` de métodos públicos
- `@Transactional` somente na camada de service, nunca em repositories
- Valide entradas na camada de controller com `@Valid` + Bean Validation
- Nomes de classes em inglês; comentários em inglês
- Testes unitários são obrigatórios para lógica de negócio
- Nunca exponha dados sensíveis (CPF, valores de benefício) em logs — mascare-os

### TypeScript / Next.js
- `strict: true` em `tsconfig.json` — sem exceções
- Use server actions para mutations; nunca exponha secrets em client components
- Prefira `async/await` a cadeias `.then()`
- Somente named exports — sem default exports em arquivos de componentes

### REST APIs
- Convenção de path: `/api/v1/{resource}`
- Use verbos HTTP corretamente (`GET`, `POST`, `PUT`, `PATCH`, `DELETE`)
- Retorne status codes apropriados (`201` para criação, `204` para sem conteúdo, `409` para conflito)
- Todos os endpoints devem ter annotations OpenAPI/Swagger

### Terraform
- Todo recurso deve ter `tags` incluindo `project`, `environment`, `owner`
- Secrets somente via `azurerm_key_vault_secret` — nunca em `locals` ou `variables`
- Um módulo por área de serviço Azure (networking, compute, database, monitoring)
- `terraform fmt` e `terraform validate` devem passar antes do commit

## Regras de Segurança (OWASP Top 10)

- Valide entradas em toda fronteira do sistema
- Nunca faça hardcode de secrets, API keys ou credenciais
- Consultas SQL somente via JPA/JPQL — sem concatenação de strings
- CORS configurado explicitamente — sem wildcard `*` em produção
- Autenticação via OAuth2/JWT (Spring Security no backend)
- Todos os recursos Azure usam Managed Identity para autenticação serviço-a-serviço

## Spec-Driven Development (Spec-Kit)

- Todo requisito usa **notação EARS** (Easy Approach to Requirements Syntax)
- Todo requisito tem um **REQ-ID** único no formato `REQ-NNN`
- **Todo requisito carrega uma linha `source_legacy:`** apontando para `01-arqueologia/legado-sifap/natural-programs/*.NSN`, `01-arqueologia/legado-sifap/adabas-ddms/*.ddm` ou `[GREENFIELD] + justificativa`. O job de CI `legacy-traceability` rejeita PRs que violam isso. Consulte [`01-arqueologia/LEGACY-EXPLORATION-CHECKLIST.md`](../01-arqueologia/LEGACY-EXPLORATION-CHECKLIST.md).
- Testes rastreiam para REQ-IDs por comentários inline
- Estratégia de branch: uma branch por spec, nomeada `spec/<NNN>-<feature>`
- Ordem de merge: `spec/*` → `develop` → `stage` → `main`

## Exploração do Legado — COMPLETA (Stage 1 Done)

Todos os 15 programas Natural e 4 DDMs foram analisados. Resultados consolidados em:
- [`01-arqueologia/business-rules-catalog.md`](../01-arqueologia/business-rules-catalog.md) — 150 regras (BR-001 a BR-150)
- [`01-arqueologia/discovery-report.md`](../01-arqueologia/discovery-report.md) — síntese + prioridades de migração
- [`01-arqueologia/glossary.md`](../01-arqueologia/glossary.md) — 45 termos (usar para naming)

**Para o Estágio 2**: toda EARS deve referenciar regras do catálogo via `source_legacy:` + BR-ID correspondente.

## Os 5 Pares e 10 Personas

> **Cada equipe tem 5 pessoas. Cada pessoa veste 2 personas (1 par).** Não há passagem interna entre personas de um par — colaboração contínua. Atualize os nomes abaixo com as atribuições da sua equipe.

| Par | Persona A | Persona B | Fase do SDLC |
|------|-----------|-----------|------------|
| 1 · Visão | [ ] 01 Product Owner — nome? | [ ] 02 Requirements Engineer — nome? | Descoberta + Especificação |
| 2 · Arquitetura | [ ] 03 Enterprise Architect — nome? | [ ] 04 Software Architect — nome? | Especificação + Design |
| 3 · Implementação | [ ] 05 Technical Lead — nome? | [ ] 06 Developer — nome? | Implementação + Evolução |
| 4 · Qualidade | [ ] 07 DBA — nome? | [ ] 08 QA Engineer — nome? | Implementação (dados + testes) |
| 5 · Operações | [ ] 09 DevOps Engineer — nome? | [ ] 10 Tech Writer — nome? | Transversal + Evolução |

Veja [`00-TEAM-FLOW.md`](../00-TEAM-FLOW.md) para diagramas de passagem e a linha do tempo do dia.

## Como Usar o Copilot (3 modos)

| Modo | Quando usar | Exemplo |
|------|-------------|---------|
| **Chat** | Explorar, planejar, debater trade-offs | "Explique este programa Natural linha por linha" |
| **Plan** | Planejamento de mudanças multi-arquivo antes da execução | "Planeje o bounded context `notification` com domain/application/infrastructure" |
| **Agent** | Delegar features completas via Issue | "Implemente REQ-PAY-03: geração de ciclo com audit log" |

Detalhes em [`09-cheat-sheets/copilot-3-modes.md`](../09-cheat-sheets/copilot-3-modes.md).

## Regras Rígidas — Não Faça Isto

- ❌ Não gere código sem antes verificar o protótipo existente em `prototype/` (symlink criado por `11-scripts/setup.sh`)
- ❌ Não escreva um EARS sem `source_legacy:` — o CI rejeitará o PR
- ❌ Não adicione dependências sem justificativa em um ADR
- ❌ Não escreva testes depois do fato — escreva-os enquanto implementa
- ❌ Não exponha secrets em mensagens de commit, logs ou descrições de PR
- ❌ Não faça merge em `main` sem pelo menos uma revisão entre pares
- ❌ Não pule as conversas guiadas de passagem nas transições de estágio (veja [`00-TEAM-FLOW.md`](../00-TEAM-FLOW.md))

## Referências

- [Team Flow](../00-TEAM-FLOW.md) — linha do tempo diária, passagens, escalonamento
- [Persona Kits](../05-personas/) — seus 2 cartões de papel em `PERSONA.md`, além de agentes, prompts e skills do Copilot (copie seus 2 kits para esta `.github/`)
- [Cheat Sheets](../09-cheat-sheets/) — Copilot, Spec-Kit, roteamento de modelo
- [Legado Incluído](../01-arqueologia/legado-sifap/) — o que você está modernizando (15 programas .NSN + 4 DDMs)
- [`01-arqueologia/LEGACY-EXPLORATION-CHECKLIST.md`](../01-arqueologia/LEGACY-EXPLORATION-CHECKLIST.md) — HARD GATE antes do Estágio 2
- Protótipo de Referência (`prototype/`) — starter code em execução (symlink, criado por setup.sh)
- Módulos de infraestrutura (`infra/`) — módulos Terraform Azure (symlink, criado por setup.sh)
- [Spec-Kit SDD Plugin](https://github.com/github/spec-kit) — engine de Spec-Driven Development
- Docs didáticos trilíngues: [`pt-br/`](../) · [`es/`](../../es/) · [`en/`](../../en/)

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan
<!-- SPECKIT END -->
