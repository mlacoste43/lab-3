package com.example.courses.repositories;

import com.example.courses.models.OnlineCourse;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository
        extends CrudRepository<OnlineCourse, Long> {
}