package com.example.curiolearn.course.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private boolean published;
    private String instructorName;
    private Set<LessonResponse> lessons;
}
