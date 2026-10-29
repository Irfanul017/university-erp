# University ERP — Examination and Grading Module

A Spring Boot REST API module for managing examinations and recording student grades in the University ERP. It uses Supabase PostgreSQL for persistent storage and exposes JSON endpoints that a web or mobile frontend can call.

## What the module does

- Creates and lists examinations for existing subjects.
- Records a student's grade for an examination.
- Lists grades for an examination, retrieves an individual grade, and updates a grade.
- Uses the existing ERP subject and student records through their IDs.
- Persists data in PostgreSQL and relies on database relationships and constraints for data integrity.

## How a request flows

`Frontend → REST controller → service → repository → Supabase PostgreSQL`

The frontend sends JSON to an API endpoint. A Spring controller receives the request, the service performs the operation, and a Spring Data repository reads or saves the entity in PostgreSQL. The API returns a JSON response for the frontend to display.

This branch provides the backend API. A separate frontend can integrate with these endpoints.

## Data relationships

- **Subject to examinations:** a subject can have multiple examinations; each examination refers to one subject through `subjectId`.
- **Examination to grades:** an examination can have grade records for multiple students; each grade refers to one examination through `examId`.
- **Student to grades:** a student can have grades across multiple examinations; each grade refers to one student through `studentId` (UUID).

A grade record therefore connects one student with one examination. The subject and student must already exist in the ERP database before their IDs are used.

The Supabase schema sets maximum marks by exam type: 20 for `ISE_1`, `ISE_2`, `ISE_3`, `IE`, and `PRACTICAL`; 60 for `ESE`.

## API reference

The examples below use port `8080`. Replace it with the port configured for your application.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `GET` | `/api/examinations` | List examinations |
| `POST` | `/api/examinations` | Create an examination |
| `GET` | `/api/grades?examId={examId}` | List grades for an examination |
| `GET` | `/api/grades/{gradingId}` | Get one grade |
| `POST` | `/api/grades` | Record a grade |
| `PUT` | `/api/grades/{gradingId}` | Update a grade |

### Create an examination

```bash
curl -i -X POST http://localhost:8080/api/examinations \
  -H "Content-Type: application/json" \
  -d '{"subjectId":1,"academicYear":"2025-2026","semester":1,"examType":"ESE","maxMarks":60,"examDate":"2026-11-01","status":"SCHEDULED","remarks":"Semester examination"}'
```

A successful create returns `201 Created` with the saved examination, including its generated `examId`.

### Record a grade

Replace the sample IDs with an existing examination ID and student UUID from your database. The UUID below is a placeholder.

```bash
curl -i -X POST http://localhost:8080/api/grades \
  -H "Content-Type: application/json" \
  -d '{"examId":4,"studentId":"<student-uuid>","marksObtained":55,"grade":"A","gradePoint":4.0,"resultStatus":"PASS","remarks":"Semester result"}'
```

### Read examinations and grades

```bash
curl -i http://localhost:8080/api/examinations
curl -i "http://localhost:8080/api/grades?examId=4"
curl -i http://localhost:8080/api/grades/1
```

### Update a grade

```bash
curl -i -X PUT http://localhost:8080/api/grades/1 \
  -H "Content-Type: application/json" \
  -d '{"examId":4,"studentId":"<student-uuid>","marksObtained":54,"grade":"A","gradePoint":4.0,"resultStatus":"PASS","remarks":"Corrected result"}'
```

## Run locally

### Prerequisites

- A JDK compatible with the version configured in `pom.xml`.
- The Maven wrapper included in the repository.
- Access to the project's Supabase PostgreSQL database.

### Configure the database

The datasource URL and username are configured in `src/main/resources/application.properties`. Supply the database password through the `SUPABASE_DB_PASSWORD` environment variable. Do not put a real password in this README or commit it to Git.

**GitHub Codespaces / Bash:**

```bash
export SUPABASE_DB_PASSWORD='your-database-password'
bash mvnw spring-boot:run
```

**Windows PowerShell:**

```powershell
$env:SUPABASE_DB_PASSWORD = 'your-database-password'
.\mvnw.cmd spring-boot:run
```

When the application starts, call the endpoints at the configured server port. If it uses a port other than `8080`, replace `8080` in the examples with that port.

## Tests and build

Run the project's Maven tests with the wrapper:

```bash
bash mvnw test
```

On Windows PowerShell, use:

```powershell
.\mvnw.cmd test
```

Database-backed checks require a reachable database and valid connection settings. API requests can also be exercised with the cURL examples above.

## Main source areas

- `src/main/java/com/university/erp/examination/controller` — REST endpoints.
- `src/main/java/com/university/erp/examination/service` — module operations and business logic.
- `src/main/java/com/university/erp/examination/repository` — database access.
- `src/main/java/com/university/erp/examination/entity` — examination and grade persistence models.
- `src/main/java/com/university/erp/examination/dto` — API request and response models.
- `src/test/java/com/university/erp/ExaminationGradingApiTests.java` — module API tests.

## Security note

Keep database credentials out of source control. Use local environment variables or your deployment platform's secret configuration for credentials.
