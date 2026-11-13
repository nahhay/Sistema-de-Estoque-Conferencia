
## Sobre o projeto

O **Estoque-Conferencia** é um sistema de gerenciamento e conferência de estoque desenvolvido em **Java 17**, com integração com **PostgreSQL** e uma **API REST própria utilizando o HttpServer do Java**.

O sistema foi desenvolvido com foco em **rastreabilidade, organização e simplicidade de uso**, permitindo registrar movimentações de estoque e realizar conferências de forma centralizada.

Cada movimentação registra o **funcionário responsável** e a **data e hora da operação**, permitindo acompanhar o histórico das atividades realizadas.

O sistema também calcula automaticamente o estoque esperado para uma conferência com base no estoque inicial, reposições e saídas registradas.

### Cálculo do estoque

```text
Estoque esperado = Estoque inicial + Reposições - Saídas

Divergência = Estoque físico - Estoque esperado
```

Dessa forma, o sistema permite identificar rapidamente quando a quantidade física encontrada durante uma conferência não corresponde à quantidade esperada.

---

## Funcionalidades

* Conferências de estoque
* Registro de reposições
* Registro de saídas
* Consulta de estoque
* Cálculo automático do estoque esperado
* Identificação de divergências
* Registro do responsável por cada operação
* Registro de data e hora das movimentações
* Painel do Gerente
* Painel do Funcionário
* API REST para comunicação entre frontend e backend
* Persistência dos dados em PostgreSQL
* Pool de conexões utilizando HikariCP
* Tratamento de erros e respostas HTTP em JSON
* Autenticação opcional por API Key
* Configuração de CORS
* Validação de dados recebidos pela API

---

## Painéis

### Gerente

O painel do gerente apresenta uma visão completa das operações realizadas no sistema.

| Informação       | Disponível |
| ---------------- | ---------- |
| Produto          | Sim        |
| Ponto            | Sim        |
| Estoque inicial  | Sim        |
| Reposições       | Sim        |
| Saídas           | Sim        |
| Estoque esperado | Sim        |
| Estoque físico   | Sim        |
| Divergência      | Sim        |
| Responsável      | Sim        |
| Data e hora      | Sim        |

### Funcionário

O painel do funcionário apresenta uma interface mais simples, focada na realização e acompanhamento das conferências.

| Informação     | Disponível |
| -------------- | ---------- |
| Produto        | Sim        |
| Ponto          | Sim        |
| Estoque físico | Sim        |
| Divergência    | Sim        |
| Responsável    | Sim        |
| Data e hora    | Sim        |

> **Importante:** os painéis representam diferentes visões da aplicação. A autorização atual da API é baseada em API Key e não possui, neste momento, um sistema completo de usuários com permissões individuais de Gerente e Funcionário.

---

## Tecnologias

### Backend

* **Java 17**
* **Java HttpServer**
* **JDBC**
* **PostgreSQL**
* **HikariCP**
* **Gson**
* API REST
* Tratamento de exceções
* API Key Authentication
* CORS

### Frontend

* **HTML5**
* **CSS3**
* **JavaScript**

### Banco de dados

* **PostgreSQL**
* Relacionamentos entre entidades
* Chaves primárias e estrangeiras
* Registro de data e hora
* Controle de movimentações

---

## Estrutura do projeto

```text
Sistema-de-Estoque-Conferencia/
│
├── frontend/
│   ├── funcionario/
│   │   ├── funcionario.html
│   │   ├── funcionario.js
│   │   └── funcionario.css
│   │
│   ├── index.html
│   ├── script.js
│   └── style.css
│
├── img/
│   ├── banner.png
│   └── demo.gif
│
├── src/
│   └── main/
│       ├── java/
│       │   └── estoque/
│       │       ├── api/
│       │       ├── db/
│       │       ├── exceptions/
│       │       ├── Conferencia.java
│       │       ├── ConferenciaService.java
│       │       ├── Estoque.java
│       │       ├── EstoqueService.java
│       │       ├── Funcionario.java
│       │       ├── FuncionarioService.java
│       │       ├── Movimentacao.java
│       │       ├── MovimentacaoService.java
│       │       ├── Ponto.java
│       │       ├── PontoService.java
│       │       ├── Produto.java
│       │       ├── ProdutoService.java
│       │       ├── Reposicao.java
│       │       ├── ReposicaoService.java
│       │       └── TipoMovimentacao.java
│       │
│       └── resources/
│           └── logging.properties
│
├── banco.sql
├── .gitignore
├── LICENSE
└── README.md
```

