# Student & Enrollment Module Guide

## 1. Purpose

This module manages the student lifecycle in the ERP system:
- Student admission/profile management
- Enrollment into academic programs/semesters
- Search and retrieval of students by department/program
- Tracking active enrollments for an individual student

This module is designed to integrate with:
- Department module for department information
- Course/Subject module for academic program/course mapping
- Attendance module for student attendance records
- Examination module for marks and grade linkage
- Finance module for fee tracking and payment status

---

## 2. Module Package Structure

```text
src/main/java/com/university/erp/student/
├── controller/
│   ├── StudentController.java
│   └── EnrollmentController.java
├── dto/
│   ├── StudentRequest.java
│   ├── StudentResponse.java
│   ├── EnrollmentRequest.java
│   └── EnrollmentResponse.java
├── entity/
│   ├── Student.java
│   └── Enrollment.java
├── mapper/
│   ├── StudentMapper.java
│   └── EnrollmentMapper.java
├── repository/
│   ├── StudentRepository.java
│   └── EnrollmentRepository.java
├── service/
│   ├── StudentService.java
│   ├── StudentServiceImpl.java
│   ├── EnrollmentService.java
│   └── EnrollmentServiceImpl.java
└──
```

---

## 3. Core Entities

### 3.1 Student

Entity: `Student`

Table: `students`

Fields:
- `id` : Long
- `rollNumber` : String
- `name` : String
- `email` : String
- `phone` : String
- `address` : String
- `dateOfBirth` : LocalDate
- `department` : String
- `program` : String

Important rules:
- `email` is unique
- `rollNumber` is unique
- `department` and `program` are mandatory

```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String rollNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(length = 500)
    private String address;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private String program;
}
```

### 3.2 Enrollment

Entity: `Enrollment`

Table: `enrollments`

Fields:
- `id` : Long
- `student` : Student (ManyToOne)
- `academicYear` : String
- `semester` : String
- `courseName` : String
- `status` : String

Important rules:
- Every enrollment belongs to one student
- Status can be like: `ACTIVE`, `COMPLETED`, `DROPPED`, etc.
- Currently module supports active filtering via `ACTIVE`

```java
@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private String academicYear;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private String status = "ACTIVE";
}
```

---

## 4. Repository Layer

### StudentRepository

Methods:
- `findByEmail(String email)`
- `findByRollNumber(String rollNumber)`
- `findByDepartmentIgnoreCase(String department)`
- `findByProgramIgnoreCase(String program)`

This allows:
- uniqueness checks
- department listing
- program filtering
- student lookups for attendance/exam modules

### EnrollmentRepository

Methods:
- `findByStudentId(Long studentId)`

This allows:
- fetch all enrollments of one student
- student dashboard/reporting data

---

## 5. DTOs

### StudentRequest

Used for create/update request body.

Fields:
- `rollNumber`
- `name`
- `email`
- `phone`
- `address`
- `dateOfBirth`
- `department`
- `program`

Validation rules:
- `@NotBlank` on required strings
- `@Email` on email
- `@NotNull` on dateOfBirth

### StudentResponse

Returned after create/list/get operations.

Contains same student fields plus `id`.

### EnrollmentRequest

Fields:
- `studentId`
- `academicYear`
- `semester`
- `courseName`
- `status`

Validation rules:
- `studentId` required
- academicYear, semester, courseName required

### EnrollmentResponse

Contains:
- `id`
- `studentId`
- `studentName`
- `academicYear`
- `semester`
- `courseName`
- `status`

This response is useful for UI/reporting screens because it gives both student and enrollment data in one object.

---

## 6. Mapping Layer

### StudentMapper

Responsibilities:
- convert `StudentRequest` -> `Student`
- update existing `Student` from `StudentRequest`
- convert `Student` -> `StudentResponse`

### EnrollmentMapper

Responsibilities:
- convert `EnrollmentRequest` + `Student` -> `Enrollment`
- update existing enrollment with new values
- convert `Enrollment` -> `EnrollmentResponse`

This keeps business logic separated from persistence code.

---

## 7. Service Layer

### StudentService

Methods:
- `getAllStudents()`
- `getStudentById(Long id)`
- `getStudentsByDepartment(String department)`
- `getStudentsByProgram(String program)`
- `createStudent(StudentRequest request)`
- `updateStudent(Long id, StudentRequest request)`
- `deleteStudent(Long id)`

### EnrollmentService

Methods:
- `getAllEnrollments()`
- `getEnrollmentById(Long id)`
- `getEnrollmentsByStudentId(Long studentId)`
- `getActiveEnrollments()`
- `createEnrollment(EnrollmentRequest request)`
- `updateEnrollment(Long id, EnrollmentRequest request)`
- `deleteEnrollment(Long id)`

Business validation in service layer:
- email uniqueness check
- roll number uniqueness check
- student existence check before enrollment creation
- not found exception handling

---

## 8. Controller API Endpoints

