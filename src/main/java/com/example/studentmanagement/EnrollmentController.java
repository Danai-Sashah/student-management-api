package com.example.studentmanagement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class EnrollmentController {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping("/api/enrollments")
    public ResponseEntity<?> create(@RequestBody Map<String, Long> body) {
        Long studentId = body.get("studentId");
        Long courseId = body.get("courseId");

        if (studentId == null || courseId == null) {
            return ResponseEntity.badRequest().body("studentId and courseId are required");
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Already enrolled");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);

        enrollmentRepository.save(enrollment);
        return ResponseEntity.ok(enrollment);
    }

    @GetMapping("/api/courses/{courseId}/students")
    public ResponseEntity<?> getStudentsByCourse(@PathVariable Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            return ResponseEntity.notFound().build();
        }
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        List<Student> students = enrollments.stream()
                .map(Enrollment::getStudent)
                .toList();
        return ResponseEntity.ok(students);
    }

    @DeleteMapping("/api/enrollments")
    public ResponseEntity<?> delete(@RequestParam Long studentId, @RequestParam Long courseId) {
        var enrollmentOpt = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId);
        if (enrollmentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        enrollmentRepository.delete(enrollmentOpt.get());
        return ResponseEntity.noContent().build();
    }
}