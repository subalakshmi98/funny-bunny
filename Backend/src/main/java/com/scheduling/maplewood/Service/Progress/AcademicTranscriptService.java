package com.scheduling.maplewood.Service.Progress;

import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AcademicTranscriptService {

    private final StudentRepository studentRepository;
    private final StudentCourseHistoryRepository studentCourseHistoryRepository;
    private final CourseRepository courseRepository;
    private final SemesterRepository semesterRepository;

    /**
     * Gets the academic transcript of the student with the given ID.
     * 
     * @param studentId The ID of the student
     * @return A map containing the student's information and their academic transcript
     */
    public Map<String, Object> getTranscript(Integer studentId) {

        Student student = studentRepository.findById(studentId).orElse(null);

        List<Map<String, Object>> transcript = buildTranscript(studentId);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("success", true);

        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("id", studentId);
        studentInfo.put("name", student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown");
        studentInfo.put("email", student != null ? student.getEmail() : "Unknown");
        studentInfo.put("gradeLevel", student != null ? student.getGradeLevel() : "Unknown");

        out.put("student", studentInfo);

        out.put("transcript", transcript);

        return out;
    }

    /**
     * Builds the academic transcript of the student with the given ID.
     * 
     * The transcript is a list of maps, each containing information about a course the student has taken.
     * The information includes the course ID, course code, course name, credits, semester, status, and recorded at date.
     * 
     * @param studentId The ID of the student
     * @return A list of maps containing the student's academic transcript
     */
    private List<Map<String, Object>> buildTranscript(Integer studentId) {

        List<StudentCourseHistory> history =
                studentCourseHistoryRepository.findByStudentId(studentId);

        List<Map<String, Object>> out = new ArrayList<>();

        for (StudentCourseHistory h : history) {

            Course course = courseRepository.findById(h.getCourseId()).orElse(null);
            Semester semester = semesterRepository.findById(h.getSemesterId()).orElse(null);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("courseId", h.getCourseId());
            row.put("courseCode", course != null ? course.getCode() : "UNKNOWN");
            row.put("courseName", course != null ? course.getName() : "Unknown");
            row.put("credits", course != null ? course.getCredits() : null);

            row.put("semester",
                    semester != null ? semester.getName() + " " + semester.getYear() : "Unknown");

            row.put("status", h.getStatus());
            row.put("recordedAt", h.getCreatedAt());

            out.add(row);
        }

        return out;
    }
}
