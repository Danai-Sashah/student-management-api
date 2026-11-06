package com.example.studentmanagement.dto;

import com.example.studentmanagement.Course;
import com.example.studentmanagement.Student;

import java.util.List;

public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Integer age;
    private Long departmentId;
    private String departmentName;
    private List<String> courseCodes;

    public StudentResponse(Student s) {
        this.id = s.getId();
        this.firstName = s.getFirstName();
        this.lastName = s.getLastName();
        this.email = s.getEmail();
        this.age = s.getAge();
        this.departmentId = s.getDepartment().getId();
        this.departmentName = s.getDepartment().getName();
        this.courseCodes = s.getCourses().stream().map(Course::getCode).toList();
    }

    public Long getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public Long getDepartmentId() { return departmentId; }
    public String getDepartmentName() { return departmentName; }
    public List<String> getCourseCodes() { return courseCodes; }
}