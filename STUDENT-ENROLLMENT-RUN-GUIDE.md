# Student and Enrollment: Complete Code and Run Guide

This guide explains how the Student and Enrollment features are structured,
why each layer is used, how their data relates, and how to run and test the
API. It includes Swagger examples for requests and typical responses.

## 1. Technology used

| Technology | How it is used here |
| --- | --- |
| Java 21 | Implements the ERP application |
| Spring Boot 4 | Starts and configures the application and its components |
| Spring Web MVC | Maps HTTP requests to Student and Enrollment controller methods |
| Spring Data JPA | Provides repository interfaces for database operations |
| Hibernate | Implements JPA entity persistence and SQL generation |
| PostgreSQL | Stores Student, Enrollment, and Course records |
| Jakarta Bean Validation | Validates required fields on entity request bodies |
| springdoc OpenAPI / Swagger UI | Documents and interactively calls REST endpoints |
| Maven Wrapper | Builds, tests, and packages the project without requiring a separately installed Maven |
| JUnit and Mockito | Test validation, service behavior, and error responses |

Dependency and build configuration is in `pom.xml`. The project is packaged
as a WAR for deployment; Spring Boot also supports local execution with the
embedded servlet container.

## 2. Student and Enrollment project structure

The relevant source files are:

| File/folder | Responsibility |
| --- | --- |
| `student/controller/StudentController.java` | Defines the Student HTTP routes |
| `student/controller/EnrollmentController.java` | Defines the Enrollment HTTP routes |
| `student/entity/Student.java` | Maps Student fields and relationship to the `student` table |
| `student/entity/Enrollment.java` | Maps Enrollment fields and relationships to the `enrollment` table |
| `student/service/StudentService.java` | Declares Student operations |
| `student/service/StudentServiceImpl.java` | Implements Student lookup, create, update, and soft-delete behavior |
| `student/service/EnrollmentService.java` | Declares Enrollment operations |
| `student/service/EnrollmentServiceImpl.java` | Implements Enrollment CRUD and resolves Student/Course references |
| `student/repository/StudentRepository.java` | Spring Data access to Student records |
| `student/repository/EnrollmentRepository.java` | Spring Data access to Enrollment records and related data |
| `common/GlobalExceptionHandler.java` | Converts expected errors into consistent HTTP error responses |

Student and Enrollment intentionally have **no separate DTO or mapper
classes**. Their controllers accept and return the JPA entities directly.
This keeps the API model straightforward, but it couples the public JSON
contract to the persistence model: changing entity properties may also
change the API.

## 3. Request processing: what each layer does and why

```text
Swagger / API client sends JSON
              |
              v
Controller: selects the route and validates the entity request (@Valid)
              |
              v
Service: applies Student/Enrollment rules and resolves referenced records
              |
              v
Repository: loads or saves JPA entities
              |
              v
Hibernate / PostgreSQL
              |
              v
Controller returns entity data as JSON
```

- **Controller:** Handles the HTTP boundary (route, method, body, and status
  code). It does not contain database/business operations.
- **Entity:** Represents both the persisted record and—by request here—the
  JSON model. JPA annotations map fields to database tables and columns.
- **Validation annotations:** `@NotBlank`, `@NotNull`, and `@Email` reject
  invalid request values before the service writes them.
- **Service:** Enforces behavior that spans multiple records. For example,
  Enrollment create/update loads the referenced Student and Course by ID and
  returns 404 if either record cannot be found.
- **Repository:** Uses Spring Data JPA to perform persistence operations
  without hand-writing simple CRUD queries.
- **Global exception handler:** Makes errors such as validation failures,
  missing records, and database conflicts readable to the API client.

The entity classes have generated identifiers, so create operations ignore
any supplied `studentId` or `enrollmentId` and let persistence generate the
new ID. Update routes use the ID in the URL to choose the record being
updated; an ID in the JSON body is not used to select another record.

## 3.1 Code walkthrough: controllers

The controllers are the HTTP/API boundary. They translate an HTTP request
into a service call and select the HTTP response shape. They do not contain
the database lookup or business rules.

### `StudentController`

`@RestController` tells Spring to expose the class as REST endpoints and
serialize returned objects as JSON. `@RequestMapping("/api/students")`
provides the common URL prefix. `@Tag` groups these operations under
**Students** in Swagger. The constructor receives `StudentService` and
`EnrollmentService`; Spring injects the implementations.

| Method | What the code does and why |
| --- | --- |
| `findAll()` | `@GetMapping` maps `GET /api/students` to `studentService.findAll()`. It returns a `List<Student>` directly. There are no query arguments, so Swagger shows no parameters. |
| `findById(UUID studentId)` | `@GetMapping("/{studentId}")` and `@PathVariable` bind the URL UUID and ask the service to look up that Student. |
| `findEnrollments(UUID studentId)` | `GET /{studentId}/enrollments` delegates to `EnrollmentService`; the controller does not query repositories itself. |
| `create(Student request)` | `@PostMapping`, `@RequestBody`, and `@Valid` mean JSON is deserialized into a Student entity, validated, and passed to the service. `ResponseEntity.created(...)` returns **201 Created**, a `Location` URL containing the generated ID, and the saved Student JSON. |
| `update(UUID studentId, Student request)` | `PUT /{studentId}` takes the target ID from the URL and the new field values from the body. `@Valid` validates the body before the service updates the existing row. |
| `delete(UUID studentId)` | Delegates the ID to the service and returns **204 No Content**. The service marks the Student inactive rather than physically deleting it. |

