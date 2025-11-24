package com.scheduling.maplewood.Service.Resource;

import com.scheduling.maplewood.Entity.*;
import com.scheduling.maplewood.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ResourceUtilizationService {

    private final TeacherRepository teacherRepo;
    private final ClassroomRepository roomRepo;
    private final CourseSectionRepository sectionRepo;
    private final SectionMeetingRepository meetingRepo;

    private final int TOTAL_WEEKLY_HOURS = 35;

    /**
     * Returns a list of maps containing the workload of each teacher in the system.
     * 
     * Each map contains the teacher's id, name, sections assigned, weekly hours, utilizationization percentage, and daily load.
     * 
     * @return A list of maps containing the teacher's workload.
     */
    public List<Map<String, Object>> getTeacherWorkload() {

        List<CourseSection> allSections = sectionRepo.findAll();

        Map<Integer, List<SectionMeeting>> teacherMeetings = new HashMap<>();

        for (CourseSection s : allSections) {
            List<SectionMeeting> meetings = meetingRepo.findBySectionId(s.getId());
            teacherMeetings
                    .computeIfAbsent(s.getTeacherId(), k -> new ArrayList<>())
                    .addAll(meetings);
        }

        List<Map<String, Object>> out = new ArrayList<>();

        for (Teacher t : teacherRepo.findAll()) {
            List<SectionMeeting> meetings = teacherMeetings.getOrDefault(t.getId(), List.of());

            double weeklyHours = calculateWeeklyHours(meetings);

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("teacherId", t.getId());
            m.put("teacherName", t.getFirstName() + " " + t.getLastName());
            m.put("sectionsAssigned", allSections.stream().filter(s -> s.getTeacherId().equals(t.getId())).count());
            m.put("weeklyHours", weeklyHours);
            m.put("utilizationPercent", round((weeklyHours / TOTAL_WEEKLY_HOURS) * 100));
            m.put("dailyLoad", meetingHoursByDay(meetings));

            out.add(m);
        }

        return out;
    }


    /**
     * Gets the usage of each room in the system.
     * 
     * Each map contains the room's id, name, weekly hours used, utilizationization percentage, daily load, and overlap count.
     * 
     * @return A list of maps containing the room's usage.
     */
    public List<Map<String, Object>> getRoomUsage() {

        List<CourseSection> allSections = sectionRepo.findAll();

        Map<Integer, List<SectionMeeting>> roomMeetings = new HashMap<>();

        for (CourseSection s : allSections) {
            List<SectionMeeting> meetings = meetingRepo.findBySectionId(s.getId());
            roomMeetings
                    .computeIfAbsent(s.getRoomId(), k -> new ArrayList<>())
                    .addAll(meetings);
        }

        List<Map<String, Object>> out = new ArrayList<>();

        for (Classroom r : roomRepo.findAll()) {
            List<SectionMeeting> meetings = roomMeetings.getOrDefault(r.getId(), List.of());

            double weeklyHours = calculateWeeklyHours(meetings);

            Map<String, Object> m = new LinkedHashMap<>();
            m.put("roomId", r.getId());
            m.put("roomName", r.getName());
            m.put("weeklyHoursUsed", weeklyHours);
            m.put("utilizationPercent", round((weeklyHours / TOTAL_WEEKLY_HOURS) * 100));
            m.put("dailyLoad", meetingHoursByDay(meetings));
            m.put("overlapCount", detectConflicts(meetings));

            out.add(m);
        }

        return out;
    }

    /**
     * Calculates the total weekly hours of a list of meetings.
     * 
     * The total weekly hours is the sum of the duration of each meeting in minutes, divided by 60.
     * 
     * @param meetings a list of section meetings
     * @return the total weekly hours of the meetings
     */
    private double calculateWeeklyHours(List<SectionMeeting> meetings) {
        return meetings.stream()
                .mapToDouble(m -> {
                    LocalTime s = LocalTime.parse(m.getStartTime());
                    LocalTime e = LocalTime.parse(m.getEndTime());
                    return ChronoUnit.MINUTES.between(s, e) / 60.0;
                })
                .sum();
    }

    /**
     * Returns a map containing the total meeting hours for each day of the week.
     * 
     * The map contains the days of the week as keys and the total meeting hours for each day as values.
     * 
     * @param meetings a list of section meetings
     * @return a map containing the total meeting hours for each day of the week
     */
    private Map<String, Double> meetingHoursByDay(List<SectionMeeting> meetings) {
        Map<String, Double> map = new LinkedHashMap<>();
        for (String d : List.of("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"))
            map.put(d, 0.0);

        for (SectionMeeting m : meetings) {
            String day = m.getDayOfWeek().toUpperCase();
            LocalTime s = LocalTime.parse(m.getStartTime());
            LocalTime e = LocalTime.parse(m.getEndTime());
            double hours = ChronoUnit.MINUTES.between(s, e) / 60.0;

            map.put(day, map.get(day) + hours);
        }

        return map;
    }

    /**
     * Detects the number of conflicts between two or more section meetings.
     * 
     * A conflict is when two section meetings occur on the same day and overlap in time.
     * 
     * @param meetings a list of section meetings
     * @return the number of conflicts between the section meetings
     */
    private int detectConflicts(List<SectionMeeting> meetings) {
        int conflicts = 0;

        for (int i = 0; i < meetings.size(); i++) {
            for (int j = i + 1; j < meetings.size(); j++) {

                SectionMeeting a = meetings.get(i);
                SectionMeeting b = meetings.get(j);

                if (!a.getDayOfWeek().equalsIgnoreCase(b.getDayOfWeek())) continue;

                LocalTime as = LocalTime.parse(a.getStartTime());
                LocalTime ae = LocalTime.parse(a.getEndTime());
                LocalTime bs = LocalTime.parse(b.getStartTime());
                LocalTime be = LocalTime.parse(b.getEndTime());

                boolean overlap = as.isBefore(be) && ae.isAfter(bs);
                if (overlap) conflicts++;
            }
        }

        return conflicts;
    }

    /**
     * Rounds a double value to two decimal places.
     * 
     * @param v the value to round
     * @return the rounded value
     */
    private double round(double v) {
        return Math.round(v * 100) / 100.0;
    }
}
