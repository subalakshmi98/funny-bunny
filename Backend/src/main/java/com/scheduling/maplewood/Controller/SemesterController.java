package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.ApiResponse;
import com.scheduling.maplewood.Service.General.SemesterSevice;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/semesters")
@RequiredArgsConstructor
public class SemesterController {

    private final SemesterSevice semesterSevice;

    /**
     * Gets all semesters.
     * 
     * @return a map containing a success status and the list of semesters
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSemester() {
        try {
            return ResponseEntity.ok(ApiResponse.success(semesterSevice.getAllSemesters()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }
}
