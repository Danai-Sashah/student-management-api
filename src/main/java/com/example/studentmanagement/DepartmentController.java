package com.example.studentmanagement;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {
    private final DepartmentRepository departments;
    public DepartmentController(DepartmentRepository departments) { this.departments = departments; }

    @GetMapping
    public List<Department> getAll() { return departments.findAll(); }

    @GetMapping("/{id}")
    public ResponseEntity<Department> getOne(@PathVariable Long id) {
        return departments.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Department> create(@RequestBody Department dept) {
        return ResponseEntity.status(201).body(departments.save(dept));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!departments.existsById(id)) return ResponseEntity.notFound().build();
        departments.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}