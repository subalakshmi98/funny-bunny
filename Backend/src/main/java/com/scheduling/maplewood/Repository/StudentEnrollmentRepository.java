package com.scheduling.maplewood.Repository;

import com.scheduling.maplewood.Entity.StudentEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Integer> {
    List<StudentEnrollment> findByStudentId(Integer studentId);
    List<StudentEnrollment> findByCourseId(Integer courseId);
    List<StudentEnrollment> findBySectionId(Integer sectionId);
}
