package com.scheduling.maplewood.Repository;

import com.scheduling.maplewood.Entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Integer> {
    Teacher findByEmail(String email);
}
