# Demo Quickstart (Single App Path)

Use only this app for evaluation/demo:
- URL: http://localhost:8081/app/index.html
- Backend: Spring MVC + MongoDB database

## Run From Project Root
```powershell
mvn -f spring-app/pom.xml spring-boot:run -Dspring-boot.run.profiles=mongo
```

## Optional Mongo URI Override
```powershell
$env:MONGO_URI="mongodb://localhost:27017/evidence_db"
mvn -f spring-app/pom.xml spring-boot:run -Dspring-boot.run.profiles=mongo
```

## If Maven Is Not Found
Use the full Maven path once:
```powershell
& "$env:USERPROFILE\tools\apache-maven-3.9.9\bin\mvn.cmd" -f spring-app/pom.xml spring-boot:run
```

## Do Not Use For Final Demo
- http://localhost:8080 (legacy fallback app)

## Demo Data Entry Example
1. Create Case
- Case ID: CASE-010
- Case Title: Insider Data Leak Investigation
- Investigator: Inspector Ananya

2. Add Evidence
- Case ID: CASE-010
- Evidence ID: EV-010
- Description: Employee laptop disk image
- Type: IMAGE
- Collected By: Inspector Ananya

3. Transfer Custody
- Evidence ID: EV-010
- New Custodian: Forensic Lab
- Reason: Malware and metadata analysis
