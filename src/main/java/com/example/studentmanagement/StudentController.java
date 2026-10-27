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

    // ----------------------------------------------------------------
    // ORIGINAL (not recommended) - kept here so you can see what changed:
    //
    // private List<Student> students = new ArrayList<>();
    // private Long nextId = 1L;
    //
    // Why this wasn't ideal:
    // 1. A List means every lookup by id (in getOne/replace/patch/delete)
    //    has to loop through every element checking s.getId().equals(id)
    //    until it finds a match. That's O(n): with 10,000 students, a
    //    single GET /api/students/9999 could compare against all 10,000.
    //    A Map keyed by id makes that lookup a single O(1) hash lookup.
    // 2. `Long nextId` with `nextId++` is not thread-safe. nextId++ is
    //    really three separate steps: read the value, add 1, write it
    //    back. If two create() requests happen at almost the same instant,
    //    both can read the same value before either writes back, so two
    //    different students get handed the same id. AtomicLong bundles
    //    "read current value and increment" into one atomic step, so
    //    that race can't happen.
    // ----------------------------------------------------------------
    private final Map<Long, Student> students = new LinkedHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    @GetMapping
    public List<Student> getAll() {
        // ORIGINAL (not recommended): return students;
        //
        // Why: that returned a direct reference to the internal
        // collection. Any caller that got that List back could add,
        // remove, or reorder entries in it directly - completely
        // bypassing create()/delete() and corrupting the controller's
        // internal state without going through any of its logic.
        // Returning a fresh ArrayList copy of the values means callers
        // can do whatever they want with what they get back without it
        // affecting what this controller stores.
        return new ArrayList<>(students.values());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        // ORIGINAL (not recommended):
        // for (Student s : students) {
        //     if (s.getId().equals(id)) {
        //         return ResponseEntity.ok(s);
        //     }
        // }
        // return ResponseEntity.notFound().build();
        //
        // Why: this is the O(n) scan described above. students.get(id)
        // on a Map does the same job in one step, and reads more clearly
        // too - "get the student with this id" instead of "loop through
        // everyone and compare".
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
        // ORIGINAL (not recommended):
        // student.setId(nextId++);
        // students.add(student);
        //
        // Why `nextId++` wasn't safe: see the note above the `nextId`
        // field - it's not atomic, so concurrent requests can be handed
        // duplicate ids. `students.add(student)` itself was fine for a
        // List, but once students became a Map we need to store it by
        // its id with put(key, value) instead of just appending it.
        student.setId(nextId.getAndIncrement());
        students.put(student.getId(), student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    // PUT replaces the whole resource, so the client is expected to send
    // every field. If they leave one out, it really does get wiped - that
    // is correct PUT semantics (contrast with PATCH below, which merges).
    @PutMapping("/{id}")
    public ResponseEntity<Student> replace(@PathVariable Long id, @Valid @RequestBody Student newStudent) {
        // ORIGINAL (not recommended):
        // for (int i = 0; i < students.size(); i++) {
        //     if (students.get(i).getId().equals(id)) {
        //         newStudent.setId(id);
        //         students.set(i, newStudent);
        //         return ResponseEntity.ok(newStudent);
        //     }
        // }
        // return ResponseEntity.notFound().build();
        //
        // Why: same O(n) scan problem as getOne, but there's a second,
        // sneakier issue - students.get(i)/students.set(i, ...) access
        // the list by *position*, not by id. This only worked because the
        // list happened to stay in id order. If any other method ever
        // sorted the list, or inserted in a different order, this method
        // would silently update or overwrite the wrong student. Map.put
        // replaces by key (the actual id), so it can never be thrown off
        // by ordering.
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
        // ORIGINAL (not recommended):
        // for (Student s : students) {
        //     if (s.getId().equals(id)) {
        //         if (p.getFirstName() != null) s.setFirstName(p.getFirstName());
        //         if (p.getLastName() != null) s.setLastName(p.getLastName());
        //         if (p.getEmail() != null) s.setEmail(p.getEmail());
        //         if (p.getAge() != null) s.setAge(p.getAge());
        //         if (p.getCourse() != null) s.setCourse(p.getCourse());
        //         return ResponseEntity.ok(s);
        //     }
        // }
        // return ResponseEntity.notFound().build();
        //
        // Why: the merge logic (only overwrite fields the client actually
        // sent) was already correct and is unchanged below. The only
        // problem was finding `s` via an O(n) loop instead of a direct
        // Map lookup.
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
        // ORIGINAL (not recommended):
        // for (int i = 0; i < students.size(); i++) {
        //     if (students.get(i).getId().equals(id)) {
        //         students.remove(i);
        //         return ResponseEntity.noContent().build();
        //     }
        // }
        // return ResponseEntity.notFound().build();
        //
        // Why: besides the O(n) scan, `students.remove(i)` removes by
        // *index* (List has an overload that takes an int), not by the
        // student's id. It only worked here because `i` had just been
        // found to be the matching index - an easy thing to get wrong if
        // this code were ever copy-pasted or reordered. Map.remove(id)
        // removes by the actual key you care about, so there's no
        // index/id confusion possible.
        if (students.remove(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
