# Order API
Essa api é parte do projeto da **Fase 4** da Especialização em Arquitetura e Desenvolvimento Java da FIAP.
Um sistema de gerenciamento de pedidos integrado com Spring e Mocrosserviços. A aplicação foi desenvolvida em **Java 21**,
utilizando **Spring Boot**, **Maven**, um banco de dados **H2** para testes, **Mockito** e **JUnit 5** para testes
unitários, **Lombok** para facilitar o desenvolvimento e documentação gerada pelo **Swagger**.

## Descrição do Projeto
O objetivo desse sistema é abranger desde a gestão de clientes e produtos até o processamento e entrega de pedidos,
enfatizando a autonomia dos serviços, comunicação eficaz e persistência de dados isolada. Esta API é responsável pela
gestão dos pedidos.

## Funcionalidades
A API permite:
- **Cadastrar, e atualizar** pedidos através de eventos.
- **Buscar e deletar** pedidos através de endpoint.

## Tecnologias Utilizadas
- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Maven**
- **Banco de Dados H2**
- **Banco de Dados Mysql**
- **Mockito** e **JUnit 5**
- **Lombok**
- **Swagger**
- **Docker Compose**
- **Spotless**
- **Jacoco**
- **Docker**
- **RabbitMQ**

## Estrutura do Projeto
O projeto segue uma arquitetura modularizada, organizada nas seguintes camadas:
- `config`: Configurações das filas do RabbitMQ. 
- `core`: Contém as regras de negócio do sistema.
- `core.domain`: Define as entidades principais do domínio.
- `core.domain.exception`: Exceções personalizadas para regras de negócio.
- `core.valueobject`: Representa os objetos de valor do domínio.
- `core.dto`: Representa as entradas e saídas de dados para a API.
- `core.gateway`: Interfaces para interação com o banco de dados.
- `core.usecase`: Contém os casos de uso do sistema.
- `core.usecase.exception`: Exceções personalizadas para regras de negócio.
- `entrypoint.configuration`: Configurações do sistema, incluindo tratamento de exceções.
- `entrypoint.consumer`: Consumidores de eventos do RabbitMQ.
- `entrypoint.controller`: Controladores responsáveis por expor os endpoints da API.
- `event`: Representação dos eventos de domínio.
- `infrastructure.gateway`: Implementações das interfaces de gateway.
- `infrastructure.gateway.dto`: Representação dos dados da Api de produtos.
- `infrastructure.gateway.exception`: Exceções personalizadas para as implementações de gateway.
- `infrastructure.gateway.queue`: Implementação da fila do RabbitMQ.
- `infrastructure.persistence.entity`: Representação das entidades persistidas no banco de dados.
- `infrastructure.persistence.repository`: Interfaces dos repositórios Spring Data JPA.
- `mapper`: Mapeamento das entidades de domínio para entidades de persistência.
- `presenter`: Representação dos dados de saída para a API.

## Pré-requisitos
- Java 21
- Maven 3.6+
- IDE como IntelliJ IDEA ou Eclipse

## Configuração e Execução
1. **Clone o repositório**:
   ```bash
   git clone https://github.com/GabiFerraz/Order-Api.git
   ```
2. **Instale as dependências:**
   ```bash
   mvn clean install
   ```
3. **Execute o projeto:**
   ```bash
   mvn spring-boot:run
   ```

## Uso da API
- **Banco H2**: http://localhost:8080/h2-console
- **Driver Class**: org.h2.Driver
- **JDBC URL**: jdbc:h2:mem:client
- **User Name**: gm
- **Password**:
  Para visualização dos dados da api no banco de dados Mysql, subir o docker-compose: **docker-compose up --build**

Os endpoints estão documentados via **Swagger**:
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **Swagger JSON**: http://localhost:8080/v3/api-docs

### Possibilidades de Chamadas da API
1. **Busca de Pedidos:**
```json
curl --location 'localhost:8084/api/orders/1'
```

2. **Delete de Cliente:**
```json
curl --location --request DELETE 'localhost:8084/api/orders/1'
```


## Testes
Para rodar os testes unitários:
```bash
mvn test
```

**Rodar o coverage:**
   ```bash
   mvn clean package
   ```
Depois acessar pasta target/site/jacoco/index.html

O projeto inclui testes unitários, testes de integração e testes de arquitetura para garantir a qualidade e
confiabilidade da API.

## Desenvolvedora:
- **Gabriela de Mesquita Ferraz** - RM: 358745