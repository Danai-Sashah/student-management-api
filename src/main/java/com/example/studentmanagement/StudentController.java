package com.example.studentmanagement;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @RestController = @Controller + @ResponseBody. It tells Spring two things:
 *   1. This class handles incoming HTTP requests (it's a web controller).
 *   2. Whatever a method returns should be written straight into the HTTP
 *      response body as JSON, instead of being treated as a view name.
 *
 * @RequestMapping("/api/students") sets the common URL prefix for every
 * method below, so @GetMapping("/{id}") actually means GET /api/students/{id}.
 *
 * NOTE for later: storing data in memory like this means everything is
 * wiped every time the app restarts, and it won't work correctly if the
 * app ever runs as more than one instance. That's fine for learning REST
 * + Spring MVC now - the next lesson is usually swapping this out for a
 * repository backed by a real database (e.g. Spring Data JPA).
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    // Map<id, Student> instead of a List: looking a student up by id is
    // O(1) (one hash lookup) instead of O(n) (scanning every student).
    // LinkedHashMap keeps insertion order, so GET /api/students still
    // returns students in the order they were created.
    private final Map<Long, Student> students = new LinkedHashMap<>();

    // AtomicLong instead of a plain `Long nextId` field: if two requests
    // hit create() at the exact same time, nextId++ on a plain field can
    // hand out the same id twice. getAndIncrement() is guaranteed atomic.
    private final AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public List<Student> getAll() {
        return new ArrayList<>(students.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        Student student = students.get(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    /**
     * @Valid tells Spring to run the @NotBlank/@Email/@Min/@Max rules
     * declared on Student *before* this method body runs. If any rule
     * fails, Spring throws MethodArgumentNotValidException and this
     * method never executes - GlobalExceptionHandler turns that into a
     * clean 400 response with the list of problems.
     */
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        student.setId(nextId.getAndIncrement());
        students.put(student.getId(), student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    // PUT replaces the whole resource, so the client is expected to send
    // every field. If they leave one out, it really does get wiped - that
    // is correct PUT semantics (contrast with PATCH below, which merges).
    @PutMapping("/{id}")
    public ResponseEntity<Student> replace(@PathVariable Long id, @Valid @RequestBody Student newStudent) {
        if (!students.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        newStudent.setId(id);
        students.put(id, newStudent);
        return ResponseEntity.ok(newStudent);
    }

    // PATCH applies a partial update: only fields the client actually sent
    // (non-null here) overwrite the existing student.
    @PatchMapping("/{id}")
    public ResponseEntity<Student> patch(@PathVariable Long id, @RequestBody Student p) {
        Student s = students.get(id);
        if (s == null) {
            return ResponseEntity.notFound().build();
        }
        if (p.getFirstName() != null) s.setFirstName(p.getFirstName());
        if (p.getLastName() != null) s.setLastName(p.getLastName());
        if (p.getEmail() != null) s.setEmail(p.getEmail());
        if (p.getAge() != null) s.setAge(p.getAge());
        if (p.getCourse() != null) s.setCourse(p.getCourse());
        return ResponseEntity.ok(s);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (students.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
