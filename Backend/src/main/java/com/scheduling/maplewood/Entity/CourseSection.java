package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="course_sections")
@Data
public class CourseSection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="course_id")
    private Integer courseId;

    @Column(name="teacher_id")
    private Integer teacherId;

    @Column(name="room_id")
    private Integer roomId;

    @Column(name="semester_id")
    private Integer semesterId;

    private Integer capacity;
}
