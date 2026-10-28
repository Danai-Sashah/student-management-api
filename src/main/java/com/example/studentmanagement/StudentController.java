package com.example.studentmanagement;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * @RestController + @RequestMapping("/api/students") mean the same thing
 * here as in Exam 1 (see docs/marking/REVIEW_1.md): every method below
 * handles HTTP directly, and @RequestMapping sets the shared URL prefix.
 *
 * What's different from Exam 1: this controller no longer stores anything
 * itself. It only talks to `service` (StudentService), which talks to
 * `StudentRepository`, which talks to MySQL - so data now survives an
 * application restart, which is the whole point of this exam. See
 * StudentService and StudentRepository for the persistence side of that
 * split.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    /**
     * @Valid tells Spring to run any Bean Validation annotations declared
     * on Student before this method body executes. Note: Student currently
     * has none (see the note on Student.java), so this is a no-op right
     * now - it's left in place, along with GlobalExceptionHandler, so
     * validation can be re-added to Student without touching this line.
     */
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        // Rule: IDs must not be supplied when creating. MySQL (via
        // @GeneratedValue on Student.id) assigns the real id once this
        // reaches repo.save(), so any id sent by the client is discarded
        // first instead of being trusted.
        student.setId(null);
        Student saved = service.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        Student student = service.getById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    // PUT replaces the whole resource: the client is expected to send
    // every field, and existing.set___() below overwrites all of them
    // unconditionally. If the id doesn't exist, this returns 404 rather
    // than creating a new row - PUT here is "replace an existing student",
    // not "create-or-replace" (contrast with PATCH below, which merges).
    @PutMapping("/{id}")
    public ResponseEntity<Student> replace(@PathVariable Long id, @Valid @RequestBody Student newStudent) {
        Student existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        existing.setFirstName(newStudent.getFirstName());
        existing.setLastName(newStudent.getLastName());
        existing.setEmail(newStudent.getEmail());
        existing.setAge(newStudent.getAge());
        existing.setCourse(newStudent.getCourse());
        return ResponseEntity.ok(service.save(existing));
    }

    // PATCH applies a partial update: only fields the client actually sent
    // (non-null on `p`) overwrite `existing`'s current values, so an
    // unsupplied field keeps whatever was already in the database - the
    // exam's "unchanged fields must retain their previous values" rule.
    @PatchMapping("/{id}")
    public ResponseEntity<Student> patch(@PathVariable Long id, @RequestBody Student p) {
        Student existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        if (p.getFirstName() != null) existing.setFirstName(p.getFirstName());
        if (p.getLastName() != null) existing.setLastName(p.getLastName());
        if (p.getEmail() != null) existing.setEmail(p.getEmail());
        if (p.getAge() != null) existing.setAge(p.getAge());
        if (p.getCourse() != null) existing.setCourse(p.getCourse());

        return ResponseEntity.ok(service.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Student existing = service.getById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}