---

## Arquitetura

A aplicação segue uma estrutura dividida entre frontend, API, regras de negócio e banco de dados.

```text
┌──────────────────────────────┐
│          Frontend            │
│      HTML / CSS / JS         │
└──────────────┬───────────────┘
               │ HTTP / JSON
               ▼
┌──────────────────────────────┐
│          API REST            │
│       Java HttpServer        │
└──────────────┬───────────────┘
               │
               ▼
┌──────────────────────────────┐
│          Services            │
│       Regras de negócio      │
└──────────────┬───────────────┘
               │ JDBC
               ▼
┌──────────────────────────────┐
│         PostgreSQL           │
└──────────────────────────────┘
```

O backend possui handlers específicos para os principais recursos da aplicação:

```text
GET/POST /api/conferencias
GET      /api/estoque
GET/POST /api/reposicoes
GET/POST /api/movimentacoes
```

A API atualmente é executada por padrão em:

```text
http://localhost:8080
```

A porta pode ser alterada através da variável de ambiente `PORT`.

---

## Demonstração

<p align="center">
  <img src="img/demo.gif" alt="Demonstração do sistema" width="700">
</p>

---

# Como executar

## Requisitos

Antes de executar o sistema, é necessário ter instalado:

* **Java JDK 17 ou superior**
* **PostgreSQL**
* **Git**
* Dependências Java utilizadas pelo projeto:

  * PostgreSQL JDBC Driver
  * HikariCP
  * Gson

> O projeto atualmente não possui `pom.xml` ou `build.gradle`. Portanto, as dependências Java precisam estar disponíveis no classpath quando o projeto for compilado/executado, ou o projeto pode ser aberto em uma IDE e configurado manualmente.

---

## 1. Clone o repositório

```bash
git clone https://github.com/nahhay/Sistema-de-Estoque-Conferencia.git

cd Sistema-de-Estoque-Conferencia
```

---

## 2. Configure o PostgreSQL

Crie um banco de dados chamado:

```text
estoque
```

No PostgreSQL:

```sql
CREATE DATABASE estoque;
```

Depois, conecte-se ao banco criado e execute o arquivo:

```text
banco.sql
```

Esse arquivo cria as tabelas necessárias para o funcionamento do sistema.

### Estrutura principal do banco

O banco possui as seguintes entidades:

```text
produto
funcionario
ponto
estoque
movimentacao
reposicao
conferencia
```

As tabelas possuem relacionamentos através de chaves estrangeiras para manter a relação entre produtos, pontos, funcionários e movimentações.

---

## 3. Cadastre os dados iniciais

Depois de criar as tabelas, é necessário cadastrar os dados que serão utilizados pelo sistema.

No mínimo, é necessário possuir:

* Produtos
* Funcionários
* Pontos
* Registros de estoque

Exemplo:

```sql
INSERT INTO produto (nome)
VALUES ('Produto Exemplo');

INSERT INTO funcionario (nome)
VALUES ('Funcionário Exemplo');

INSERT INTO ponto (nome)
VALUES ('Ponto Principal');
```

Também é necessário criar o registro correspondente na tabela `estoque` para o produto e o ponto utilizados.

Exemplo:

```sql
INSERT INTO estoque (produto_id, ponto_id, quantidade)
VALUES (1, 1, 100);
```

> Os IDs utilizados nos exemplos dependem dos registros existentes no seu banco.

---

# Configuração do banco

O sistema procura as configurações nesta ordem:

1. Variáveis de ambiente
2. `config.properties`
3. Valores padrão

As configurações padrão do projeto são:

```text
URL:      jdbc:postgresql://localhost:5432/estoque
Usuário:  postgres
Senha:    postgres
```

Esses valores podem ser alterados para corresponder à instalação local do PostgreSQL.

---

## Configuração através de variáveis de ambiente

