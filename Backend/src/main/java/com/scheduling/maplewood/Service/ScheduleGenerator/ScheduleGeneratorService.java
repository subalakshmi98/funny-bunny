package com.scheduling.maplewood.Service.ScheduleGenerator;

import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleGeneratorService {

    private final CourseOfferingService offeringService;
    private final AvailabilityService availabilityService;
    private final AssignmentService assignmentService;
    private final WeeklySchedulerService weeklyScheduler;

    private final CourseSectionRepository courseSectionRepository;
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final SectionMeetingRepository sectionMeetingRepository;
    private final StudentEnrollmentRepository studentEnrollmentRepository;
    private final SemesterRepository semesterRepository;

    /**
     * Generates a master schedule for a given semester.
     * This function is transactional.
     * 
     * @param semesterId the semester's id
     * @return a list of created CourseSections
     */
    @Transactional
    public List<CourseSection> generateMasterSchedule(Integer semesterId) {

        List<Course> courses = offeringService.getCoursesForSemester(semesterId);

        offeringService.clearExistingSections(semesterId);

        Map<Integer, AvailabilityService.Availability> teacherAvail = availabilityService.buildTeacherAvailability();
        Map<Integer, AvailabilityService.Availability> roomAvail = availabilityService.buildRoomAvailability();
        Map<Integer, Map<String, Integer>> teacherDailyHours = availabilityService.buildTeacherDailyHours();

        List<CourseSection> createdSections = new ArrayList<>();

        for (Course course : courses) {

            Teacher teacher = assignmentService.pickTeacherForCourse(course, teacherAvail);
            if (teacher == null) continue;

            Classroom room = assignmentService.pickRoomForCourse(course, roomAvail);
            if (room == null) continue;

            CourseSection section = offeringService.createSection(course, teacher, room, semesterId);

            weeklyScheduler.scheduleSection(
                    section,
                    course,
                    teacher,
                    room,
                    teacherAvail,
                    roomAvail,
                    teacherDailyHours
            );

            createdSections.add(section);
        }

        return createdSections;
    }

    /**
     * Returns the master schedule for a given semester.
     * 
     * @param semesterId the semester's id
     * @return a map containing the semester's name and year, and a list of sections
     * @throws RuntimeException if the semester is not found
     */
    public Map<String, Object> getMasterScheduleResponse(Integer semesterId) {
        Semester semester = semesterRepository.findById(semesterId)
                .orElseThrow(() -> new RuntimeException("Semester not found: " + semesterId));

        String semesterNameYear = (semester.getName() == null ? "" : semester.getName())
                + " " + (semester.getYear() == null ? "" : semester.getYear().toString());
        semesterNameYear = semesterNameYear.trim();

        List<CourseSection> sections = courseSectionRepository.findBySemesterId(semesterId);

        List<Map<String, Object>> sectionList = sections.stream()
                .map(this::mapSectionToMasterScheduleItem)
                .collect(Collectors.toList());

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("semester", semesterNameYear);
        out.put("sections", sectionList);

        return out;
    }

    /**
     * Maps a CourseSection to a detailed item containing its course ID, section ID, course name, teacher name, email, room name, schedule, and student count.
     *
     * @param s the CourseSection to map
     * @return a map containing the detailed item
     */
    private Map<String, Object> mapSectionToMasterScheduleItem(CourseSection s) {
        Course course = courseRepository.findById(s.getCourseId()).orElse(null);
        Teacher teacher = teacherRepository.findById(s.getTeacherId()).orElse(null);
        Classroom room = classroomRepository.findById(s.getRoomId()).orElse(null);

        List<SectionMeeting> meetings = sectionMeetingRepository.findBySectionId(s.getId());

        Map<String, Integer> dayOrder = Map.of(
                "MONDAY", 1,
                "TUESDAY", 2,
                "WEDNESDAY", 3,
                "THURSDAY", 4,
                "FRIDAY", 5
        );

        meetings.sort(Comparator.comparingInt(
                m -> dayOrder.getOrDefault(m.getDayOfWeek().toUpperCase(), 99))
        );

        List<String> schedule = meetings.stream()
                .map(this::formatMeeting)
                .collect(Collectors.toList());

        int enrolled = studentEnrollmentRepository.findBySectionId(s.getId()).size();
        int available = (s.getCapacity() == null ? 0 : s.getCapacity()) - enrolled;

        String studentsText;
        if (s.getCapacity() == null) {
            studentsText = enrolled + " (capacity unknown)";
        } else if (available <= 0) {
            studentsText = enrolled + " (capacity full)";
        } else {
            studentsText = enrolled + " (" + available + " spots available)";
        }

        Map<String, Object> item = new LinkedHashMap<>();

        item.put("courseId", course != null ? course.getId() : null);
        item.put("sectionId", s.getId());

        item.put("course", (course != null ? course.getCode() : "UNKNOWN")
                + " - " + (course != null ? course.getName() : "Unknown Course"));
        item.put("teacher", teacher != null ? (teacher.getFirstName() + " " + teacher.getLastName()) : "TBD");
        item.put("email", teacher != null ? teacher.getEmail() : "TBD");
        item.put("room", room != null ? room.getName() : "TBD");
        item.put("schedule", schedule);
        item.put("students", studentsText);

        return item;
    }

    /**
     * Maps a CourseSection to a detailed item containing its course name, room name, schedule, and student count.
     *
     * @param s the CourseSection to map
     * @return a map containing the detailed item
     */
    private Map<String, Object> mapSectionToTeacherScheduleItem(CourseSection s) {
        Course course = courseRepository.findById(s.getCourseId()).orElse(null);
        Teacher teacher = teacherRepository.findById(s.getTeacherId()).orElse(null);
        Classroom room = classroomRepository.findById(s.getRoomId()).orElse(null);

        List<SectionMeeting> meetings = sectionMeetingRepository.findBySectionId(s.getId());

        Map<String, Integer> dayOrder = Map.of(
                "MONDAY", 1,
                "TUESDAY", 2,
                "WEDNESDAY", 3,
                "THURSDAY", 4,
                "FRIDAY", 5
        );

        meetings.sort(Comparator.comparingInt(m -> dayOrder.getOrDefault(m.getDayOfWeek().toUpperCase(), 99)));

        List<String> schedule = meetings.stream()
                .map(this::formatMeeting)
                .collect(Collectors.toList());

        int enrolled = studentEnrollmentRepository.findByCourseId(s.getId()).size();
        int available = (s.getCapacity() == null ? 0 : s.getCapacity()) - enrolled;

        String studentsText;
        if (s.getCapacity() == null) {
            studentsText = enrolled + " (capacity unknown)";
        } else if (available <= 0) {
            studentsText = enrolled + " (capacity full)";
        } else {
            studentsText = enrolled + " (" + available + " spots available)";
        }

        Map<String, Object> item = new LinkedHashMap<>();
        item.put("course", (course != null ? course.getCode() : "UNKNOWN") + " - " + (course != null ? course.getName() : "Unknown Course"));
        item.put("room", room != null ? room.getName() : "TBD");
        item.put("schedule", schedule);
        item.put("students", studentsText);

        return item;
    }

    /**
     * Returns a string representation of a section meeting in the format "DAY START-END"
     * 
     * @param m the section meeting to format
     * @return a string representation of the section meeting
     */
   private String formatMeeting(SectionMeeting m) {
        String day = m.getDayOfWeek() == null ? "" : m.getDayOfWeek().toUpperCase();
        String start = formatTime(m.getStartTime());
        String end = formatTime(m.getEndTime());
        return day + " " + start + "-" + end;
    }

    /**
     * Returns a string representation of a given time string in the format "HH AM/PM".
     * 
     * @param time the time string to format
     * @return a string representation of the given time string
     * 
     * If the input time string is null or blank, this function returns an empty string.
     */
    private String formatTime(String time) {
        if (time == null || time.isBlank()) return "";
        java.time.LocalTime t = java.time.LocalTime.parse(time);
        int hour = t.getHour();
        String ampm = hour >= 12 ? "PM" : "AM";
        int displayHour = hour % 12;
        if (displayHour == 0) displayHour = 12;
        return displayHour + ampm;
    }

    /**
     * Returns a map containing the teacher's information and their schedule.
     * 
     * @param teacherId the teacher's ID
     * @return a map containing the teacher's information and their schedule
     * 
     * The map contains the following keys:
     * 
     * - "teacher": a map containing the teacher's ID, name, and email
     * - "schedule": a list of maps, each containing the course name, room name, and schedule
     */
    public Map<String, Object> getTeacherScheduleResponse(Integer teacherId) {

        Teacher teacher = teacherRepository.findById(teacherId).orElse(null);

        List<CourseSection> sections = courseSectionRepository
                .findAll()
                .stream()
                .filter(s -> Objects.equals(s.getTeacherId(), teacherId))
                .collect(Collectors.toList());

        List<Map<String, Object>> formatted =
                sections.stream().map(this::mapSectionToTeacherScheduleItem).toList();

        Map<String, Object> out = new LinkedHashMap<>();
        Map<String, Object> teacherInfo = new LinkedHashMap<>();
        teacherInfo.put("id", teacher != null ? teacher.getId() : null);
        teacherInfo.put("name", teacher != null ? teacher.getFirstName() + " " + teacher.getLastName() : "Unknown");
        teacherInfo.put("email", teacher != null ? teacher.getEmail() : "Unknown");

        out.put("teacher", teacherInfo);
        out.put("schedule", formatted);

        return out;
    }

    /**
     * Returns a map containing the course schedule for a given semester.
     * 
     * The map contains the following keys:
     * 
     * - "semesterId": the semester's ID
     * - "courses": a map containing the course codes as keys and lists of maps as values.
     *   Each map contains the course section's information and schedule.
     *   The map contains the following keys:
     *   - "course": the course name
     *   - "room": the room name
     *   - "schedule": a list of strings, each representing a meeting time in the format "DAY START-END"
     *   - "students": a string representing the enrolled and available students
     */
    public Map<String, Object> getCourseScheduleResponse(Integer semesterId) {

        List<CourseSection> sections =
                courseSectionRepository.findBySemesterId(semesterId);

        Map<String, List<Map<String, Object>>> grouped =
                sections.stream()
                        .collect(Collectors.groupingBy(
                                s -> courseRepository.findById(s.getCourseId())
                                        .map(Course::getCode)
                                        .orElse("UNKNOWN"),
                                Collectors.mapping(this::mapSectionToMasterScheduleItem, Collectors.toList())
                        ));

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("semesterId", semesterId);
        out.put("courses", grouped);

        return out;
    }
}
