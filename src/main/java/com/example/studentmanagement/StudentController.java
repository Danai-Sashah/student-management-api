package com.example.studentmanagement;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private List<Student> students = new ArrayList<>();
    private Long nextId = 1L;

    @GetMapping
    public List<Student> getAll() {
        return students;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getOne(@PathVariable Long id) {
        for (Student s : students) {
            if (s.getId().equals(id)) {
                return ResponseEntity.ok(s);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        student.setId(nextId++);
        students.add(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> replace(@PathVariable Long id, @RequestBody Student newStudent) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(id)) {
                newStudent.setId(id);
                students.set(i, newStudent);
                return ResponseEntity.ok(newStudent);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Student> patch(@PathVariable Long id, @RequestBody Student p) {
        for (Student s : students) {
            if (s.getId().equals(id)) {
                if (p.getFirstName() != null) s.setFirstName(p.getFirstName());
                if (p.getLastName() != null) s.setLastName(p.getLastName());
                if (p.getEmail() != null) s.setEmail(p.getEmail());
                if (p.getAge() != null) s.setAge(p.getAge());
                if (p.getCourse() != null) s.setCourse(p.getCourse());
                return ResponseEntity.ok(s);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        for (int i = 0; i < students.size(); i++) {
            if (students.get(i).getId().equals(id)) {
                students.remove(i);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}