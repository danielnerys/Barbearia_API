# Spec — Ajustes no backend antes do front-end

Objetivo: preparar a API para consumo por um front-end SPA (navegador). Hoje existem lacunas
que impedem o front de funcionar, além de duas falhas de autorização.

Implementar na ordem abaixo, **um commit por item**, em um branch novo
(`improvements/preparar-para-frontend`).

Contexto obrigatório: ler o `CLAUDE.md` da raiz antes de começar. Seguir os padrões já
existentes no projeto — exceção dedicada por caso de erro + handler no `GlobalExceptionHandler`,
DTOs em `DTO/Request` e `DTO/response` (respeitando a diferença de maiúscula/minúscula das pastas),
flag `ativo` em vez de exclusão.

---

## Item 1 — CORS centralizado

**Problema.** Os controllers `Usuario`, `Barbeiro`, `Servico` e `Agendamento` têm
`@CrossOrigin("*")`, mas o `AuthController` não tem. Pior: o `SecurityConfig` não habilita CORS
no filter chain, então as requisições de preflight (`OPTIONS`) chegam sem `Authorization`,
caem no `anyRequest().authenticated()` e são rejeitadas com 401. Do navegador, praticamente
nenhuma chamada autenticada funciona.

**O que fazer.**

1. Remover `@CrossOrigin` de todos os controllers.
2. No `SecurityConfig`, declarar um bean `CorsConfigurationSource` e habilitar
   `.cors(Customizer.withDefaults())` no filter chain.
3. Origens permitidas devem vir de configuração, não hardcoded. Ler de uma propriedade
   `api.cors.allowed-origins` (lista separada por vírgula), com default
   `http://localhost:5173,http://localhost:3000`. No `docker-compose.yml`, expor via variável
   de ambiente `CORS_ALLOWED_ORIGINS`.
4. Métodos permitidos: `GET, POST, PUT, PATCH, DELETE, OPTIONS`. Headers: `*`.
   `allowCredentials` pode ficar `false` (a autenticação é por header `Authorization`, não cookie).

**Critério de aceite.** Um `OPTIONS` para `/api/agendamentos` com
`Origin: http://localhost:5173` responde 200 com os headers `Access-Control-Allow-*`.
Um `POST /auth/login` a partir do navegador não é bloqueado por CORS.

---

## Item 2 — Falha de autorização: `/api/usuarios` está público

**Problema.** O `SecurityConfig` tem `.requestMatchers(..., "/api/usuarios").permitAll()`.
Isso libera o path inteiro, **para todos os métodos**. O intuito era liberar só o cadastro
(`POST`), mas na prática `GET /api/usuarios` — que devolve a lista de todos os usuários do
sistema — está acessível sem autenticação alguma.

**O que fazer.** Trocar por um matcher com método explícito:

```java
.requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
```

**Critério de aceite.** `GET /api/usuarios` sem token retorna 401.
`POST /api/usuarios` sem token continua criando usuário (201).

---

## Item 3 — Autorização por papel (`@PreAuthorize`)

**Problema.** Nenhum endpoint verifica o papel do usuário. Qualquer cliente autenticado pode
cadastrar barbeiro, desativar serviço ou listar os agendamentos de toda a barbearia.

**O que fazer.**

1. Anotar o `SecurityConfig` com `@EnableMethodSecurity`.
2. Aplicar `@PreAuthorize("hasRole('ADMIN')")` nos endpoints administrativos:

   - `BarbeiroController`: `cadastrar`, `ativar`, `desativar`, `listarTodos`
   - `ServicoController`: `cadastrarServico`, `ativar`, `desativar`, `listarTodos`
   - `UsuarioController`: `listarTodos`, `buscarPorId`
   - `AgendamentoController`: `listarTodos`

   Permanecem abertos a qualquer usuário autenticado: `/api/barbeiros/ativos`,
   `/api/servicos/ativos`, `/api/barbeiros/{id}`, `/api/servicos/{id}`, e o cadastro de
   agendamento.

3. Verificar que `hasRole('ADMIN')` funciona: `Usuario.getAuthorities()` já devolve
   `ROLE_ADMIN`, então o prefixo está correto — **não** trocar para `hasAuthority`.

4. No `GlobalExceptionHandler`, adicionar um handler para `AccessDeniedException`
   retornando 403 com o formato padrão `{"mensagem": "..."}`.

   Atenção: por padrão o Spring Security trata `AccessDeniedException` na cadeia de filtros,
   e um `@RestControllerAdvice` pode não capturá-la. Se o 403 não sair no formato padrão,
   registrar um `AccessDeniedHandler` no `SecurityConfig` em vez do handler no advice.

**Critério de aceite.** Um usuário com role `CLIENTE` recebe 403 ao chamar
`POST /api/barbeiros`. Um `ADMIN` recebe 201.

---

## Item 4 — Identidade do usuário logado (`GET /auth/me`)

**Problema.** `LoginResponse` devolve apenas o token. O front não tem como saber o `id` do
usuário (necessário para agendar), o `nome` (para exibir) nem o `role` (para decidir entre a
área de cliente e a de admin).

**O que fazer.**

1. Criar `GET /auth/me` no `AuthController`. Requer autenticação. Devolve um novo DTO
   `UsuarioLogadoResponse(UUID id, String nome, String email, Role role)`.
