package com.scheduling.maplewood.Service.Login;

import com.scheduling.maplewood.Entity.Student;
import com.scheduling.maplewood.Entity.Teacher;
import com.scheduling.maplewood.Repository.StudentRepository;
import com.scheduling.maplewood.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;

    /**
     * Returns a teacher repository based on the given email address.
     * @param email is the email address of the teacher.
     * @return the teacher repository associated with the given email address.
     */
    public Teacher findByTeacherEmail(String email) {
        return teacherRepository.findByEmail(email);
    }

    /**
     * Returns a student repository based on the given email address.
     * @param email is the email address of the student.
     * @return the student repository associated with the given email address.
     */
    public Student findByStudentEmail(String email) {
        return studentRepository.findByEmail(email);
    }
}
