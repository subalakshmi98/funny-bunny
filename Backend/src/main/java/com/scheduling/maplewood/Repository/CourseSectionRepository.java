package com.scheduling.maplewood.Repository;

import com.scheduling.maplewood.Entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, Integer> {
    List<CourseSection> findBySemesterId(Integer semesterId);
    List<CourseSection> findByCourseIdAndSemesterId(Integer courseId, Integer semesterId);
}
