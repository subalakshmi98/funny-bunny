package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.ApiResponse;
import com.scheduling.maplewood.Service.General.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    /**
     * Gets all teachers in the system.
     * 
     * @return a map containing a success status and a list of teachers
     * @throws Exception if an unexpected error occurred
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllTeachers() {
        try {
            return ResponseEntity.ok(ApiResponse.success(teacherService.getAllTeachers()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }
}
