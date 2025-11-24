package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.ApiResponse;
import com.scheduling.maplewood.Service.Progress.AcademicProgressService;
import com.scheduling.maplewood.Service.Progress.AcademicTranscriptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/student/academic")
@RequiredArgsConstructor
public class AcademicProgressController {

    private final AcademicProgressService progressService;
    private final AcademicTranscriptService transcriptService;

    /**
     * Returns the academic progress of the student with the given ID.
     * 
     * @param studentId The ID of the student
     * @return A response containing the academic progress of the student
     */
    @GetMapping("/{studentId}/progress")
    public ResponseEntity<Map<String,Object>> progress(@PathVariable Integer studentId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(progressService.getProgress(studentId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Returns the academic transcript of the student with the given ID.
     * 
     * @param studentId The ID of the student
     * @return A response containing the academic transcript of the student
     */
    @GetMapping("/{studentId}/transcript")
    public ResponseEntity<Map<String,Object>> getTranscript(@PathVariable Integer studentId) {
        try {
            return ResponseEntity.ok(ApiResponse.success(transcriptService.getTranscript(studentId)));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }
}
