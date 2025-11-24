package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_course_history")
@Data
public class StudentCourseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="student_id")
    private Integer studentId;

    @Column(name="course_id")
    private Integer courseId;

    @Column(name="semester_id")
    private Integer semesterId;

    private String status;   // passed / failed

    @Column(name="created_at")
    private LocalDateTime createdAt;
}
