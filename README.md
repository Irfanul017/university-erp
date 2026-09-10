# University ERP API

A Spring Boot REST API for managing a university's core academic and administrative data: departments, faculty, courses, students, attendance, classrooms, finance, examinations, and library transactions.

## Technology

- Java 21
- Spring Boot 4 / Spring Web MVC
- Spring Data JPA and Hibernate
- PostgreSQL
- Maven
- Lombok

## Project structure

Each domain follows the same layers:

```text
controller/  HTTP endpoints
service/     business and persistence operations
repository/  Spring Data JPA access
entity/      database mappings
dto/         request and response contracts
mapper/      DTO/entity conversion helpers
```

`attendance` has additional domain behavior for duplicate prevention, batch marking, filtering, and student summaries. Shared CRUD support is located in `com.university.erp.common`.

## Prerequisites

- JDK 21 or later
- PostgreSQL 14 or later
- Maven 3.9+ (or use the included Maven wrapper)

## Database setup

Create a local database:

```sql
CREATE DATABASE erp;
```

Set the database password before starting the application.

PowerShell:

```powershell
$env:POSTGRES_PASSWORD = "your-postgres-password"
$env:POSTGRES_USERNAME = "postgres" # optional; this is the default
```

The standard datasource configuration is:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/erp
spring.datasource.username=${POSTGRES_USERNAME:postgres}
spring.datasource.password=${POSTGRES_PASSWORD}
```

Hibernate is configured with `spring.jpa.hibernate.ddl-auto=update`. It maintains tables mapped by the application and creates the library tables, which were present in the original source scaffold but not in the supplied schema. For production deployments, use versioned database migrations (for example, Flyway) and change this setting to `validate`.

## Run the application

```powershell
# Format source code
mvn formatter:format

# Compile
mvn -DskipTests compile

# Start the API
mvn spring-boot:run
```

The API listens on `http://localhost:8080` by default.

## Standard CRUD endpoints

Every resource below supports the following operations:

| Operation | Method | Path |
| --- | --- | --- |
| Create | `POST` | resource path |
| List | `GET` | resource path |
| Read | `GET` | resource path + `/{id}` |
| Update | `PUT` | resource path + `/{id}` |
| Delete | `DELETE` | resource path + `/{id}` |

| Domain | Resource path |
| --- | --- |
| Departments | `/api/departments` |
| Faculty | `/api/faculties` |
| Courses | `/api/courses` |
| Subjects | `/api/subjects` |
| Students | `/api/students` |
| Enrollments | `/api/enrollments` |
| Classrooms | `/api/classrooms` |
| Timetables | `/api/timetables` |
| Lectures | `/api/lectures` |
| Fee definitions | `/api/fees` |
| Payments | `/api/payments` |
| Library books | `/api/library/books` |
| Library transactions | `/api/library/transactions` |
| Examinations | `/api/examinations` |
| Grades | `/api/grades` |

For example, create a department:

```http
POST /api/departments
Content-Type: application/json

{
  "departmentName": "Computer Engineering",
  "location": "Block A"
}
```

## Attendance API

Base path: `/api/attendance`

| Purpose | Method | Endpoint |
| --- | --- | --- |
| Mark one record | `POST` | `/api/attendance` |
| List/filter records | `GET` | `/api/attendance` |
| Read one record | `GET` | `/api/attendance/{id}` |
| Update a record | `PUT` | `/api/attendance/{id}` |
| Delete a record | `DELETE` | `/api/attendance/{id}` |
| Mark a class in one request | `POST` | `/api/attendance/bulk` |
| Student attendance summary | `GET` | `/api/attendance/summary/{studentId}` |

### Mark attendance

```http
POST /api/attendance
Content-Type: application/json

{
  "lectureId": 1,
  "studentId": "8e8ef61b-5621-4c6f-9daa-89c029092e4c",
  "status": "Present",
  "remarks": "Arrived on time",
  "markedBy": "faculty@university.edu"
}
```

Valid API status values are `Present`, `Absent`, and `C_WORK`. `C_WORK` is persisted as `C-Work` to match the provided database constraint.

Attendance rules:

- `lectureId`, `studentId`, and `status` are required.
- Only one attendance record can exist for a student in a lecture.
- A duplicate single-record request returns `409 Conflict`.
- Batch requests reject duplicate student/lecture combinations in the submitted list.
- Re-marking an existing item through the batch endpoint updates the existing attendance record.
- Summary percentage counts `Present` and `C_WORK` as attended sessions.

### Filter attendance

```http
GET /api/attendance?lectureId=1&status=Present
GET /api/attendance?studentId=8e8ef61b-5621-4c6f-9daa-89c029092e4c
GET /api/attendance?lectureId=1&studentId=8e8ef61b-5621-4c6f-9daa-89c029092e4c
```

### Mark a lecture in bulk

```http
POST /api/attendance/bulk
Content-Type: application/json

[
  {
    "lectureId": 1,
    "studentId": "8e8ef61b-5621-4c6f-9daa-89c029092e4c",
    "status": "Present",
    "markedBy": "faculty@university.edu"
  },
  {
    "lectureId": 1,
    "studentId": "b84132ee-2d75-41de-b53c-ee4e94748346",
    "status": "Absent",
    "markedBy": "faculty@university.edu"
  }
]
```

### Get a summary

```http
GET /api/attendance/summary/8e8ef61b-5621-4c6f-9daa-89c029092e4c
GET /api/attendance/summary/8e8ef61b-5621-4c6f-9daa-89c029092e4c?lectureId=1
```

Example response:

```json
{
  "studentId": "8e8ef61b-5621-4c6f-9daa-89c029092e4c",
  "total": 12,
  "present": 9,
  "absent": 2,
  "cWork": 1,
  "percentage": 83.33
}
```

## HTTP error behavior

| Status | Meaning |
| --- | --- |
| `201 Created` | A record was created. |
| `204 No Content` | A record was deleted. |
| `400 Bad Request` | Invalid or missing required attendance input. |
| `404 Not Found` | The requested ID does not exist. |
| `409 Conflict` | A duplicate attendance record or a referenced record deletion was attempted. |

Error messages are enabled through `server.error.include-message=always` for local development.

## Development commands

```powershell
# Compile without running tests
mvn -DskipTests compile

# Run tests (requires a reachable PostgreSQL datasource unless test settings are supplied)
mvn test

# Apply Java formatting
mvn formatter:format

# Verify formatting without changing files
mvn formatter:validate
```

## Current scope

This project provides REST and persistence foundations for the university ERP. Authentication/authorization, API versioning, pagination, production database migrations, and OpenAPI documentation are logical next enhancements before a production deployment.