For example, the create method's important pieces are:

```java
public ResponseEntity<Student> create(@Valid @RequestBody Student request) {
    Student response = studentService.create(request);
    return ResponseEntity.created(
            URI.create("/api/students/" + response.getStudentId())).body(response);
}
```

`@RequestBody` makes the JSON body the `Student` argument. `@Valid` activates
the validation annotations on that entity. The service returns the saved
entity, which provides the generated ID needed for the response `Location`
header.

### `EnrollmentController`

`@RequestMapping("/api/enrollments")` defines the Enrollment base URL. Each
method follows the same controller pattern, but Enrollment creation and
updates require nested Student and Course references in the request body.

| Method | What the code does and why |
| --- | --- |
| `findAll()` | `GET /api/enrollments`; returns all Enrollment entities from the service. |
| `findById(UUID enrollmentId)` | `GET /{enrollmentId}`; binds the UUID path segment and delegates lookup. |
| `create(Enrollment request)` | `POST /api/enrollments`; validates the entity, calls the service, then returns **201 Created**, a `Location` header, and the saved entity JSON. |
| `update(UUID enrollmentId, Enrollment request)` | `PUT /{enrollmentId}`; URL ID selects the target Enrollment, and body values provide the replacement fields and related entity IDs. |
| `delete(UUID enrollmentId)` | `DELETE /{enrollmentId}`; delegates physical Enrollment deletion and returns **204 No Content**. |

Controllers return entities directly because the separate Student and
Enrollment response DTOs were removed. The `ResponseEntity` wrapper is used
on POST because the API includes an explicit created status and resource URL;
simple GET and PUT methods return the object and use Spring's default
successful status.

## 3.2 Code walkthrough: entities

The entity classes are used both by JPA to map database records and by the
controllers as JSON request/response models.

### `Student`

- `@Entity` registers the class with JPA; `@Table(name = "student")` maps it
  to the PostgreSQL `student` table.
- `@Id`, `@GeneratedValue`, and `@UuidGenerator` make `studentId` the
  generated UUID primary key. Clients should copy it from the create response
  rather than inventing one.
- `@Column(name = ..., nullable = false, unique = true)` maps Java property
  names to database column names and documents/enforces schema constraints
  (the deployed database should still be checked for actual constraints).
- `@NotBlank`, `@NotNull`, and `@Email` validate incoming entity JSON:
  required text cannot be blank, admission date cannot be null, and email
  must have email syntax.
- `currentStatus = "Active"` initializes new Java objects. It is not
  necessarily a PostgreSQL column default.
- `@OneToMany(mappedBy = "student")` marks `enrollments` as the inverse side
  of the relation; Enrollment owns the `student_id` join column.
- `@JsonIgnore` on `enrollments` prevents recursive Student → Enrollment →
  Student JSON. The separate Student enrollments endpoint exposes that data.
- Timestamp fields are read-only to Hibernate on insert/update according to
  their column options; database defaults/triggers determine whether values
  are populated.

### `Enrollment`

- `@Entity` and `@Table(name = "enrollment")` map this class to the
  `enrollment` table.
- Its `enrollmentId` uses a generated UUID primary key.
- `student` is `@ManyToOne(optional = false)` with a non-null
  `@JoinColumn(name = "student_id")`; this is the owning foreign-key side.
  `@NotNull` rejects a request without a Student object. The service then
  checks the nested Student UUID against the database.
- `course` is a `@ManyToOne` mapped through `course_id`. The mapping allows
  null, but the service requires a Course object with an existing ID for API
  create/update. This is a business/API requirement rather than an entity
  nullability constraint.
- Required scalar properties use Bean Validation annotations so invalid
  JSON can be rejected before persistence.
- `enrollmentStatus = "Active"` supplies a Java-side default on construction.
- `getStudentName()` joins the related Student's nonblank first, middle, and
  last names. `@Transient` means it is a calculated JSON property, not a
  stored database column.

## 3.3 Code walkthrough: services

The service implementation is where record lookups, field updates, status
defaults, related-record validation, and logging live. This keeps those
decisions out of the HTTP controllers.

### Student service

- `findAll()` calls `studentRepository.findAll()`, logs the number of records,
  and returns the list.
- `findById()` uses the private `getStudent()` helper. That helper wraps
  `findById()` and raises a `ResponseStatusException` with **404** when the
  UUID is not present.
- `create()` clears `studentId` so a client cannot choose/overwrite an
  existing primary key, supplies `Active` if `currentStatus` is null, then
  saves and returns the entity.
