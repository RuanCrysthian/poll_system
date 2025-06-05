# Autenticação JWT - Guia de Uso

## Visão Geral

O sistema de autenticação JWT foi implementado seguindo os princípios de Clean Architecture e as melhores práticas de segurança. O sistema oferece:

- **Autenticação baseada em JWT** com tokens stateless
- **Autorização baseada em roles** (ADMIN, VOTER)
- **Validação de credenciais** com BCrypt
- **Filtros de segurança** para proteção de endpoints
- **Gerenciamento de contexto de usuário** autenticado

## Endpoints de Autenticação

### 1. Login de Usuário
**POST** `/api/v1/auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "SecurePassword123!"
}
```

**Response (200 OK):**
```json
{
  "id": "user-123",
  "name": "John Doe",
  "email": "user@example.com",
  "role": "VOTER",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400
}
```

**Responses de Erro:**
- **400 Bad Request**: Dados de entrada inválidos
- **401 Unauthorized**: Email ou senha incorretos
- **403 Forbidden**: Conta do usuário está inativa

## Uso do Token JWT

### Headers de Autorização
Para acessar endpoints protegidos, inclua o token JWT no header Authorization:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Estrutura do Token JWT
O token contém as seguintes informações:
- **subject**: Email do usuário
- **userId**: ID único do usuário
- **name**: Nome do usuário
- **role**: Role do usuário (ADMIN ou VOTER)
- **iat**: Data de emissão
- **exp**: Data de expiração

## Configuração de Segurança

### Endpoints Públicos
- `POST /api/v1/auth/login` - Login de usuário
- `POST /api/v1/users` - Registro de usuário

### Endpoints Protegidos
- `POST /api/v1/votes` - Submeter voto (ADMIN, VOTER)
- `GET /api/v1/polls/**` - Operações com polls (ADMIN, VOTER)
- Todos os outros endpoints requerem autenticação

### Configurações JWT
As seguintes propriedades podem ser configuradas no `application.properties`:

```properties
# JWT Configuration
app.jwt.secret=mySecretKey123456789012345678901234567890abcdefghijklmnopqrstuvwxyz
app.jwt.expiration=86400  # 24 horas em segundos
```

## Exemplos de Uso

### 1. Login como Voter
```bash
curl -X POST http://localhost:8090/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "voter@example.com",
    "password": "VoterPassword123!"
  }'
```

### 2. Login como Admin
```bash
curl -X POST http://localhost:8090/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "AdminPassword123!"
  }'
```

### 3. Usar Token para Acessar Endpoint Protegido
```bash
curl -X GET http://localhost:8090/api/v1/polls \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

## Arquitetura da Implementação

### Use Cases (Application Layer)
- **LoginImpl**: Implementa a lógica de autenticação
  - Valida credenciais do usuário
  - Verifica se a conta está ativa
  - Gera token JWT
  - Retorna dados do usuário autenticado

### Gateways (Domain Layer)
- **JwtTokenGateway**: Interface para operações com JWT
  - `generateToken(User user)`: Gera token para usuário
  - `extractEmailFromToken(String token)`: Extrai email do token
  - `isTokenValid(String token, String email)`: Valida token
  - `isTokenExpired(String token)`: Verifica se token expirou

### Infrastructure Layer
- **JwtTokenGatewayImpl**: Implementação do gateway JWT usando JJWT
- **JwtAuthenticationFilter**: Filtro para interceptar e validar tokens
- **SecurityConfig**: Configuração de segurança do Spring Security
- **AuthController**: Controller REST para endpoints de autenticação
- **AuthenticationService**: Serviço para recuperar usuário do contexto

### Segurança
- **Algoritmo**: HMAC SHA-256 para assinatura dos tokens
- **Expiração**: Tokens expiram em 24 horas por padrão
- **Validação**: Tokens são validados a cada requisição
- **Stateless**: Não há sessões no servidor, apenas tokens

## Testes Unitários

### LoginImplTest
Testa todas as funcionalidades do use case de login:
- ✅ Login bem-sucedido com credenciais válidas
- ✅ Validação de entrada (email e senha obrigatórios)
- ✅ Tratamento de usuário não encontrado
- ✅ Validação de senha incorreta
- ✅ Verificação de conta inativa
- ✅ Geração correta de token JWT

### JwtTokenGatewayImplTest
Testa todas as operações com JWT:
- ✅ Geração de tokens válidos
- ✅ Extração de dados do token
- ✅ Validação de tokens
- ✅ Verificação de expiração
- ✅ Tratamento de tokens inválidos
- ✅ Geração de tokens únicos por usuário

## Benefícios da Implementação

1. **Segurança**: Uso de JWT com algoritmo HMAC SHA-256
2. **Escalabilidade**: Tokens stateless sem necessidade de sessões
3. **Flexibilidade**: Suporte a diferentes roles de usuário
4. **Manutenibilidade**: Código bem estruturado seguindo Clean Architecture
5. **Testabilidade**: Cobertura completa de testes unitários
6. **Performance**: Validação rápida de tokens sem consultas ao banco

## Fluxo de Autenticação

1. **Login**: Usuário envia credenciais para `/api/v1/auth/login`
2. **Validação**: Sistema valida email, senha e status da conta
3. **Token**: Sistema gera JWT com informações do usuário
4. **Resposta**: Sistema retorna token e dados do usuário
5. **Uso**: Cliente inclui token no header Authorization
6. **Filtro**: JwtAuthenticationFilter valida token em cada requisição
7. **Contexto**: Usuário autenticado é adicionado ao SecurityContext
8. **Autorização**: Endpoints verificam roles do usuário autenticado
