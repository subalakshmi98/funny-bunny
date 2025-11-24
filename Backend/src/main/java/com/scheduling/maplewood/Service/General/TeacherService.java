package com.scheduling.maplewood.Service.General;

import com.scheduling.maplewood.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepository teacherRepository;

    /**
     * Returns a list of all teachers in the system.
     * 
     * @return a list of maps, each containing the teacher's id, first name, last name, and email
     */
    public List<Map<String, Object>> getAllTeachers() {
        List<Map<String, Object>> out = teacherRepository.findAll().stream()
                .map(t -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", t.getId());
                    m.put("firstName", t.getFirstName());
                    m.put("lastName", t.getLastName());
                    m.put("email", t.getEmail());
                    return m;
                })
                .collect(Collectors.toList());
        return out;
    }

}
