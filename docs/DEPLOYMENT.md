# Deployment Guide

This guide covers deploying Synkronos AI to various cloud platforms.

## Prerequisites

- Docker and Docker Compose installed
- Git installed
- Cloud provider account (AWS/DigitalOcean/Azure)
- Domain name (optional)

## General Deployment Steps

1. **Prepare Environment Variables**
   ```bash
   cp .env.example .env
   # Edit .env with production values
   ```

2. **Build and Test Locally**
   ```bash
   docker-compose up -d --build
   docker-compose logs -f
   ```

3. **Push to Git Repository**
   ```bash
   git add .
   git commit -m "Production deployment"
   git push origin main
   ```

## AWS EC2 Deployment

### Step 1: Launch EC2 Instance

1. Go to AWS Console → EC2
2. Launch Instance with:
   - OS: Ubuntu 22.04 LTS
   - Instance Type: t3.medium or larger
   - Storage: 20GB minimum
   - Security Group: Allow ports 22, 80, 443, 8080

### Step 2: Connect and Setup

```bash
ssh -i your-key.pem ubuntu@your-ec2-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Logout and login again
exit
```

### Step 3: Deploy Application

```bash
# Clone repository
git clone <your-repo-url>
cd ai-job-portal

# Setup environment
cp .env.example .env
nano .env  # Edit with production values

# Start services
docker-compose up -d

# Check status
docker-compose ps
docker-compose logs -f
```

### Step 4: Setup Nginx (Optional)

```bash
sudo apt install nginx -y

# Create nginx config
sudo nano /etc/nginx/sites-available/synkronos

# Add configuration:
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:80;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}

# Enable site
sudo ln -s /etc/nginx/sites-available/synkronos /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

### Step 5: Setup SSL with Let's Encrypt

```bash
sudo apt install certbot python3-certbot-nginx -y
sudo certbot --nginx -d your-domain.com
```

## DigitalOcean Deployment

### Option 1: Droplet (Similar to EC2)

Follow AWS EC2 steps, but use DigitalOcean Droplet instead.

### Option 2: App Platform

1. Go to DigitalOcean App Platform
2. Create new app from GitHub repository
3. Configure:
   - Backend: Java service
   - Frontend: Static site
   - ML Engine: Web service
   - Database: MongoDB (managed)
4. Set environment variables
5. Deploy

## Azure App Service Deployment

### Step 1: Create Resources

```bash
# Install Azure CLI
curl -sL https://aka.ms/InstallAzureCLIDeb | sudo bash

# Login
az login

# Create resource group
az group create --name synkronos-rg --location eastus

# Create App Service Plan
az appservice plan create --name synkronos-plan --resource-group synkronos-rg --sku B1 --is-linux

# Create Web Apps
az webapp create --resource-group synkronos-rg --plan synkronos-plan --name synkronos-backend --runtime "JAVA:17-java17"
az webapp create --resource-group synkronos-rg --plan synkronos-plan --name synkronos-frontend --runtime "NODE:18-lts"
az webapp create --resource-group synkronos-rg --plan synkronos-plan --name synkronos-ml --runtime "PYTHON:3.11"
```

### Step 2: Configure Environment Variables

```bash
az webapp config appsettings set --resource-group synkronos-rg --name synkronos-backend --settings \
  MONGODB_URI="your-mongodb-uri" \
  JWT_SECRET="your-secret" \
  CLOUDINARY_CLOUD_NAME="your-cloud-name"
```

### Step 3: Deploy

```bash
# Backend
cd backend-java
mvn clean package
az webapp deploy --resource-group synkronos-rg --name synkronos-backend --type jar --src-path target/*.jar

# Frontend
cd frontend
npm run build
az webapp deploy --resource-group synkronos-rg --name synkronos-frontend --type static --src-path dist
```

## Render Deployment

1. **Create MongoDB Database**
   - Go to Render Dashboard
   - Create new MongoDB database
   - Note connection string

2. **Deploy Backend**
   - New → Web Service
   - Connect GitHub repository
   - Root Directory: `backend-java`
   - Build Command: `mvn clean package -DskipTests`
   - Start Command: `java -jar target/*.jar`
   - Environment Variables: Set all required vars

3. **Deploy ML Engine**
   - New → Web Service
   - Root Directory: `ml-engine`
   - Build Command: `pip install -r requirements.txt && python -m spacy download en_core_web_sm`
   - Start Command: `gunicorn --bind 0.0.0.0:$PORT app:app`
   - Environment Variables: Set PORT

4. **Deploy Frontend**
   - New → Static Site
   - Root Directory: `frontend`
   - Build Command: `npm install && npm run build`
   - Publish Directory: `dist`
   - Environment Variables: `VITE_API_URL=https://your-backend-url.onrender.com/api`

## Post-Deployment Checklist

- [ ] All services are running
- [ ] Environment variables are set correctly
- [ ] Database is accessible
- [ ] API endpoints are responding
- [ ] Frontend can connect to backend
- [ ] ML engine is processing requests
- [ ] SSL certificate is configured (if using domain)
- [ ] Monitoring and logging are set up
- [ ] Backup strategy is in place

## Monitoring

### Health Checks

- Backend: `http://your-domain/api/health` (if implemented)
- ML Engine: `http://your-ml-service/health`
- Frontend: Check if serving static files

### Logs

```bash
# Docker
docker-compose logs -f

# System logs
journalctl -u docker -f
```

## Backup Strategy

1. **Database Backup**
   ```bash
   # MongoDB backup
   docker exec synkronos-mongodb mongodump --out /backup
   ```

2. **Automated Backups**
   - Setup cron job for daily backups
   - Store backups in S3/Blob Storage

## Scaling

### Horizontal Scaling

- Use load balancer for multiple backend instances
- Scale ML engine separately
- Use CDN for frontend

### Vertical Scaling

- Increase instance size
- Optimize database queries
- Add caching layer (Redis)

## Security Best Practices

1. Use strong JWT secrets
2. Enable HTTPS only
3. Regular security updates
4. Database access restrictions
5. API rate limiting
6. Input validation
7. Regular backups

---

For more help, refer to the main README.md or open an issue.