- `update()` first loads the existing record. It copies only allowed
  Student fields from the request into that managed entity, preserving
  database-managed fields and relationships. A null `currentStatus` keeps
  the old status. It then saves and returns the updated Student.
- `delete()` sets `currentStatus` to `Inactive` and saves the row. This is a
  soft delete, so the Student remains in the database.

### Enrollment service

- Read methods are annotated `@Transactional(readOnly = true)`. The
  repository fetches Student and Course for the returned Enrollment; keeping
  the read operation in a transaction also supports access to lazy JPA
  relationships while JSON is prepared.
- `create()` reads the nested IDs using `getStudentId()` and `getCourseId()`.
  Those helpers reject a missing nested object with **400**; `getStudent()`
  and `getCourse()` load persisted records and return **404** if an ID does
  not exist. The service replaces request references with these managed
  entities, clears any client-supplied Enrollment UUID, defaults null status
  to `Active`, then saves.
- `update()` first finds the target Enrollment by its URL ID; if missing, it
  returns **404**. It then resolves the Student and Course references and
  copies the request's Enrollment fields to the existing entity. A null
  `enrollmentStatus` preserves the old value. The service saves and returns
  the updated entity.
- `delete()` loads the target Enrollment and calls repository deletion. A
  missing Enrollment is **404**; a successful delete is logged.
- Service logging reports operation names and IDs/counts rather than logging
  Student names, emails, or complete request bodies.

This update approach is intentionally a full entity-field update, not a
partial PATCH. For required fields, include valid values in PUT bodies.
Enrollment related entities are resolved by ID; sending other changed
Student/Course fields in those nested objects does not update those records.

## 3.4 Code walkthrough: repositories and error handling

### Repositories

`StudentRepository` and `EnrollmentRepository` extend
`JpaRepository<Entity, ID>`. This gives the application standard methods such
as `findAll`, `findById`, `save`, and `delete` without implementing them
manually.

`EnrollmentRepository` adds `findByStudentStudentId(UUID studentId)`.
Spring Data derives the query from the property path: find Enrollment records
whose `student.studentId` equals the supplied UUID. Its `@EntityGraph` on
read methods requests the associated Student and Course together with each
Enrollment. This helps produce the nested entity JSON without relying on
uninitialized lazy proxies after the repository call.

### `GlobalExceptionHandler`

The `@RestControllerAdvice` class handles exceptions across REST controllers
so clients receive a consistent JSON shape:

```json
{
  "timestamp": "2026-10-29T22:00:00",
  "status": 404,
  "error": "Student not found: <uuid>"
}
```

- A `ResponseStatusException` keeps the status selected by the service (for
  example, 404 for a missing ID or 400 for a missing reference).
- `MethodArgumentNotValidException` collects validation errors into a
  `details` map and returns **400**.
- Malformed JSON or invalid parameter conversion returns **400**.
- `DataIntegrityViolationException` returns a generic **409 Conflict** so
  raw database details are not exposed in the response.
- Unexpected runtime failures are logged with stack traces and return a
  generic **500 Internal Server Error** message.

## 4. Data model and relationship

### Student

The Student entity maps to the `student` table. Its primary key is a generated
UUID named `studentId`. Important fields include:

- Required: `enrollmentNo`, `firstName`, `lastName`, `email`, and
  `admissionDate`.
- `enrollmentNo` and `email` are unique in the database; email must also have
  valid email syntax.
- Optional: `middleName`, `gender`, `dob`, `phone`, and `address`.
- `currentStatus` defaults to `Active` for a new Java entity if not provided.
- `createdAt` and `updatedAt` are read from the database mapping; their
  returned values depend on database defaults/triggers.

The entity has an inverse `@OneToMany` list of Enrollments. It is marked
`@JsonIgnore` because directly serializing both sides of the relationship
would otherwise create a recursive Student → Enrollment → Student JSON
structure. Use `GET /api/students/{studentId}/enrollments` to retrieve a
student's enrollments.

### Enrollment

The Enrollment entity maps to the `enrollment` table. Its primary key is a
generated UUID named `enrollmentId`. It contains:

- `student`: a required `@ManyToOne` reference to one existing Student; this
  relationship owns the non-null `student_id` foreign key.
- `course`: a `@ManyToOne` reference to Course. Although the entity mapping
  permits a null course, the Enrollment API service requires a Course ID on
  create and update.
- Required fields: `academicYear`, `rollNo`, `division`, and `startDate`.
- Optional fields: `semester`, `admissionType`, `endDate`, `exitReason`, and
  `finalCgpa`.
- `enrollmentStatus` defaults to `Active` for new entities. On update, an
  omitted/null status leaves the current status unchanged.
- `studentName` is a transient derived property: it joins nonblank name parts
  from the related Student and is not stored as an Enrollment database column.

Each Enrollment belongs to exactly one Student. A Student may have zero or
many Enrollments. Enrollment responses include nested Student and Course
objects because these are direct entity responses.

### JSON relationship safeguards

Course's department and collection relationships are ignored during JSON
serialization in the Course entity. Student's enrollment collection is also
ignored. Enrollment's owning Student and Course references are returned.
Enrollment repository queries fetch Student and Course associations so these
relationships are available for API output.

