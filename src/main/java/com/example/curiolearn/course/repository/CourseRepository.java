package com.example.curiolearn.course.repository;

import com.example.curiolearn.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByIdAndInstructorEmail(Long id, String email);
}
