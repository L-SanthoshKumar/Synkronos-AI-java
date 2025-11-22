# Synkronos-AI-java

This repository contains a full-stack AI job-portal project with three main components:

- `frontend` — React (Vite + Tailwind) web app (default port 5173)
- `backend-java` — Spring Boot API (default port 8080)
- `ml-engine` — Python Flask ML microservice (default port 5000)

## Quick Start (recommended: Docker Compose)

1. Copy environment template and edit (Cloudinary, JWT secret, MongoDB URI):

```powershell
cp .env.example .env
# Edit .env with your credentials (Cloudinary, MONGODB_URI, JWT_SECRET, etc.)
```

2. Start all services with Docker Compose:

```powershell
docker-compose up -d --build
docker-compose logs -f
```

3. Access services in your browser:

- Frontend: http://localhost
- Backend API / Swagger: http://localhost:8080/swagger-ui.html
- ML Engine: http://localhost:5000

## Local Development (no Docker)

Prerequisites:
- Java 17+
- Maven
- Node.js 18+
- Python 3.11+
- MongoDB running locally (or update `MONGODB_URI` to your instance)

### 1) Backend (Spring Boot)

```powershell
cd backend-java
mvn clean package
# Run with default port (8080):
mvn spring-boot:run
# Or run the packaged jar and override port:
$env:SERVER_PORT='8081'
java -jar target\\ai-job-portal-1.0.0.jar
```

Health check (after backend is running):

```powershell
# GET health
Invoke-RestMethod -Uri http://localhost:8080/actuator/health -UseBasicParsing
```

### 2) Frontend (React)

```powershell
cd frontend
npm install
npm run dev
```

Open: http://localhost:5173

### 3) ML Engine (Python Flask)

```powershell
cd ml-engine
python -m venv venv
venv\\Scripts\\activate
pip install -r requirements.txt
python -m spacy download en_core_web_sm
python app.py
```

Health check:

```powershell
# GET ML engine health
Invoke-RestMethod -Uri http://localhost:5000/health -UseBasicParsing
```

## Troubleshooting
- If port 8080 (backend) or 5173 (frontend) is busy, change ports or stop the process using them.
- If the ML engine cannot connect: ensure spaCy model is downloaded and the service is running.
- Lombok/IDE warnings (null-safety, @Builder defaults) are non-fatal; run `mvn clean package` to confirm build success.

## Useful commands

- Build all locally (backend + frontend build):

```powershell
cd backend-java; mvn clean package
cd ..\\frontend; npm run build
```

## Contributing / Deployment
- Use `docker-compose.yml` for local multi-service development and for simple deployments.
- For production, configure environment variables securely (do NOT commit secrets).

---
If you want, I can start each service locally now and verify their health endpoints — tell me which to run first.