## 5. Configure the database

Database settings are in
`src/main/resources/application.properties`. Connection values can be
overridden using environment variables. Set credentials in your terminal,
not in committed source files:

```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://<host>:<port>/<database>"
$env:SPRING_DATASOURCE_USERNAME = "<database-username>"
$env:SPRING_DATASOURCE_PASSWORD = "<database-password>"
```

If no URL override is supplied, the configuration uses the project's
configured Supabase PostgreSQL connection. Use a valid password for that
database and ensure the database is reachable from your machine.

The default connection pool maximum is three; it can be overridden with
`SPRING_DATASOURCE_MAX_POOL_SIZE`. Hibernate uses
`spring.jpa.hibernate.ddl-auto=update`; it may update mapped tables on startup.
This is convenient for development but should be used cautiously for
production databases, where versioned migrations are generally preferable.

### Apply the Student inactive-status migration

The Student DELETE endpoint implements a soft delete by setting
`current_status` to `Inactive`. The database check constraint must permit that
value. The Supabase migration
`supabase/migrations/20261030003000_allow_inactive_student_status.sql` updates
the allowed status list while retaining the existing values. Apply this
migration to the same database used by the application before testing Student
DELETE. `spring.jpa.hibernate.ddl-auto=update` does not reliably update this
existing manually defined check constraint.

If startup reports that Hibernate cannot determine a dialect, inspect the
earlier datasource error first. A wrong URL, password, unavailable database,
or network problem can stop Hibernate from reading JDBC metadata.

## 6. Run, build, and package

Prerequisites:

- JDK 21
- PowerShell on Windows
- Valid PostgreSQL connection settings

From the repository root, set the database environment variables above and
start the application:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternatively, run `com.university.erp.ErpApplication` from the Java tools in
VS Code. The default application port is `8080`. On successful startup, the
application logs the Swagger URL.

Run the focused Student/Enrollment tests:

```powershell
.\mvnw.cmd "-Dtest=GlobalExceptionHandlerTest,StudentEnrollmentEntityValidationTest,StudentEnrollmentJsonTest,StudentServiceImplTest,EnrollmentServiceImplTest" test
```

Build the WAR:

```powershell
.\mvnw.cmd clean package
```

The expected artifact is `target/erp-0.0.1-SNAPSHOT.war`. A clean build can
fail if another Java or IDE process is holding files in `target`; stop the
process using those files and retry the build.

For external Tomcat deployment, copy the WAR to Tomcat's `webapps` directory
and configure the datasource environment variables for the Tomcat process.
With the default WAR name, the context path is `/erp-0.0.1-SNAPSHOT`.

## 7. Open Swagger

For a local run on the default port:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html?urls.primaryName=all-apis`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs/all-apis`

In Swagger:

1. Expand **Students** or **Enrollments**.
2. Expand the operation you want.
3. Click **Try it out**.
4. Enter a path UUID or JSON request body, as appropriate.
5. Click **Execute** and inspect the response code, response body, and
   response headers.

The list operations `GET /api/students` and `GET /api/enrollments` currently
have no query parameters, so Swagger correctly displays **No parameters**.
Individual GET/PUT/DELETE operations use path parameters. POST/PUT operations
use a **Request body**, which Swagger displays separately from Parameters.

## 8. Student API

Base path: `/api/students`

| Method | Path | Purpose | Success |
| --- | --- | --- | --- |
| `POST` | `/api/students` | Create a Student | `201 Created` with entity JSON and a generated UUID |
| `GET` | `/api/students` | List Students | `200 OK` with an array |
| `GET` | `/api/students/{studentId}` | Get one Student | `200 OK` with entity JSON |
| `GET` | `/api/students/{studentId}/enrollments` | List that Student's Enrollments | `200 OK` with an array |
| `PUT` | `/api/students/{studentId}` | Update a Student | `200 OK` with updated entity JSON |
| `DELETE` | `/api/students/{studentId}` | Soft delete: mark Student inactive | `204 No Content` |
| `DELETE` | `/api/students/{studentId}/permanent` | Hard delete: physically remove Student | `204 No Content` |

### Create Student in Swagger

Choose **POST `/api/students`**, click **Try it out**, and submit this body.
Change the sample enrollment number/email if they already exist in your
database:

```json
{
  "enrollmentNo": "STU-2026-101",
  "firstName": "Aarav",
  "middleName": "Raj",
  "lastName": "Shah",
  "gender": "Male",
  "dob": "2005-06-15",
  "email": "aarav.shah.2026@example.edu",
  "phone": "9876543210",
  "address": "Ahmedabad, Gujarat",
  "admissionDate": "2026-08-01",
  "currentStatus": "Active"
}
```

Dates use ISO format `YYYY-MM-DD`. Do not send a `studentId` for a new
record; the application generates it.

Typical successful response (the UUID and database-managed timestamps vary):

