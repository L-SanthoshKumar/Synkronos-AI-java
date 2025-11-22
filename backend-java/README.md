# Backend - Synkronos AI (Spring Boot)

This folder contains the Spring Boot backend for the Synkronos AI job portal.

## Requirements
- Java 17+
- Maven 3.8+
- MongoDB (local or Docker)

## Quick start (local)
1. Build the project:

```powershell
cd backend-java
mvn clean package
```

2. Run with embedded server (default port 8080):

```powershell
cd backend-java
mvn spring-boot:run
```

If port 8080 is in use, run on another port:

```powershell
cd backend-java
# set SERVER_PORT env var (PowerShell)
$env:SERVER_PORT='8081'
mvn spring-boot:run
```

or run the packaged jar:

```powershell
$env:SERVER_PORT='8081'
java -jar target\ai-job-portal-1.0.0.jar
```

3. Health & API docs
- Health endpoint: `http://localhost:8080/actuator/health` (or other port)
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Configuration
Configuration values are in `src/main/resources/application.yml` and can be overridden via environment variables.
Key environment variables:
- `MONGODB_URI` - MongoDB connection string (default: `mongodb://localhost:27017/synkronos_db`)
- `JWT_SECRET` - JWT secret
- `ML_SERVICE_URL` - ML microservice URL
- `SERVER_PORT` - port override

## Docker
A Dockerfile is included. Use `docker-compose.yml` from the repo root to run all services.

## Troubleshooting
- If the app fails to start due to port in use, change `SERVER_PORT` or stop the process using the port.
- If MongoDB connection fails, ensure MongoDB is running and `MONGODB_URI` is correct.

## Development notes
- Lombok is used throughout the codebase; ensure your IDE supports Lombok.
- Warnings about `@Builder` defaults or null-safety are non-fatal; they are suggestions to improve null-safety.

---
Created/updated by automation script.