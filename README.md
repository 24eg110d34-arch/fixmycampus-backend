# FixMyCampus - Backend (REST API)

Java 17, Spring Boot 3.3, Spring MVC, Spring Security + JWT, Spring Data JPA, MySQL (or H2 for a quick demo).
This project contains **only the API**. The web pages are in the separate `fixmycampus-frontend` project.

## Run
1. Install JDK 17+ and Maven (and MySQL, unless you use the H2 demo).
2. With MySQL: set your username/password in `src/main/resources/application.properties`
   (or env vars `DB_USERNAME`, `DB_PASSWORD`). The database is created automatically.
   ```
   mvn spring-boot:run
   ```
   Without MySQL (in-memory demo):
   ```
   mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
   ```
3. The API is now at http://localhost:8080. Quick check: opening http://localhost:8080/api/admin/stats in a browser should show an error (401), which means the API is up and protected.
4. Start the frontend project and open http://localhost:5500.

## Accounts
- Admin (created on first start): `admin@fixmycampus.com` / `Admin@123` (change in `application.properties`).
- Students and staff register through the frontend (`POST /api/auth/register`).
- Six departments are created automatically.

## CORS
The browser only lets the frontend call this API if its address is listed in
`app.cors.allowed-origins` (default: ports 5500, 3000 and 5173 on localhost).
If you serve the frontend from another port, add it there and restart.

## REST API
All endpoints except register/login need `Authorization: Bearer <token>`.

| Method | URL | Who | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | public | JSON: name, email, password, role (STUDENT or STAFF) |
| POST | `/api/auth/login` | public | Returns `{ token, name, email, role }` |
| POST | `/api/issues` | logged in | Multipart form: title, category, location, description, priority, image |
| GET | `/api/issues/my` | logged in | My issues |
| GET | `/api/issues/{id}` | owner or admin | Issue + history |
| GET | `/api/admin/issues?status=&category=&priority=&q=` | admin | All issues, filterable |
| PUT | `/api/admin/issues/{id}/assign` | admin | `{ departmentId, remarks }` |
| PUT | `/api/admin/issues/{id}/status` | admin | `{ status, remarks }` |
| GET | `/api/admin/stats` | admin | Totals by status and category |
| GET | `/api/admin/departments` | admin | Departments |
| GET | `/uploads/{file}` | public | Uploaded issue photos |

Test with Postman: log in as admin, copy the token, then use *Authorization -> Bearer Token*.

## Structure
```
src/main/java/com/fixmycampus
  config/      SecurityConfig, CorsConfig, WebConfig (serves /uploads), DataSeeder
  security/    JwtService, JwtAuthFilter
  model/       User, Issue, Department, IssueHistory + enums
  repository/  JPA repositories, IssueSpecifications (admin filters)
  dto/         request/response records
  service/     AuthService, IssueService (business rules), FileStorageService
  controller/  AuthController, IssueController, AdminController
  exception/   custom exceptions + GlobalExceptionHandler
src/main/resources  application.properties, application-h2.properties
database/schema.sql reference schema
```

## Business rules
Status only moves forward (Reported, Assigned, In progress, Resolved); a department must be assigned before In progress or Resolved;
resolving needs a note and closes the issue; users see only their own issues; passwords are BCrypt hashes; JWT expires after 8 hours;
photos must be JPG/PNG/GIF/WEBP up to 5 MB.

Before real use: set a long random `JWT_SECRET`, change the admin password, and use HTTPS.