```json
{
  "studentId": "generated-uuid-from-the-response",
  "enrollmentNo": "STU-2026-101",
  "firstName": "Aarav",
  "middleName": "Raj",
  "lastName": "Shah",
  "gender": "Male",
  "dob": "2005-06-15",
  "email": "aarav.shah.2026@example.edu",
  "phone": "9876543210",
  "address": "Ahmedabad, Gujarat",
  "admissionDate": "2026-08-01",
  "currentStatus": "Active",
  "createdAt": null,
  "updatedAt": null
}
```

Timestamp values shown as `null` depend on the database; defaults or triggers
may populate them. Copy the actual `studentId` from your response. It is
needed for Enrollment creation and for Student routes that take
`{studentId}`.

### Student GET, update, and delete

- **GET all:** `GET /api/students`; returns an array. This route currently
  does not support pagination, search, or query filters.
- **GET one:** `GET /api/students/{studentId}`; replace the UUID with the
  generated Student ID.
- **GET a Student's Enrollments:**
  `GET /api/students/{studentId}/enrollments`.
- **PUT update:** `PUT /api/students/{studentId}`; supply the complete
  Student fields required by validation. The URL UUID selects the record.
  If `currentStatus` is omitted/null, the existing status is kept.
- **DELETE:** `DELETE /api/students/{studentId}` performs a soft delete by
  changing `currentStatus` to `Inactive`. It does not physically delete the
  Student row. The database must have the inactive-status migration applied;
  otherwise PostgreSQL rejects the status update and the request fails.
  Success is `204 No Content` with no response body.
- **Permanent DELETE:** `DELETE /api/students/{studentId}/permanent`
  physically removes the Student row. This cannot be undone. If Enrollment
  records or other database rows still reference the Student, PostgreSQL's
  foreign-key constraint may reject the deletion; the API then returns
  `409 Conflict`. Related records are not automatically deleted.

## 9. Enrollment API

Base path: `/api/enrollments`

| Method | Path | Purpose | Success |
| --- | --- | --- | --- |
| `POST` | `/api/enrollments` | Create an Enrollment | `201 Created` with entity JSON and a generated UUID |
| `GET` | `/api/enrollments` | List Enrollments | `200 OK` with an array |
| `GET` | `/api/enrollments/{enrollmentId}` | Get one Enrollment | `200 OK` with entity JSON |
| `PUT` | `/api/enrollments/{enrollmentId}` | Update an Enrollment | `200 OK` with updated entity JSON |
| `DELETE` | `/api/enrollments/{enrollmentId}` | Delete an Enrollment | `204 No Content` |

### Create Enrollment prerequisites

Before creating an Enrollment, you need:

1. An existing Student UUID, usually copied from the Student create response.
2. An existing Course numeric ID.

Use **GET `/api/courses`** in Swagger to list Courses. Its `departmentId` and
`courseType` filters are optional; leave them blank to list all available
courses. Copy an actual Course `id` from the result. Do not assume that ID 1
exists. For example, if the Course list returns a course with `"id": 5`, use
5 below. If no suitable Course exists, create one through the Course API
using an existing department or omit `departmentId` when the service permits
it.

### Create Enrollment in Swagger

Choose **POST `/api/enrollments`**, click **Try it out**, replace the Student
UUID with your real value, and use an existing Course ID:

```json
{
  "student": {
    "studentId": "PASTE-THE-REAL-STUDENT-UUID-HERE"
  },
  "course": {
    "id": 5
  },
  "semester": 1,
  "academicYear": "2026-2027",
  "rollNo": 101,
  "division": "A",
  "admissionType": "Regular",
  "enrollmentStatus": "Active",
  "startDate": "2026-08-01",
  "endDate": null,
  "exitReason": null,
  "finalCgpa": null
}
```

Because the request body is an `Enrollment` entity, `student` and `course`
are nested objects. The service reads only `student.studentId` and
`course.id`, loads those existing database rows, then assigns the managed
entities to the Enrollment. Other Student/Course values in those nested
objects are not used to update the related records. IDs in the example are
placeholders: the Student and Course must exist in the database used by the
running application.

Typical successful response (IDs and timestamps vary):

```json
{
  "enrollmentId": "generated-enrollment-uuid",
  "student": {
    "studentId": "the-student-uuid-you-sent",
    "enrollmentNo": "STU-2026-101",
    "firstName": "Aarav",
    "middleName": "Raj",
    "lastName": "Shah",
    "gender": "Male",
    "dob": "2005-06-15",
    "email": "aarav.shah.2026@example.edu",
    "phone": "9876543210",
    "address": "Ahmedabad, Gujarat",
    "admissionDate": "2026-08-01",
    "currentStatus": "Active",
    "createdAt": null,
    "updatedAt": null
  },
  "course": {
    "id": 5,
    "courseCode": "TEST-BSC-CS",
    "courseName": "Test Bachelor of Computer Science",
    "courseType": "UNDERGRADUATE",
    "courseDuration": 4,
    "departmentId": 1
  },
  "semester": 1,
  "academicYear": "2026-2027",
  "rollNo": 101,
  "division": "A",
  "admissionType": "Regular",
  "enrollmentStatus": "Active",
  "startDate": "2026-08-01",
  "endDate": null,
  "exitReason": null,
  "finalCgpa": null,
  "createdAt": null,
  "studentName": "Aarav Raj Shah"
}
```