### Windows PowerShell

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/estoque"
$env:DB_USER="postgres"
$env:DB_PASSWORD="sua_senha"
```

### Linux / macOS

```bash
export DB_URL="jdbc:postgresql://localhost:5432/estoque"
export DB_USER="postgres"
export DB_PASSWORD="sua_senha"
```

O projeto também permite configurar o tamanho do pool de conexões:

```text
DB_POOL_MAX_SIZE
DB_POOL_MIN_IDLE
DB_POOL_CONN_TIMEOUT_MS
```

Os valores padrão são:

```text
DB_POOL_MAX_SIZE=10
DB_POOL_MIN_IDLE=2
DB_POOL_CONN_TIMEOUT_MS=30000
```

---

# API Key

A API possui suporte a autenticação através do cabeçalho:

```text
X-API-Key
```

As chaves podem ser configuradas através da variável de ambiente:

```text
API_KEYS
```

É possível informar uma ou mais chaves separadas por vírgula.

Exemplo:

```text
API_KEYS=minha-chave-secreta
```

Ou:

```text
API_KEYS=chave-gerente,chave-funcionario
```

Quando uma API Key está configurada, as requisições precisam enviar:

```http
X-API-Key: minha-chave-secreta
```

### Importante

Se nenhuma API Key for configurada, a autenticação da API fica **desabilitada**.

Isso pode ser útil durante o desenvolvimento local, mas **não é recomendado para um ambiente de produção**.

Nunca publique uma API Key real no GitHub.

---

# CORS

O backend possui configuração de CORS através da variável:

```text
CORS_ALLOWED_ORIGIN
```

Por padrão, a aplicação permite:

```text
*
```

Para um ambiente de produção, recomenda-se informar explicitamente a origem permitida.

Exemplo:

```text
CORS_ALLOWED_ORIGIN=http://localhost:5500
```

ou:

```text
CORS_ALLOWED_ORIGIN=https://seu-dominio.com
```

---

# Executando o backend

Depois de:

1. Instalar o Java 17
2. Instalar o PostgreSQL
3. Criar o banco `estoque`
4. Executar o `banco.sql`
5. Cadastrar os dados iniciais
6. Configurar as dependências Java
7. Configurar as credenciais do banco

execute a classe:

```text
estoque.api.ApiServer
```

Ao iniciar corretamente, o servidor ficará disponível em:

```text
http://localhost:8080
```

O backend registra no console a porta utilizada e informa que o frontend pode ser acessado através de `frontend/index.html`.

---

# Executando o frontend

Depois que a API estiver em execução, abra:

```text
frontend/index.html
```

para acessar o painel do gerente.

Para acessar o painel do funcionário:

```text
frontend/funcionario/funcionario.html
```

O frontend realiza as requisições para a API REST executada pelo backend.

> Dependendo do navegador e da forma como os arquivos locais forem abertos, pode ser necessário utilizar um servidor HTTP local para servir o frontend.

Uma alternativa simples é utilizar uma extensão de servidor local na IDE ou um servidor HTTP disponível no ambiente.

---

# Endpoints da API

## Conferências

### Listar conferências

```http
GET /api/conferencias
```

### Registrar conferência

```http
POST /api/conferencias
```

Exemplo de corpo:

```json
{
  "produtoId": 1,
  "pontoId": 1,
  "funcionarioId": 1,
  "quantidadeFisica": 95
}
```

---

## Estoque

### Consultar estoque

```http
GET /api/estoque?produtoId=1&pontoId=1
```

---

## Reposições

### Listar reposições

```http
GET /api/reposicoes
```

### Registrar reposição

```http
POST /api/reposicoes
```

Exemplo:

```json
{
  "produtoId": 1,
  "pontoId": 1,
  "funcionarioId": 1,
  "quantidade": 20
}
```

---

## Saídas

### Listar saídas

```http
GET /api/movimentacoes
```

### Registrar saída

```http
POST /api/movimentacoes
```

Exemplo:

```json
{
  "produtoId": 1,
  "pontoId": 1,
  "funcionarioId": 1,
  "quantidade": 5
}
```

---

# Fluxo de utilização

Para utilizar o sistema corretamente pela primeira vez:

```text
1. Criar o banco PostgreSQL
        ↓
2. Executar banco.sql
        ↓
3. Cadastrar produtos
        ↓
4. Cadastrar funcionários
        ↓
5. Cadastrar pontos
        ↓
6. Cadastrar o estoque inicial
        ↓
7. Iniciar a API
        ↓
8. Abrir o frontend
        ↓
9. Registrar reposições e saídas
        ↓
10. Realizar conferências
        ↓
11. Comparar estoque esperado x estoque físico
```

---

# Regras da conferência

Ao realizar uma conferência, o sistema considera:

```text
Estoque inicial
+ Reposições recebidas
- Saídas realizadas
= Estoque esperado
```

Depois compara o estoque esperado com o estoque físico informado:

```text
Estoque físico - Estoque esperado = Divergência
```

### Exemplo

```text
Estoque inicial:     100
Reposições:           20
Saídas:               10
-------------------------
Estoque esperado:    110

