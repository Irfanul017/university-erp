# Student and Enrollment Guide

This document describes the student and enrollment functionality implemented
in this Spring Boot application, including setup, the entity relationship,
REST endpoints, examples, logging, and verification.

## Scope and code layout

| Area | Location |
| --- | --- |
| Student REST endpoints | `src/main/java/com/university/erp/student/controller/StudentController.java` |
| Enrollment REST endpoints | `src/main/java/com/university/erp/student/controller/EnrollmentController.java` |
| Student entity and API model | `src/main/java/com/university/erp/student/entity/Student.java` |
| Enrollment entity and API model | `src/main/java/com/university/erp/student/entity/Enrollment.java` |
| Persistence access | `src/main/java/com/university/erp/student/repository/` |
| Business operations and logging | `src/main/java/com/university/erp/student/service/` |
| Focused service tests | `src/test/java/com/university/erp/student/service/` |

## Database and environment setup

The application uses PostgreSQL. The current datasource configuration is in
`src/main/resources/application.properties`. It provides a default Supabase
PostgreSQL URL and username; the password is supplied through the environment.
Do not put a real password in source code, this document, or a committed
configuration file.

In PowerShell, set the password in the same terminal used to run the
application:

```powershell
$env:SPRING_DATASOURCE_PASSWORD = "your-database-password"
```

Optional datasource overrides:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://your-host:5432/your-database"
$env:SPRING_DATASOURCE_USERNAME = "your-database-user"
$env:SPRING_DATASOURCE_MAX_POOL_SIZE = "3"
```

Start the application from the repository root:

```powershell
.\mvnw.cmd spring-boot:run
```

The default application port is `8080`. For an external Tomcat WAR deployment,
set these environment variables in the Tomcat process environment before
starting Tomcat. If using the generated WAR filename, the context path is
`/erp-0.0.1-SNAPSHOT`, so prepend that path to the endpoints below.

If Hibernate reports that it cannot determine a dialect, first inspect the
preceding database connection error. Authentication, network, DNS, or
datasource configuration failures can prevent Hibernate from reading JDBC
metadata; setting a dialect alone does not fix a failed connection.

## Student-to-enrollment relationship

- `Student` has a `@OneToMany` collection of enrollments, mapped by the
  enrollment's `student` field.
- `Enrollment` owns the `@ManyToOne` relationship. Its non-null `student_id`
  join column references the student's `student_id`.
- `Enrollment` also references `Course` through a `course_id` `@ManyToOne`
  association. The JPA mapping allows a null course, but the enrollment
  service requires a valid `courseId` for create and update requests.
- Each enrollment belongs to one student; one student can have multiple
  enrollments.
- Student deletion through the API is a soft delete: it marks the student's
  `currentStatus` as `Inactive`; it does not physically delete the row.
- `Enrollment.getStudentName()` derives the student's full name from the
  related entity. It is `@Transient`, so it is not a database column.
- Controllers accept and return `Student` and `Enrollment` JPA entities
  directly. Student's enrollment collection is excluded from JSON to avoid
  recursive entity serialization.
- Enrollment responses contain nested Student and Course entities.
  `Enrollment.getStudentName()` remains available as a derived JSON property.
- Enrollment repositories fetch the Student and Course associations for
  entity responses, while read operations run in read-only transactions.

To verify the foreign key in PostgreSQL:

```sql
SELECT
    conname AS constraint_name,
    pg_get_constraintdef(oid) AS definition
FROM pg_constraint
WHERE conrelid = 'public.enrollment'::regclass
  AND contype = 'f';
```

To list enrollment data together with names from the related student row:

```sql
SELECT
    e.enrollment_id,
    e.student_id,
    s.first_name,
    s.middle_name,
    s.last_name
