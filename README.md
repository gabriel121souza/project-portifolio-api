# Project Portfolio API

API REST para gerenciamento de portfolio de projetos.

## Tecnologias

- Java 21
- Spring Boot 4.0.6
- Maven 3.9.16 via Maven Wrapper
- Spring Web MVC
- Spring Data JPA
- Spring Security
- Spring Validation
- Flyway
- PostgreSQL 16
- Springdoc OpenAPI 3.0.1
- Lombok
- JaCoCo 0.8.13
- Docker Compose 3.8

## Como Executar

### 1. Subir o banco

```powershell
docker compose up -d
```

O banco sobe com as credenciais:

```text
Host: localhost
Porta: 5432
Database: portfolio_db
Usuario: portfolio_user
Senha: portfolio_pass
```

### 2. Rodar a aplicacao

Se o `JAVA_HOME` ja estiver configurado:

```powershell
.\mvnw.cmd spring-boot:run
```

Se estiver usando o JBR do IntelliJ:

```powershell
$env:JAVA_HOME='C:\Users\gabri\AppData\Local\Programs\IntelliJ IDEA 2025.3.1\jbr'
.\mvnw.cmd spring-boot:run
```

Aplicacao:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

## Como Testar

```powershell
.\mvnw.cmd verify
```

Com `JAVA_HOME` temporario:

```powershell
$env:JAVA_HOME='C:\Users\gabri\AppData\Local\Programs\IntelliJ IDEA 2025.3.1\jbr'
.\mvnw.cmd verify
```

Relatorio de cobertura:

```text
target/site/jacoco/index.html
```

## Autenticacao

Endpoints `/api/**` usam Basic Auth:

```text
Usuario: portfolio_user
Senha: portfolio123
```

Endpoints `/external-api/**` nao exigem autenticacao.

