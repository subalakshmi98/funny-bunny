package com.scheduling.maplewood.Repository;

import com.scheduling.maplewood.Entity.StudentCourseHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentCourseHistoryRepository extends JpaRepository<StudentCourseHistory, Integer> {
    List<StudentCourseHistory> findByStudentId(Integer studentId);
    List<StudentCourseHistory> findByStudentIdAndStatus(Integer studentId, String status);
}
