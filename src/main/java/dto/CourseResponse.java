package com.example.studentmanagement.dto;

import com.example.studentmanagement.Course;

public class CourseResponse {
    private Long id;
    private String code;
    private String title;
    private Integer credits;

    public CourseResponse(Course c) {
        this.id = c.getId();
        this.code = c.getCode();
        this.title = c.getTitle();
        this.credits = c.getCredits();
    }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public Integer getCredits() { return credits; }
}