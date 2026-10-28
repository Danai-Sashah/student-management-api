package com.example.studentmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @Repository marks this as a Spring-managed persistence component (mostly
 * documentation here - Spring Data would pick it up anyway because it
 * extends JpaRepository, but being explicit doesn't hurt).
 *
 * Extending JpaRepository<Student, Long> (entity type, id type) is enough
 * on its own: at startup, Spring Data JPA generates a working
 * implementation of this interface for you - findAll(), findById(),
 * save(), deleteById(), etc. - with no SQL and no method bodies written
 * here. This fully replaces the Map<Long, Student> that Exam 1 used for
 * in-memory storage; every read/write now goes through MySQL via this
 * interface instead of an in-process collection.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}