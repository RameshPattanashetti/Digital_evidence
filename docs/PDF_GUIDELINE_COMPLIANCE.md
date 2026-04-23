# PDF Guideline Compliance (Extracted and Applied)

This document maps the uploaded PDF requirements to implementation status.

## 1. Java Technology + MVC Framework
- Requirement: Use Java technologies with MVC framework (example: Spring).
- Applied: Added Spring MVC app in [spring-app/pom.xml](../spring-app/pom.xml) using Spring Boot Web + Thymeleaf + Spring Data MongoDB.

## 2. Desktop/Web Only
- Requirement: Only desktop or web app accepted.
- Applied: Web app implemented with Spring MVC pages.

## 3. Data Persistence in Database
- Requirement: Persist data in any database.
- Applied: MongoDB configured in [spring-app/src/main/resources/application.properties](../spring-app/src/main/resources/application.properties).

## 4. Analysis and Design Models
- Requirement: Use Case, Class, Activity, and State diagrams.
- Applied: Existing class and use case are in [README.md](../README.md). Added activity and state diagrams in report update task (next section below).

## 5. Features Complexity
- Requirement: At least 4 major and 4 minor use cases for a team of 4.
- Applied use cases:
1. Major: Create case
2. Major: Add evidence
3. Major: Transfer custody
4. Major: Generate case report
5. Minor: List cases
6. Minor: Search evidence by ID
7. Minor: Archive evidence
8. Minor: View selected case report from dashboard

## 6. Team-based Ownership
- Requirement: Each team member should own complete use case(s).
- Applied template:
1. Member 1: Create case + list cases
2. Member 2: Add evidence + search evidence
3. Member 3: Transfer custody + archive evidence
4. Member 4: Report generation + dashboard flow integration

## 7. Submission Checklist From PDF
1. Title page with PESU template and team details
2. Problem statement
3. Models (Use Case, Class, Activity, State)
4. Architecture, design principles, design patterns
5. Public GitHub link
6. Individual contributions
7. White-background screenshots with input and output

## 8. Run Spring MVC App
```powershell
cd spring-app
mvn spring-boot:run
```
Open `http://localhost:8081`
