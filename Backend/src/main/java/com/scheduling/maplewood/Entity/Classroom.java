package com.scheduling.maplewood.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "classrooms")
@Data
public class Classroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name="room_type_id")
    private Integer roomTypeId;

    private Integer capacity;

    private String equipment;

    private Integer floor;

    @Column(name="created_at")
    private LocalDateTime createdAt;
}
