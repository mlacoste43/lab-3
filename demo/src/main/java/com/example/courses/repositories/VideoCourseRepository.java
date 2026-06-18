package com.example.courses.repositories;

import com.example.courses.models.VideoCourse;
import org.springframework.data.repository.CrudRepository;

public interface VideoCourseRepository
        extends CrudRepository<VideoCourse, Long> {
}