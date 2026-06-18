package com.example.courses.repositories;

import com.example.courses.models.InteractiveCourse;
import org.springframework.data.repository.CrudRepository;

public interface InteractiveCourseRepository
        extends CrudRepository<InteractiveCourse, Long> {
}