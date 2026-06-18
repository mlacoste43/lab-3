package com.example.courses.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class InteractiveCourse extends OnlineCourse {

    private int progress;

    public void completeModule() {

        if (progress < 100) {
            progress += 20;
        }

        if (progress > 100) {
            progress = 100;
        }
    }

    public String getProgressStatus() {

        if (progress >= 100) {
            return "Пройден";
        }

        if (!isRunning()) {
            return "Не начат";
        }

        return "В процессе";
    }

    @Override
    public String getInfo() {
        return super.getInfo() +
                ", Тип: Интерактивный курс";
    }
}