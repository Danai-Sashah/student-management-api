package com.example.studentmanagement;

import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Thin service layer between StudentController and StudentRepository.
 *
 * @Service marks this as a Spring-managed bean so it can be constructor-
 * injected into StudentController below, rather than the controller
 * `new`-ing one up itself.
 *
 * ORIGINAL (Exam 1 version): there was no service layer at all - the
 * controller held the `Map<Long, Student>` directly and did every lookup
 * itself. Why add this layer: it separates "how a student is looked up or
 * stored" (here) from "how HTTP requests/responses are shaped"
 * (StudentController), so the controller doesn't need to change just
 * because the storage mechanism does - which is exactly what happened
 * going from Exam 1 (ArrayList, then Map) to Exam 2 (JpaRepository/MySQL).
 */
@Service
public class StudentService {

    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    public List<Student> getAll() {
        return repo.findAll();
    }

    // findById() returns an Optional<Student> because JPA has no way to
    // know in advance whether that id exists. orElse(null) unwraps it into
    // the plain "null means not found" contract StudentController already
    // checks for in getOne/replace/patch/delete.
    public Student getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // repo.save() does double duty: it INSERTs when the entity's id is
    // null (as on create, where StudentController sets id to null first),
    // and UPDATEs the existing row when the id is already set (as on
    // replace/patch, which start from an entity loaded via getById). JPA
    // decides which based solely on whether the id is already populated.
    public Student save(Student student) {
        return repo.save(student);
    }

    // deleteById() throws if the id doesn't exist, but StudentController
    // always calls getById() first and returns 404 itself before this is
    // ever reached, so that case never actually occurs here in practice.
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
