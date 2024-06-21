
package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

    public interface AnnouncementRepo extends JpaRepository<Announcement, Long> {
        List<Announcement> findByCourse_CourseId(Long courseId);
    }

