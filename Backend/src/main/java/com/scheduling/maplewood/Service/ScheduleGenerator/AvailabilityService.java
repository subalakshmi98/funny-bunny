package com.scheduling.maplewood.Service.ScheduleGenerator;

import com.scheduling.maplewood.Entity.Classroom;
import com.scheduling.maplewood.Entity.Teacher;
import com.scheduling.maplewood.Repository.ClassroomRepository;
import com.scheduling.maplewood.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;

    private final List<LocalTime> MORNING = List.of(
            LocalTime.of(9, 0), LocalTime.of(10, 0), LocalTime.of(11, 0)
    );
    private final List<LocalTime> AFTERNOON = List.of(
            LocalTime.of(13, 0), LocalTime.of(14, 0), LocalTime.of(15, 0), LocalTime.of(16, 0)
    );
    private final List<String> DAYS = List.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"
    );

    public static class Availability {
        public Map<String, LinkedHashSet<LocalTime>> slots = new HashMap<>();
        /**
         * Returns the total number of slots in the availability map.
         * 
         * The total number of slots is calculated by summing the size of all the sets in the availability map.
         * 
         * @return the total number of slots in the availability map
         */
        public int totalSlots() {
            return slots.values().stream().mapToInt(Set::size).sum();
        }
    }

    /**
     * Builds a map of teachers to their full availability.
     * 
     * This method iterates over all teachers in the database and creates a map where the key is the teacher's ID and the value is their full availability.
     * 
     * @return a map of teachers to their full availability
     */
    public Map<Integer, Availability> buildTeacherAvailability() {
        Map<Integer, Availability> map = new HashMap<>();
        for (Teacher t : teacherRepository.findAll()) {
            map.put(t.getId(), fullAvailability());
        }
        return map;
    }

    /**
     * Builds a map of rooms to their full availability.
     * 
     * This method iterates over all rooms in the database and creates a map where the key is the room's ID and the value is their full availability.
     * 
     * @return a map of rooms to their full availability
     */
    public Map<Integer, Availability> buildRoomAvailability() {
        Map<Integer, Availability> map = new HashMap<>();
        for (Classroom r : classroomRepository.findAll()) {
            map.put(r.getId(), fullAvailability());
        }
        return map;
    }

    /**
     * Builds a map of teachers to their daily hours.
     * 
     * This method iterates over all teachers in the database and creates a map where the key is the teacher's ID and the value is a map of days to their daily hours.
     * 
     * @return a map of teachers to their daily hours
     */
    public Map<Integer, Map<String, Integer>> buildTeacherDailyHours() {
        Map<Integer, Map<String, Integer>> tracking = new HashMap<>();
        for (Teacher t : teacherRepository.findAll()) {
            Map<String, Integer> hours = new HashMap<>();
            for (String d : DAYS) hours.put(d, 0);
            tracking.put(t.getId(), hours);
        }
        return tracking;
    }

    /**
     * Builds an availability map that represents full availability for a given day.
     * 
     * This method creates an availability map where all time slots for a given day are marked as available.
     * 
     * @return an availability map representing full availability for a given day
     */
    public Availability fullAvailability() {
        Availability a = new Availability();
        for (String d : DAYS) {
            LinkedHashSet<LocalTime> times = new LinkedHashSet<>();
            times.addAll(MORNING);
            times.addAll(AFTERNOON);
            a.slots.put(d, times);
        }
        return a;
    }
}

