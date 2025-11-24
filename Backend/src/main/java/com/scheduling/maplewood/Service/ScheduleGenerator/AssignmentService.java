package com.scheduling.maplewood.Service.ScheduleGenerator;

import com.scheduling.maplewood.Entity.Classroom;
import com.scheduling.maplewood.Entity.Course;
import com.scheduling.maplewood.Entity.Specialization;
import com.scheduling.maplewood.Entity.Teacher;
import com.scheduling.maplewood.Repository.ClassroomRepository;
import com.scheduling.maplewood.Repository.SpecializationRepository;
import com.scheduling.maplewood.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final SpecializationRepository specializationRepository;

    /**
     * Pick a teacher for the given course based on availability.
     * If there are no teachers with the same specialization, all teachers are considered.
     * The teacher with the highest availability is chosen.
     *
     * @param course the course to pick a teacher for
     * @param teacherAvail a map of teacher ID to availability
     * @return the chosen teacher, or null if no teachers are available
     */
    public Teacher pickTeacherForCourse(Course course, Map<Integer, AvailabilityService.Availability> teacherAvail) {

        List<Teacher> candidates = teacherRepository.findAll().stream()
                .filter(t -> Objects.equals(t.getSpecializationId(), course.getSpecializationId()))
                .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            candidates = teacherRepository.findAll();
        }

        return candidates.stream()
                .max(Comparator.comparingInt(t -> teacherAvail
                        .getOrDefault(t.getId(), empty()).totalSlots()))
                .orElse(null);
    }

    /**
     * Pick a room for the given course based on availability.
     * If the course has a specialization, only rooms with the same room type are considered.
     * The room with the highest availability is chosen.
     *
     * @param course the course to pick a room for
     * @param roomAvail a map of room ID to availability
     * @return the chosen room, or null if no rooms are available
     */
    public Classroom pickRoomForCourse(Course course, Map<Integer, AvailabilityService.Availability> roomAvail) {

        Specialization spec = null;
        if (course.getSpecializationId() != null) {
            spec = specializationRepository.findById(course.getSpecializationId()).orElse(null);
        }

        List<Classroom> candidates = classroomRepository.findAll();

        if (spec != null && spec.getRoomTypeId() != null) {
            Integer neededRoom = spec.getRoomTypeId();
            List<Classroom> filtered = candidates.stream()
                    .filter(r -> Objects.equals(r.getRoomTypeId(), neededRoom))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) candidates = filtered;
        }

        return candidates.stream()
                .max(Comparator.comparingInt(r -> roomAvail
                        .getOrDefault(r.getId(), empty()).totalSlots()))
                .orElse(null);
    }

    /**
     * Return an empty Availability object.
     * This is used when the teacher or room has no available slots.
     * @return an empty Availability object
     */
    private AvailabilityService.Availability empty() {
        return new AvailabilityService.Availability();
    }
}

