package com.example.studentmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @PostMapping
    public ResponseEntity<Student> create(@RequestBody Student student) {
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @GetMapping
    public ResponseEntity<Page<Student>> getAll(@PageableDefault(size = 2) Pageable pageable) {
        return ResponseEntity.ok(studentRepository.findAll(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> search(@RequestParam String lastName) {
        return ResponseEntity.ok(studentRepository.findByLastNameIgnoreCase(lastName));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Student> updateDepartment(@PathVariable Long id, @RequestBody Map<String, Long> body) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        if (body.containsKey("departmentId")) {
            Long departmentId = body.get("departmentId");
            Department department = departmentRepository.findById(departmentId).orElse(null);
            if (department == null) {
                return ResponseEntity.notFound().build();
            }
            student.setDepartment(department);
        }
        return ResponseEntity.ok(studentRepository.save(student));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        studentRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
