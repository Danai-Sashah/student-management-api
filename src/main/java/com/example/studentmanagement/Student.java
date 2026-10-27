package com.example.studentmanagement;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Student is a plain model class (a "POJO" - Plain Old Java Object).
 * It just holds data and has no Spring annotations of its own, because
 * Jackson (the JSON library Spring Boot uses under the hood) only needs
 * a no-args constructor plus getters/setters to convert this object
 * to/from JSON automatically. You never call toJson()/fromJson() yourself.
 *
 * The @NotBlank/@Email/@Min/@Max annotations below are from Jakarta
 * Bean Validation. They don't do anything by themselves - they are just
 * metadata. It's StudentController's job to tell Spring to actually check
 * them, which it does with @Valid on the @RequestBody parameter.
 */
public class Student {
    private Long id;

    @NotBlank(message = "firstName is required")
    private String firstName;

    @NotBlank(message = "lastName is required")
    private String lastName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    private String email;

    @NotNull(message = "age is required")
    @Min(value = 16, message = "age must be at least 16")
    @Max(value = 100, message = "age must be at most 100")
    private Integer age;

    @NotBlank(message = "course is required")
    private String course;

    // Spring/Jackson needs this empty constructor to build a Student
    // before filling it in field-by-field from incoming JSON.
    public Student() {

    }

    public Student(Long id, String firstName, String lastName, String email, Integer age, String course) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.age = age;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }
}
