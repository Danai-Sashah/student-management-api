package com.example.studentmanagement.dto;

import com.example.studentmanagement.Department;

public class DepartmentResponse {
    private Long id;
    private String name;

    public DepartmentResponse(Department d) {
        this.id = d.getId();
        this.name = d.getName();
    }
    public Long getId() { return id; }
    public String getName() { return name; }
}