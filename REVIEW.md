# Code Review — Student Management API

Reviewed commit: `623b170 "Student Management API final -ready"`

Overall this is a solid first REST API. The core CRUD flow, URL design, and
HTTP status codes are all correct, which is the hard part to get right as a
beginner. The notes below are things to learn from, not things that were
"wrong" in a disqualifying sense — a few are genuine bugs, most are the kind
of improvement you only know to make once someone points them out.

## What's good ✅

- Correct REST shape: `GET/POST /api/students`, `GET/PUT/PATCH/DELETE /api/students/{id}`.
- Right status codes per verb: `201 Created` on POST, `204 No Content` on
  DELETE, `404 Not Found` when an id doesn't exist.
- Clear distinction between PUT (full replace) and PATCH (partial update) —
  a lot of beginners merge these into one method.
- Clean POJO (`Student`) with no Spring annotations leaking into the model.

## Issues found & fixed in this pass 🔧

1. **No input validation** (`Student.java`, `StudentController.java`)
   Nothing stopped a POST with a blank name, an invalid email, or an age of
   `-5` or `9000`. Added Jakarta Bean Validation annotations
   (`@NotBlank`, `@Email`, `@Min`, `@Max`) on `Student`, and `@Valid` on the
   `@RequestBody` parameters in `create`/`replace`. Added
   `GlobalExceptionHandler` so a failed validation now returns a clean
   `400 Bad Request` with the specific field errors, instead of a generic
   500-style failure.

2. **O(n) lookups via `List<Student>`** (`StudentController.java`)
   Every `getOne`/`replace`/`patch`/`delete` looped over the whole list to
   find a student by id. Switched the backing store to
   `Map<Long, Student>` (a `LinkedHashMap` to keep insertion order for
   `getAll`), so lookups are O(1).

3. **`nextId++` is not thread-safe** (`StudentController.java`)
   Two requests arriving at the same instant could read the same value of
   `nextId` before either increments it, handing out a duplicate id.
   Replaced with `AtomicLong`, whose `getAndIncrement()` is atomic.

4. **Repetitive manual loops replaced with `Map` operations** — `replace`,
   `patch`, and `delete` now use `containsKey`/`get`/`remove` instead of
   hand-rolled `for` loops, which is both shorter and removes a class of
   "forgot to `break`/return correctly" bugs.

## Comments added for teaching

Both `Student.java` and `StudentController.java` now have comments
explaining *why* each annotation exists (`@RestController`, `@RequestMapping`,
`@PathVariable`, `@RequestBody`, `@Valid`, etc.), not just restating what the
code does line by line.

## Not changed, but worth knowing for next time 📚

These are intentionally left as-is for now — they're good "next lesson"
topics rather than bugs in this one:

- **In-memory storage.** All data is lost on restart and this design breaks
  if you ever run more than one instance of the app. The natural next step
  is Spring Data JPA with a real database.
- **No layering.** The controller does everything itself (storage + HTTP).
  Once you're comfortable with this version, try splitting it into
  `StudentController` → `StudentService` → `StudentRepository` so each class
  has one job.
- **No tests beyond `contextLoads()`.** Worth adding `@WebMvcTest` tests for
  the controller (e.g. "POST with missing email returns 400") once
  validation is in place, since that's exactly the kind of thing tests are
  good at guarding.
- **Duplicate emails are allowed.** There's no uniqueness check on `email`.
  Fine for now; becomes relevant once there's a real datastore with a
  unique constraint to mirror.
