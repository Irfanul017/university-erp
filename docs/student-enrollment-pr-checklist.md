# Student & Enrollment PR Readiness Checklist

## 1. Current branch rule

This work must stay on the feature branch and must not be pushed directly to `main`.

Correct branch flow:
- `module/student-enrollment` = shared entity branch
- `feature/student-enrollment-crud` = personal working branch
- PR target = `module/student-enrollment`
- Final merge into `main` = done later by project lead/technical lead

Do not do this:
- no direct commit to `main`
- no direct push to `main`
- no force push to shared branches
- no work on another module branch without permission

---

## 2. Current developer status

Current feature branch:
- `feature/student-enrollment-crud`

This branch contains the Student & Enrollment implementation work.

---

## 3. What is included in this feature

The current feature branch includes:
- Student entity and repository
- Enrollment entity and repository
- Student request/response DTOs
- Enrollment request/response DTOs
- Mapper classes
- Student service implementation
- Enrollment service implementation
- REST controllers for student and enrollment APIs
- H2 datasource setup for local application startup
- test coverage for student enrollment flow

---

## 4. PR checklist before review

Before creating the pull request, confirm all of the following:

### Git / branch
- [ ] You are on `feature/student-enrollment-crud`
- [ ] You are not on `main`
- [ ] You have not pushed to `main`
- [ ] No unrelated files are mixed into this feature

### Build / verification
- [ ] Project compiles successfully
- [ ] Spring Boot app starts without errors
- [ ] Relevant tests are passing
- [ ] No broken imports or missing classes remain

### Code quality
- [ ] Meaningful commit messages are used
- [ ] No secrets or sensitive data included
- [ ] No debug logs or accidental test data remain
- [ ] Controller/service logic is consistent with project naming

### Module correctness
- [ ] Student CRUD works
- [ ] Enrollment CRUD works
- [ ] Duplicate validation is in place
- [ ] Student search by department/program is working
- [ ] Active enrollments filter is working

---

## 5. PR creation flow

When ready for review:
1. Ensure branch is correct
2. Check git status
3. Review changed files
4. Create PR with:
   - base: `module/student-enrollment`
   - compare: `feature/student-enrollment-crud`
5. Request review from the team lead or reviewer assigned to the module

---

## 6. Standard review notes

Reviewer should check:
- student and enrollment API contract
- database relationship validity
- validation logic
- naming consistency
- modular integration readiness
- no direct changes to shared or other module branches

---

## 7. Final rule

The correct workflow is:

`feature/student-enrollment-crud` -> PR -> `module/student-enrollment` -> later merge into `main`

This project does not allow direct push to `main`.

---

## 8. Optional next actions

After approval, the following can happen:
- merge feature branch into `module/student-enrollment`
- continue other student-specific enhancements
- integrate with department/course modules after shared API contracts are agreed
