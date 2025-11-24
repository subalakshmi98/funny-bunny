package com.scheduling.maplewood.Service.General;

import com.scheduling.maplewood.Entity.Semester;
import com.scheduling.maplewood.Repository.SemesterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SemesterSevice {
    private final SemesterRepository semesterRepository;

    /**
     * Gets all semesters sorted by ID.
     * 
     * @return a list of maps containing the semester's ID and label
     */
    public List<Map<String, Object>> getAllSemesters() {
        return semesterRepository.findAll().stream()
                .sorted(Comparator.comparing(Semester::getId))
                .map(s -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id", s.getId());
                    m.put("label", s.getName() + " " + s.getYear());
                    return m;
                })
                .toList();
    }
}
