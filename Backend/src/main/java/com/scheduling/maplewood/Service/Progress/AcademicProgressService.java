package com.scheduling.maplewood.Service.Progress;

import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicProgressService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final CoreRequiredCoursesRepository coreRequiredCoursesRepository;
    private final StudentCourseHistoryRepository historyRepository;

    private final int TOTAL_REQUIRED_CREDITS = 30;
    private final int TOTAL_CORE_REQUIRED = 20;

    /**
     * Returns the academic progress of the student with the given ID.
     * 
     * @param studentId The ID of the student
     * @return A map containing the student's information and their academic progress
     */
    public Map<String, Object> getProgress(Integer studentId) {

        Student student = studentRepository.findById(studentId).orElse(null);

        Map<String, Object> progress = calculateProgress(studentId);
        List<Map<String, Object>> remainingCore = findRemainingCoreWithLightDetails(studentId);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("success", true);

        Map<String, Object> studentInfo = new LinkedHashMap<>();
        studentInfo.put("id", studentId);
        studentInfo.put("name", student != null ? student.getFirstName() + " " + student.getLastName() : "Unknown");
        studentInfo.put("email", student != null ? student.getEmail() : "Unknown");
        studentInfo.put("gradeLevel", student != null ? student.getGradeLevel() : "Unknown");

        out.put("student", studentInfo);

        out.put("progress", progress);
        out.put("remainingCoreCourses", remainingCore);

        return out;
    }

    /**
     * Calculates the academic progress of the student with the given ID.
     * 
     * @param studentId The ID of the student
     * @return A map containing the student's information and their academic progress
     * 
     * The map contains the following information:
     * - creditsEarned: The total number of credits the student has earned.
     * - creditsRequired: The total number of credits required to graduate.
     * - creditsRemaining: The total number of credits the student still needs to earn.
     * - corePassed: The number of core courses the student has passed.
     * - coreRequired: The total number of core courses required to graduate.
     * - gpa: The student's GPA.
     * - predictedSemestersToGraduate: The predicted number of semesters the student will take to graduate.
     */
    private Map<String, Object> calculateProgress(Integer studentId) {

        List<StudentCourseHistory> history = historyRepository.findByStudentId(studentId);

        Set<Integer> passedIds = history.stream()
                .filter(h -> "passed".equalsIgnoreCase(h.getStatus()))
                .map(StudentCourseHistory::getCourseId)
                .collect(Collectors.toSet());

        double creditsEarned = passedIds.stream()
                .map(id -> courseRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .mapToDouble(c -> c.getCredits().doubleValue())
                .sum();

        double totalQualityPoints = history.stream()
                .mapToDouble(h -> {
                    Course c = courseRepository.findById(h.getCourseId()).orElse(null);
                    if (c == null) return 0;
                    double grade = h.getStatus().equalsIgnoreCase("passed") ? 4.0 : 0.0;
                    return c.getCredits().doubleValue() * grade;
                }).sum();

        double totalAttemptedCredits = history.stream()
                .mapToDouble(h -> {
                    Course c = courseRepository.findById(h.getCourseId()).orElse(null);
                    return c != null ? c.getCredits().doubleValue() : 0.0;
                }).sum();

        double gpa = totalAttemptedCredits > 0 ?
                totalQualityPoints / totalAttemptedCredits : 0.0;

        double remainingCredits = Math.max(0, TOTAL_REQUIRED_CREDITS - creditsEarned);

        double avgLoad = 4.0;
        int predictedSemesters = (int) Math.ceil(remainingCredits / avgLoad);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("creditsEarned", creditsEarned);
        out.put("creditsRequired", TOTAL_REQUIRED_CREDITS);
        out.put("creditsRemaining", remainingCredits);

        out.put("corePassed", passedCoreCount(passedIds));
        out.put("coreRequired", TOTAL_CORE_REQUIRED);

        out.put("gpa", Math.round(gpa * 100.0) / 100.0);
        out.put("predictedSemestersToGraduate", predictedSemesters);

        return out;
    }

    /**
     * Returns the number of core courses the student has passed.
     * @param passedIds A set of course IDs that the student has passed.
     * @return The number of core courses the student has passed.
     */
    private long passedCoreCount(Set<Integer> passedIds) {
        List<Integer> coreIds = coreRequiredCoursesRepository.findAll()
                .stream()
                .map(CoreRequiredCourse::getCourseId)
                .toList();

        return coreIds.stream()
                .filter(passedIds::contains)
                .count();
    }


    /**
     * Finds the remaining core courses for a given student ID.
     * 
     * @param studentId The ID of the student
     * @return A list of maps containing the course's information and the student's progress
     */
    private List<Map<String, Object>> findRemainingCoreWithLightDetails(Integer studentId) {

        List<StudentCourseHistory> passedHistory =
                historyRepository.findByStudentIdAndStatus(studentId, "passed");

        Set<Integer> passedIds = passedHistory.stream()
                .map(StudentCourseHistory::getCourseId)
                .collect(Collectors.toSet());

        List<Integer> coreCourseIds = coreRequiredCoursesRepository.findAll().stream()
                .map(CoreRequiredCourse::getCourseId)
                .toList();

        return coreCourseIds.stream()
                .filter(id -> !passedIds.contains(id))
                .map(id -> courseRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(c -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("courseId", c.getId());
                    m.put("code", c.getCode());
                    m.put("name", c.getName());
                    m.put("semesterOrder", c.getSemesterOrder());
                    return m;
                })
                .collect(Collectors.toList());
    }
}