This is an illustrative shape, not a promise that IDs or Course details in
your database match the sample. Database-managed timestamp values may also
be populated rather than null.

### Enrollment GET, update, and delete

- **GET all:** `GET /api/enrollments`; returns an array of Enrollment entity
  JSON with nested Student and Course details.
- **GET one:** `GET /api/enrollments/{enrollmentId}`; use the ID returned by
  the Enrollment create request.
- **PUT update:** `PUT /api/enrollments/{enrollmentId}`; send the Enrollment
  entity body, including nested existing Student and Course IDs and required
  Enrollment fields. The URL UUID selects the target Enrollment. Null or
  omitted `enrollmentStatus` preserves the old value.
- **DELETE:** `DELETE /api/enrollments/{enrollmentId}` physically deletes
  that Enrollment record and returns `204 No Content`.

## 10. Validation and error responses

### Validation rules

Student create/update requires:

- Nonblank `enrollmentNo`, `firstName`, `lastName`, and `email`.
- Email in a valid email format.
- Non-null `admissionDate`.

Enrollment create/update requires:

- A Student object with an existing non-null `studentId`.
- A Course object with an existing non-null `id` (required by the service,
  even though the JPA `course_id` mapping is nullable).
- Nonblank `academicYear` and `division`.
- Non-null `rollNo` and `startDate`.

The validation annotations run through `@Valid` on POST and PUT controller
request bodies. The database enforces additional constraints, including
unique Student enrollment numbers and emails.

### Common responses

| HTTP status | Meaning in this module |
| --- | --- |
| `200 OK` | Successful GET or PUT; response body contains the entity or entity list |
| `201 Created` | Successful POST; response body contains the saved entity and generated ID |
| `204 No Content` | Successful DELETE; no response body |
| `400 Bad Request` | Missing/invalid fields, malformed JSON, invalid UUID, or missing Student/Course reference |
| `404 Not Found` | Student, Enrollment, or referenced Course ID does not exist |
| `409 Conflict` | Duplicate unique value or other database constraint conflict |
| `500 Internal Server Error` | Unexpected server failure; response uses a generic message |

Typical structured error:

```json
{
  "timestamp": "2026-10-29T22:00:00",
  "status": 404,
  "error": "Course not found: 1"
}
```

Validation errors also include a `details` map with invalid field names and
messages. For database integrity failures, the client receives a generic 409
message; server logs contain the database diagnostic.

## 11. Troubleshooting common API errors

### `Course not found: 1`

The Course ID in the Enrollment body does not identify a Course in the
configured database. Run **GET `/api/courses`**, leave optional filters
blank, and copy an existing result's numeric `id`. A Course record may have
an ID such as 5 or 11; use the actual value returned by your API, not an
assumed ID.

### Duplicate Student enrollment number or email

Student `enrollmentNo` and `email` are unique. Change the request to values
not already used by another Student. A duplicate causes `409 Conflict`.

### Duplicate Course department assignment

If PostgreSQL reports a unique constraint such as
`course_department_id_key`, that database schema currently allows only one
Course per Department. Use an existing Course, select a different eligible
department if one is available, or have the schema/business rule reviewed
before attempting another create.

### `No parameters` in Swagger

This is expected for `GET /api/students` and `GET /api/enrollments`: they
currently list all records without pagination or query filters. A JSON body
appears under **Request body**, not **Parameters**. For **GET
`/api/courses`**, the `departmentId` and `courseType` filters are optional
query parameters.

### Student DELETE fails with `student_current_status_check`

The configured database's check constraint does not yet include `Inactive`.
Apply `supabase/migrations/20261030003000_allow_inactive_student_status.sql`
to that database, then retry DELETE. The failed update is rolled back, so the
Student row remains unchanged until the constraint is updated.

### Permanent Student DELETE returns `409 Conflict`

The Student is referenced by Enrollment or another row protected by a
foreign-key constraint. The API does not cascade-delete related records.
Review and remove or reassign dependent records using the appropriate APIs
before retrying the permanent deletion.

### Application startup / connection errors

Verify that Java 21 is selected, Maven compilation succeeds, datasource URL
and credentials are valid, and PostgreSQL is reachable. A Hibernate dialect
error can be a consequence of a failed database connection.

## 12. Logging and automated verification

Student and Enrollment services log successful operations at INFO and
missing-record/request events at WARN. The shared exception handler logs
unexpected failures at ERROR. Service logs use record IDs and counts rather
than Student names, email addresses, or complete request bodies.

Focused tests can be run with the command in Section 6. They cover:

- Required Student and Enrollment entity fields.
- Entity JSON serialization and relationship recursion protection.
- Missing-record handling and Enrollment Student/Course lookup.
- Status defaults/preservation and service update behavior.
- Structured exception responses.

The full application-context test may need valid database credentials and
network access to PostgreSQL.

