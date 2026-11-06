package com.example.studentmanagement.dto;

import jakarta.validation.constraints.*;
public class StudentRequest {
    @NotBlank private String firstName;
    @NotBlank private String lastName;
    @NotBlank @Email private String email;
    @Min(0) @Max(120) private Integer age;
    @NotNull private Long departmentId;

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public Integer getAge() { return age; }
    public Long getDepartmentId() { return departmentId; }
}