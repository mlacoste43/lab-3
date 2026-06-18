package com.example.courses.controllers;

import com.example.courses.models.*;
import com.example.courses.repositories.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CourseController {

    private final VideoCourseRepository videoRepo;
    private final InteractiveCourseRepository interactiveRepo;
    private final CategoryRepository categoryRepo;
    private final StudentRepository studentRepo;

    public CourseController(VideoCourseRepository v,
                            InteractiveCourseRepository i,
                            CategoryRepository c,
                            StudentRepository s) {
        this.videoRepo = v;
        this.interactiveRepo = i;
        this.categoryRepo = c;
        this.studentRepo = s;
    }


    @GetMapping("/")
    public String home(Model model) {

        // Создаем или загружаем категории
        Category programming = null;
        Category databases = null;
        Category algorithms = null;
        
        if (categoryRepo.count() == 0) {
            // Если категорий нет - создаем
            programming = new Category();
            programming.setName("Программирование");
            categoryRepo.save(programming);
            
            databases = new Category();
            databases.setName("Базы данных");
            categoryRepo.save(databases);
            
            algorithms = new Category();
            algorithms.setName("Алгоритмы");
            categoryRepo.save(algorithms);
        } else {
            // Если категории уже есть - загружаем их из базы
            Iterable<Category> allCategories = categoryRepo.findAll();
            for (Category cat : allCategories) {
                if (cat.getName().equals("Программирование")) {
                    programming = cat;
                } else if (cat.getName().equals("Базы данных")) {
                    databases = cat;
                } else if (cat.getName().equals("Алгоритмы")) {
                    algorithms = cat;
                }
            }
        }

        if (videoRepo.count() == 0) {
            VideoCourse v1 = new VideoCourse();
            v1.setTitle("Java Basics");
            v1.setDuration(120);
            v1.setResolution("720p");
            v1.setSubtitles(false);
            v1.setRunning(false);
            v1.setDifficulty("Легкий");
            v1.setCategory(programming);

            VideoCourse v2 = new VideoCourse();
            v2.setTitle("Spring Boot");
            v2.setDuration(200);
            v2.setResolution("1080p");
            v2.setSubtitles(true);
            v2.setRunning(false);
            v2.setDifficulty("Средний");
            v2.setCategory(programming);

            videoRepo.save(v1);
            videoRepo.save(v2);
        }

        if (interactiveRepo.count() == 0) {
            InteractiveCourse i1 = new InteractiveCourse();
            i1.setTitle("SQL Trainer");
            i1.setDuration(90);
            i1.setProgress(0);
            i1.setRunning(false);
            i1.setDifficulty("Средний");
            i1.setCategory(databases);

            InteractiveCourse i2 = new InteractiveCourse();
            i2.setTitle("Algorithms");
            i2.setDuration(150);
            i2.setProgress(40);
            i2.setRunning(false);
            i2.setDifficulty("Сложный");
            i2.setCategory(algorithms);

            interactiveRepo.save(i1);
            interactiveRepo.save(i2);
        } else {
            // Если курсы уже есть, но у них нет категории - обновляем
            for (VideoCourse course : videoRepo.findAll()) {
                if (course.getCategory() == null) {
                    course.setCategory(programming);
                    videoRepo.save(course);
                }
            }
            for (InteractiveCourse course : interactiveRepo.findAll()) {
                if (course.getCategory() == null) {
                    if (course.getTitle().equals("SQL Trainer")) {
                        course.setCategory(databases);
                    } else if (course.getTitle().equals("Algorithms")) {
                        course.setCategory(algorithms);
                    }
                    interactiveRepo.save(course);
                }
            }
        }

        Student student = studentRepo.findById(1L).orElse(null);

        if (student == null) {
            student = new Student();
            student.setName("Alex");
            studentRepo.save(student);
        }

        model.addAttribute("videos", videoRepo.findAll());
        model.addAttribute("interactive", interactiveRepo.findAll());
        model.addAttribute("myCourses", student.getCourses());

        return "index";
    }

    @GetMapping("/videoInfo/{id}")
    @ResponseBody
    public String videoInfo(@PathVariable Long id) {
        return videoRepo.findById(id)
                .map(VideoCourse::getInfo)
                .orElse("Курс не найден");
    }

    @GetMapping("/interactiveInfo/{id}")
    @ResponseBody
    public String interactiveInfo(@PathVariable Long id) {
        return interactiveRepo.findById(id)
                .map(InteractiveCourse::getInfo)
                .orElse("Курс не найден");
    }

    @GetMapping("/course/{id}")
    public String openCourse(@PathVariable Long id, Model model) {
        OnlineCourse course = findCourseById(id);
        if (course == null) {
            return "redirect:/";
        }
        model.addAttribute("course", course);
        return "course";
    }

    @GetMapping("/addVideoToStudent/{id}")
    public String addVideo(@PathVariable Long id) {
        Student student = studentRepo.findById(1L).orElseThrow();
        VideoCourse course = videoRepo.findById(id).orElse(null);
        if (course != null) {
            boolean alreadyHasCourse = student.getCourses().stream()
                    .anyMatch(c -> c.getId().equals(course.getId()));
            if (!alreadyHasCourse) {
                student.getCourses().add(course);
                studentRepo.save(student);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/addInteractiveToStudent/{id}")
    public String addInteractive(@PathVariable Long id) {
        Student student = studentRepo.findById(1L).orElseThrow();
        InteractiveCourse course = interactiveRepo.findById(id).orElse(null);
        if (course != null) {
            boolean alreadyHasCourse = student.getCourses().stream()
                    .anyMatch(c -> c.getId().equals(course.getId()));
            if (!alreadyHasCourse) {
                student.getCourses().add(course);
                studentRepo.save(student);
            }
        }
        return "redirect:/";
    }

    @GetMapping("/removeCourse/{id}")
    public String removeCourse(@PathVariable Long id) {
        Student student = studentRepo.findById(1L).orElseThrow();
        OnlineCourse course = findCourseById(id);
        if (course != null) {
            course.setRunning(false);
            if (course instanceof VideoCourse) {
                videoRepo.save((VideoCourse) course);
            } else if (course instanceof InteractiveCourse) {
                interactiveRepo.save((InteractiveCourse) course);
            }
            student.getCourses().removeIf(c -> c.getId().equals(id));
            studentRepo.save(student);
        }
        return "redirect:/";
    }

    @GetMapping("/courseInfo/{id}")
    public String courseInfo(@PathVariable Long id, Model model) {
        OnlineCourse course = findCourseById(id);
        if (course == null) {
            return "redirect:/";
        }
        model.addAttribute("course", course);
        return "info";
    }

    @GetMapping("/course/start/{id}")
    public String startCourse(@PathVariable Long id) {
        OnlineCourse course = findCourseById(id);
        if (course != null && !course.isRunning()) {
            course.start();
            if (course instanceof VideoCourse) {
                videoRepo.save((VideoCourse) course);
            } else if (course instanceof InteractiveCourse) {
                interactiveRepo.save((InteractiveCourse) course);
            }
        }
        return "redirect:/course/" + id;
    }

    @GetMapping("/course/toggleSubtitles/{id}")
    public String toggleSubtitles(@PathVariable Long id) {
        VideoCourse course = videoRepo.findById(id).orElse(null);
        if (course != null) {
            course.toggleSubtitles();
            videoRepo.save(course);
        }
        return "redirect:/course/" + id;
    }

    @GetMapping("/course/nextResolution/{id}")
    public String nextResolution(@PathVariable Long id) {
        VideoCourse course = videoRepo.findById(id).orElse(null);
        if (course != null) {
            course.nextResolution();
            videoRepo.save(course);
        }
        return "redirect:/course/" + id;
    }

    @GetMapping("/course/getProgress/{id}")
    @ResponseBody
    public String getProgress(@PathVariable Long id) {
        InteractiveCourse course = interactiveRepo.findById(id).orElse(null);
        if (course != null) {
            return String.valueOf(course.getProgress());
        }
        return "0";
    }

    @PostMapping("/course/completeModule/{id}")
    @ResponseBody
    public String completeModule(@PathVariable Long id) {

        InteractiveCourse course =
                interactiveRepo.findById(id).orElse(null);

        if (course == null) {
            return "Курс не найден";
        }

        if (course.getProgress() >= 100) {
            return "Курс уже полностью пройден!";
        }

        course.completeModule();

        interactiveRepo.save(course);

        return "Модуль пройден! Прогресс: "
                + course.getProgress() + "%";
    }

    @GetMapping("/course/getInfoText/{id}")
    @ResponseBody
    public String getCourseInfoText(@PathVariable Long id) {
        OnlineCourse course = findCourseById(id);
        if (course != null) {
            return course.getInfo();
        }
        return "Курс не найден";
    }

    @GetMapping("/search/courseStudents")
    public String searchCourseStudents(@RequestParam Long courseId, Model model) {
        OnlineCourse course = findCourseById(courseId);

        if (course == null) {
            model.addAttribute("error", "Курс с ID " + courseId + " не найден");
            model.addAttribute("videos", videoRepo.findAll());
            model.addAttribute("interactive", interactiveRepo.findAll());
            model.addAttribute("myCourses", new ArrayList<>());
            return "index";
        }

        model.addAttribute("searchType", "students");
        model.addAttribute("searchTarget", course.getTitle());
        model.addAttribute("students", course.getStudents());
        model.addAttribute("course", course);

        model.addAttribute("videos", videoRepo.findAll());
        model.addAttribute("interactive", interactiveRepo.findAll());

        return "search-results";
    }

    @GetMapping("/search/studentCourses")
    public String searchStudentCourses(@RequestParam Long studentId, Model model) {
        Student student = studentRepo.findById(studentId).orElse(null);
        
        if (student == null) {
            model.addAttribute("error", "Студент с ID " + studentId + " не найден");
            model.addAttribute("videos", videoRepo.findAll());
            model.addAttribute("interactive", interactiveRepo.findAll());
            model.addAttribute("myCourses", studentRepo.findById(1L).orElseThrow().getCourses());
            return "index";
        }
        
        model.addAttribute("searchType", "courses");
        model.addAttribute("searchTarget", student.getName());
        model.addAttribute("courses", student.getCourses());
        model.addAttribute("student", student);
        
        // Сохраняем основные данные для отображения навигации
        model.addAttribute("videos", videoRepo.findAll());
        model.addAttribute("interactive", interactiveRepo.findAll());
        model.addAttribute("myCourses", studentRepo.findById(1L).orElseThrow().getCourses());
        
        return "search-results";
    }
    
    private OnlineCourse findCourseById(Long id) {
        OnlineCourse course = videoRepo.findById(id).map(c -> (OnlineCourse) c).orElse(null);
        if (course == null) {
            course = interactiveRepo.findById(id).map(c -> (OnlineCourse) c).orElse(null);
        }
        return course;
    }
}