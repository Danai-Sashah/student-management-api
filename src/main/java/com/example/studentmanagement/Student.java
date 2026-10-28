package com.example.studentmanagement;

import jakarta.persistence.*;

/**
 * Student is now a JPA *entity*, not a plain POJO. @Entity tells Spring Data
 * JPA "this class maps to a database table"; @Table(name = "students") names
 * that table explicitly.
 *
 * ORIGINAL (Exam 1 version) - kept here so you can see what changed:
 *
 *   public class Student {
 *       private Long id;
 *       @NotBlank(message = "firstName is required") private String firstName;
 *       @NotBlank @Email private String email;
 *       @NotNull @Min(16) @Max(100) private Integer age;
 *       ...
 *   }
 *
 * Why this changed: Exam 1's @NotBlank/@Email/@Min/@Max are Jakarta *Bean
 * Validation* annotations - they run in Java, before anything touches the
 * database, and only do something because @Valid on the controller's
 * @RequestBody tells Spring to check them (see StudentController#create).
 * They were dropped in favour of the @Column(nullable = ..., unique = ...)
 * constraints below, which are enforced by MySQL itself instead of Java.
 * The two aren't interchangeable: without a Bean Validation annotation, a
 * bad value (e.g. a blank firstName) now fails as a database constraint
 * violation (a less friendly 500-style error) instead of @Valid's clean
 * 400. Re-adding both together - Bean Validation for a nice 400, @Column
 * as the guaranteed last line of defence - is the usual real-world setup.
 * See docs/marking/REVIEW_2.md.
 */
@Entity
@Table(name = "students")
public class Student {

    // @Id marks this as the primary key column.
    // @GeneratedValue(strategy = GenerationType.IDENTITY) hands id
    // generation to MySQL's AUTO_INCREMENT instead of Java - the database
    // decides the next id, not application code. This is what makes the
    // exam's "IDs must not be supplied when creating a student" rule
    // enforceable: the controller just sets id to null before saving
    // (see StudentController#create) and MySQL fills it in on insert.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // nullable = false adds a NOT NULL constraint on this column, matching
    // the spec's "Required" for firstName/lastName/age/course.
    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    // unique = true additionally adds a UNIQUE constraint, matching the
    // spec's "email: Required, unique".
    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String course;

    // Hibernate needs a no-args constructor so it can create a Student
    // instance via reflection and then populate its fields from a
    // database row - it never calls the all-args constructor you'd use
    // in application code.
    public Student() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
}