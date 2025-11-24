package com.scheduling.maplewood.Controller;

import com.scheduling.maplewood.Dto.LoginRequest;
import com.scheduling.maplewood.Dto.LoginResponse;
import com.scheduling.maplewood.Entity.Student;
import com.scheduling.maplewood.Entity.Teacher;
import com.scheduling.maplewood.Service.Login.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private static final String TEACHER_PASS = "teacher123";
    private static final String STUDENT_PASS = "student123";

    /**
     * Login user.
     * 
     * @param req LoginRequest containing username and password
     * @return ResponseEntity containing LoginResponse
     */
    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {

        String username = req.getUsername();
        String password = req.getPassword();

        if (username.equalsIgnoreCase(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            return ResponseEntity.ok(new LoginResponse(1, "ADMIN", "Admin User", username));
        }

        Teacher teacher = loginService.findByTeacherEmail(username);
        if (teacher != null && password.equals(TEACHER_PASS)) {
            return ResponseEntity.ok(
                    new LoginResponse(
                            teacher.getId(),
                            "TEACHER",
                            teacher.getFirstName() + " " + teacher.getLastName(),
                            teacher.getEmail()
                    )
            );
        }

        Student student = loginService.findByStudentEmail(username);
        if (student != null && password.equals(STUDENT_PASS)) {
            return ResponseEntity.ok(
                    new LoginResponse(
                            student.getId(),
                            "STUDENT",
                            student.getFirstName() + " " + student.getLastName(),
                            student.getEmail()
                    )
            );
        }

        return ResponseEntity.status(401).body(Map.of("message", "Invalid username/password"));
    }
}

