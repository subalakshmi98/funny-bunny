package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.ApiResponse;
import com.scheduling.maplewood.Entity.CourseSection;
import com.scheduling.maplewood.Service.ScheduleGenerator.ScheduleGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/schedule")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleGeneratorService scheduleGeneratorService;

    /**
     * Generate a master schedule for a given semester.
     * 
     * @param semesterId the semester's id
     * @return a map containing a success status and the number of generated sections
     * @throws Exception if an unexpected error occurred
     */
    @PostMapping("/generate")
    public ResponseEntity<Map<String, Object>> generate(@RequestParam Integer semesterId) {

        List<CourseSection> sections = null;
        try {
            sections = scheduleGeneratorService.generateMasterSchedule(semesterId);
            return ResponseEntity.ok(
                    Map.of("success", true, "generatedSections", sections.size())
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Gets the master schedule for a given semester.
     * 
     * @param semesterId the semester's id
     * @return a map containing the master schedule
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping("/{semesterId}")
    public ResponseEntity<Map<String, Object>> getMasterSchedule(@PathVariable Integer semesterId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(scheduleGeneratorService.getMasterScheduleResponse(semesterId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Returns the course schedules for a given semester.
     * 
     * @param semesterId The semester's id
     * @return A map containing the course schedules
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping("/courses/{semesterId}")
    public ResponseEntity<Map<String, Object>> getCourseSchedules(@PathVariable Integer semesterId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(scheduleGeneratorService.getCourseScheduleResponse(semesterId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Returns the schedule for a given teacher.
     * 
     * @param teacherId The teacher's id
     * @return A map containing the teacher's schedule
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Map<String, Object>> getScheduleByTeacher(
            @PathVariable Integer teacherId) {

        try {
            return ResponseEntity.ok(ApiResponse.success(scheduleGeneratorService.getTeacherScheduleResponse(teacherId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }
}
