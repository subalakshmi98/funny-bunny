package com.scheduling.maplewood.Service.ScheduleGenerator;

import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.CourseRepository;
import com.scheduling.maplewood.Repository.CourseSectionRepository;
import com.scheduling.maplewood.Repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseOfferingService {

    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;
    private final CourseSectionRepository courseSectionRepository;

    /**
     * Get all courses for a given semester.
     * 
     * @param semesterId the semester's id
     * @return a list of courses for the given semester
     * @throws RuntimeException if the semester is not found
     */
    public List<Course> getCoursesForSemester(Integer semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found: " + semesterId));

        return courseRepository.findAll().stream()
                .filter(c -> Objects.equals(c.getSemesterOrder(), semester.getOrderInYear()))
                .collect(Collectors.toList());
    }

    /**
     * Deletes all existing sections for a given semester.
     * This is used to clear out old sections before generating new ones.
     * 
     * @param semesterId the semester's id
     */
    public void clearExistingSections(Integer semesterId) {
        List<CourseSection> existing = courseSectionRepository.findBySemesterId(semesterId);
        existing.forEach(courseSectionRepository::delete);
    }

    /**
     * Creates a new CourseSection with the given course, teacher, room, and semester id.
     * The section's capacity is set to the minimum of the room's capacity and 10.
     * The section is then saved to the database.
     * 
     * @param course the course to associate with the section
     * @param teacher the teacher to associate with the section
     * @param room the room to associate with the section
     * @param semesterId the semester id to associate with the section
     * @return the created CourseSection
     */
    public CourseSection createSection(Course course, Teacher teacher, Classroom room, Integer semesterId) {

        CourseSection section = new CourseSection();
        section.setCourseId(course.getId());
        section.setTeacherId(teacher.getId());
        section.setRoomId(room.getId());
        section.setSemesterId(semesterId);

        Integer cap = room.getCapacity() == null ? 10 : Math.min(room.getCapacity(), 10);
        section.setCapacity(cap);

        courseSectionRepository.save(section);
        return section;
    }
}