FROM public.enrollment e
JOIN public.student s ON s.student_id = e.student_id;
```

The application uses `spring.jpa.hibernate.ddl-auto=update`; schema changes
are applied by Hibernate at startup after it connects to the configured
database.

## Student API

Base path: `/api/students`

| Operation | Method | Path | Success |
| --- | --- | --- | --- |
| Create a student | `POST` | `/api/students` | `201 Created` with student body and `studentId` |
| List students | `GET` | `/api/students` | `200 OK` with an array |
| Get one student | `GET` | `/api/students/{studentId}` | `200 OK` with student body |
| Update a student | `PUT` | `/api/students/{studentId}` | `200 OK` with updated body when found |
| List a student's enrollments | `GET` | `/api/students/{studentId}/enrollments` | `200 OK` with an array |
| Soft-delete a student | `DELETE` | `/api/students/{studentId}` | `204 No Content`; marks status `Inactive` |
| Permanently delete a student | `DELETE` | `/api/students/{studentId}/permanent` | `204 No Content`; physically removes the row |

Permanent deletion cannot be undone and may return `409 Conflict` if
enrollments or other database rows still reference the student. Related rows
are not automatically deleted.

### Student entity request body

Use `Content-Type: application/json`. Dates use ISO format, `YYYY-MM-DD`.

```json
{
  "enrollmentNo": "STU-2026-003",
  "firstName": "Mira",
  "middleName": "Anil",
  "lastName": "Desai",
  "gender": "Female",
  "dob": "2005-02-20",
  "email": "mira.desai@example.com",
  "phone": "9876543212",
  "address": "18 College Road",
  "admissionDate": "2026-08-01",
  "currentStatus": "Active"
}
```

The create response contains a generated UUID `studentId`. Reuse that exact
UUID in subsequent student and enrollment requests. The request is validated:
enrollment number, first name, last name, email, and admission date are
required, and email must have a valid format. The database also enforces
unique enrollment numbers and emails.

## Enrollment API

Base path: `/api/enrollments`

| Operation | Method | Path | Success |
| --- | --- | --- | --- |
| Create an enrollment | `POST` | `/api/enrollments` | `201 Created` with enrollment body and `enrollmentId` |
| List enrollments | `GET` | `/api/enrollments` | `200 OK` with an array |
| Get one enrollment | `GET` | `/api/enrollments/{enrollmentId}` | `200 OK` with enrollment body |
| Update an enrollment | `PUT` | `/api/enrollments/{enrollmentId}` | `200 OK` with updated body when found |
| Delete an enrollment | `DELETE` | `/api/enrollments/{enrollmentId}` | `204 No Content` |

### Enrollment entity request body

Create the student and a course first. Since the API accepts an Enrollment
entity, provide the related entity objects with their existing IDs:

```json
{
  "student": {
    "studentId": "paste-existing-student-uuid-here"
  },
  "course": {
    "id": 1
  },
  "semester": 1,
  "academicYear": "2026-2027",
  "rollNo": 3,
  "division": "A",
  "admissionType": "Regular",
  "enrollmentStatus": "Active",
  "startDate": "2026-08-01",
  "endDate": null,
  "exitReason": null,
  "finalCgpa": null
}
```

The response contains the Enrollment entity with generated `enrollmentId`
and nested Student and Course entities. `student`, `course`, `academicYear`,
`rollNo`, `division`, and `startDate` are required for an API create/update.
The service resolves the nested IDs to existing database records. For a new
enrollment, if `enrollmentStatus` is omitted or null, the entity's default is
`Active`. On update, a null `enrollmentStatus` leaves the existing status
unchanged. Other fields can be null where the database schema permits it.

Because entities are the API models, the JSON structure follows the entity
properties and is coupled to the persistence model. The Student-to-
Enrollments collection is excluded from JSON to prevent a circular response;
the separate `/api/students/{studentId}/enrollments` endpoint provides those
records.

## Missing IDs and errors

- A syntactically valid UUID that does not identify a student returns
  `404 Not Found` for student GET, PUT, and DELETE operations.
- A missing enrollment ID returns `404 Not Found` for enrollment GET and
  PUT and DELETE operations.
- A missing student referenced by an enrollment create/update request returns
  `404 Not Found`.
- Student create/update requests require the documented student fields;
  enrollment create/update requests require Student and Course references,
  academic year, roll number, division, and start date. Invalid fields return
  `400 Bad Request` with field-level details.
- A malformed UUID in a path is rejected by Spring as `400 Bad Request`
  before the service looks up a record.
- A duplicate unique value or other database integrity violation returns
  `409 Conflict` with a generic message that does not expose database details.
- Unexpected server errors return a generic `500 Internal Server Error`; the
  underlying exception is logged by the application.

## Swagger UI and manual testing

Open Swagger UI while the application is running:

- Direct run: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI document: `http://localhost:8080/v3/api-docs/all-apis`

The `all-apis` OpenAPI group includes endpoints under `/api/**`. In
Swagger, expand **Students** or **Enrollments**, click **Try it out**, provide
the path parameter or JSON body, and click **Execute**.

Recommended test sequence:

1. `POST /api/students`; copy its `studentId`.
2. `GET /api/students/{studentId}` to read it.
3. `PUT /api/students/{studentId}` with the full request body to update it.
4. `GET /api/students/{studentId}/enrollments` to verify the student lookup.
5. Create or identify a course, then `POST /api/enrollments` using the
   existing `studentId` and course `courseId`; copy `enrollmentId`.
6. `GET /api/enrollments` or `GET /api/enrollments/{enrollmentId}` and verify
   `studentName` is present in the response.
7. `PUT /api/enrollments/{enrollmentId}` using the full enrollment body and
   valid student and course IDs.
8. Delete the enrollment with `DELETE /api/enrollments/{enrollmentId}`.
   `DELETE /api/students/{studentId}` marks the student inactive rather than
   physically removing the student row.

## Logging and tests

Student and enrollment services log CRUD activity through SLF4J:

- `INFO`: successful create, read, update, delete, and list operations.
- `WARN`: missing student/enrollment IDs and rejected client requests.
- `ERROR`: unexpected server exceptions, with stack traces logged internally
  while the client receives a generic message.

Logs contain IDs and list counts, not student names, email addresses, or
request bodies. View them in the terminal running the application.

Run the focused service tests from the repository root:

```powershell
.\mvnw.cmd "-Dtest=StudentServiceImplTest,EnrollmentServiceImplTest" test
```

The general Spring application-context test connects to the configured
database, so it requires valid datasource credentials and database access.
