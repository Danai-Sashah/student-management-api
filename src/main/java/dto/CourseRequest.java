package com.example.studentmanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseRequest {
    @NotBlank private String code;
    @NotBlank private String title;
    @NotNull private Integer credits;

    public String getCode() { return code; }
    public String getTitle() { return title; }
    public Integer getCredits() { return credits; }
}