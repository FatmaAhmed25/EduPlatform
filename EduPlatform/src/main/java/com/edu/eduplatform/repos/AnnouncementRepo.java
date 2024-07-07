
package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnnouncementRepo extends JpaRepository<Announcement, Long> {
    List<Announcement> findByCourse_CourseId(Long courseId);

    List<Announcement> findByCourseOrderByCreatedAtDesc(Course course);

    List<Announcement >findByCourse(Course course);

    List<Announcement> findAnnouncementsByFileNameStartingWithAndCourse_CourseId(String fileName, Long courseId);


}

