package com.example.courses.repositories;

import com.example.courses.models.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository
        extends CrudRepository<Category, Long> {
}