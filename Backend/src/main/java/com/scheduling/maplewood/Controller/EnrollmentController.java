package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.ApiResponse;
import com.scheduling.maplewood.Dto.EnrollmentRequest;
import com.scheduling.maplewood.Service.Enroll.EligibilityService;
import com.scheduling.maplewood.Service.Enroll.EnrollmentService;
import com.scheduling.maplewood.Service.Enroll.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/enrollment")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EligibilityService eligibilityService;
    private final ScheduleService scheduleService;

    /**
     * Enroll a student in a course.
     *
     * @param req the enrollment request with studentId, courseId, and semesterId
     * @return an EnrollmentResponse object with success and message
     * @throws EnrollmentException if studentId, courseId, or semesterId is null
     */
    @PostMapping
    public ResponseEntity<?> enroll(@RequestBody EnrollmentRequest req) {
        try {
            var resp = enrollmentService.enroll(req);
            return ResponseEntity.ok(ApiResponse.success(resp));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Get the student's schedule for a given semester.
     * 
     * @param studentId the student's id
     * @param semesterId the semester's id (optional)
     * @return a map containing the student's information and a list of sections they are enrolled in
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping("/student/{studentId}/schedule")
    public ResponseEntity<Map<String,Object>> studentSchedule( @PathVariable Integer studentId,
                                                                     @RequestParam(required = false) Integer semesterId) {
        try {
            return ResponseEntity.ok(
                    ApiResponse.success(scheduleService.getStudentScheduleResponse(studentId, semesterId))
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Get the eligible sections for a given student and semester.
     * 
     * @param studentId the student's id
     * @param semesterId the semester's id (optional)
     * @return a map containing the student's information and a list of eligible sections
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping("/student/{studentId}/eligible")
    public ResponseEntity<Map<String,Object>> eligibleSections( @PathVariable Integer studentId,
                                                    @RequestParam Integer semesterId) {

        try {
            Map<String,Object> response = eligibilityService.getEligibleSections(studentId, semesterId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }
}

