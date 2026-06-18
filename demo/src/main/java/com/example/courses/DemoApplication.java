package com.example.courses;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.example.courses.models"})  
@EnableJpaRepositories(basePackages = {"com.example.courses.repositories"}) 
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
		System.out.println("Приложение успешно запущено!");
		System.out.println("Проверьте: http://localhost:8080");
	}

	@Bean
	public CommandLineRunner test() {
		return args -> {
			System.out.println(">>> CommandLineRunner выполнен!");
			System.out.println(">>> Приложение работает и готово к использованию!");
		};
	}
}