package com.scheduling.maplewood.Service.Enroll;

import com.scheduling.maplewood.Dto.EnrollmentRequest;
import com.scheduling.maplewood.Dto.EnrollmentResponse;
import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Exception.EnrollmentException;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentCourseHistoryRepository studentCourseHistoryRepository;

    private static final int MAX_COURSES_PER_SEMESTER = 5;

    /**
     * Enroll a student in a course.
     *
     * @param req the enrollment request with studentId, courseId, and semesterId
     * @return an EnrollmentResponse object with success and message
     * @throws EnrollmentException if studentId, courseId, or semesterId is null
     */
    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest req) {

        Integer studentId = req.getStudentId();
        Integer courseId = req.getCourseId();
        Integer semesterId = req.getSemesterId();

        if (studentId == null || courseId == null || semesterId == null) {
            throw new EnrollmentException("studentId, courseId and semesterId are required.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EnrollmentException("Student not found: " + studentId));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EnrollmentException("Course not found: " + courseId));

        List<CourseSection> sections = courseSectionRepository.findByCourseIdAndSemesterId(courseId, semesterId);
        if (sections.isEmpty()) {
            throw new EnrollmentException("No section available for this course in the selected semester.");
        }

        CourseSection chosen = null;
        for (CourseSection cs : sections) {
            int enrolled = studentEnrollmentRepository.findBySectionId(cs.getId()).size();
            if (cs.getCapacity() == null || enrolled < cs.getCapacity()) {
                chosen = cs;
                break;
            }
        }
        if (chosen == null) chosen = sections.get(0);
        Integer sectionId = chosen.getId();

        List<StudentCourseHistory> history = studentCourseHistoryRepository.findByStudentId(studentId);

        validateNotPassedPreviously(history, courseId, semesterId);
        validateNotAlreadyEnrolled(studentId, courseId, semesterId);
        validateSectionCapacity(sectionId, chosen.getCapacity());
        validateMaxSemesterCourses(studentId, semesterId);
        validatePrerequisites(studentId, courseId);
        validateNoTimeConflict(studentId, sectionId);

        StudentEnrollment enrollment = new StudentEnrollment();
        enrollment.setStudentId(studentId);
        enrollment.setCourseId(courseId);
        enrollment.setSemesterId(semesterId);
        enrollment.setSectionId(sectionId);
        studentEnrollmentRepository.save(enrollment);

        return new EnrollmentResponse(true, "Enrolled successfully.");
    }

    /**
     * Validate that the student has not passed the course previously.
     * 
     * @param history the student's course history
     * @param courseId the course's id
     * @param semesterId the semester's id
     * @throws EnrollmentException if the student has already passed the course earlier
     */
    private void validateNotPassedPreviously(List<StudentCourseHistory> history, Integer courseId, Integer semesterId) {
        boolean passed = history.stream()
                .filter(h -> "passed".equalsIgnoreCase(h.getStatus()))
                .anyMatch(h -> Objects.equals(h.getCourseId(), courseId)
                        && h.getSemesterId() != null && h.getSemesterId() <= semesterId);
        if (passed) throw new EnrollmentException("Student already passed this course earlier.");
    }

    /**
     * Validate that the student is not already enrolled in the course this semester.
     * 
     * @param studentId the student's id
     * @param courseId the course's id
     * @param semesterId the semester's id
     * @throws EnrollmentException if the student is already enrolled
     */
    private void validateNotAlreadyEnrolled(Integer studentId, Integer courseId, Integer semesterId) {
        boolean exists = studentEnrollmentRepository.findByStudentId(studentId)
                .stream()
                .anyMatch(e -> Objects.equals(e.getCourseId(), courseId)
                        && Objects.equals(e.getSemesterId(), semesterId));
        if (exists) throw new EnrollmentException("Student already enrolled in this course this semester.");
    }

    /**
     * Validates that the student has not exceeded the section's capacity.
     *
     * @param sectionId the section's id
     * @param capacity the section's capacity
     * @throws EnrollmentException if the section is full
     */
    private void validateSectionCapacity(Integer sectionId, Integer capacity) {
        int enrolled = studentEnrollmentRepository.findBySectionId(sectionId).size();
        if (capacity != null && enrolled >= capacity) throw new EnrollmentException("Section is full.");
    }

    /**
     * Validates that the student has not exceeded the maximum number of courses for this semester.
     * 
     * @param studentId the student's id
     * @param semesterId the semester's id
     * @throws EnrollmentException if the student has exceeded the maximum number of courses for this semester
     */
    private void validateMaxSemesterCourses(Integer studentId, Integer semesterId) {
        long count = studentEnrollmentRepository.findByStudentId(studentId)
                .stream()
                .filter(e -> Objects.equals(e.getSemesterId(), semesterId))
                .count();
        if (count >= MAX_COURSES_PER_SEMESTER) throw new EnrollmentException("Student reached maximum courses for this semester.");
    }

    /**
     * Validates that the student has satisfied the prerequisites for a given course.
     *
     * @param studentId the student's id
     * @param courseId the course's id
     * @throws EnrollmentException if the prerequisites are not satisfied
    */
    private void validatePrerequisites(Integer studentId, Integer courseId) {
        if (!prerequisitesSatisfied(studentId, courseId)) {
            Course c = courseRepository.findById(courseId).orElse(null);
            throw new EnrollmentException("Prerequisites not satisfied for course: " + (c != null ? c.getCode() : courseId));
        }
    }

    /**
     * Recursively checks if the student has satisfied the prerequisites for a given course.
     *
     * @param studentId the student's id
     * @param courseId the course's id
     * @return true if the student has satisfied the prerequisites, false otherwise
     */
    private boolean prerequisitesSatisfied(Integer studentId, Integer courseId) {
        Course c = courseRepository.findById(courseId).orElse(null);
        if (c == null) return false;
        Integer prereq = c.getPrerequisiteId();
        if (prereq == null) return true;
        boolean passed = studentCourseHistoryRepository.findByStudentIdAndStatus(studentId, "passed")
                .stream().anyMatch(h -> Objects.equals(h.getCourseId(), prereq));
        if (passed) return true;
        return prerequisitesSatisfied(studentId, prereq);
    }

    /**
     * Validates that there is no time conflict between the given section and the student's existing schedule.
     *
     * @param studentId the student's id
     * @param sectionId the section's id
     * @throws EnrollmentException if there is a time conflict
     */
    private void validateNoTimeConflict(Integer studentId, Integer sectionId) {
        List<SectionMeeting> target = sectionMeetingRepository.findBySectionId(sectionId);
        List<SectionMeeting> current = studentEnrollmentRepository.findByStudentId(studentId)
                .stream()
                .flatMap(e -> sectionMeetingRepository.findBySectionId(e.getSectionId()).stream())
                .collect(Collectors.toList());
        if (hasTimeConflict(target, current)) throw new EnrollmentException("Time conflict with existing schedule.");
    }

    /**
     * Checks if there is a time conflict between two lists of section meetings.
     *
     * A time conflict is when two section meetings occur on the same day and
     * overlap in time.
     *
     * @param target the list of section meetings to check for conflicts
     * @param current the list of section meetings that may have conflicts with the target
     * @return true if there is a time conflict, false otherwise
     */
    private boolean hasTimeConflict(List<SectionMeeting> target, List<SectionMeeting> current) {
        for (SectionMeeting t : target) {
            for (SectionMeeting s : current) {
                if (!t.getDayOfWeek().equalsIgnoreCase(s.getDayOfWeek())) continue;
                LocalTime tStart = LocalTime.parse(t.getStartTime());
                LocalTime tEnd = LocalTime.parse(t.getEndTime());
                LocalTime sStart = LocalTime.parse(s.getStartTime());
                LocalTime sEnd = LocalTime.parse(s.getEndTime());
                if (tStart.isBefore(sEnd) && tEnd.isAfter(sStart)) return true;
            }
        }
        return false;
    }
}
