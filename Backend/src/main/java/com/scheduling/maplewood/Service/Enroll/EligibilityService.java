package com.scheduling.maplewood.Service.Enroll;
import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    private final CourseSectionRepository courseSectionRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final StudentCourseHistoryRepository studentCourseHistoryRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;

    /**
     * Gets all eligible sections for a given student and semester.
     * @param studentId the student's id
     * @param semesterId the semester's id
     * @return a map containing the student's information and a list of eligible sections
     */
    public Map<String, Object> getEligibleSections(Integer studentId, Integer semesterId) {


        Student student = studentRepository.findById(studentId).orElse(null);
        List<CourseSection> allSections = courseSectionRepository.findBySemesterId(semesterId);

        List<StudentEnrollment> studentEnrollments = studentEnrollmentRepository.findByStudentId(studentId);
        Set<Integer> enrolledSectionIds = studentEnrollments.stream()
                .map(StudentEnrollment::getSectionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        List<SectionMeeting> currentMeetings = enrolledSectionIds.stream()
                .flatMap(id -> sectionMeetingRepository.findBySectionId(id).stream())
                .collect(Collectors.toList());

        List<StudentCourseHistory> history = studentCourseHistoryRepository.findByStudentId(studentId);

        List<Map<String, Object>> eligibleFormatted = new ArrayList<>();

        for (CourseSection cs : allSections) {

            if (enrolledSectionIds.contains(cs.getId())) continue;

            boolean passedBefore = history.stream()
                    .filter(h -> "passed".equalsIgnoreCase(h.getStatus()))
                    .anyMatch(h ->
                            Objects.equals(h.getCourseId(), cs.getCourseId()) &&
                                    h.getSemesterId() != null &&
                                    h.getSemesterId() <= semesterId
                    );
            if (passedBefore) continue;


            int enrolledCount = studentEnrollmentRepository.findBySectionId(cs.getId()).size();
            if (cs.getCapacity() != null && enrolledCount >= cs.getCapacity()) continue;

            if (!prerequisitesSatisfied(studentId, cs.getCourseId())) continue;

            List<SectionMeeting> sectionMeetings = sectionMeetingRepository.findBySectionId(cs.getId());
            if (hasTimeConflict(sectionMeetings, currentMeetings)) continue;

            eligibleFormatted.add(mapSectionToDetailedItem(cs));
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("message", eligibleFormatted.isEmpty()
                ? "No eligible sections available for this student."
                : "Eligible sections found.");

        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("id", studentId);
        studentInfo.put("name", student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown");
        studentInfo.put("email", student != null ? student.getEmail() : "Unknown");
        studentInfo.put("gradeLevel", student != null ? student.getGradeLevel() : "Unknown");

        out.put("student", studentInfo);
        out.put("sections", eligibleFormatted);

        return out;
    }

    /**
     * Recursively checks if the student has satisfied the prerequisites for a given course.
     *
     * @param studentId the student ID to check
     * @param courseId the course ID to check
     * @return true if the student has satisfied the prerequisites, false otherwise
     */
    private boolean prerequisitesSatisfied(Integer studentId, Integer courseId) {

        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) return false;

        Integer prereqId = course.getPrerequisiteId();
        if (prereqId == null) return true;

        boolean passed = studentCourseHistoryRepository.findByStudentIdAndStatus(studentId, "passed")
                .stream().anyMatch(h -> Objects.equals(h.getCourseId(), prereqId));

        if (passed) return true;

        return prerequisitesSatisfied(studentId, prereqId);
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

        if (target == null || target.isEmpty() || current == null || current.isEmpty()) return false;

        for (SectionMeeting t : target) {
            for (SectionMeeting s : current) {
                if (t.getDayOfWeek() == null || s.getDayOfWeek() == null) continue;
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


    /**
     * Maps a CourseSection to a detailed item containing its section ID, course ID, course name, teacher name, email, room name, schedule, and student count.
     *
     * @param cs the CourseSection to map
     * @return a map containing the detailed item
     */
    private Map<String, Object> mapSectionToDetailedItem(CourseSection cs) {
        Course course = courseRepository.findById(cs.getCourseId()).orElse(null);
        Teacher teacher = teacherRepository.findById(cs.getTeacherId()).orElse(null);
        Classroom room = classroomRepository.findById(cs.getRoomId()).orElse(null);
        List<SectionMeeting> meetings = sectionMeetingRepository.findBySectionId(cs.getId());

        Map<String, Integer> dayOrder = Map.of(
                "MONDAY", 1, "TUESDAY", 2, "WEDNESDAY", 3, "THURSDAY", 4, "FRIDAY", 5
        );
        if (meetings != null) {
            meetings.sort(Comparator.comparingInt(m -> dayOrder.getOrDefault(m.getDayOfWeek().toUpperCase(), 99)));
        } else {
            meetings = Collections.emptyList();
        }

        List<String> schedule = meetings.stream()
                .map(m -> m.getDayOfWeek() + " " + formatTime(m.getStartTime()) + "-" + formatTime(m.getEndTime()))
                .collect(Collectors.toList());

        int enrolled = studentEnrollmentRepository.findBySectionId(cs.getId()).size();
        int available = cs.getCapacity() == null ? 0 : cs.getCapacity() - enrolled;

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("sectionId", cs.getId());
        out.put("courseId", course.getId());
        out.put("course", (course != null ? course.getCode() : "UNKNOWN") + " - " + (course != null ? course.getName() : "Unknown"));
        out.put("teacher", teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "TBD");
        out.put("email", teacher != null ? teacher.getEmail() : "TBD");
        out.put("room", room != null ? room.getName() : "TBD");
        out.put("schedule", schedule);
        out.put("students", enrolled + " (" + (available <= 0 ? "capacity full" : available + " spots available") + ")");
        return out;
    }

    /**
     * Format a given time string into a more human-readable format.
     *
     * @param time the time string to format
     * @return the formatted time string
     *
     * This function takes a time string in the format "HH:mm" and formats it into a more human-readable format,
     * for example "12:00 PM". It also handles the case where the input time string is null or blank.
     */
    private String formatTime(String time) {
        if (time == null || time.isBlank()) return "";
        java.time.LocalTime t = java.time.LocalTime.parse(time);
        int hour = t.getHour();
        String ampm = hour >= 12 ? "PM" : "AM";
        int h = hour % 12;
        if (h == 0) h = 12;
        return h + ampm;
    }
}
