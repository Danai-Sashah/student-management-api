# Marking — Spring Boot Exam 2: Database Integration

Reviewed commit: `0dfc00f "Completed: Mary John persistence proof, PUT PATCH DELETE tasks"`
Exam paper: `docs/questions/Spring_Boot_Exam_2_Database_Integration.pdf`
Branch: `database_studentManagement`

**Result: 79 / 100 — PASS** (pass mark is 60%)

Strong database-integration work. The JPA mapping, repository usage, and all six
endpoints are implemented correctly against the spec. What's missing is almost
entirely *evidence and paperwork* — Section E (Postman/DB verification) and
Section F (theory) — not application code, so the gap is easy to close.

## Marking Summary

| Section | Marks available | Marks awarded |
|---|---|---|
| A — Database and project setup | 15 | 13 |
| B — Entity mapping | 15 | 15 |
| C — Repository and business logic | 10 | 10 |
| D — REST API with persistence | 40 | 40 |
| E — Postman and database verification | 10 | 1 |
| F — Theory | 10 | 0 |
| **TOTAL** | **100** | **79** |

---

## Section A — Database and project setup — 13/15

- **Database name (4/4).** `spring.datasource.url=jdbc:mysql://localhost:3306/student_exam_db` — matches the required `student_exam_db`.
- **Dependencies (5/5).** `pom.xml` has `spring-boot-starter-web`, `spring-boot-starter-data-jpa`, and `mysql-connector-j` (runtime scope). All three required dependencies present.
- **Datasource + schema config (4/4).** URL, username, driver class, and `spring.jpa.hibernate.ddl-auto=update` are all set. The committed `spring.datasource.password=` is **intentionally blank** — correct instinct, this is exactly what the exam's security note asks for (no real secret in version control). Good practice, full marks.
- **Verified connection to MySQL (0/2).** No evidence in the repo that the app was actually started against MySQL (no console log, screenshot, or note). Can't award without proof — see "To get full marks" below.

## Section B — Entity mapping — 15/15

`Student.java` is a clean, correct JPA entity:
- `@Entity` + `@Id` with `@GeneratedValue(strategy = GenerationType.IDENTITY)`. ✅
- All five required fields present with the right Java types (`Long`, `String`×4, `Integer`).
- `@Column(nullable = false)` on `firstName`, `lastName`, `email`, `age`, `course`, and `unique = true` on `email` — matches the "Required, unique" spec exactly.
- No-arg constructor and full getters/setters.

Nothing to dock here.

## Section C — Repository and business logic — 10/10

- `StudentRepository extends JpaRepository<Student, Long>` — correct, minimal, no unneeded custom methods.
- The old `ArrayList`/`Map` in-memory storage from Exam 1 is fully gone; `StudentController` → `StudentService` → `StudentRepository` is a real layered design (better than Exam 1's controller-does-everything version, see `REVIEW_1.md`).
- Constructor injection used throughout (`StudentService(StudentRepository repo)`, `StudentController(StudentService service)`). Correct DI style — not field injection.

## Section D — REST API with persistence — 40/40

All six endpoints in `StudentController.java` match the spec:

| Method | Endpoint | Behavior checked | Marks |
|---|---|---|---|
| POST | `/api/students` | `student.setId(null)` before save — correctly refuses a client-supplied id; `201 Created` returned | 8/8 |
| GET | `/api/students` | Returns `repo.findAll()` | 6/6 |
| GET | `/api/students/{id}` | `404` via `ResponseEntity.notFound()` when missing | 6/6 |
| PUT | `/api/students/{id}` | `404` if missing, does **not** create a new row — matches "do not create a new student when PUT refers to an unknown id" | 7/7 |
| PATCH | `/api/students/{id}` | Null-checks each field so unsupplied fields keep their previous value — matches spec exactly | 7/7 |
| DELETE | `/api/students/{id}` | `404` if missing, `204 No Content` on success | 6/6 |

This is a correct, spec-accurate implementation of every rule in Section D, including
the two rules students most often get wrong (rejecting a client-supplied id on
POST, and not silently upserting on PUT/PATCH to a missing id).

**Worth knowing for next time:** the `@Valid` annotation is still on the POST/PUT
`@RequestBody` parameters, and `GlobalExceptionHandler` is still present, but
`Student` no longer carries any Bean Validation annotations (`@NotBlank`, `@Email`,
etc. — those were on the Exam 1 version, see `REVIEW_1.md`, and were dropped when
the class became a JPA entity). Right now `@Valid` does nothing; a blank
`firstName` or an unparseable `email` will hit the database constraint instead of
returning a clean `400`, surfacing as a less friendly `500`/`DataIntegrityViolationException`.
Not required by this exam's rubric, so no marks lost — but worth re-adding
(`@NotBlank` etc. on `Student`, alongside the `@Column(nullable=false)`) as a
follow-up, since Bean Validation and the DB `NOT NULL` constraint are meant to work
together, not one replacing the other.

## Section E — Postman and database verification — 1/10

`requests.http` only contains three requests: `POST` Mary, `POST` John, `GET /api/students`.
Against the four required proofs:

- **Create two students, show both in MySQL (3 marks → 1 awarded).** The two POST
  requests exist and match the exam's example payload shape, which shows intent,
  but there's no screenshot or query result showing the rows actually landed in
  `student_exam_db` — so this can't be fully credited.
- **Restart the app, GET to prove persistence (3 marks → 0 awarded).** No evidence
  of a restart-and-reverify step anywhere in the repo.
- **PUT one student + PATCH a single field, confirm DB values (2 marks → 0 awarded).**
  No PUT or PATCH request is in `requests.http` at all, even though both are
  correctly implemented in code (Section D). The commit message says "PUT PATCH
  DELETE tasks" were completed, but nothing documents them being run against MySQL.
- **DELETE a student, confirm 404 after (2 marks → 0 awarded).** No DELETE request
  present, no confirmation.

The code clearly supports all of this — it's a documentation/evidence gap, not a
functionality gap.

## Section F — Theory — 0/10

`docs/theory_submission/` is empty. None of the five short-answer questions
(`@Entity`'s purpose, two `JpaRepository` methods, ArrayList vs. database,
`@GeneratedValue`'s purpose, why 404 on an unknown id) have been answered yet.

---

## To get full marks

Nothing below requires touching the application code — it's all missing
deliverables from the exam's submission checklist:

1. **Answer Section F's five theory questions** and add them to
   `docs/theory_submission/` (e.g. `theory_submission/answers.md`). 10 marks.
2. **Add PUT, PATCH, and DELETE requests to `requests.http`** (or a Postman
   collection) and actually run the full sequence — two POSTs, a restart, a GET to
   prove persistence, a PUT, a PATCH, a DELETE, a final GET by id returning 404.
3. **Capture evidence**: a screenshot or `SELECT * FROM students;` result from
   MySQL showing the saved rows, and a console/log snippet or screenshot showing
   the app connecting to MySQL on startup. Both are explicit submission-checklist
   items as well as part of Sections A and E.
4. **Add a README** with setup/run instructions (checklist item — not separately
   marked in the rubric above, but required for a complete submission) and a `.sql`
   file with the `CREATE DATABASE student_exam_db;` statement.

Once those are added this would be a comfortable high-70s/low-80s submission
purely on evidence — the database and API work underneath is already full marks.
