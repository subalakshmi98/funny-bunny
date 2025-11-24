package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.ApiResponse;
import com.scheduling.maplewood.Service.Resource.ResourceUtilizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/resource")
@RequiredArgsConstructor
public class ResourceController {
    private final ResourceUtilizationService utilizationService;

    /**
     * Returns the workload of each teacher in the system.
     * 
     * @return A response containing a list of maps, each containing the teacher's id, name, sections assigned, weekly hours, and utilizationization percentage.
     * @throws Exception if an unexpected error occurred.
     */
    @GetMapping("/teachers")
    public ResponseEntity<?> getTeacherWorkload() {
        try {
            return ResponseEntity.ok(ApiResponse.success(utilizationService.getTeacherWorkload()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }

    /**
     * Returns the usage of each room in the system.
     * 
     * @return A response containing a list of maps, each containing the room's id, name, weekly hours used, utilizationization percentage, and overlap count.
     * @throws Exception if an unexpected error occurred.
     */
    @GetMapping("/rooms")
    public ResponseEntity<?> getRoomUsage() {
        try {
            return ResponseEntity.ok(ApiResponse.success(utilizationService.getRoomUsage()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(ApiResponse.error("An unexpected error occurred.", e.getMessage()));
        }
    }
}
