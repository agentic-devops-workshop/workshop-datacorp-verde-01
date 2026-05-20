<!-- markdownlint-disable MD013 MD025 MD026 MD028 MD029 MD034 MD040 MD051 MD060 -->

# Decisões de Escopo — SIFAP 2.0

![ESTÁGIO 02 Spec](https://img.shields.io/badge/ESTÁGIO-02%20Spec-00A4EF?style=for-the-badge) ![TIPO Worksheet](https://img.shields.io/badge/TIPO-Worksheet-1A1A1A?style=for-the-badge) ![PREENCHA Durante S2](https://img.shields.io/badge/PREENCHA-Durante%20S2-737373?style=for-the-badge)

> 🗺 **Você está aqui:** [Kit PT-BR](../README.md) → [Estágio 2](README.md) → **Scope Decisions**

> **Para quem é isto?** Este é um **artefato preenchido pelo time** durante o Estágio 2 (Spec Moderna).
>
> **O que você terá ao final do estágio:**
>
> 1. Este documento preenchido para sua feature
> 2. Rastreabilidade `source_legacy:` para cada REQ-ID
> 3. Sign-off do Product Owner antes da passagem H2
>
> 📘 **Guia passo a passo:** [`GUIDE.md`](GUIDE.md).


> Para cada funcionalidade encontrada no Estágio 1, decida: **Migrar**, **Descartar** ou **Evoluir**.
>
> - **Migrar**: trazer para o SIFAP 2.0 como está (mesma lógica, nova tecnologia)
> - **Descartar**: não trazer — funcionalidade obsoleta ou desnecessária
> - **Evoluir**: trazer E melhorar (nova UX, novo fluxo, nova capacidade)

**Time**: DataCorp Verde 01
**Data**: 20/05/2026
**Edição**: 1.0
**Par 1 (Product Owner) responsável**: [Definir na sessão]

## Por que isso importa

O escopo é o que protege o time de chegar às 17h00 com 12 features pela metade. Se o Par 1 não cortar, o Estágio 3 não fecha. **Decisão difícil é tomada aqui, não no Estágio 3.**

## Como decidir

Pergunte de cada funcionalidade:

1. **Afeta o ciclo mensal de pagamento?** Sim → Migrar. Não → considere descartar.
2. **Tem uso documentado nos últimos 12 meses?** Não → descartar.
3. **Faz parte de um relatório regulatório obrigatório (TCU, CGU, BB)?** Sim → Migrar como está.
4. **Tem uma versão moderna mais barata de implementar?** Sim → Evoluir.

---

## Decisões por Funcionalidade

| #   | Funcionalidade            | Decisão    | Justificativa | Regra de Negócio (BR-XXX) | Prioridade |
| --- | ------------------------- | ---------- | ------------- | ------------------------- | ---------- |
| 1   | Cadastro de Beneficiários | **Evoluir** | Entidade hub central (11 programas dependem). Migrar lógica de negócio, modernizar UX (3270→web), unificar validação CPF (3 cópias divergentes), migrar PE group (dependentes) para tabela separada | BR-001, BR-004, BR-006 | Alta |
| 2   | Consulta de Beneficiários | **Evoluir** | Substituir tela MAP 3270 por UI moderna com filtros e mascaramento CPF consistente (LGPD). Lógica de leitura simples | BR-001 | Média |
| 3   | Geração de Pagamentos (Batch) | **Migrar** | Processo crítico mensal (1° dia útil). Ciclo G→P/D/E sem transição reversa. Eliminar duplicação com CALCBENF — unificar fórmula em serviço único | BR-007, BR-008, BR-009, BR-010, BR-020, BR-064 | Alta |
| 4   | Cálculo de Benefícios | **Migrar** | Core financeiro: fórmula com 6 fatores (regional, familiar, renda, idade, reajuste). Truncamento obrigatório. Manter exata reprodução para auditoria comparativa | BR-003 a BR-012 | Alta |
| 5   | Cálculo de Descontos | **Evoluir** | Unificar divergência batch (3% fixo) vs online (4 faixas progressivas, 6 tipos). Criar engine única parametrizável. Manter exceção judicial sem teto | BR-013 a BR-019 | Alta |
| 6   | Validação de CPF | **Evoluir** | Unificar 3 algoritmos divergentes (CADBENEF, VALBENEF, VALDOCS) em serviço único. Decisão pendente sobre 8 prefixos especiais | BR-023 a BR-027 | Média |
| 7   | Validação de Elegibilidade | **Migrar** | Regras complexas de status + dependência de VALDOCS. Migrar com parametrização moderna (JSON config vs hardcode) | BR-001, BR-002 | Alta |
| 8   | Relatórios (RELPGT + RELAUDIT) | **Evoluir** | Substituir PRINT 66 lin/pag por relatórios dinâmicos (PDF export + dashboard). Manter dados obrigatórios para TCU/CGU | BR-074 a BR-082 | Média |
| 9   | Auditoria | **Evoluir** | Incluir exclusões (tipo 'EX' atualmente filtrado), adicionar IP/sessão/JWT claims, trilha completa por compliance | BR-064, BR-082 | Alta |
| 10  | Correção Monetária (CALCCORR) | **Descartar** | Tabela IPCA congelada desde 2012, última carga em 2014. Programa possivelmente abandonado. Se reativado no futuro, será greenfield com API IBGE | BR-121 | Baixa |
| 11  | Conciliação Bancária (BATCHCON) | **Evoluir** | Eliminar código morto do Banco Real (2007). Modernizar integração BB de CNAB 240 flat file para API. Guard clause de status obrigatório (MYS-017) | BR-074 a BR-076 | Média |
| 12  | Gestão de Programas Sociais | **Migrar** | CADPROG: tipos A/P/T, status A/I. Lógica simples de CRUD com validação de tipo. Migrar como está para JPA | BR-002, BR-121 | Média |
| 13  | Gestão de Dependentes | **Evoluir** | Migrar de PE group (desnormalizado) para tabela separada. NUM-DEPENDENTES vira campo calculado (COUNT) em vez de redundante | BR-004 | Média |

> Adicione linhas para cada funcionalidade identificada no `discovery-report.md` do Estágio 1.

---

## Funcionalidades Novas (não existem no legado)

> Liste funcionalidades que o SIFAP 2.0 deveria ter e que não existem no sistema legado. Cada uma vira REQ-ID com `source_legacy: [GREENFIELD] <justificativa>`.

| #   | Funcionalidade Nova | Justificativa | Prioridade | Complexidade |
| --- | ------------------- | ------------- | ---------- | ------------ |
| N1  | API REST (OpenAPI/Swagger) | Legado é 3270/MAP sem API. Modernização requer endpoints RESTful para integração com frontend Next.js e sistemas externos | Alta | Média |
| N2  | Dashboard Analítico | Substituir relatórios estáticos (66 lin/pag) por visualizações interativas com filtros e export. Necessidade de TCU/CGU por dados em tempo real | Média | Média |
| N3  | Notificações (Email/Push) | Legado não notifica beneficiários. Modernização permite avisar sobre pagamentos gerados, status e pendências documentais | Baixa | Baixa |
| N4  | Autenticação OAuth2/JWT | Legado não tem autenticação real (USUARIO='BATCH' hardcoded). Sistema moderno requer SSO, roles e sessão auditável | Alta | Alta |
| N5  | Parametrização dinâmica | Substituir 27 fatores regionais, faixas IPCA e alíquotas hardcoded por tabelas de referência no banco, editáveis sem deploy | Média | Média |

---

## Resumo de Escopo

| Decisão   | Quantidade | Percentual |
| --------- | ---------- | ---------- |
| Migrar    | 5          | 38%        |
| Descartar | 1          | 8%         |
| Evoluir   | 7          | 54%        |
| **Total** | **13**     | 100%       |

## Riscos de Escopo

> Liste os riscos das decisões tomadas:

| Risco | Probabilidade | Impacto | Mitigação |
| ----- | ------------- | ------- | --------- |
| Reajuste duplo (Fator K + FATOR-REAJ) — sem validação do negócio, cálculo pode divergir do legado | Alta | Alto | Validar com área de negócio antes de implementar REQ de cálculo. Manter ambos até decisão formal |
| Unificação de descontos (batch 3% vs online 4 faixas) pode gerar valores diferentes para beneficiários em transição | Alta | Alto | Implementar engine parametrizável que suporte ambos os modos durante migração gradual (Strangler Fig) |
| Remoção de bypass região 99 pode bloquear fluxo legítimo desconhecido | Média | Alto | Documentar como exceção controlada com flag, log detalhado e revisão trimestral |
| Escopo de 13 funcionalidades + 5 greenfield pode não caber no tempo do Estágio 3 | Média | Alto | Priorizar: Alta primeiro (itens 1,3,4,5,7,9 + N4). Média e Baixa viram backlog |
| Migração de PE group para tabela separada pode causar inconsistência se NUM-DEPENDENTES dessincronizar durante coexistência | Média | Médio | Manter campo redundante durante migração, derivar COUNT apenas quando legado desligado |

## Aprovação

- [ ] Par 1 (Product Owner) aprovou as decisões de escopo
- [ ] Par 2 (Enterprise Architect) validou a viabilidade técnica
- [ ] Par 3 (Technical Lead) confirmou que cabe nas 3 horas do Estágio 3
- [ ] Time concordou com as prioridades

> **Aprovação obrigatória na Passagem #2** (~16:00). Sem ela, o Estágio 3 não começa.

— Paula


---

### Continuar a leitura

<table width="100%">
<tr>
<td width="50%" valign="top" align="left">
<sub><strong>← ANTERIOR</strong></sub><br/>
<a href="GUIDE.md"><strong>GUIDE do Estágio 2</strong></a><br/>
<sub>Passo a passo do estágio.</sub>
</td>
<td width="50%" valign="top" align="right">
<sub><strong>PRÓXIMO →</strong></sub><br/>
<a href="ADR-TEMPLATE.md"><strong>ADR-TEMPLATE</strong></a><br/>
<sub>Template de ADR.</sub>
</td>
</tr>
</table>

<sub>↑ <a href="../README.md">Voltar ao Kit PT-BR</a></sub>

