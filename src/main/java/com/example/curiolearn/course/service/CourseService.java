package com.example.curiolearn.course.service;

import com.example.curiolearn.course.dto.CourseRequest;
import com.example.curiolearn.course.dto.CourseResponse;
import com.example.curiolearn.course.dto.LessonResponse;
import com.example.curiolearn.course.entity.Course;
import com.example.curiolearn.course.entity.Lesson;
import com.example.curiolearn.course.repository.CourseRepository;
import com.example.curiolearn.course.repository.LessonRepository;
import com.example.curiolearn.exception.ResourceNotFoundException;
import com.example.curiolearn.user.entity.User;
import com.example.curiolearn.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public CourseResponse createCourse(CourseRequest request, String instructorEmail) {
        User instructor = userRepository.findByEmail(instructorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Instructor not found with email: " + instructorEmail));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .isPublished(request.isPublished())
                .instructor(instructor)
                .build();

        Course savedCourse = courseRepository.save(course);
        return mapToResponse(savedCourse);
    }

    public CourseResponse updateCourse(Long courseId, CourseRequest request, String instructorEmail) {
        Course course = getOwnedCourse(courseId, instructorEmail);
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPublished(request.isPublished());

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    private Course getOwnedCourse(Long courseId, String instructorEmail) {
        return courseRepository.findByIdAndInstructorEmail(courseId, instructorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found or you are not the owner."));
    }

    private CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .published(course.isPublished())
                .instructorName(course.getInstructor().getFullName())
                .lessons(course.getLessons()
                        .stream()
                        .map(this::mapLessonToResponse)
                        .collect(Collectors.toSet()))
                .build();
    }

    private LessonResponse mapLessonToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .orderIndex(lesson.getOrderIndex())
                .build();
    }
}
