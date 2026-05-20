# ADR-003 — Autenticação e Autorização via OAuth2/JWT com Spring Security

## Status

✅ **Accepted** · 20/05/2026 · Par 2 (EA + SA) · Revisão: Par 5 (DevOps)

## REQs vinculados

REQ-AUD-001 (auditoria com identificação de usuário via JWT), REQ-AUD-003 (trilha completa incluindo sessão), REQ-BEN-001 (mascaramento CPF por role)

## ADRs vinculados

ADR-001 (Monolito Modular — segurança transversal no shared kernel)

## Contexto

O legado SIFAP **não possui autenticação real**. O campo USUARIO é hardcoded como 'BATCH' em processos batch e não existe controle de acesso em programas online (tela 3270 sem login). O discovery-report identificou 5 perfis de usuário inferidos do código (operador de cadastro, analista de benefícios, operador batch, auditor, administrador) — nenhum enforced pelo sistema.

A modernização precisa:
- Autenticar usuários reais (não mais 'BATCH' hardcoded).
- Autorizar por role (OPERATOR, ANALYST, AUDITOR, ADMIN, BATCH_PROCESS).
- Propagar identidade para auditoria (REQ-AUD-001 exige JWT subject em todo registro).
- Suportar integração com SSO corporativo (Azure AD / Entra ID).
- Proteger endpoints REST (REQ-BEN-001 — CPF acessível apenas por roles autorizados).

## Opções Consideradas

### Opção A — Sessão server-side (HttpSession + cookies)

- **Prós:** Simples de implementar; familiar para devs Java; Spring Security suporta nativamente.
- **Contras:** Não escala horizontalmente sem sticky sessions ou Redis; stateful; não funciona para API consumida por frontend SPA (Next.js); não propaga identidade para batch sem workaround.
- **Custo/Risco:** Custo baixo; risco médio (limitações de escala e integração).

### Opção B — OAuth2/JWT com Spring Security (escolhida)

- **Prós:** Stateless; escala horizontalmente sem afinidade; padrão da indústria; JWT contém claims (subject, roles, session_id) que satisfazem REQ-AUD-001; integra nativamente com Azure AD/Entra ID; Next.js suporta via next-auth; batch recebe service-to-service token via client_credentials.
- **Contras:** JWT não é revogável instantaneamente (precisa de TTL curto + refresh); complexidade inicial de setup do Authorization Server; token size cresce com claims.
- **Custo/Risco:** Custo médio; risco baixo; padrão maduro.

### Opção C — API Keys por serviço

- **Prós:** Extremamente simples; sem infraestrutura de auth.
- **Contras:** Sem identidade de usuário (inviabiliza REQ-AUD-001); sem roles (inviabiliza controle de acesso); secrets management complexo; não suporta SSO.
- **Custo/Risco:** Custo baixíssimo; risco altíssimo de compliance (TCU requer identificação de usuário).

## Decisão

Adotar **Opção B** — OAuth2/JWT com Spring Security 6.x como Resource Server.

### Arquitetura de Autenticação

```
┌─────────────┐     ┌──────────────────┐     ┌─────────────────┐
│  Next.js    │────▶│  Azure AD /      │────▶│  SIFAP Backend  │
│  Frontend   │     │  Entra ID (IdP)  │     │  (Resource Srv) │
└─────────────┘     └──────────────────┘     └─────────────────┘
                           │                          │
                    JWT (access_token)         Valida JWT signature
                    Claims: sub, roles,       Extrai roles → @PreAuthorize
                    session_id, iat, exp      Propaga sub → AuditEvent
```

### Roles Definidos

| Role | Acesso | Derivado de |
|------|--------|-------------|
| `ROLE_OPERATOR` | CRUD beneficiários, gerar pagamentos | Operador de cadastro + Operador batch |
| `ROLE_ANALYST` | Consulta beneficiários, cálculos, elegibilidade | Analista de benefícios |
| `ROLE_AUDITOR` | Somente leitura em tudo + relatórios de auditoria | Auditor |
| `ROLE_ADMIN` | Tudo + gestão de programas sociais + correções | Administrador |
| `ROLE_BATCH` | Execução de processos batch (service-to-service) | Processos automatizados (client_credentials) |

### Configuração Spring Security

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/audit/**").hasAnyRole("AUDITOR", "ADMIN")
            .requestMatchers("/api/v1/beneficiaries/**").hasAnyRole("OPERATOR", "ANALYST", "ADMIN")
            .requestMatchers("/api/v1/payments/generate").hasAnyRole("OPERATOR", "BATCH", "ADMIN")
            .requestMatchers("/actuator/health").permitAll()
            .anyRequest().authenticated()
        );
    return http.build();
}
```

### Propagação para Auditoria

```java
// Shared kernel — usado por todos os módulos
public record AuditContext(String userId, String sessionId, String ipAddress) {
    public static AuditContext fromSecurityContext() {
        var jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return new AuditContext(
            jwt.getSubject(),
            jwt.getClaimAsString("session_id"),
            RequestContextHolder.currentRequestAttributes()
                .getAttribute("remoteAddr", RequestAttributes.SCOPE_REQUEST).toString()
        );
    }
}
```

### Token Configuration

- Access token TTL: 15 minutos (curto — compensar impossibilidade de revogação imediata)
- Refresh token TTL: 8 horas (jornada de trabalho)
- Batch token (client_credentials): 1 hora (duração máxima do batch)
- Claims obrigatórios: `sub`, `roles`, `session_id`, `iat`, `exp`

## Consequências

### Positivas
- REQ-AUD-001 satisfeito: toda auditoria terá `userId` real (JWT subject), `sessionId` e `ipAddress`.
- Controle de acesso granular por endpoint e role.
- Integração nativa com Azure AD (Managed Identity para service-to-service).
- Frontend Next.js integra via `next-auth` com provider Azure AD.
- Batch identificado como `ROLE_BATCH` com token específico (não mais 'BATCH' hardcoded).

### Negativas
- Setup inicial de Azure AD/Entra ID é necessário (configuração de App Registration).
- JWT não revogável instantaneamente — se token vazado, é válido por até 15 min.
- Claims no JWT crescem com roles — monitorar tamanho do token.

### Riscos
- Se Azure AD ficar indisponível, ninguém consegue novo token (mitigação: cache de JWKS com TTL de 24h).
- Se TTL do access token for muito longo, risco de uso indevido de token roubado.
- Batch rodando com `ROLE_BATCH` tem acesso amplo — princípio de menor privilégio deve ser revisado.

## Critérios de Envelhecimento

Revisitar se:
- 🚨 Necessidade de revogação instantânea de token (considerar token introspection ou blacklist em Redis)
- 🚨 Mais de 10 roles distintos (considerar ABAC em vez de RBAC)
- 🚨 Integração com IdP não-Azure (considerar Keycloak como broker)
- 🚨 Requisitos de MFA para operações críticas (pagamentos > X valor)

## Referências

- `01-arqueologia/discovery-report.md` §2.3 — perfis de usuário inferidos
- `01-arqueologia/business-rules-catalog.md` — BR-078 (auditoria com USUARIO='BATCH')
- `02-spec-moderna/SPECIFICATION.md` — REQ-AUD-001, REQ-AUD-003, REQ-BEN-001
- Spring Security OAuth2 Resource Server: https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/
- OWASP Top 10 — A07:2021 (Identification and Authentication Failures)