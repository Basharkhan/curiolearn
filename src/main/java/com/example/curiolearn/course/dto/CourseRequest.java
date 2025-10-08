package com.example.curiolearn.course.dto;

import lombok.Data;

@Data
public class CourseRequest {
    private String title;
    private String description;
    private boolean published;
}
