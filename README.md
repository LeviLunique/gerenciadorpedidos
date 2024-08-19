
# Gerenciador de Pedidos

Este projeto é uma aplicação de gerenciamento de pedidos utilizando Spring Boot, RabbitMQ e MySQL. O objetivo deste README é fornecer instruções detalhadas sobre como configurar a infraestrutura necessária na AWS, testar os endpoints da aplicação e executar o projeto localmente.

## Requisitos

Antes de começar, certifique-se de ter as seguintes ferramentas instaladas em sua máquina local:

- [Docker](https://docs.docker.com/get-docker/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- [AWS CLI](https://aws.amazon.com/cli/)
- [Terraform (opcional)](https://www.terraform.io/downloads)
- [Postman ou cURL](https://www.postman.com/downloads/)

## 1. Configuração da Infraestrutura na AWS

### 1.1. Configuração do Amazon RDS (MySQL)

1. **Criar uma instância RDS MySQL**:
   - Acesse o [Amazon RDS Console](https://console.aws.amazon.com/rds/home).
   - Clique em **Create database**.
   - Escolha o método de criação **Standard Create**.
   - Em **Engine options**, selecione **MySQL**.
   - Escolha a versão do MySQL desejada.
   - Em **Templates**, selecione **Free tier** se estiver usando a conta free tier da AWS.
   - Configure as credenciais de administrador (nome de usuário e senha).
   - Em **DB instance size**, selecione o tipo de instância db.t2.micro (free tier).
   - Configure o armazenamento (20 GB é suficiente para este projeto).
   - Em **Connectivity**, selecione a VPC e as configurações de sub-rede.
   - Habilite o acesso público para a instância e crie ou selecione um grupo de segurança que permita acesso na porta 3306.
   - Finalize a criação da instância e anote o **endpoint** do RDS e as credenciais para uso posterior.

### 1.2. Configuração do Amazon ECS (Elastic Container Service)

#### 1.2.1. Criar um Cluster ECS

1. **Criar um Cluster ECS**:
   - Acesse o [Amazon ECS Console](https://console.aws.amazon.com/ecs/home).
   - Clique em **Create Cluster**.
   - Escolha o modelo **EC2 Linux + Networking**.
   - Dê um nome ao cluster e configure o tamanho do instance type para `t2.micro`.
   - Configure a VPC e sub-rede onde o cluster será criado.
   - Configure as instâncias EC2 para que possam acessar o RDS criado na etapa anterior.
   - Finalize a criação do cluster.

#### 1.2.2. Criar uma Task Definition para o ECS

1. **Criar uma Task Definition**:
   - No Console do ECS, acesse **Task Definitions** e clique em **Create new Task Definition**.
   - Selecione o tipo de lançamento **EC2**.
   - Dê um nome à task definition.
   - Adicione containers à task:
     - **Container 1**:
       - Nome: `gerenciador-pedidos-app`
       - Imagem: Aponte para a imagem Docker que será criada para o Spring Boot.
       - Defina a memória e CPU conforme necessário.
       - Configurações de porta: 8080.
     - **Container 2**:
       - Nome: `rabbitmq`
       - Imagem: `rabbitmq:3-management`
       - Defina a memória e CPU conforme necessário.
       - Configurações de porta: 15672 e 5672.
   - Adicione as variáveis de ambiente necessárias, como credenciais do RDS e parâmetros de configuração do RabbitMQ.
   - Finalize a criação da task definition.

#### 1.2.3. Executar a Task no Cluster

1. **Rodar a Task**:
   - No Console do ECS, selecione o cluster criado.
   - Vá até **Tasks** e clique em **Run new Task**.
   - Selecione a Task Definition criada anteriormente.
   - Configure o número de instâncias da task que deseja executar.
   - Execute a task e aguarde até que esteja rodando.

## 2. Testando os Endpoints da Aplicação

### 2.1. Endpoints Disponíveis

1. **Produtos**
   - **GET /produtos**: Retorna todos os produtos.
   - **POST /produtos**: Cria um novo produto.
   - **GET /produtos/{id}**: Retorna um produto específico por ID.
   - **PUT /produtos/{id}**: Atualiza um produto específico por ID.
   - **DELETE /produtos/{id}**: Deleta um produto específico por ID.

2. **Pedidos**
   - **GET /pedidos**: Retorna todos os pedidos.
   - **POST /pedidos**: Cria um novo pedido.
   - **GET /pedidos/{id}**: Retorna um pedido específico por ID.
   - **PUT /pedidos/{id}**: Atualiza um pedido específico por ID.
   - **DELETE /pedidos/{id}**: Deleta um pedido específico por ID.
   - **PUT /pedidos/{id}/status**: Atualiza o status de um pedido específico.

### 2.2. Testando com Postman ou cURL

1. **Testar criação de produto**:
   - Request: `POST /produtos`
   - Body:
     ```json
     {
       "nome": "Produto Exemplo",
       "descricao": "Descrição do Produto Exemplo",
       "preco": 29.99
     }
     ```
   - Resposta esperada: 201 Created

2. **Testar criação de pedido**:
   - Request: `POST /pedidos`
   - Body:
     ```json
     {
       "dataPedido": "2024-08-19T15:00:00",
       "itens": [
         {
           "produtoId": 1,
           "quantidade": 2
         }
       ]
     }
     ```
   - Resposta esperada: 201 Created

3. **Testar atualização de status de pedido**:
   - Request: `PUT /pedidos/1/status`
   - Body:
     ```json
     {
       "status": "processando"
     }
     ```
   - Resposta esperada: 200 OK

## 3. Executando o Projeto Localmente

### 3.1. Build e Execução com Docker Compose

1. **Clonar o repositório**:
   ```bash
   git clone <URL_DO_REPOSITORIO>
   cd gerenciadorpedidos
   ```

2. **Arquivo `docker-compose.yml`**:
   - Certifique-se de que o arquivo `docker-compose.yml` está configurado corretamente com os serviços para a aplicação, RabbitMQ e MySQL.

3. **Subir a aplicação**:
   ```bash
   docker-compose up --build
   ```
   - Isso vai iniciar os serviços MySQL, RabbitMQ e a aplicação Spring Boot.

4. **Verificar a execução**:
   - Acesse o RabbitMQ Management UI: `http://localhost:15672` (login: guest, senha: guest).
   - Verifique se o MySQL está rodando na porta 3306.
   - A aplicação Spring Boot estará disponível em `http://localhost:8080`.

### 3.2. Configuração de Variáveis de Ambiente

Certifique-se de configurar as seguintes variáveis de ambiente no seu sistema ou no Docker Compose:

```env
RABBITMQ_HOST=rabbitmq
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest
MYSQL_HOST=mysql
MYSQL_PORT=3306
MYSQL_DATABASE=gerenciadorpedidos
MYSQL_USER=root
MYSQL_PASSWORD=secret
```

## 4. Deploy na AWS

### 4.1. Subir Imagem Docker para ECR (Elastic Container Registry)

1. **Criar um repositório ECR**:
   - Acesse o [Amazon ECR Console](https://console.aws.amazon.com/ecr/repositories).
   - Crie um novo repositório para a imagem da aplicação.

2. **Build e push da imagem Docker**:
   ```bash
   docker build -t gerenciador-pedidos-app .
   aws ecr get-login-password --region <REGION> | docker login --username AWS --password-stdin <AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com
   docker tag gerenciador-pedidos-app:latest <AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com/gerenciador-pedidos-app:latest
   docker push <AWS_ACCOUNT_ID>.dkr.ecr.<REGION>.amazonaws.com/gerenciador-pedidos-app:latest
   ```

### 4.2. Atualizar Task Definition no ECS

1. **Atualizar Task Definition**:
   - No Console do ECS, edite a Task Definition para usar a nova imagem Docker do ECR.

2. **Deploy da Task**:
   - Execute novamente a task no cluster para refletir as mudanças.

## 5. Conclusão

Agora você deve ter sua aplicação de gerenciamento de pedidos rodando na AWS com todos os serviços configurados. Certifique-se de testar todos os endpoints e garantir que a aplicação esteja funcionando como esperado.
