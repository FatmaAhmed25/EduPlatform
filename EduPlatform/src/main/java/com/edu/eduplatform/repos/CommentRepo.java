package com.edu.eduplatform.repos;

import com.edu.eduplatform.models.Announcement;
import com.edu.eduplatform.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepo extends JpaRepository<Comment, Long> {
    List<Comment> findByAnnouncementOrderByCreatedAtAsc(Announcement announcement);
}
