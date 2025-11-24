package com.scheduling.maplewood.Service.ScheduleGenerator;

import com.scheduling.maplewood.Repository.SectionMeetingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.scheduling.maplewood.Entity.Classroom;
import com.scheduling.maplewood.Entity.Course;
import com.scheduling.maplewood.Entity.CourseSection;
import com.scheduling.maplewood.Entity.SectionMeeting;
import com.scheduling.maplewood.Entity.Teacher;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WeeklySchedulerService {

    private final SectionMeetingRepository sectionMeetingRepository;

    private final List<String> DAYS = List.of(
            "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"
    );

    private final List<TimeBlock> BLOCKS = List.of(
            new TimeBlock(LocalTime.of(9, 0), LocalTime.of(11, 0)),   // 2 hours
            new TimeBlock(LocalTime.of(11, 0), LocalTime.of(12, 0)),  // 1 hour
            new TimeBlock(LocalTime.of(13, 0), LocalTime.of(15, 0)),  // 2 hours
            new TimeBlock(LocalTime.of(15, 0), LocalTime.of(17, 0))   // 2 hours
    );

    /**
     * Attempts to schedule a course section for a given course, teacher, classroom, teacher availability, room availability, and teacher daily hours.
     * 
     * @param section the course section to schedule
     * @param course the course to schedule
     * @param teacher the teacher to schedule
     * @param room the classroom to schedule
     * @param teacherAvail a map of teacher ID to availability
     * @param roomAvail a map of room ID to availability
     * @param teacherDailyHoursGlobal a map of teacher ID to daily hours
     */
    public void scheduleSection(
            CourseSection section,
            Course course,
            Teacher teacher,
            Classroom room,
            Map<Integer, AvailabilityService.Availability> teacherAvail,
            Map<Integer, AvailabilityService.Availability> roomAvail,
            Map<Integer, Map<String, Integer>> teacherDailyHoursGlobal
    ) {

        int weeklyHours = (course.getHoursPerWeek() == null ? 3 : course.getHoursPerWeek());

        AvailabilityService.Availability tAvail = teacherAvail.get(teacher.getId());
        AvailabilityService.Availability rAvail = roomAvail.get(room.getId());
        Map<String, Integer> tDaily = teacherDailyHoursGlobal.get(teacher.getId());

        Random random = new Random();
        int startIndex = random.nextInt(DAYS.size()); // RANDOM START DAY

        int attempts = 0;
        int maxAttempts = 100;

        while (weeklyHours > 0 && attempts < maxAttempts) {
            attempts++;

            for (int offset = 0; offset < DAYS.size(); offset++) {

                String day = DAYS.get((startIndex + offset) % DAYS.size());

                if (weeklyHours <= 0) break;
                if (tDaily.get(day) >= 4) continue;

                List<TimeBlock> eligible = getEligibleBlocks(
                        day, weeklyHours, tAvail, rAvail, tDaily
                );

                if (eligible.isEmpty()) continue;

                TimeBlock chosen = pickSmartRandomBlock(eligible);
                if (chosen == null) continue;

                createMeeting(section, day, chosen.start, chosen.end);

                removeBlockFromAvailability(chosen, tAvail.slots.get(day));
                removeBlockFromAvailability(chosen, rAvail.slots.get(day));

                tDaily.put(day, tDaily.get(day) + chosen.durationHours());
                weeklyHours -= chosen.durationHours();
            }
        }
    }

    /**
     * Gets a list of time blocks that are eligible for scheduling a meeting.
     * 
     * A time block is eligible if it fits the remaining hours of the section,
     * does not exceed the teacher's daily limit, and is available in both the
     * teacher's and the room's availability.
     * 
     * @param day the day of the week
     * @param weeklyHours the remaining hours of the section
     * @param teacherAvail the teacher's availability
     * @param roomAvail the room's availability
     * @param tDaily the teacher's daily hours
     * @return a list of eligible time blocks
     */
    private List<TimeBlock> getEligibleBlocks(
            String day,
            int weeklyHours,
            AvailabilityService.Availability teacherAvail,
            AvailabilityService.Availability roomAvail,
            Map<String, Integer> tDaily
    ) {
        return BLOCKS.stream()
                .filter(b -> b.durationHours() <= weeklyHours)      // fits remaining hours
                .filter(b -> tDaily.get(day) + b.durationHours() <= 4) // teacher limit
                .filter(b -> isBlockAvailable(b, teacherAvail.slots.get(day)))
                .filter(b -> isBlockAvailable(b, roomAvail.slots.get(day)))
                .collect(Collectors.toList());
    }

    /**
     * Checks if a given TimeBlock is available in a set of available slots.
     * 
     * @param block the TimeBlock to check
     * @param availableSlots the set of available slots
     * @return true if the TimeBlock is available, false otherwise
     */
    private boolean isBlockAvailable(TimeBlock block, Set<LocalTime> availableSlots) {
        for (LocalTime t = block.start; t.isBefore(block.end); t = t.plusHours(1)) {
            if (!availableSlots.contains(t)) return false;
        }
        return true;
    }

    /**
     * Picks a random TimeBlock from the given list of eligible blocks.
     * 
     * The blocks are sorted in descending order of duration, and the top 3
     * blocks are chosen. A random block is then picked from the top 3.
     * 
     * @param eligible the list of eligible blocks
     * @return a random TimeBlock from the eligible blocks
     */
    private TimeBlock pickSmartRandomBlock(List<TimeBlock> eligible) {

        eligible.sort(Comparator.comparingInt(TimeBlock::durationHours).reversed()); // longest first
        int limit = Math.min(3, eligible.size());
        List<TimeBlock> top = eligible.subList(0, limit);

        Random random = new Random();
        return top.get(random.nextInt(top.size()));
    }

    /**
     * Removes a given TimeBlock from a set of available slots.
     * 
     * This method iterates over the given TimeBlock and removes each LocalTime
     * from the given set of available slots.
     * 
     * @param block the TimeBlock to remove
     * @param slots the set of available slots
     */
    private void removeBlockFromAvailability(TimeBlock block, Set<LocalTime> slots) {
        for (LocalTime t = block.start; t.isBefore(block.end); t = t.plusHours(1)) {
            slots.remove(t);
        }
    }

    /**
     * Creates a new SectionMeeting and saves it to the database.
     * 
     * @param section the section to which the meeting belongs
     * @param day the day of the meeting
     * @param start the start time of the meeting
     * @param end the end time of the meeting
     */
    private void createMeeting(CourseSection section, String day, LocalTime start, LocalTime end) {
        SectionMeeting m = new SectionMeeting();
        m.setSectionId(section.getId());
        m.setDayOfWeek(day);
        m.setStartTime(start.toString());
        m.setEndTime(end.toString());
        sectionMeetingRepository.save(m);
    }

    /**
     * A class representing a time block.
     */
    public static class TimeBlock {
        public LocalTime start;
        public LocalTime end;

        public TimeBlock(LocalTime start, LocalTime end) {
            this.start = start;
            this.end = end;
        }

        /**
         * Returns the duration of the time block in hours.
         * 
         * This method returns the difference in hours between the end and start times
         * of the time block.
         * 
         * @return the duration of the time block in hours
         */
        public int durationHours() {
            return end.getHour() - start.getHour();
        }
    }
}