## 13. Important design trade-off

Using JPA entities directly is the requested design for Student and
Enrollment. It avoids DTO and mapper classes, but exposes persistence fields
as API fields and makes nested relationships part of the public JSON shape.
The current `@JsonIgnore` safeguards prevent the most direct recursive
relationships; any future entity relationship should be reviewed for JSON
serialization before being exposed through these endpoints.

## 14. Student package: file-by-file code explanation

The files under `src/main/java/com/university/erp/student/` work together as
one feature. The package is divided by responsibility:

```text
student/
  controller/   HTTP routes and request/response handling
  entity/       Student and Enrollment table mappings and relationships
  repository/   database access interfaces
  service/      operation contracts and business/persistence logic
```

There are no Student/Enrollment `dto` or `mapper` classes in the current
implementation. The controller body type and response type are the JPA
entities themselves.

### 14.1 `controller/StudentController.java`

This class defines the Student API. Its annotations establish the HTTP
behavior:

- `@RestController` makes method return values HTTP response bodies (JSON).
- `@RequestMapping("/api/students")` is the prefix shared by its routes.
- `@Tag(name = "Students")` groups the methods in Swagger UI.
- Constructor injection supplies `StudentService` and `EnrollmentService`.
  The controller delegates Enrollment lookup to the latter for the nested
  Student-enrollments route.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, and `@DeleteMapping` map
  Java methods to HTTP operations.
- `@PathVariable` takes a UUID from the URL. `@RequestBody` converts incoming
  JSON to a `Student` object. `@Valid` runs entity validation before service
  logic.

Method behavior:

1. `findAll()` calls `studentService.findAll()` and returns every Student.
   There are no method arguments, which is why Swagger shows no parameters.
2. `findById(studentId)` delegates a single-record lookup.
3. `findEnrollments(studentId)` returns the Enrollment list for that Student
   by calling `enrollmentService.findByStudentId(studentId)`.
4. `create(request)` calls the service, then returns HTTP **201**, the new
   Student JSON, and a `Location` header containing the generated ID.
5. `update(studentId, request)` sends the URL ID and validated entity body
   to the service; the URL ID decides which Student to change.
6. `delete(studentId)` delegates deletion behavior and returns **204** with
   no body. The service implements this as a soft delete.

### 14.2 `controller/EnrollmentController.java`

This controller maps `/api/enrollments` to Enrollment operations. It takes
`Enrollment` entities directly as POST/PUT request bodies and returns
Enrollment entities from GET/POST/PUT operations.

- `findAll()` lists all Enrollment records.
- `findById(enrollmentId)` returns one record selected by the UUID path.
- `create(request)` validates and delegates creation. On success, the
  controller builds a **201 Created** response with the generated
  Enrollment UUID in the `Location` header.
- `update(enrollmentId, request)` uses the path UUID as the update target;
  the body supplies Enrollment fields plus nested Student and Course IDs.
- `delete(enrollmentId)` delegates physical removal of the Enrollment and
  returns **204 No Content**.

The controller does not directly assign foreign keys or call repositories.
Those tasks belong in the service so the referenced records can be checked.

### 14.3 `entity/Student.java`

`Student` is both the JPA representation of a row in the `student` table and
the API's JSON type.

| Code element | Meaning |
| --- | --- |
| `@Entity` / `@Table(name = "student")` | Register the class with JPA and map it to the database table |
| `studentId` with `@Id`, `@GeneratedValue`, `@UuidGenerator` | Generated UUID primary key |
| `@Column(name = "enrollment_no", nullable = false, unique = true)` | Map the Java property to the enrollment-number column, which is required and unique |
| `@NotBlank` on required text / `@NotNull` on admission date | Reject missing or blank API input before service execution |
| `@Email` | Validate that the email value has email-address syntax |
| `currentStatus = "Active"` | Java object default used when the client omits the status on create |
| `createdAt` / `updatedAt` column options | Read timestamp columns without having Hibernate write them as regular request values |
| `@OneToMany(mappedBy = "student")` | Inverse collection; `Enrollment.student` owns the foreign key |
| `@JsonIgnore` on `enrollments` | Avoid recursive JSON and avoid exposing the whole collection on every Student response |
| Getters and setters | Allow Jackson to bind/serialize entity properties and application code to update fields |

Fields such as first/middle/last name and contact details are ordinary Java
properties; the JPA annotations and validation constraints determine how
they map and which incoming values are required. The database remains the
final authority for deployed unique/null constraints.

### 14.4 `entity/Enrollment.java`

`Enrollment` maps to the `enrollment` table and is returned directly from
Enrollment APIs.

- `enrollmentId` is a generated UUID primary key.
- `student` is the required owning `@ManyToOne` relationship. Its
  `@JoinColumn(name = "student_id", nullable = false)` maps the foreign-key
  column, while `@NotNull` ensures the JSON contains a Student object.
- `course` is a `@ManyToOne` relationship through `course_id`. The entity
  mapping permits null, but the service requires a nested Course ID and
  confirms that Course exists.
