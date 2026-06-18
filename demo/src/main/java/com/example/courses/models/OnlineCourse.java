package com.example.courses.models;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "online_course")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class OnlineCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private int duration;

    private boolean running;

    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();
    

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String difficulty;

    public void start() {
        running = true;
    }

    public String getInfo() {
        return "Курс: " + title +
                ", Длительность: " + duration + " мин" +
                ", Сложность: " + (difficulty != null ? difficulty : "Не указана");
    }
}