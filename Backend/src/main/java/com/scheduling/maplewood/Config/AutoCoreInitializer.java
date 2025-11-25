package com.scheduling.maplewood.Config;

import com.scheduling.maplewood.Entity.CoreRequiredCourse;
import com.scheduling.maplewood.Entity.Course;
import com.scheduling.maplewood.Repository.CoreRequiredCoursesRepository;
import com.scheduling.maplewood.Repository.CourseRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class AutoCoreInitializer {

    private final CoreRequiredCoursesRepository coreRequiredCoursesRepository;
    private final CourseRepository courseRepository;

    /**
     * Initialize core courses.
     * 
     * This method is used to initialize the core courses table.
     * It only runs if the core table is empty.
     * It must run after courses are loaded.
     */
    @EventListener(ApplicationReadyEvent.class)  // runs only after full startup
    @Transactional
    public void initializeCoreCourses() {

        // Only run if core table is empty
        if (coreRequiredCoursesRepository.count() > 0) {
            return;
        }

        // Must run AFTER courses are loaded
        if (courseRepository.count() == 0) {
            return;
        }

        List<Course> coreCourses = courseRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(Course::getId))
                .limit(20)
                .toList();

        for (Course c : coreCourses) {
            CoreRequiredCourse entry = new CoreRequiredCourse();
            entry.setCourseId(c.getId());
            coreRequiredCoursesRepository.save(entry);
        }
    }
}