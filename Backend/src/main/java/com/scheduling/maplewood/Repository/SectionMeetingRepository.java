package com.scheduling.maplewood.Repository;

import com.scheduling.maplewood.Entity.SectionMeeting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionMeetingRepository extends JpaRepository<SectionMeeting, Integer> {
    List<SectionMeeting> findBySectionId(Integer sectionId);
}
