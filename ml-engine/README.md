# ML Engine - Synkronos AI (Flask)

This folder contains the ML microservice used for skill extraction and match scoring.

## Requirements
- Python 3.11+
- pip

## Quick start (local)
1. Create and activate a virtual environment (Windows PowerShell):

```powershell
cd ml-engine
python -m venv venv
venv\Scripts\activate
```

2. Install dependencies:

```powershell
pip install -r requirements.txt
```

3. Download spaCy model (if required):

```powershell
python -m spacy download en_core_web_sm
```

4. Run the app:

```powershell
python app.py
```

The ML engine will listen on `http://localhost:5000` by default.

## Docker
A `Dockerfile` is provided to containerize the ML engine. Use the repo `docker-compose.yml` to run the service with other components.

## Endpoints
- `POST /predict-score` - calculate candidate-job match score
- `POST /extract-skills` - extract skills from resume text
- `GET /health` - health check

## Troubleshooting
- If spaCy model is missing, run the model download step above.
- If the service cannot be reached from the backend, confirm `ML_SERVICE_URL` in backend `application.yml` or environment variables.

---
Created/updated by automation script.