2. Obter o usuário a partir do `SecurityContext`. O principal é a própria entidade `Usuario`
   (ela implementa `UserDetails`), então dá para fazer cast direto — mas prefira buscar pelo
   e-mail via `Authentication.getName()` no `UsuarioRepository`, que é mais explícito.
3. Não incluir a senha no retorno.

**Critério de aceite.** `GET /auth/me` com token válido devolve os dados do dono do token.
Sem token, 401.

---

## Item 5 — `clienteId` deve vir do token, não do corpo

**Problema (segurança).** `AgendamentoRequest` recebe `clienteId` do cliente HTTP. Um usuário
autenticado pode agendar em nome de qualquer outro, apenas trocando o UUID no JSON.

**O que fazer.**

1. Remover o campo `clienteId` de `AgendamentoRequest`.
2. Em `AgendamentoController.cadastrarAgendamento`, obter o usuário autenticado e passá-lo ao
   service (via parâmetro `Authentication` ou `@AuthenticationPrincipal`).
3. Ajustar `AgendamentoService.cadastrar` para receber o cliente já resolvido.

Se quiser permitir que um ADMIN agende em nome de outro cliente, isso deve ser um endpoint
separado e explícito — não um campo opcional no mesmo request.

**Critério de aceite.** O agendamento criado sempre pertence ao dono do token, independente do
corpo enviado.

---

## Item 6 — Meus agendamentos

**Problema.** `GET /api/agendamentos` devolve os agendamentos de todos. Um cliente precisa ver
apenas os seus.

**O que fazer.**

1. `GET /api/agendamentos/meus` — lista os agendamentos do usuário do token, ordenados por
   `dataHoraVisita` decrescente. Acessível a qualquer usuário autenticado.
2. Adicionar ao `AgendamentoRepository`:
   `List<Agendamento> findByClienteOrderByDataHoraVisitaDesc(Usuario cliente)`.
3. `GET /api/agendamentos/barbeiro/{barbeiroId}?data=YYYY-MM-DD` — agenda de um barbeiro num
   dia. Restrito a `ADMIN`. Repository:
   `List<Agendamento> findByBarbeiroAndDataHoraVisitaBetween(Barbeiro b, LocalDateTime inicio, LocalDateTime fim)`.

**Critério de aceite.** Um cliente vê só os próprios agendamentos em `/meus`, mesmo que existam
outros no banco.

---

## Item 7 — Reforçar o cancelamento

**Problema.** `PATCH /api/agendamentos/{id}/cancelar` não verifica quem está cancelando.
Qualquer usuário autenticado pode cancelar o agendamento de outra pessoa passando o UUID.

**O que fazer.** No service, permitir o cancelamento apenas se o agendamento pertence ao
usuário do token **ou** se o usuário é `ADMIN`. Caso contrário, lançar uma exceção nova
(`OperacaoNaoPermitidaException`) mapeada para 403 no `GlobalExceptionHandler`.

Verificar também se já existe validação impedindo cancelar um agendamento que já está
`CANCELADO` ou que já passou. Se não existir, adicionar.

---

## Item 8 — Horários disponíveis

**Problema.** Para a tela de marcação, o front precisa saber quais horários estão livres. Hoje
só é possível tentar agendar e receber erro.

**O que fazer.**

1. `GET /api/agendamentos/disponibilidade?barbeiroId={uuid}&data=YYYY-MM-DD`
   Acessível a qualquer usuário autenticado. Retorna `List<LocalTime>` (ou
   `List<String>` no formato `"HH:mm"`) com os slots livres.

2. Lógica:
   - Gerar slots de 30 em 30 minutos dentro do horário de funcionamento.
   - Remover os que já possuem agendamento **não cancelado** para aquele barbeiro naquele dia.
   - Se a data for hoje, remover os horários que já passaram.
   - Se o barbeiro não existir → 404 (reusar `BarbeiroNaoEncontrado`).
   - Se o barbeiro estiver inativo → lista vazia ou `BarbeiroInativoException`; escolher um e
     documentar no Swagger.

3. Horário de funcionamento: não existe no modelo hoje. Começar com propriedades de
   configuração (`api.barbearia.horario-abertura=09:00`, `api.barbearia.horario-fechamento=19:00`),
   com esses valores como default. **Não** hardcodar dentro do service.

4. Buscar os agendamentos do dia de uma vez (uma query com `between`), não um
   `exists` por slot — evita ~20 queries por requisição.

**Critério de aceite.** Com um agendamento às 10:00, a resposta para aquele barbeiro/dia não
inclui `10:00` mas inclui `10:30`.

---

## Observações gerais

- **Não** alterar o `application.properties` (é gitignored). Toda propriedade nova precisa de
  um default sensato no código (`@Value("${prop:default}")`) para o projeto subir sem ela, e
  deve ser documentada no `CLAUDE.md` e no `.env.example` quando aplicável.
- Ao final, atualizar o `CLAUDE.md`: novos endpoints, o esquema de autorização por papel, e as
  propriedades de configuração novas.
- Rodar `./mvnw clean package` ao final e garantir que compila e que o teste de contexto passa.
- Não existe suíte de testes de service/controller hoje. Se der tempo, os itens 5, 7 e 8 são os
  que mais se beneficiam de teste — mas não é bloqueante.
