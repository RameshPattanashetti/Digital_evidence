# Spring MVC Module (PDF Guideline Aligned)

This module is the guideline-compliant implementation using:
- Spring Boot MVC (Web + Thymeleaf)
- Spring Data MongoDB
- MongoDB persistence (default)
- Plain HTML/CSS/JavaScript frontend under static assets

## Implemented Use Cases
1. Create case
2. Add evidence
3. Transfer custody
4. Generate case report
5. List cases
6. Search evidence by ID
7. Archive evidence
8. Select case-wise report view

## Run
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mongo
```

## Run With Environment Variable (Optional)
```powershell
$env:MONGO_URI="mongodb://localhost:27017/evidence_db"
mvn spring-boot:run -Dspring-boot.run.profiles=mongo
```

## Run Frontend Only (No Spring) with Live Server
You can run the UI without Spring Boot.

1. Open this file in VS Code:
	- `spring-app/src/main/resources/static/app/index.html`
2. Right-click and choose **Open with Live Server**.
3. Use the app directly in the browser.

Notes:
- This mode uses browser `localStorage` (no database, no backend API).
- Data stays in your browser until you clear site storage.
- Spring Boot remains available as an optional backend mode when needed.

Open:
- http://localhost:8081
- http://localhost:8081/app/index.html

## MongoDB API Endpoints
- GET `/api/mongo/v1/cases`
- POST `/api/mongo/v1/cases`
- GET `/api/mongo/v1/evidence`
- POST `/api/mongo/v1/evidence`
- GET `/api/mongo/v1/custody`
- POST `/api/mongo/v1/custody`
- POST `/api/mongo/v1/users/register`
- POST `/api/mongo/v1/users/login`

## API Endpoints Used By HTML/CSS Frontend
- GET `/api/cases`
- POST `/api/cases`
- POST `/api/evidence`
- POST `/api/custody`
- GET `/api/evidence/{evidenceId}`
- POST `/api/evidence/{evidenceId}/archive`
- GET `/api/reports/{caseId}`
