package com.example.studentmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
    List<Student> findByLastNameIgnoreCase(String lastName);

    @Query("SELECT s FROM Student s JOIN s.courses c WHERE c.code = :code")
    List<Student> findByCourseCode(@Param("code") String code);

    // This is the method CourseController needs
    List<Student> findByCoursesId(Long courseId);
}