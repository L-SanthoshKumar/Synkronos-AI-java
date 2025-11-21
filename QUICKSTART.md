# Quick Start Guide

Get Synkronos AI up and running in 5 minutes!

## Prerequisites

- Docker Desktop installed and running
- Git installed

## Steps

### 1. Clone and Navigate

```bash
cd ai-job-portal
```

### 2. Configure Environment (Optional)

If you want to use Cloudinary for resume uploads:

```bash
cp .env.example .env
# Edit .env and add your Cloudinary credentials
```

**Note:** The app will work without Cloudinary, but resume uploads won't function.

### 3. Start Everything

```bash
docker-compose up -d
```

This will:
- Start MongoDB
- Build and start the Java backend
- Build and start the Python ML engine
- Build and start the React frontend

### 4. Wait for Services

Give it 1-2 minutes for all services to start. Check status:

```bash
docker-compose ps
```

All services should show "Up" status.

### 5. Access the Application

- **Frontend:** http://localhost
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **ML Engine:** http://localhost:5000

### 6. Login

Use the demo credentials:

**Job Seeker:**
- Email: `jobseeker@demo.com`
- Password: `password123`

**Recruiter:**
- Email: `recruiter@demo.com`
- Password: `password123`

## Troubleshooting

### Services won't start

```bash
# Check logs
docker-compose logs

# Restart services
docker-compose restart

# Rebuild if needed
docker-compose up -d --build
```

### Port conflicts

If ports 80, 8080, or 5000 are in use, edit `docker-compose.yml` and change the port mappings.

### MongoDB connection issues

```bash
# Check MongoDB is running
docker-compose ps mongodb

# View MongoDB logs
docker-compose logs mongodb
```

### Frontend not loading

```bash
# Check frontend logs
docker-compose logs frontend

# Rebuild frontend
docker-compose up -d --build frontend
```

## Next Steps

1. **Explore the API:** Visit Swagger UI at http://localhost:8080/swagger-ui.html
2. **Test ML Engine:** Try the `/predict-score` endpoint with sample data
3. **Create Jobs:** Login as recruiter and post jobs
4. **Apply to Jobs:** Login as job seeker and apply with AI scoring

## Development Mode

To run services individually for development:

### Backend
```bash
cd backend-java
mvn spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### ML Engine
```bash
cd ml-engine
pip install -r requirements.txt
python -m spacy download en_core_web_sm
python app.py
```

## Stop Services

```bash
docker-compose down
```

To remove volumes (clears database):
```bash
docker-compose down -v
```

---

**Need help?** Check the main README.md or open an issue.

