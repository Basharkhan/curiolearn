package com.example.curiolearn.course.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonResponse {
    private Long id;
    private String title;
    private String content;
    private int orderIndex;
}