- `academicYear`, `rollNo`, `division`, and `startDate` have validation
  constraints matching the API's required Enrollment data.
- Semester, admission type, end date, exit reason, final CGPA, and creation
  timestamp represent optional or database-managed information.
- `enrollmentStatus` is initialized to `Active` for a new object.
- `getStudentName()` composes the related Student's nonblank name parts.
  `@Transient` tells JPA not to store this derived value as a column; Jackson
  can still serialize it as part of the JSON response.
- Getters/setters support JSON binding, service updates, and persistence.

### 14.5 `repository/StudentRepository.java`

`StudentRepository extends JpaRepository<Student, UUID>` tells Spring Data:
manage `Student` entities whose ID type is `UUID`. Spring creates the
implementation at runtime. The interface therefore inherits common methods:

- `findAll()` — fetch all Students.
- `findById(id)` — return an `Optional<Student>` for one ID.
- `save(student)` — insert a new entity or persist changes.
- `delete(student)` — physically remove an entity when called.

The repository is deliberately small because Student needs only these
standard operations; custom SQL is not required for its current API.

### 14.6 `repository/EnrollmentRepository.java`

This repository extends `JpaRepository<Enrollment, UUID>` and adds the
derived query `findByStudentStudentId(UUID studentId)`. Spring Data parses
the property path as `Enrollment.student.studentId` and selects matching
enrollments.

`@EntityGraph(attributePaths = {"student", "course"})` on the read methods
asks JPA to fetch each Enrollment together with its Student and Course. This
is used because the API returns entity relationships as nested JSON and the
associations are otherwise lazy. Fetching the required associations on the
repository read keeps the response data available when serialized.

### 14.7 `service/StudentService.java` and `EnrollmentService.java`

These are Java interfaces that define the operations controllers may call.
They describe the service contract without specifying how database lookups
and updates work:

- `StudentService` declares list, get-by-ID, create, update, and delete.
- `EnrollmentService` declares list, get-by-ID, list-by-Student, create,
  update, and delete.

The create/update arguments and returned values are `Student` or
`Enrollment`, not DTOs. The interfaces make the controller depend on the
feature's operation contract rather than a concrete implementation and allow
tests to substitute mocks.

### 14.8 `service/StudentServiceImpl.java`

`@Service` registers this implementation as a Spring-managed service.
Constructor injection provides `StudentRepository`. The class uses SLF4J
logging to record operation results without logging personal Student fields.

The main code path is:

```text
findAll -> repository.findAll -> log count -> return list
findById -> getStudent -> return found Student
create -> clear client ID -> apply status default -> repository.save
update -> getStudent -> copy allowed request fields -> repository.save
delete -> getStudent -> set status Inactive -> repository.save
```

The private `getStudent` helper centralizes lookup and missing-record
behavior. It calls `findById` and throws a `ResponseStatusException` with
**404 Not Found** if no row matches. Reusing the helper keeps GET, update,
and soft-delete behavior consistent.

On update, the implementation copies the editable fields individually onto
the existing entity. This preserves its generated ID, timestamp properties,
and relationship collection rather than replacing the record with arbitrary
client-supplied values.

### 14.9 `service/EnrollmentServiceImpl.java`

`@Service` registers the service, and constructor injection supplies three
repositories: Enrollment, Student, and Course. The extra repositories are
needed because Enrollment creation and update must validate its related
records.

The main flow for creation is:

```text
request Enrollment entity
    -> read request.student.studentId
    -> load existing Student or return 404
    -> read request.course.id
    -> load existing Course or return 404
    -> replace request associations with managed entities
    -> clear client Enrollment ID and default status
    -> save and return Enrollment
```

`getStudentId` and `getCourseId` reject a missing nested object with **400**.
`getStudent` and `getCourse` reject a null ID with **400**, then query the
database and return **404** when the referenced record does not exist. This
distinguishes malformed/incomplete input from a well-formed ID that has no
matching record.

Update first loads the target Enrollment; a missing target is **404**. It
then resolves both related entities and copies each editable Enrollment
field. A null `enrollmentStatus` preserves the old value. Read operations
use `@Transactional(readOnly = true)` because returned Enrollment entities
contain lazy associations. Delete loads the target and physically deletes
the Enrollment row.

### 14.10 How one API call travels through the Student package

For `POST /api/enrollments`, the full path is:

1. Spring MVC matches the URL to `EnrollmentController.create`.
2. Jackson converts the JSON body into an `Enrollment`; nested `student` and
   `course` JSON become related entity objects.
3. `@Valid` checks required Enrollment fields.
4. `EnrollmentServiceImpl.create` extracts the nested IDs and uses the
   Student and Course repositories to ensure both records exist.
5. The Enrollment repository persists the entity; Hibernate translates that
   operation into SQL and PostgreSQL stores the foreign keys and scalar data.
6. The controller returns **201 Created**, the saved entity JSON, and a
   `Location` header containing the new Enrollment URL.

For Student creation, the same general route is used, but only
`StudentRepository` is needed. `StudentServiceImpl` clears the request ID,
supplies the default status if needed, saves, and returns the Student.