Base path: `/api`

### Student Endpoints

#### Get all students
```http
GET /api/students
```

#### Search students by department or program
```http
GET /api/students/search?department=CSE
GET /api/students/search?program=Computer%20Science
```

#### Get one student
```http
GET /api/students/{id}
```

#### Create student
```http
POST /api/students
Content-Type: application/json
```

Example request body:
```json
{
  "rollNumber": "2024-CS-101",
  "name": "Aisha Khan",
  "email": "aisha.khan@example.com",
  "phone": "9876543210",
  "address": "Dhaka",
  "dateOfBirth": "2003-06-15",
  "department": "CSE",
  "program": "BSc in Computer Science"
}
```

#### Update student
```http
PUT /api/students/{id}
```

#### Delete student
```http
DELETE /api/students/{id}
```

### Enrollment Endpoints

#### Get all enrollments
```http
GET /api/enrollments
```

#### Get only active enrollments
```http
GET /api/enrollments/active
```

#### Get enrollments for a student
```http
GET /api/students/{studentId}/enrollments
```

#### Get enrollment by id
```http
GET /api/enrollments/{id}
```

#### Create enrollment
```http
POST /api/enrollments
Content-Type: application/json
```

Example request body:
```json
{
  "studentId": 1,
  "academicYear": "2024-2025",
  "semester": "Spring",
  "courseName": "Data Structures",
  "status": "ACTIVE"
}
```

#### Update enrollment
```http
PUT /api/enrollments/{id}
```

#### Delete enrollment
```http
DELETE /api/enrollments/{id}
```

---

## 9. Request/Response Flow

### Student creation flow
1. Client sends `StudentRequest` to `/api/students`
2. Controller validates request
3. StudentService checks duplicate email and roll number
4. Service saves `Student` via repository
5. Mapper converts entity to `StudentResponse`
6. Response returned to client

### Enrollment creation flow
1. Client sends `EnrollmentRequest` to `/api/enrollments`
2. Controller validates request
3. Service loads the `Student` by `studentId`
4. Service creates `Enrollment` linked to that student
5. Mapper converts `Enrollment` to `EnrollmentResponse`
6. Response returned to client

---

## 10. Database Design Notes

This module currently uses H2 in-memory database for local development/test environment.

Tables created automatically:
- `students`
- `enrollments`

Relation:
- One `Student` can have many `Enrollment` records
- `Enrollment.student_id` is a foreign key to `students.id`

---

## 11. Integration Points for Other Modules

### With Department Module
Expected relation:
- Student belongs to a department
- Department module should provide `departmentId` or department metadata later

Current implementation uses department as a plain string field to keep the project lightweight and decoupled initially.

Later migration suggestion:
- replace `String department` with `Long departmentId` or `@ManyToOne Department`

### With Course/Subject Module
Expected relation:
- Enrollment should eventually reference a `Course` or `Subject`
- Right now `courseName` is kept as a string to avoid dependency issues across modules

Later migration suggestion:
- replace `courseName` with `courseId` or `subjectId`

### With Attendance Module
Attendance module can fetch student records from this module using:
- student id
- department/program filters
- enrollment data

Useful integration calls:
- `GET /api/students/{id}`
- `GET /api/students/{studentId}/enrollments`
- `GET /api/students/search?department=...`

### With Examination Module
Examination module can use:
- student id
- enrolled course and semester
- academic year

### With Finance Module
Fee module can use:
- `studentId`
- `department`
- `program`
- `academicYear`

---

## 12. Suggested Future Enhancements

To make this module production-grade, the next improvements should be:

1. Replace string department/program fields with actual foreign-key relationships
2. Add course enrollment table linked to subject/course entities
3. Add status enum instead of raw string
4. Add audit fields: `createdAt`, `updatedAt`, `createdBy`
5. Add pagination and sorting for student list endpoints
6. Add custom exception handlers for validation and not-found errors
7. Add student photo/profile image support
8. Add bulk import for student records

---

## 13. Best Practices for Other Modules

When integrating from another module:
- use `studentId` as the primary key from this module
- prefer `GET /api/students/{id}` for full profile data
- use `/api/students/search` for filtering by department/program
- use `/api/students/{studentId}/enrollments` for enrollment-based reporting

Do not directly create duplicate records in this module from another module without checking existence first.

---

## 14. Example Integration Scenarios

### Scenario A: Attendance module wants present students in a department
```http
GET /api/students/search?department=CSE
```

### Scenario B: Exam module wants student enrollments for a semester
```http
GET /api/students/5/enrollments
```

### Scenario C: Finance module wants student details for fees
```http
GET /api/students/7
```

---

## 15. Final Notes

This Student & Enrollment module is intentionally designed as a clean base for the ERP system:
- simple to understand
- easy to extend
- easy for other modules to integrate
- scalable for later foreign-key linking with department/course modules

Any new module can consume this service by using the exposed REST endpoints or by reusing the repository/entity patterns already established here.