Estoque físico:      105

Divergência:
105 - 110 = -5
```

Nesse caso, o sistema identifica uma divergência de **-5 unidades**.

---

# Validações

A API possui validações para evitar dados inválidos.

Entre elas:

* Quantidades de reposição devem ser positivas
* Quantidades de saída devem ser positivas
* Quantidade física não pode ser negativa
* IDs devem corresponder a registros existentes
* JSON inválido retorna erro HTTP
* Parâmetros obrigatórios são validados
* Métodos HTTP não suportados retornam erro
* Erros de negócio são tratados e retornados em JSON

---

# Segurança

O projeto possui mecanismos básicos de proteção da API através de:

* API Key
* Validação das requisições
* Tratamento de exceções
* CORS configurável
* Uso de Prepared Statements através do acesso JDBC
* Separação entre regras de negócio e camada HTTP

### Recomendações para produção

Antes de utilizar o sistema em um ambiente real, recomenda-se:

* Configurar uma API Key forte
* Não armazenar senhas ou chaves diretamente no código
* Utilizar variáveis de ambiente
* Configurar CORS para domínios específicos
* Utilizar HTTPS
* Criar usuários individuais
* Implementar autenticação por usuário e senha
* Implementar permissões por função
* Não utilizar as credenciais padrão do PostgreSQL
* Fazer backups regulares do banco
* Utilizar um usuário PostgreSQL específico para a aplicação
* Evitar utilizar o usuário administrador do PostgreSQL em produção

---

# Limitações atuais

O projeto ainda está em desenvolvimento e possui algumas limitações conhecidas.

### Autenticação

A autenticação atual é baseada em API Key. Ainda não existe um sistema completo de login com usuários, senhas e permissões individuais.

### Perfis

Os painéis de Gerente e Funcionário possuem interfaces diferentes, porém a API ainda não implementa um sistema completo de autorização baseado em função.

### Gerenciamento de cadastros

O cadastro inicial de produtos, funcionários, pontos e estoque ainda precisa ser realizado diretamente no banco de dados.

### Dependências

O projeto ainda não utiliza um gerenciador de dependências como Maven ou Gradle.

### Deploy

O projeto atualmente está estruturado principalmente para execução local e não possui configuração pronta para deploy em produção.

---

# Melhorias planejadas

Algumas funcionalidades que podem ser adicionadas futuramente:

* [ ] Migrar o projeto para Maven ou Gradle
* [ ] Criar sistema de login
* [ ] Implementar autenticação por usuário e senha
* [ ] Criar níveis de acesso reais para Gerente e Funcionário
* [ ] Criar tela de cadastro de produtos
* [ ] Criar tela de cadastro de funcionários
* [ ] Criar tela de cadastro de pontos
* [ ] Permitir selecionar produtos e funcionários pelo nome em vez de informar IDs
* [ ] Criar endpoint de health check da API
* [ ] Melhorar o controle do estoque atual
* [ ] Permitir filtrar conferências por período
* [ ] Criar histórico detalhado de movimentações
* [ ] Adicionar edição e exclusão de registros
* [ ] Criar testes automatizados
* [ ] Adicionar documentação Swagger/OpenAPI
* [ ] Criar Dockerfile
* [ ] Criar Docker Compose com aplicação + PostgreSQL
* [ ] Preparar ambiente para deploy
* [ ] Implementar logs mais detalhados
* [ ] Criar sistema de backup e restauração
* [ ] Melhorar responsividade dos painéis
* [ ] Adicionar relatórios de divergência

---

# Próximos passos para utilizar em produção

O sistema pode ser executado localmente seguindo as instruções acima.

Para transformá-lo em uma aplicação adequada para uso em produção, ainda é recomendado realizar as seguintes etapas:

```text
1. Configurar PostgreSQL em um servidor
2. Criar um banco exclusivo para a aplicação
3. Criar um usuário PostgreSQL específico
4. Alterar as credenciais padrão
5. Configurar API_KEYS
6. Configurar CORS
7. Utilizar HTTPS
8. Configurar backup do banco
9. Implementar autenticação de usuários
10. Implementar permissões por função
11. Configurar o frontend para o endereço da API
12. Criar processo de build/deploy
13. Configurar monitoramento e logs
```

---

# Licença

Este projeto está licenciado sob a licença **MIT**.

Consulte o arquivo [LICENSE](LICENSE) para mais informações.

---

## Autora

Desenvolvido por **Nathaly Alencar**.

[GitHub](https://github.com/nahhay)
