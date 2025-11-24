package com.scheduling.maplewood.Service.Enroll;

import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final StudentRepository studentRepository;

    /**
     * Gets the student's schedule for a given semester.
     * 
     * @param studentId the student's ID
     * @param semesterId the semester's ID
     * @return a map containing the student's information and a list of sections they are enrolled in
     */
    public Map<String, Object> getStudentScheduleResponse(Integer studentId, Integer semesterId) {
        Student student = studentRepository.findById(studentId).orElse(null);

        List<StudentEnrollment> enrollments = studentEnrollmentRepository.findByStudentId(studentId).stream()
                .filter(e -> Objects.equals(e.getSemesterId(), semesterId))
                .collect(Collectors.toList());

        List<CourseSection> sections = enrollments.stream()
                .map(e -> courseSectionRepository.findById(e.getSectionId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<Map<String, Object>> sectionList = sections.stream()
                .map(this::mapSectionToScheduleItem)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("id", student != null ? student.getId() : null);
        studentInfo.put("name", student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown");
        studentInfo.put("email", student != null ? student.getEmail() : "Unknown");
        studentInfo.put("Grade level", student != null ? student.getGradeLevel() : null);

        response.put("student", studentInfo);
        response.put("sections", sectionList);

        return response;
    }

    /**
     * Maps a CourseSection to a detailed item containing its course name, teacher name, room name, schedule, and student count.
     *
     * @param s the CourseSection to map
     * @return a map containing the detailed item
     */
    private Map<String, Object> mapSectionToScheduleItem(CourseSection s) {
        Course course = courseRepository.findById(s.getCourseId()).orElse(null);
        Teacher teacher = teacherRepository.findById(s.getTeacherId()).orElse(null);
        Classroom room = classroomRepository.findById(s.getRoomId()).orElse(null);
        List<SectionMeeting> meetings = sectionMeetingRepository.findBySectionId(s.getId());

        Map<String, Integer> dayOrder = Map.of("MONDAY",1,"TUESDAY",2,"WEDNESDAY",3,"THURSDAY",4,"FRIDAY",5);
        meetings.sort(Comparator.comparingInt(m -> dayOrder.getOrDefault(m.getDayOfWeek().toUpperCase(),99)));

        List<String> schedule = meetings.stream()
                .map(m -> m.getDayOfWeek() + " " + formatTime(m.getStartTime()) + "-" + formatTime(m.getEndTime()))
                .collect(Collectors.toList());

        int enrolled = studentEnrollmentRepository.findBySectionId(s.getId()).size();
        int available = s.getCapacity() == null ? 0 : s.getCapacity() - enrolled;

        Map<String,Object> out = new LinkedHashMap<>();
        out.put("course", (course != null ? course.getCode() : "UNKNOWN") + " - " + (course != null ? course.getName() : "Unknown"));
        out.put("teacher", teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "TBD");
        out.put("room", room != null ? room.getName() : "TBD");
        out.put("schedule", schedule);
        out.put("students", enrolled + " (" + (available <= 0 ? "capacity full" : available + " spots available") + ")");
        return out;
    }

    /**
     * Formats a given time string into a more human-readable format.
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
