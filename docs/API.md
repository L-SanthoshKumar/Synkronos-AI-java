# API Documentation

Complete API reference for Synkronos AI Backend.

Base URL: `http://localhost:8080/api`

## Authentication

All endpoints except `/auth/*` require JWT token in Authorization header:
```
Authorization: Bearer <token>
```

## Endpoints

### Authentication

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "role": "JOB_SEEKER",
  "companyName": "" // Optional, for recruiters
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": "user-id",
    "email": "user@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "role": "JOB_SEEKER"
  }
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** Same as register

#### Refresh Token
```http
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Jobs

#### Get All Active Jobs
```http
GET /api/jobs
```

**Response:**
```json
[
  {
    "id": "job-id",
    "title": "Senior Backend Developer",
    "description": "Job description...",
    "companyName": "TechCorp",
    "location": "San Francisco, CA",
    "employmentType": "FULL_TIME",
    "minSalary": 120000,
    "maxSalary": 180000,
    "currency": "USD",
    "requiredSkills": ["Java", "Spring Boot"],
    "minYearsOfExperience": 5,
    "status": "ACTIVE"
  }
]
```

#### Get Job by ID
```http
GET /api/jobs/{id}
```

#### Search Jobs
```http
GET /api/jobs/search?q=developer
```

#### Create Job (Recruiter Only)
```http
POST /api/jobs
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Senior Backend Developer",
  "description": "Job description...",
  "companyName": "TechCorp",
  "location": "San Francisco, CA",
  "employmentType": "FULL_TIME",
  "minSalary": 120000,
  "maxSalary": 180000,
  "currency": "USD",
  "requiredSkills": ["Java", "Spring Boot", "MongoDB"],
  "minYearsOfExperience": 5,
  "educationLevel": "BACHELORS"
}
```

#### Update Job (Recruiter Only)
```http
PUT /api/jobs/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "title": "Updated Title",
  "status": "CLOSED"
}
```

#### Delete Job (Recruiter Only)
```http
DELETE /api/jobs/{id}
Authorization: Bearer <token>
```

#### Get My Jobs (Recruiter Only)
```http
GET /api/jobs/recruiter/my-jobs
Authorization: Bearer <token>
```

### Applications

#### Apply to Job
```http
POST /api/applications
Authorization: Bearer <token>
Content-Type: application/json

{
  "jobId": "job-id",
  "coverLetter": "I am interested in this position..."
}
```

**Response:**
```json
{
  "id": "application-id",
  "jobId": "job-id",
  "jobSeekerId": "user-id",
  "status": "PENDING",
  "matchScore": 85.5,
  "matchBreakdown": "{\"skillMatch\":90.0,\"experienceMatch\":80.0}",
  "coverLetter": "I am interested...",
  "appliedAt": "2024-01-15T10:30:00"
}
```

#### Get My Applications
```http
GET /api/applications/my-applications
Authorization: Bearer <token>
```

#### Get Applications for Job (Recruiter Only)
```http
GET /api/applications/job/{jobId}
Authorization: Bearer <token>
```

#### Update Application Status (Recruiter Only)
```http
PUT /api/applications/{id}/status
Authorization: Bearer <token>
Content-Type: application/json

{
  "status": "SHORTLISTED"
}
```

**Status values:** PENDING, REVIEWING, SHORTLISTED, REJECTED, INTERVIEW_SCHEDULED, ACCEPTED

### Users

#### Get Current User
```http
GET /api/users/me
Authorization: Bearer <token>
```

#### Get User by ID
```http
GET /api/users/{id}
Authorization: Bearer <token>
```

#### Update User Profile
```http
PUT /api/users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phone": "+1234567890",
  "location": "San Francisco, CA",
  "bio": "Experienced developer...",
  "skills": ["Java", "React"],
  "currentPosition": "Senior Developer",
  "yearsOfExperience": 5
}
```

#### Get All Job Seekers
```http
GET /api/users/job-seekers
Authorization: Bearer <token>
```

### File Upload

#### Upload Resume
```http
POST /api/upload/resume
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: <PDF file>
```

**Response:**
```json
{
  "resumeUrl": "https://res.cloudinary.com/.../resume.pdf",
  "message": "Resume uploaded successfully"
}
```

## ML Engine Endpoints

Base URL: `http://localhost:5000`

#### Predict Match Score
```http
POST /predict-score
Content-Type: application/json

{
  "resumeText": "Resume text content...",
  "jobRequiredSkills": ["Java", "Spring Boot", "MongoDB"],
  "jobMinYearsOfExperience": 5,
  "jobDescription": "Job description text..."
}
```

**Response:**
```json
{
  "overallScore": 85.5,
  "skillMatchScores": {
    "Java": 100.0,
    "Spring Boot": 100.0,
    "MongoDB": 0.0
  },
  "breakdown": {
    "skillMatch": 90.0,
    "experienceMatch": 80.0,
    "textSimilarity": 85.0
  },
  "extractedSkills": ["Java", "Spring Boot", "React"]
}
```

#### Extract Skills
```http
POST /extract-skills
Content-Type: application/json

{
  "resumeText": "Resume text content..."
}
```

**Response:**
```json
{
  "skills": ["Java", "Spring Boot", "React", "MongoDB"]
}
```

#### Health Check
```http
GET /health
```

## Error Responses

All errors follow this format:

```json
{
  "error": "Error message",
  "message": "Detailed error description"
}
```

**Status Codes:**
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 403: Forbidden
- 404: Not Found
- 500: Internal Server Error

## Rate Limiting

Currently no rate limiting is implemented. For production, consider adding rate limiting middleware.

## Pagination

Currently, all list endpoints return all results. For production, implement pagination:
- `?page=1&size=20`
- Response includes pagination metadata

---

For interactive API documentation, visit Swagger UI at:
`http://localhost:8080/swagger-ui.html`

