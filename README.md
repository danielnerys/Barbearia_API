# Barbearia API

API REST desenvolvida com Spring Boot para gerenciamento de uma barbearia.

O projeto permite o cadastro de usuários, barbeiros, serviços e agendamentos, além de possuir autenticação utilizando JWT e documentação interativa com Swagger.

<img src="https://github.com/danielnerys/Barbearia_API/blob/main/images/jwt_authenticated.png" alt="autenticado">

## Tecnologias

- Java 21
- Spring Boot 3
- Spring Security
- JWT (Auth0)
- Spring Data JPA
- PostgreSQL
- Maven
- Swagger / OpenAPI
- Bean Validation
- Lombok

---

## Funcionalidades

### Autenticação

- Login utilizando JWT
- Senhas criptografadas com BCrypt
- Proteção de rotas com Spring Security
- Controle de acesso por token Bearer

### Usuários

- Cadastro
- Consulta por ID
- Listagem

### Barbeiros

- Cadastro
- Consulta por ID
- Listagem
- Ativação
- Desativação
- Listagem apenas de barbeiros ativos

### Serviços

- Cadastro
- Consulta por ID
- Listagem
- Ativação
- Desativação
- Listagem apenas de serviços ativos

### Agendamentos

- Cadastro de agendamento
- Cancelamento
- Consulta por ID
- Listagem

Validações implementadas:

- barbeiro ativo
- serviço ativo
- horário disponível
- agendamento em blocos de 30 minutos

---

## Segurança

A API utiliza autenticação baseada em JWT.

Fluxo de autenticação:

1. O usuário realiza login utilizando e-mail e senha.
2. A API valida as credenciais.
3. Um token JWT é gerado.
4. O token deve ser enviado no header:

```http
Authorization: Bearer SEU_TOKEN
```

---

## Estrutura do projeto

```
src
├── Controller
├── DTO
│   ├── Request
│   └── Response
├── Exception
├── Model
├── Repository
├── Security
├── Service
└── Config
```

---

## Documentação

Após iniciar a aplicação:

```
http://localhost:8080/swagger-ui/index.html
```

O Swagger possui autenticação via botão **Authorize**.

---

## Como executar

### Clone o projeto

```bash
git clone https://github.com/danielnerys/Barbearia_API.git
```

### Configure o banco PostgreSQL

Altere o arquivo:

```
application.properties
```

com suas credenciais.

### Execute

```bash
mvn spring-boot:run
```

ou execute pela sua IDE.

---

## Exemplos

### Login

### Request

```json
{
  "email": "usuario@email.com",
  "senha": "123456"
}
```

### Response

```json
{
  "token": "eyJhbGc..."
}
```

---

## Prints

### Swagger

<img src="https://github.com/danielnerys/Barbearia_API/blob/main/images/swagger.png" alt="Texto Alternativo">

---

### Login

<img src="https://github.com/danielnerys/Barbearia_API/blob/main/images/login.png" alt="Texto Alternativo">

---

### Listagem autenticada

<img src="https://github.com/danielnerys/Barbearia_API/blob/main/images/listagem_autenticada.png" alt="Texto Alternativo">

---

### Estrutura do projeto

<img src="https://github.com/danielnerys/Barbearia_API/blob/main/images/estrutura_projeto.png" alt="Texto Alternativo">

---

## Melhorias futuras

- Testes unitários
- Docker
- Deploy da API
- Refresh Token
- Controle de permissões por perfil
- Logs de auditoria

---

## Autor

Daniel Nery

GitHub

https://github.com/danielnerys

LinkedIn

https://www.linkedin.com/in/danielnerys
