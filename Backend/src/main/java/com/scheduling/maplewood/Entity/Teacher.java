package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="teachers")
@Data
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="specialization_id")
    private Integer specializationId;

    private String email;

    @Column(name="max_daily_hours")
    private Integer maxDailyHours;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}
