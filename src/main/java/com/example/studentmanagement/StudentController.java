package com.example.studentmanagement;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    // POST
    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody Student student) {
        // Rule: IDs must not be supplied when creating
        student.setId(null);
        Student saved = service.save(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET
    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    // GET ONE
    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        Student student = service.getById(id);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    // PUT
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

    // PATCH
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

    // DELETE
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