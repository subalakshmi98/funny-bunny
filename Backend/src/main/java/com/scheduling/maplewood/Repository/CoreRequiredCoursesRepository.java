package com.scheduling.maplewood.Repository;

import com.scheduling.maplewood.Entity.CoreRequiredCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoreRequiredCoursesRepository extends JpaRepository<CoreRequiredCourse, Integer> {
}