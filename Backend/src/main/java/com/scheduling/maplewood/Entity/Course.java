package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
@Data
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;
    private String name;
    private String description;

    private BigDecimal credits;

    @Column(name="hours_per_week")
    private Integer hoursPerWeek;

    @Column(name="specialization_id")
    private Integer specializationId;

    @Column(name="prerequisite_id")
    private Integer prerequisiteId;

    @Column(name="course_type")
    private String courseType;

    @Column(name="grade_level_min")
    private Integer gradeLevelMin;

    @Column(name="grade_level_max")
    private Integer gradeLevelMax;

    @Column(name="semester_order")
    private Integer semesterOrder;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}
